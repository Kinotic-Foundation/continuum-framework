import {Command, Flags} from '@oclif/core'
import * as inquirer from 'inquirer'

export default class Create extends Command {
  static description = 'Creates Continuum projects, applications, and services'

  static examples = [
    `$ continuum create`,
  ]

  static flags = {
    force: Flags.string({char: 'f', description: 'Will overwrite existing files if they already exist', required: false}),
  }

  async run(): Promise<void> {
    const {args, flags} = await this.parse(Create)

    let questions: any[] = [
      {
        type: 'list',
        name: 'what',
        message: 'What do you want to create?',
        choices: ['Microservice', 'Standalone Web Application', 'Example Project'],
        filter(val: string) {
          return val.toLowerCase();
        },
      }
    ]

    let answers: any = await inquirer.prompt(questions)

    this.log(`Answers ${answers}`)


  }

}
