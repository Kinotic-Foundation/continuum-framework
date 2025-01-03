@kinotic/z-cli
=================

The CLI to help you move fast!


[![oclif](https://img.shields.io/badge/cli-oclif-brightgreen.svg)](https://oclif.io)
[![Version](https://img.shields.io/npm/v/@kinotic/z-cli.svg)](https://npmjs.org/package/@kinotic/z-cli)
[![Downloads/week](https://img.shields.io/npm/dw/@kinotic/z-cli.svg)](https://npmjs.org/package/@kinotic/z-cli)


<!-- toc -->
* [Usage](#usage)
* [Commands](#commands)
<!-- tocstop -->
# Usage
<!-- usage -->
```sh-session
$ npm install -g @kinotic/z-cli
$ z COMMAND
running command...
$ z (--version)
@kinotic/z-cli/0.0.0 darwin-arm64 node-v22.11.0
$ z --help [COMMAND]
USAGE
  $ z COMMAND
...
```
<!-- usagestop -->
# Commands
<!-- commands -->
* [`z hello PERSON`](#z-hello-person)
* [`z hello world`](#z-hello-world)
* [`z help [COMMAND]`](#z-help-command)
* [`z plugins`](#z-plugins)
* [`z plugins add PLUGIN`](#z-plugins-add-plugin)
* [`z plugins:inspect PLUGIN...`](#z-pluginsinspect-plugin)
* [`z plugins install PLUGIN`](#z-plugins-install-plugin)
* [`z plugins link PATH`](#z-plugins-link-path)
* [`z plugins remove [PLUGIN]`](#z-plugins-remove-plugin)
* [`z plugins reset`](#z-plugins-reset)
* [`z plugins uninstall [PLUGIN]`](#z-plugins-uninstall-plugin)
* [`z plugins unlink [PLUGIN]`](#z-plugins-unlink-plugin)
* [`z plugins update`](#z-plugins-update)

## `z hello PERSON`

Say hello

```
USAGE
  $ z hello PERSON -f <value>

ARGUMENTS
  PERSON  Person to say hello to

FLAGS
  -f, --from=<value>  (required) Who is saying hello

DESCRIPTION
  Say hello

EXAMPLES
  $ z hello friend --from oclif
  hello friend from oclif! (./src/commands/hello/index.ts)
```

_See code: [src/commands/hello/index.ts](https://github.com/continuum-js/z-cli/blob/v0.0.0/src/commands/hello/index.ts)_

## `z hello world`

Say hello world

```
USAGE
  $ z hello world

DESCRIPTION
  Say hello world

EXAMPLES
  $ z hello world
  hello world! (./src/commands/hello/world.ts)
```

_See code: [src/commands/hello/world.ts](https://github.com/continuum-js/z-cli/blob/v0.0.0/src/commands/hello/world.ts)_

## `z help [COMMAND]`

Display help for z.

```
USAGE
  $ z help [COMMAND...] [-n]

ARGUMENTS
  COMMAND...  Command to show help for.

FLAGS
  -n, --nested-commands  Include all nested commands in the output.

DESCRIPTION
  Display help for z.
```

_See code: [@oclif/plugin-help](https://github.com/oclif/plugin-help/blob/v6.2.20/src/commands/help.ts)_

## `z plugins`

List installed plugins.

```
USAGE
  $ z plugins [--json] [--core]

FLAGS
  --core  Show core plugins.

GLOBAL FLAGS
  --json  Format output as json.

DESCRIPTION
  List installed plugins.

EXAMPLES
  $ z plugins
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v5.4.23/src/commands/plugins/index.ts)_

## `z plugins add PLUGIN`

Installs a plugin into z.

```
USAGE
  $ z plugins add PLUGIN... [--json] [-f] [-h] [-s | -v]

ARGUMENTS
  PLUGIN...  Plugin to install.

FLAGS
  -f, --force    Force npm to fetch remote resources even if a local copy exists on disk.
  -h, --help     Show CLI help.
  -s, --silent   Silences npm output.
  -v, --verbose  Show verbose npm output.

GLOBAL FLAGS
  --json  Format output as json.

DESCRIPTION
  Installs a plugin into z.

  Uses npm to install plugins.

  Installation of a user-installed plugin will override a core plugin.

  Use the Z_NPM_LOG_LEVEL environment variable to set the npm loglevel.
  Use the Z_NPM_REGISTRY environment variable to set the npm registry.

ALIASES
  $ z plugins add

EXAMPLES
  Install a plugin from npm registry.

    $ z plugins add myplugin

  Install a plugin from a github url.

    $ z plugins add https://github.com/someuser/someplugin

  Install a plugin from a github slug.

    $ z plugins add someuser/someplugin
```

## `z plugins:inspect PLUGIN...`

Displays installation properties of a plugin.

```
USAGE
  $ z plugins inspect PLUGIN...

ARGUMENTS
  PLUGIN...  [default: .] Plugin to inspect.

FLAGS
  -h, --help     Show CLI help.
  -v, --verbose

GLOBAL FLAGS
  --json  Format output as json.

DESCRIPTION
  Displays installation properties of a plugin.

EXAMPLES
  $ z plugins inspect myplugin
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v5.4.23/src/commands/plugins/inspect.ts)_

## `z plugins install PLUGIN`

Installs a plugin into z.

```
USAGE
  $ z plugins install PLUGIN... [--json] [-f] [-h] [-s | -v]

ARGUMENTS
  PLUGIN...  Plugin to install.

FLAGS
  -f, --force    Force npm to fetch remote resources even if a local copy exists on disk.
  -h, --help     Show CLI help.
  -s, --silent   Silences npm output.
  -v, --verbose  Show verbose npm output.

GLOBAL FLAGS
  --json  Format output as json.

DESCRIPTION
  Installs a plugin into z.

  Uses npm to install plugins.

  Installation of a user-installed plugin will override a core plugin.

  Use the Z_NPM_LOG_LEVEL environment variable to set the npm loglevel.
  Use the Z_NPM_REGISTRY environment variable to set the npm registry.

ALIASES
  $ z plugins add

EXAMPLES
  Install a plugin from npm registry.

    $ z plugins install myplugin

  Install a plugin from a github url.

    $ z plugins install https://github.com/someuser/someplugin

  Install a plugin from a github slug.

    $ z plugins install someuser/someplugin
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v5.4.23/src/commands/plugins/install.ts)_

## `z plugins link PATH`

Links a plugin into the CLI for development.

```
USAGE
  $ z plugins link PATH [-h] [--install] [-v]

ARGUMENTS
  PATH  [default: .] path to plugin

FLAGS
  -h, --help          Show CLI help.
  -v, --verbose
      --[no-]install  Install dependencies after linking the plugin.

DESCRIPTION
  Links a plugin into the CLI for development.

  Installation of a linked plugin will override a user-installed or core plugin.

  e.g. If you have a user-installed or core plugin that has a 'hello' command, installing a linked plugin with a 'hello'
  command will override the user-installed or core plugin implementation. This is useful for development work.


EXAMPLES
  $ z plugins link myplugin
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v5.4.23/src/commands/plugins/link.ts)_

## `z plugins remove [PLUGIN]`

Removes a plugin from the CLI.

```
USAGE
  $ z plugins remove [PLUGIN...] [-h] [-v]

ARGUMENTS
  PLUGIN...  plugin to uninstall

FLAGS
  -h, --help     Show CLI help.
  -v, --verbose

DESCRIPTION
  Removes a plugin from the CLI.

ALIASES
  $ z plugins unlink
  $ z plugins remove

EXAMPLES
  $ z plugins remove myplugin
```

## `z plugins reset`

Remove all user-installed and linked plugins.

```
USAGE
  $ z plugins reset [--hard] [--reinstall]

FLAGS
  --hard       Delete node_modules and package manager related files in addition to uninstalling plugins.
  --reinstall  Reinstall all plugins after uninstalling.
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v5.4.23/src/commands/plugins/reset.ts)_

## `z plugins uninstall [PLUGIN]`

Removes a plugin from the CLI.

```
USAGE
  $ z plugins uninstall [PLUGIN...] [-h] [-v]

ARGUMENTS
  PLUGIN...  plugin to uninstall

FLAGS
  -h, --help     Show CLI help.
  -v, --verbose

DESCRIPTION
  Removes a plugin from the CLI.

ALIASES
  $ z plugins unlink
  $ z plugins remove

EXAMPLES
  $ z plugins uninstall myplugin
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v5.4.23/src/commands/plugins/uninstall.ts)_

## `z plugins unlink [PLUGIN]`

Removes a plugin from the CLI.

```
USAGE
  $ z plugins unlink [PLUGIN...] [-h] [-v]

ARGUMENTS
  PLUGIN...  plugin to uninstall

FLAGS
  -h, --help     Show CLI help.
  -v, --verbose

DESCRIPTION
  Removes a plugin from the CLI.

ALIASES
  $ z plugins unlink
  $ z plugins remove

EXAMPLES
  $ z plugins unlink myplugin
```

## `z plugins update`

Update installed plugins.

```
USAGE
  $ z plugins update [-h] [-v]

FLAGS
  -h, --help     Show CLI help.
  -v, --verbose

DESCRIPTION
  Update installed plugins.
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v5.4.23/src/commands/plugins/update.ts)_
<!-- commandsstop -->
