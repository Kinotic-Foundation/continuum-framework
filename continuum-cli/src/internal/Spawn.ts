import * as fs from 'fs'
import {Dirent} from "fs"
import * as fsP from 'fs/promises'
import * as makeDir from 'make-dir'
import * as path from 'path'
import { Liquid } from 'liquidjs'
import * as oclif from "@oclif/core"
import {SpawnConfig, GlobalsType, PropertySchemaType} from "./SpawnConfig"


export default class Spawn {

  private engine: Liquid

  constructor() {
    this.engine = new Liquid({
                               cache: true
                             })
    this.engine.registerFilter("packageToPath", v => v.replace('.', '/'))
  }

  public async renderFile(source: string, destination: string, context?: object): Promise<void> {
    return this._renderFile(source, destination, true, context)
  }

  /**
   * Renders the specified directory to the specified target.
   * This is done by performing the following
   *
   * - Will recursively walk the source directory copying any files or directories encountered
   * -- If a template file is encountered it will be parsed and rendered prior to copying
   *
   *
   * @param source the directory to parse and render
   * @param destination the target directory where rendered data will be sent
   * @param context the values to be provided to the templates while rendering
   */
  public async renderDirectory(source: string, destination: string, context?: object): Promise<void> {
    if(!fs.existsSync(destination)){
      let sources: string[] = [source]

      // process spawn config if found
      let currentConfig: string = path.resolve(source, 'spawn.json')
      if(fs.existsSync(currentConfig)){
        let spawns: SpawnConfig[] = []
        let currentSpawn: SpawnConfig = JSON.parse(fs.readFileSync(currentConfig,{encoding:'utf8'}))
        spawns.push(currentSpawn)

        // follow inheritance and build stack
        while (currentSpawn.inherits){
          let inheritDir = path.resolve(path.dirname(currentConfig), currentSpawn.inherits)
          currentConfig = path.resolve(inheritDir, 'spawn.json')

          this.logDebug(`Inheriting from ${currentConfig}`)

          if(!fs.existsSync(currentConfig)){
            throw new Error(`Inherited spawn ${currentConfig} does not exist`)
          }
          currentSpawn = JSON.parse(fs.readFileSync(currentConfig,{encoding:'utf8'}))

          spawns.push(currentSpawn)
          sources.push(inheritDir)
        }

        let globals: GlobalsType = {}
        let propertySchemas: PropertySchemaType = {}

        for(let spawn of spawns.reverse()){
          // merge all globals
          if(spawn.globals){
            globals = {...globals, ...spawn.globals}
          }

          // merge all property schemas
          if(spawn.propertySchema){
            propertySchemas = {...propertySchemas, ...spawn.propertySchema}
          }
        }

        // now merge globals with context, context taking precedence
        context = {...globals, ...context}

        // now prompt user for any needed properties not provided in the context
      }

      await makeDir(destination)

      for(let src of sources.reverse()){
        await this._renderDirectory(src, src, destination, context)
      }

    }else{
      throw new Error(`The target directory ${destination} already exists`)
    }
  }


  /**
   * Copies or renders the source to the destination parsing any templates encountered if desired
   *
   * @param source file to copy or parse and render
   * @param destination to save the source output to
   * @param errorIfExists if true will throw an error if the destination file already exists
   * @param context the values to be provided to the templates while rendering
   * @private
   */
  private async _renderFile(source: string, destination: string, errorIfExists: boolean, context?: object): Promise<void> {
    if(destination.includes('{{')){
      destination = await this.engine.parseAndRender(destination, context)
    }

    if(errorIfExists){
      if(fs.existsSync(destination)){
        throw new Error(`The target file ${destination} already exists`)
      }
    }

    let readStream: NodeJS.ReadableStream

    if(destination.endsWith('.liquid')){
      destination = destination.substring(0, destination.length - 7)
      readStream = await this.engine.renderFileToNodeStream(source, context)
    }else{
      readStream = fs.createReadStream(source)
    }

    this.logDebug(`Writing File\n${source}\nto\n${destination}\n`)

    await makeDir(path.dirname(destination))

    let writeStream = fs.createWriteStream(destination)
    readStream.pipe(writeStream)
  }


  private async _renderDirectory(baseFrom: string, from: string, baseTo: string, context?: object): Promise<void> {
    let files: Dirent[] = await fsP.readdir(from, {withFileTypes: true})

    for(let file of files){

      let filePath: string = path.resolve(from, file.name)
      let to: string = filePath.replace(baseFrom, baseTo)

      if(file.isFile()){
        if(!this.shouldIgnore(file.name)) {

          // we do not need to check if file exists since entire directory was checked initially
          await this._renderFile(filePath, to, false, context)

        }else{
          this.logDebug(`Skipping File\n${filePath}\n`)
        }
      }else{
        await this._renderDirectory(baseFrom, filePath, baseTo, context)
      }
    }
  }

  private logDebug(message?: any, ...optionalParams: any[]): void{
    if(oclif.settings.debug) {
      console.debug(message, ...optionalParams)
    }
  }

  private shouldIgnore(fileName: string): boolean{
    let ret: boolean = false
    if(fileName === '.DS_Store'){
      ret = true
    }
    return ret
  }

}

export const spawn = new Spawn()
