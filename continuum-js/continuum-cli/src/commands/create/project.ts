import {Args, Command, ux} from '@oclif/core'
import inquirer from 'inquirer'
import path from 'node:path'
import process from 'node:process'
import {spawnEngine} from '../../internal/SpawnEngine.js'
import {spawnResolver} from '../../internal/SpawnResolver.js'
import {templateRepositoryManager} from '../../internal/TemplateRepositoryManager.js'
import upperFirst from 'lodash-es/upperFirst.js'
import {createFrontEnd} from '../../internal/CommandHelper.js'

export class Project extends Command {
  static description = 'Creates a Continuum Project'

  static examples = [
    `$ continuum create project MyContinuumProject`,
  ]

  static args = {
    name: Args.string({description: 'The Name for the Project', required: true})
  }

  async run(): Promise<void> {
    const {args} = await this.parse(Project)

    let projectDir: string = path.resolve(args.name)

    let context: any = { projectName: args.name}

    ux.action.start('Syncing project templates')
    await templateRepositoryManager.updateRepositoryIfNecessary()
    ux.action.stop()

    this.log('Creating Continuum Project')
    context = await spawnEngine.renderSpawn('project', projectDir, context)

    this.log('Creating Continuum Gateway')
    process.chdir(projectDir)
    // now create Continuum Gateway project
    await spawnEngine.renderSpawn('project-gateway',
                                  context.projectName + '-gateway',
                                  {...context,
                                           name: upperFirst(context.projectName) + 'Gateway',
                                           artifactId: context.projectName.toLowerCase() + '-gateway',
                                           basePackage: context.groupId + '.gateway'
                                  }
    )

    ux.action.start('Storing Templates')
    await spawnResolver.copySpawnsFromRepositoryToDirectory('microservice-common', 'project-microservice', 'project-library')
    ux.action.stop()

    let answers: any
    let first: boolean = true
    do {

      answers = await inquirer.prompt([
        {
          type: 'list',
          name: 'type',
          message: 'What would you like to add?',
          choices: ['Microservice', 'Library', 'Frontend', 'Quit']
        },
        {
          type: 'input',
          name: 'artifactId',
          message: 'Maven Artifact Id',
          when: (ans: any) => {
            return  ans?.type != 'Frontend' && ans?.type != 'Quit'
          }
        }
      ])

      let artifactId: string = answers.artifactId
      context.artifactId = artifactId

      switch(answers.type){
        case 'Microservice':
          await this.renderProjectModule('project-microservice', artifactId, projectDir, context);
          break;
        case 'Library':
          await this.renderProjectModule('project-library', artifactId, projectDir, context);
          break;
        case 'Frontend':
          const {name} = await inquirer.prompt([
            {
              type: 'input',
              name: 'name',
              message: 'Project Name'
            }
          ])
          await createFrontEnd(name)
          break;
        case 'Quit':
          break;
      }

      first = false
    } while(answers?.type != 'Quit')

  }

  private async renderProjectModule(spawn: string, artifactId: string, projectDir: string, context: any) {
    let dir: string = path.resolve(artifactId)

    if (!dir.startsWith(projectDir)) {
      throw new Error(`Artifact Dir ${dir} must be within ${projectDir}`)
    }

    await spawnEngine.renderSpawn(spawn, dir, context)
  }
}
