//JAVA 21
//DEPS com.enofex:taikai:2.9.0

import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.name;
import static com.tngtech.archunit.core.domain.properties.HasOwner.Predicates.With.owner;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.enofex.taikai.Taikai;
import com.enofex.taikai.TaikaiRule;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SpringRules {
  // Read in a repo's own copy only: a project on another version is blocked
  // until its rules are checked for that version and this line is set to it.
  static final String WRITTEN_FOR_SPRING_BOOT = "";

  static final String WEB_PACKAGES = "..web..";
  static final String SERVICE_PACKAGES = "..service..";
  static final String DATA_PACKAGES = "..data..";
  static final String CLIENT_PACKAGES = "..client..";
  static final String CONFIG_PACKAGES = "..config..";
  static final String UTILS_PACKAGES = "..utils..";

  // What reaches another service, whatever the protocol. A repo that uses
  // another client type adds it here.
  static final String CLIENTS_OF_ANOTHER_SERVICE =
      "org\\.springframework\\.web\\.(client\\.(RestClient|RestTemplate)"
          + "|reactive\\.function\\.client\\.WebClient)(\\$.*)?"
          + "|java\\.net\\.http\\.HttpClient(\\$.*)?"
          + "|io\\.grpc\\.(Managed)?Channel(Builder)?"
          + "|.*Grpc\\$\\w*Stub";

  static final List<String> FOLDERS_OF_HAND_WRITTEN_SOURCES =
      List.of("src/main/java", "src/main/kotlin");

  static final String SPRING_CONFIGURERS = "org\\.springframework\\..*Configurer";

  // flow-cli reads this exit code as broken rules. JBang's own failures exit 1 or 2.
  static final int RULES_BROKEN = 3;

  public static void main(String[] classFolders) {
    JavaClasses classes =
        new ClassFileImporter().importPaths(classFolders).that(writtenByHand());

    try {
      Taikai.builder()
          .classes(classes)
          .spring(spring -> spring
              .noAutowiredFields()
              .noInjectionIntoStaticFields()
              .noSelfInvocationOfProxiedMethods()
              .controllers(controllers -> controllers
                  .shouldNotDependOnOtherControllers()
                  .shouldNotDependOnRepositories())
              .services(services -> services.shouldNotDependOnControllers())
              .repositories(repositories -> repositories
                  .shouldNotDependOnServices()
                  .shouldNotDependOnControllers())
              .transactional(transactional -> transactional.shouldNotBeUsedInControllers()))
          .java(java -> java
              .classesShouldResideInPackage(configClasses(), CONFIG_PACKAGES)
              .classesShouldResideInPackage(
                  annotatedWith("org.springframework.cloud.openfeign.FeignClient"),
                  CLIENT_PACKAGES)
              .classesShouldResideInPackage(
                  annotatedWith("org.springframework.web.service.annotation.HttpExchange"),
                  CLIENT_PACKAGES)
              .classesShouldResideInPackage(helperClasses(), UTILS_PACKAGES))
          .addRule(TaikaiRule.of(callOtherServicesFromTheClientPackages()))
          .addRule(TaikaiRule.of(banConstructorOf(
              "tools.jackson.databind.json.JsonMapper",
              "Spring Boot configures a JsonMapper: inject it, a new one misses its modules"
                  + " and the spring.jackson settings")))
          .addRule(TaikaiRule.of(banMethod(
              "tools.jackson.databind.json.JsonMapper", "builder",
              "a JsonMapperBuilderCustomizer bean changes the JsonMapper Spring Boot configures")))
          .addRule(TaikaiRule.of(banConstructorOf(
              "tools.jackson.databind.ObjectMapper",
              "Spring Boot configures a JsonMapper: inject it")))
          .addRule(TaikaiRule.of(banConstructorOf(
              "com.fasterxml.jackson.databind.ObjectMapper",
              "Jackson 2 is deprecated in Spring Boot 4: inject the JsonMapper it configures")))
          .addRule(TaikaiRule.of(banMethod(
              "java.lang.Thread", "sleep",
              "a retry is Spring's @Retryable with @EnableResilientMethods and a schedule"
                  + " is @Scheduled")))
          .addRule(TaikaiRule.of(banClass(
              "java.net.HttpURLConnection",
              "Spring's RestClient makes HTTP calls, or WebClient in reactive code")))
          .addRule(TaikaiRule.of(banMethod(
              "java.lang.System", "getenv",
              "@ConfigurationProperties binds settings: Spring reads the environment for you")))
          .addRule(TaikaiRule.of(layout()))
          .build()
          .checkAll();
    } catch (AssertionError violations) {
      System.out.println(violations.getMessage());
      System.exit(RULES_BROKEN);
    }
  }

  static ArchRule layout() {
    return layeredArchitecture().consideringOnlyDependenciesInLayers()
        .withOptionalLayers(true)
        .layer("web").definedBy(handWrittenIn(WEB_PACKAGES))
        .layer("service").definedBy(handWrittenIn(SERVICE_PACKAGES))
        .layer("data").definedBy(handWrittenIn(DATA_PACKAGES))
        .layer("client").definedBy(handWrittenIn(CLIENT_PACKAGES))
        .whereLayer("web").mayNotBeAccessedByAnyLayer()
        .whereLayer("service").mayOnlyBeAccessedByLayers("web")
        .whereLayer("data").mayOnlyBeAccessedByLayers("service")
        .whereLayer("client").mayOnlyBeAccessedByLayers("service");
  }

  static ArchRule callOtherServicesFromTheClientPackages() {
    return noClasses().that().resideOutsideOfPackages(CLIENT_PACKAGES, CONFIG_PACKAGES)
        .should().dependOnClassesThat().haveNameMatching(CLIENTS_OF_ANOTHER_SERVICE)
        .because("a call to another service lives in a client package");
  }

  // Generated code is not checked: a class counts only when its source file
  // is in its module's own source folder, not in target/generated-sources.
  static DescribedPredicate<JavaClass> writtenByHand() {
    return DescribedPredicate.describe("written by hand", javaClass -> {
      String classFile = javaClass.getSource().map(source -> source.getUri().getPath()).orElse("");
      int moduleEnd = classFile.indexOf("/target/classes/");
      if (moduleEnd < 0) {
        return false;
      }

      String topLevelName = javaClass.getName().substring(javaClass.getPackageName().length() + 1);
      String sourceFile = javaClass.getSource()
          .flatMap(source -> source.getFileName())
          .orElse(topLevelName.split("\\$")[0] + ".java");
      Path packageFolder = Path.of(javaClass.getPackageName().replace('.', '/'));

      return FOLDERS_OF_HAND_WRITTEN_SOURCES.stream().anyMatch(sources -> Files.exists(
          Path.of(classFile.substring(0, moduleEnd), sources)
              .resolve(packageFolder)
              .resolve(sourceFile)));
    });
  }

  static DescribedPredicate<JavaClass> handWrittenIn(String packages) {
    return JavaClass.Predicates.resideInAPackage(packages).and(writtenByHand());
  }

  static DescribedPredicate<JavaClass> configClasses() {
    return DescribedPredicate.describe(
        "that set the application up (@Configuration, @ConfigurationProperties, a @Bean method"
            + " or a Spring Configurer)",
        javaClass -> !javaClass.isAnnotatedWith(
                "org.springframework.boot.autoconfigure.SpringBootApplication")
            && (javaClass.isAnnotatedWith("org.springframework.context.annotation.Configuration")
                || javaClass.isAnnotatedWith(
                    "org.springframework.boot.context.properties.ConfigurationProperties")
                || javaClass.getMethods().stream().anyMatch(method ->
                    method.isAnnotatedWith("org.springframework.context.annotation.Bean"))
                || javaClass.getAllRawInterfaces().stream().anyMatch(implemented ->
                    implemented.getName().matches(SPRING_CONFIGURERS))));
  }

  static DescribedPredicate<JavaClass> helperClasses() {
    return DescribedPredicate.describe(
        "that are helpers, with static methods only,",
        javaClass -> !javaClass.isEnum()
            && !javaClass.getMethods().isEmpty()
            && javaClass.getMethods().stream().allMatch(method ->
                method.getModifiers().contains(JavaModifier.STATIC)
                    && !method.getName().equals("main")));
  }

  static ArchRule banConstructorOf(String className, String whatToUse) {
    return noClasses().should()
        .callConstructorWhere(target(owner(name(className))))
        .because(whatToUse);
  }

  static ArchRule banMethod(String className, String method, String whatToUse) {
    return noClasses().should()
        .callMethodWhere(target(owner(name(className))).and(target(name(method))))
        .because(whatToUse);
  }

  static ArchRule banClass(String className, String whatToUse) {
    return noClasses().should()
        .dependOnClassesThat().haveFullyQualifiedName(className)
        .because(whatToUse);
  }
}
