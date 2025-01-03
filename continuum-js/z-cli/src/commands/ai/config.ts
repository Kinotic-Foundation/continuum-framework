import {loadConfig, saveConfig} from '../../internal/state/Config.js'
import {input} from '@inquirer/prompts'
import {Command} from '@oclif/core'
import chalk from 'chalk'

export default class Config extends Command {

  static override description = 'Configures Z for use'

  static override examples = [
    '<%= config.bin %> <%= command.id %>',
  ]

  public async run(): Promise<void> {

    const config = await loadConfig(this.config.configDir)

    config.openAIKey = await input({message: 'Enter your OpenAI API Key'})

    await saveConfig(this.config.configDir, config)

    this.log(chalk.blue('Z') + chalk.green(' Configured'))
  }
}
