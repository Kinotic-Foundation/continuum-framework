import {Args, Command} from '@oclif/core'
import {createFrontEnd} from "../../internal/CommandHelper.js"

export default class Library extends Command {
  static description = 'Creates a Continuum Frontend Project'

  static examples = [
    `$ continuum create frontend my-frontend`,
  ]

  static args = {
    name: Args.string({description: 'The Name for the Frontend Project', required: true})
  }


  async run(): Promise<void> {
    const {args} = await this.parse(Library)

    await createFrontEnd(args.name)
  }

}
