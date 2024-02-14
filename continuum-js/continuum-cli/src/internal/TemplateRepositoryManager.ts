import fs from 'node:fs'
import { simpleGit } from 'simple-git';
import path from 'node:path'
import os from 'node:os'


export default class TemplateRepositoryManager {

  private static REPOSITORY_URL = 'https://github.com/Kinotic-Foundation/continuum-cli-templates.git'
  public static CONTINUUM_HOME = path.resolve(os.homedir(), '.continuum')
  public static REPOSITORY_PATH = path.resolve(TemplateRepositoryManager.CONTINUUM_HOME, 'spawns')
  private static GIT_REF = 'develop'


  /**
   * Clone the repository if it doesn't exist
   */
  private async cloneRepository(): Promise<void> {
    if (!fs.existsSync(TemplateRepositoryManager.CONTINUUM_HOME)) {
      fs.mkdirSync(TemplateRepositoryManager.CONTINUUM_HOME, {recursive: true})
    }
    if (!fs.existsSync(TemplateRepositoryManager.REPOSITORY_PATH)) {
      await simpleGit().clone(TemplateRepositoryManager.REPOSITORY_URL,
                              TemplateRepositoryManager.REPOSITORY_PATH,
                      { '--depth': 1, '--branch': TemplateRepositoryManager.GIT_REF })
    }
  }

  /**
   * Update the repository if necessary
   */
  async updateRepositoryIfNecessary(): Promise<void> {
    await this.cloneRepository()
    await simpleGit().cwd(TemplateRepositoryManager.REPOSITORY_PATH).pull()
  }

}

export const templateRepositoryManager = new TemplateRepositoryManager()
