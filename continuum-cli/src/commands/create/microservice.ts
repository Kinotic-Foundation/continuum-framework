import {Command, Flags} from '@oclif/core'
import fs from 'fs'
import path from 'path'
import {spawnEngine} from '../../internal/SpawnEngine'

export default class Microservice extends Command {
  static description = 'Creates a Continuum Microservice'

  static examples = [
    `$ continuum create microservice MyMicroservice`,
  ]

  static args = [{name: 'artifactId', description: 'The Maven Artifact Id of the Microservice', required: false}]

  // static flags = {
  //   force: Flags.boolean({char: 'f', description: 'Will overwrite existing files if they already exist', required: false}),
  // }

  async run(): Promise<void> {
    const {args, flags} = await this.parse(Microservice)

    let serviceDir: string = path.resolve(args.artifactId)

    await spawnEngine.renderSpawn('microserviceProject',
                                  serviceDir,
                                  { artifactId: args.artifactId})

  }


}
