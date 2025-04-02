import {loadGptConfig, saveGptConfig} from '../../internal/state/GptConfig.js'
import {input} from '@inquirer/prompts'
import {Command} from '@oclif/core'
import chalk from 'chalk'

export default class Config extends Command {

  static override description = 'Configures Z for use'

  static override examples = [
    '<%= config.bin %> <%= command.id %>',
  ]

  public async run(): Promise<void> {

    const config = await loadGptConfig(this.config.configDir)

    config.openAIKey = await input({message: 'Enter your OpenAI API Key'})

    await saveGptConfig(this.config.configDir, config)

    this.log(chalk.blue('Z') + chalk.green(' Configured'))
  }
}
