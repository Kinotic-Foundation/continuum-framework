import {Args, Command} from '@oclif/core'
import path from 'node:path'
import {spawnEngine} from '../../internal/SpawnEngine.js'

export default class Microservice extends Command {
  static description = 'Creates a Continuum Microservice'

  static examples = [
    `$ continuum create microservice my-microservice`,
  ]

  static args = {
    artifactId: Args.string({description: 'The Maven Artifact Id for the Microservice', required: true})
  }

  async run(): Promise<void> {
    const {args} = await this.parse(Microservice)

    let serviceDir: string = path.resolve(args.artifactId)

    await spawnEngine.renderSpawn('project-microservice',
                                  serviceDir,
                                  { artifactId: args.artifactId})

  }


}
