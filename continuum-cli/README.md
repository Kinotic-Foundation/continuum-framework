oclif-hello-world
=================

oclif example Hello World CLI

[![oclif](https://img.shields.io/badge/cli-oclif-brightgreen.svg)](https://oclif.io)
[![Version](https://img.shields.io/npm/v/oclif-hello-world.svg)](https://npmjs.org/package/oclif-hello-world)
[![CircleCI](https://circleci.com/gh/oclif/hello-world/tree/main.svg?style=shield)](https://circleci.com/gh/oclif/hello-world/tree/main)
[![Downloads/week](https://img.shields.io/npm/dw/oclif-hello-world.svg)](https://npmjs.org/package/oclif-hello-world)
[![License](https://img.shields.io/npm/l/oclif-hello-world.svg)](https://github.com/oclif/hello-world/blob/main/package.json)

<!-- toc -->
* [Usage](#usage)
* [Commands](#commands)
<!-- tocstop -->
# Usage
<!-- usage -->
```sh-session
$ npm install -g continuum-cli
$ continuum COMMAND
running command...
$ continuum (--version)
continuum-cli/0.0.0 darwin-x64 node-v16.13.1
$ continuum --help [COMMAND]
USAGE
  $ continuum COMMAND
...
```
<!-- usagestop -->
# Commands
<!-- commands -->
* [`continuum hello PERSON`](#continuum-hello-person)
* [`continuum hello world`](#continuum-hello-world)
* [`continuum help [COMMAND]`](#continuum-help-command)
* [`continuum plugins`](#continuum-plugins)
* [`continuum plugins:inspect PLUGIN...`](#continuum-pluginsinspect-plugin)
* [`continuum plugins:install PLUGIN...`](#continuum-pluginsinstall-plugin)
* [`continuum plugins:link PLUGIN`](#continuum-pluginslink-plugin)
* [`continuum plugins:uninstall PLUGIN...`](#continuum-pluginsuninstall-plugin)
* [`continuum plugins update`](#continuum-plugins-update)

## `continuum hello PERSON`

Say hello

```
USAGE
  $ continuum hello [PERSON] -f <value>

ARGUMENTS
  PERSON  Person to say hello to

FLAGS
  -f, --from=<value>  (required) Whom is saying hello

DESCRIPTION
  Say hello

EXAMPLES
  $ oex hello friend --from oclif
  hello friend from oclif! (./src/commands/hello/index.ts)
```

_See code: [dist/commands/hello/index.ts](https://github.com/NavidMitchell/continuum-framework/blob/v0.0.0/dist/commands/hello/index.ts)_

## `continuum hello world`

Say hello world

```
USAGE
  $ continuum hello world

DESCRIPTION
  Say hello world

EXAMPLES
  $ oex hello world
  hello world! (./src/commands/hello/world.ts)
```

## `continuum help [COMMAND]`

Display help for continuum.

```
USAGE
  $ continuum help [COMMAND] [-n]

ARGUMENTS
  COMMAND  Command to show help for.

FLAGS
  -n, --nested-commands  Include all nested commands in the output.

DESCRIPTION
  Display help for continuum.
```

_See code: [@oclif/plugin-help](https://github.com/oclif/plugin-help/blob/v5.1.10/src/commands/help.ts)_

## `continuum plugins`

List installed plugins.

```
USAGE
  $ continuum plugins [--core]

FLAGS
  --core  Show core plugins.

DESCRIPTION
  List installed plugins.

EXAMPLES
  $ continuum plugins
```

_See code: [@oclif/plugin-plugins](https://github.com/oclif/plugin-plugins/blob/v2.0.11/src/commands/plugins/index.ts)_

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

DESCRIPTION
  Displays installation properties of a plugin.

EXAMPLES
  $ continuum plugins:inspect myplugin
```

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

## `continuum plugins:link PLUGIN`

Links a plugin into the CLI for development.

```
USAGE
  $ continuum plugins:link PLUGIN

ARGUMENTS
  PATH  [default: .] path to plugin

FLAGS
  -h, --help     Show CLI help.
  -v, --verbose

DESCRIPTION
  Links a plugin into the CLI for development.

  Installation of a linked plugin will override a user-installed or core plugin.

  e.g. If you have a user-installed or core plugin that has a 'hello' command, installing a linked plugin with a 'hello'
  command will override the user-installed or core plugin implementation. This is useful for development work.

EXAMPLES
  $ continuum plugins:link myplugin
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
<!-- commandsstop -->
