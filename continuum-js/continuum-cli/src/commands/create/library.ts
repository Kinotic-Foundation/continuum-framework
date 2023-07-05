import {Args, Command} from '@oclif/core'
import path from 'node:path'
import {spawnEngine} from '../../internal/SpawnEngine.js'

export default class Library extends Command {
  static description = 'Creates a Continuum Library'

  static examples = [
    `$ continuum create library my-library`,
  ]

  static args = {
    artifactId: Args.string({description: 'The Maven Artifact Id for the Library', required: true})
  }


  async run(): Promise<void> {
    const {args} = await this.parse(Library)

    let serviceDir: string = path.resolve(args.artifactId)

    await spawnEngine.renderSpawn('project-library',
                                  serviceDir,
                                  { artifactId: args.artifactId})

  }


}
