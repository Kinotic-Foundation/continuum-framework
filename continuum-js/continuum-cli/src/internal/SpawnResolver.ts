import fs, {Dirent} from 'node:fs'
import path from 'node:path'
import fsP from 'node:fs/promises'
import makeDir from 'make-dir'
import TemplateRepositoryManager from "./TemplateRepositoryManager.js";

/**
 * Handles resolving "Spawn"'s on the local filesystem
 */
export interface SpawnResolver {

  /**
   * Copies the given "Spawn"s from the repository to the directory .continuum/spawns in the current directory
   * @param spawns to copy
   * @return a Promise that resolves if copying completes properly
   */
  copySpawnsFromRepositoryToDirectory(...spawns: string[]): Promise<void>

  /**
   * Checks out or updates the spawns repository to the filesystem
   */
  syncSpawnsRepository(): Promise<void>

  /**
   * Checks if the spawns directory exists under the current directory inside the path .continuum/spawns
   * @return true if the spawns directory exists false if not
   */
  spawnsDirectoryExists(): boolean

  /**
   * The directory path would be inside the current directory at .continuum/spawns
   * @return the absolute path to a spawns directory under the current directory
   */
  spawnsDirectoryPath(): string

  /**
   * Checks if the spawns repository is checked out to the filesystem
   * @return true if the spawns repository is present on the filesystem
   */
  spawnsRepositoryExists(): boolean

  /**
   * The absolute path to where the spawns repository should be checked out
   */
  spawnsRepositoryPath(): string

  /**
   * Finds the correct absolute path for the spawn provided
   * @param spawn the name of desired spawn. Ex: applicationStandalone
   * @return a {@link Promise} containing the absolute path or an error if the spawn could not be found
   */
  resolveSpawn(spawn: string): Promise<string>

}


class DefaultSpawnResolver implements SpawnResolver{

  private async copyDirectory(fromDir: string, toDir: string, baseDir?: string): Promise<void> {
    if(!baseDir){
      baseDir = fromDir
    }

    let files: Dirent[] = await fsP.readdir(fromDir, {withFileTypes: true})

    for(let file of files){
      let filePath: string = path.resolve(fromDir, file.name)
      let to: string = filePath.replace(baseDir, toDir)

      if(fs.existsSync(to)){
        throw new Error(`${to} Exists!`)
      }

      await makeDir(path.dirname(to))

      if(file.isFile()){
        let readStream: NodeJS.ReadableStream = fs.createReadStream(filePath)
        let writeStream = fs.createWriteStream(to)
        readStream.pipe(writeStream)
      }else{
        await this.copyDirectory(filePath, toDir, baseDir)
      }
    }
  }

  async copySpawnsFromRepositoryToDirectory(...spawns: string[]): Promise<void> {
    let spawnPaths: any[] = []
    let spawnsDirectory = this.spawnsDirectoryPath()
    for(let spawn of spawns){
      let spawnSourcePath: string = path.resolve(this.spawnsRepositoryPath(), spawn)
      if(!fs.existsSync(spawnSourcePath)){
        throw new Error(`Spawn ${spawn} does not exist`)
      }
      let spawnTargetPath: string = path.resolve(spawnsDirectory, spawn)
      if(fs.existsSync(spawnTargetPath)){
        throw new Error(`Spawn target path ${spawnTargetPath} exists`)
      }

      spawnPaths.push({source: spawnSourcePath, target: spawnTargetPath})
    }

    for(let spawnPath of spawnPaths){
      console.log(`Copying\n${spawnPath.source}\nTo\n${spawnPath.target}\n`)
      await this.copyDirectory(spawnPath.source, spawnPath.target)
    }
  }

  syncSpawnsRepository(): Promise<void> {
    return Promise.resolve(undefined);
  }

  spawnsDirectoryExists(): boolean {
    return fs.existsSync(this.spawnsDirectoryPath());
  }

  spawnsDirectoryPath(): string {
    return path.resolve('.continuum', 'spawns')
  }

  spawnsRepositoryExists(): boolean {
    return fs.existsSync(this.spawnsRepositoryPath())
  }

  spawnsRepositoryPath(): string {
    return TemplateRepositoryManager.REPOSITORY_PATH
  }

  async resolveSpawn(spawn: string): Promise<string> {
    // first check if there is a spawn directory in the local directory this will be the case if inside a continuum project
    let spawnDir: string
    if(this.spawnsDirectoryExists()){
      spawnDir = path.resolve(this.spawnsDirectoryPath(), spawn)
    }else if(this.spawnsRepositoryExists()){
      spawnDir = path.resolve(this.spawnsRepositoryPath(), spawn)
    }else{
      throw new Error('No spawn directory or spawn repository was found')
    }

    if(!fs.existsSync(spawnDir)){
      throw new Error(`No spawn could be found with the name ${spawn}`)
    }

    return  spawnDir
  }

}

export const spawnResolver = new DefaultSpawnResolver()
