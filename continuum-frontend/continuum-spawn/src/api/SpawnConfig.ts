import {JSONSchema7} from "json-schema";

export type GlobalsType = {
  [key: string]: any
}

export type PropertySchemaType = {
  [key: string]: JSONSchema7
}

/**
 * spawn.json type definition
 */
export interface SpawnConfig {

  /**
   * This spawn inherits from the given spawn
   */
  inherits?: string

  /**
   * Contains all values that will be provided by default if not overridden by the user
   */
  globals?: GlobalsType

  /**
   * The JSON schemas for all available properties that must be provided by the user
   */
  propertySchema?: PropertySchemaType

}
