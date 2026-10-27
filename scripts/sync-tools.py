#!/usr/bin/env python3
"""/flow:sync's tools step: keeps ~/.claude/flow/tools.yml in step with the
tools flow's lockfile lists.

usage: sync-tools.py PLUGIN_ROOT [--switch TOOL]

Each tool installed under flow's plugin root has an entry with its version,
first at flow's default (the lockfile's version). A tool flow no longer
lists loses its entry; a listed tool that is not installed keeps it, so a
failed install doesn't lose the user's version. When flow's default differs
from the entry's version, the entry gains flow_default and the difference is
reported; sync asks the user to keep their version or switch, and runs this
again with --switch TOOL to take flow's default. Prints nothing when every
entry already runs flow's default.
"""
import argparse
import copy
import json
import sys
import tempfile
from pathlib import Path

try:
    import yaml
except ImportError:
    yaml = None

TOOLS_FILE = Path.home() / ".claude/flow/tools.yml"


def main():
    parser = argparse.ArgumentParser(description="Keep tools.yml in step with flow's lockfile.")
    parser.add_argument("plugin_root", type=Path)
    parser.add_argument("--switch", metavar="TOOL", help="set TOOL's version to flow's default")
    args = parser.parse_args()
    plugin_root = args.plugin_root

    lockfile = plugin_root / "package-lock.json"
    if not lockfile.exists():
        print("no tools are included yet")
        return

    if yaml is None:
        sys.exit("the tools step needs PyYAML: python3 -m pip install pyyaml")

    packages = json.loads(lockfile.read_text())["packages"]
    defaults, missing = {}, []
    for package in packages[""].get("dependencies", {}):
        name = package.rsplit("/", 1)[-1]
        if not (plugin_root / "node_modules" / package).is_dir():
            missing.append(name)
            continue

        defaults[name] = packages[f"node_modules/{package}"]["version"]

    if args.switch and args.switch not in defaults:
        sys.exit(f"cannot switch {args.switch}: flow installed no such tool")

    tools = {}
    if TOOLS_FILE.exists():
        try:
            tools = yaml.safe_load(TOOLS_FILE.read_text()) or {}
        except yaml.YAMLError as error:
            sys.exit(f"{TOOLS_FILE} is not valid YAML: {error}")

    original = copy.deepcopy(tools)
    if args.switch:
        tools[args.switch] = {"version": defaults[args.switch]}
        print(f"{args.switch} switched to {defaults[args.switch]}")
    else:
        for name in missing:
            print(f"{name} is not installed in {plugin_root / 'node_modules'}")

        for name in [name for name in tools if name not in defaults and name not in missing]:
            del tools[name]
            print(f"{name} removed from {TOOLS_FILE}")

        for name, default in defaults.items():
            if name not in tools:
                tools[name] = {"version": default}
                print(f"{name} {default} added to {TOOLS_FILE}")

            if tools[name]["version"] == default:
                tools[name].pop("flow_default", None)
                continue

            tools[name]["flow_default"] = default
            print(f"{name}: tools.yml runs {tools[name]['version']}, flow's default is {default}")

    if tools == original:
        return

    # Written aside and moved into place, so a write that fails partway leaves
    # the previous file whole.
    TOOLS_FILE.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.NamedTemporaryFile("w", dir=TOOLS_FILE.parent, delete=False) as temporary:
        yaml.safe_dump(tools, temporary, sort_keys=False)

    Path(temporary.name).replace(TOOLS_FILE)


if __name__ == "__main__":
    main()
