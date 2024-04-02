Continuum CLI
=================

Continuum CLI

[//]: # ([![Version]&#40;https://img.shields.io/npm/v/oclif-hello-world.svg&#41;]&#40;https://npmjs.org/package/oclif-hello-world&#41;)

[//]: # ([![CircleCI]&#40;https://circleci.com/gh/oclif/hello-world/tree/main.svg?style=shield&#41;]&#40;https://circleci.com/gh/oclif/hello-world/tree/main&#41;)

[//]: # ([![Downloads/week]&#40;https://img.shields.io/npm/dw/oclif-hello-world.svg&#41;]&#40;https://npmjs.org/package/oclif-hello-world&#41;)

[//]: # ([![License]&#40;https://img.shields.io/npm/l/oclif-hello-world.svg&#41;]&#40;https://github.com/oclif/hello-world/blob/main/package.json&#41;)

<!-- toc -->
* [Usage](#usage)
* [Commands](#commands)
<!-- tocstop -->
# Usage
<!-- usage -->
```sh-session
$ npm install -g @kinotic/continuum-cli
$ continuum COMMAND
running command...
$ continuum (--version)
@kinotic/continuum-cli/0.2.0 darwin-x64 node-v20.11.0
$ continuum --help [COMMAND]
USAGE
  $ continuum COMMAND
...
```
<!-- usagestop -->
# Commands
<!-- commands -->
* [`continuum autocomplete [SHELL]`](#continuum-autocomplete-shell)
* [`continuum create frontend NAME`](#continuum-create-frontend-name)
* [`continuum create library ARTIFACTID`](#continuum-create-library-artifactid)
* [`continuum create microservice ARTIFACTID`](#continuum-create-microservice-artifactid)
* [`continuum create project NAME`](#continuum-create-project-name)
* [`continuum help [COMMAND]`](#continuum-help-command)
* [`continuum plugins`](#continuum-plugins)
* [`continuum plugins:install PLUGIN...`](#continuum-pluginsinstall-plugin)
* [`continuum plugins:inspect PLUGIN...`](#continuum-pluginsinspect-plugin)
* [`continuum plugins:install PLUGIN...`](#continuum-pluginsinstall-plugin-1)
* [`continuum plugins:link PLUGIN`](#continuum-pluginslink-plugin)
* [`continuum plugins:uninstall PLUGIN...`](#continuum-pluginsuninstall-plugin)
* [`continuum plugins:uninstall PLUGIN...`](#continuum-pluginsuninstall-plugin-1)
* [`continuum plugins:uninstall PLUGIN...`](#continuum-pluginsuninstall-plugin-2)
* [`continuum plugins update`](#continuum-plugins-update)
* [`continuum update [CHANNEL]`](#continuum-update-channel)

## `continuum autocomplete [SHELL]`

display autocomplete installation instructions

```
USAGE
  $ continuum autocomplete [SHELL] [-r]

ARGUMENTS
  SHELL  (zsh|bash|powershell) Shell type

FLAGS
  -r, --refresh-cache  Refresh cache (ignores displaying instructions)

DESCRIPTION
  display autocomplete installation instructions

EXAMPLES
  $ continuum autocomplete

  $ continuum autocomplete bash

  $ continuum autocomplete zsh

  $ continuum autocomplete powershell

  $ continuum autocomplete --refresh-cache
```

_See code: [@oclif/plugin-autocomplete](https://github.com/oclif/plugin-autocomplete/blob/v2.3.10/src/commands/autocomplete/index.ts)_

## `continuum create frontend NAME`

Creates a Continuum Frontend Project

```
USAGE
  $ continuum create frontend NAME

ARGUMENTS
  NAME  The Name for the Frontend Project

DESCRIPTION
  Creates a Continuum Frontend Project

EXAMPLES
  $ continuum create frontend my-frontend
```

_See code: [src/commands/create/frontend.ts](https://github.com/Kinotic-Foundation/continuum-framework/blob/v0.2.0/src/commands/create/frontend.ts)_

## `continuum create library ARTIFACTID`

Creates a Continuum Library

```
USAGE
  $ continuum create library ARTIFACTID

ARGUMENTS
  ARTIFACTID  The Maven Artifact Id for the Library

DESCRIPTION
  Creates a Continuum Library

EXAMPLES
  $ continuum create library my-library
```

_See code: [src/commands/create/library.ts](https://github.com/Kinotic-Foundation/continuum-framework/blob/v0.2.0/src/commands/create/library.ts)_

## `continuum create microservice ARTIFACTID`

Creates a Continuum Microservice

```
USAGE
  $ continuum create microservice ARTIFACTID

ARGUMENTS
  ARTIFACTID  The Maven Artifact Id for the Microservice

DESCRIPTION
  Creates a Continuum Microservice

EXAMPLES
  $ continuum create microservice my-microservice
```

_See code: [src/commands/create/microservice.ts](https://github.com/Kinotic-Foundation/continuum-framework/blob/v0.2.0/src/commands/create/microservice.ts)_

## `continuum create project NAME`

Creates a Continuum Project

```
USAGE
  $ continuum create project NAME

ARGUMENTS
  NAME  The Name for the Project

DESCRIPTION
  Creates a Continuum Project

EXAMPLES
  $ continuum create project MyContinuumProject
```

_See code: [src/commands/create/project.ts](https://github.com/Kinotic-Foundation/continuum-framework/blob/v0.2.0/src/commands/create/project.ts)_

## `continuum help [COMMAND]`

display help for continuum

```
USAGE
  $ continuum help [COMMAND] [--json] [--all]

ARGUMENTS
  COMMAND  command to show help for

FLAGS
  --all   see all commands in CLI
  --json  Format output as json.

DESCRIPTION
  display help for continuum
```

_See code: [@oclif/plugin-help](https://github.com/oclif/plugin-help/blob/v5.0.0/src/commands/help.ts)_

## `continuum plugins`

List installed plugins.

```
USAGE
  $ continuum plugins [--json] [--core]

FLAGS
  --core  Show core plugins.

GLOBAL FLAGS
  --json  Format output as json.

DESCRIPTION
  List installed plugins.

EXAMPLES
  $ continuum plugins
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v3.9.4/src/commands/plugins/index.ts)_

## `continuum plugins:install PLUGIN...`

Installs a plugin into the CLI.

```
USAGE
  $ continuum plugins:install PLUGIN...

ARGUMENTS
  PLUGIN  Plugin to install.

FLAGS
  -f, --force    Run yarn install with force flag.
  -h, --help     Show CLI help.
  -v, --verbose

DESCRIPTION
  Installs a plugin into the CLI.
  Can be installed from npm or a git url.

  Installation of a user-installed plugin will override a core plugin.

  e.g. If you have a core plugin that has a 'hello' command, installing a user-installed plugin with a 'hello' command
  will override the core plugin implementation. This is useful if a user needs to update core plugin functionality in
  the CLI without the need to patch and update the whole CLI.


ALIASES
  $ continuum plugins add

EXAMPLES
  $ continuum plugins:install myplugin 

  $ continuum plugins:install https://github.com/someuser/someplugin

  $ continuum plugins:install someuser/someplugin
```

## `continuum plugins:inspect PLUGIN...`

Displays installation properties of a plugin.

```
USAGE
  $ continuum plugins:inspect PLUGIN...

ARGUMENTS
  PLUGIN  [default: .] Plugin to inspect.

FLAGS
  -h, --help     Show CLI help.
  -v, --verbose

GLOBAL FLAGS
  --json  Format output as json.

DESCRIPTION
  Displays installation properties of a plugin.

EXAMPLES
  $ continuum plugins:inspect myplugin
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v3.9.4/src/commands/plugins/inspect.ts)_

## `continuum plugins:install PLUGIN...`

Installs a plugin into the CLI.

```
USAGE
  $ continuum plugins:install PLUGIN...

ARGUMENTS
  PLUGIN  Plugin to install.

FLAGS
  -f, --force    Run yarn install with force flag.
  -h, --help     Show CLI help.
  -v, --verbose

DESCRIPTION
  Installs a plugin into the CLI.
  Can be installed from npm or a git url.

  Installation of a user-installed plugin will override a core plugin.

  e.g. If you have a core plugin that has a 'hello' command, installing a user-installed plugin with a 'hello' command
  will override the core plugin implementation. This is useful if a user needs to update core plugin functionality in
  the CLI without the need to patch and update the whole CLI.


ALIASES
  $ continuum plugins add

EXAMPLES
  $ continuum plugins:install myplugin 

  $ continuum plugins:install https://github.com/someuser/someplugin

  $ continuum plugins:install someuser/someplugin
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v3.9.4/src/commands/plugins/install.ts)_

## `continuum plugins:link PLUGIN`

Links a plugin into the CLI for development.

```
USAGE
  $ continuum plugins:link PLUGIN

ARGUMENTS
  PATH  [default: .] path to plugin

FLAGS
  -h, --help      Show CLI help.
  -v, --verbose
  --[no-]install  Install dependencies after linking the plugin.

DESCRIPTION
  Links a plugin into the CLI for development.
  Installation of a linked plugin will override a user-installed or core plugin.

  e.g. If you have a user-installed or core plugin that has a 'hello' command, installing a linked plugin with a 'hello'
  command will override the user-installed or core plugin implementation. This is useful for development work.


EXAMPLES
  $ continuum plugins:link myplugin
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v3.9.4/src/commands/plugins/link.ts)_

## `continuum plugins:uninstall PLUGIN...`

Removes a plugin from the CLI.

```
USAGE
  $ continuum plugins:uninstall PLUGIN...

ARGUMENTS
  PLUGIN  plugin to uninstall

FLAGS
  -h, --help     Show CLI help.
  -v, --verbose

DESCRIPTION
  Removes a plugin from the CLI.

ALIASES
  $ continuum plugins unlink
  $ continuum plugins remove
```

## `continuum plugins:uninstall PLUGIN...`

Removes a plugin from the CLI.

```
USAGE
  $ continuum plugins:uninstall PLUGIN...

ARGUMENTS
  PLUGIN  plugin to uninstall

FLAGS
  -h, --help     Show CLI help.
  -v, --verbose

DESCRIPTION
  Removes a plugin from the CLI.

ALIASES
  $ continuum plugins unlink
  $ continuum plugins remove
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v3.9.4/src/commands/plugins/uninstall.ts)_

## `continuum plugins:uninstall PLUGIN...`

Removes a plugin from the CLI.

```
USAGE
  $ continuum plugins:uninstall PLUGIN...

ARGUMENTS
  PLUGIN  plugin to uninstall

FLAGS
  -h, --help     Show CLI help.
  -v, --verbose

DESCRIPTION
  Removes a plugin from the CLI.

ALIASES
  $ continuum plugins unlink
  $ continuum plugins remove
```

## `continuum plugins update`

Update installed plugins.

```
USAGE
  $ continuum plugins update [-h] [-v]

FLAGS
  -h, --help     Show CLI help.
  -v, --verbose

DESCRIPTION
  Update installed plugins.
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v3.9.4/src/commands/plugins/update.ts)_

## `continuum update [CHANNEL]`

update the continuum CLI

```
USAGE
  $ continuum update [CHANNEL] [-a] [-v <value> | -i] [--force]

FLAGS
  -a, --available        Install a specific version.
  -i, --interactive      Interactively select version to install. This is ignored if a channel is provided.
  -v, --version=<value>  Install a specific version.
  --force                Force a re-download of the requested version.

DESCRIPTION
  update the continuum CLI

EXAMPLES
  Update to the stable channel:

    $ continuum update stable

  Update to a specific version:

    $ continuum update --version 1.0.0

  Interactively select version:

    $ continuum update --interactive

  See available versions:

    $ continuum update --available
```

_See code: [@oclif/plugin-update](https://github.com/oclif/plugin-update/blob/v3.2.4/src/commands/update.ts)_
<!-- commandsstop -->
