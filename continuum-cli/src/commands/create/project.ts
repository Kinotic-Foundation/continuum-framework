import {Command, Flags} from '@oclif/core'
import inquirer from 'inquirer'
import path from 'path'
import process from 'process'
import {spawnEngine} from '../../internal/SpawnEngine'
import {spawnResolver} from '../../internal/SpawnResolver'

export default class Project extends Command {
  static description = 'Creates a Continuum Project'

  static examples = [
    `$ continuum create project MyContinuumProject`,
  ]

  static args = [{name: 'name', description: 'The Name of the Project', required: false}]

  // static flags = {
  //   force: Flags.boolean({char: 'f', description: 'Will overwrite existing files if they already exist', required: false}),
  // }

  async run(): Promise<void> {
    const {args, flags} = await this.parse(Project)

    let projectDir: string = path.resolve(args.name)

    let context: any = { projectName: args.name}

    context = await spawnEngine.renderSpawn('project',
                                  projectDir,
                                  context)

    process.chdir(projectDir)

    await spawnResolver.copySpawnsFromRepositoryToDirectory('microserviceCommon', 'microserviceProject')

    let answers: any
    let first: boolean = true
    do {
      answers = await inquirer.prompt([
                                        {
                                          type: 'confirm',
                                          name: 'yes',
                                          message: `Would you like to add ${first ? 'a' : 'another'} Microservice`
                                        },
                                        {
                                          type: 'input',
                                          name: 'artifactId',
                                          message: 'Maven Artifact Id',
                                          when: ans => ans?.yes
                                        }
                                      ])
      if(answers.yes){
        context.artifactId = answers.artifactId
        let microserviceDir: string = path.resolve(answers.artifactId)

        if(!microserviceDir.startsWith(projectDir)){
          throw new Error(`Microservice Dir ${microserviceDir} must be within ${projectDir}`)
        }

        await spawnEngine.renderSpawn('microserviceProject',
                                      microserviceDir,
                                      context)
      }
      first = false
    } while(answers?.yes)


  }


}
