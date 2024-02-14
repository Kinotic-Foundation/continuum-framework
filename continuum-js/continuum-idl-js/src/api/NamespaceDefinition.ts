import {ObjectC3Type} from "@/api/ObjectC3Type"
import {ServiceDefinition} from "@/api/ServiceDefinition"

/**
 * Provides functionality to define a namespace with a Continuum schema.
 * A {@link NamespaceDefinition} is a collection of {@link ServiceDefinition}'s and {@link ObjectC3Type}'s defined within a particular namespace.
 * <p>
 * Created by navid on 2023-4-13.
 */
export class NamespaceDefinition {

    /**
     * This is the name of this {@link NamespaceDefinition}
     */
    public name: string = ''

    /**
     * This is all the objects defined for a given namespace
     */
    public objects: Set<ObjectC3Type> = new Set<ObjectC3Type>()

    /**
     * This is all the services defined for a given namespace
     */
    public services: Set<ServiceDefinition> = new Set<ServiceDefinition>()

    public addObject(type: ObjectC3Type): NamespaceDefinition {
        if (this.objects.has(type)) {
            throw new Error(`This NamespaceSchema already contains an ObjectSchema for name ${type.name}`)
        }
        this.objects.add(type)
        return this
    }

    public addServiceSchema(service: ServiceDefinition): NamespaceDefinition {
        if (this.services.has(service)) {
            throw new Error(`This NamespaceSchema already contains an ServiceSchema for name ${service.name}`)
        }
        this.services.add(service)
        return this
    }
}
