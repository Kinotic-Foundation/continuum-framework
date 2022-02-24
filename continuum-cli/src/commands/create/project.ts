import {Command, Flags} from '@oclif/core'
import * as fs from 'fs'
import * as path from 'path'
import * as makeDir from 'make-dir'
import {spawn} from "../../internal/Spawn";

export default class Project extends Command {
  static description = 'Creates a Continuum project'

  static examples = [
    `$ continuum create project MyContinuumProject`,
  ]

  static args = [{name: 'name', description: 'The name of the Continuum Project', required: false}]

  // static flags = {
  //   force: Flags.boolean({char: 'f', description: 'Will overwrite existing files if they already exist', required: false}),
  // }

  async run(): Promise<void> {
    const {args, flags} = await this.parse(Project)

    let projectDir: string = path.resolve(args.name)
    let exists: boolean = fs.existsSync(projectDir)

    if(!exists){
      await makeDir(projectDir)

      let template = '/Users/navid/workspace/git/continuum-cli-templates/applicationStandalone'
      let outputPath = path.resolve(projectDir, 'user-service')

      let groupId: string = 'com.test'

      await spawn.renderDirectory(template,
                                  outputPath,
                                  { groupId: groupId,
                                    artifactId: 'user-service',
                                    basePackage: groupId,
                                    applicationName: 'UserService'
                                  })


    }else{
      this.error(`A file or folder with the name ${args.name} already exists`)
    }
  }


}
