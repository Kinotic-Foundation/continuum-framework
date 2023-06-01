import { C3Type } from '@/api/C3Type';
import { NamespaceDefinition } from '@/api/NamespaceDefinition';
import {ClassDeclaration} from "ts-morph";

/**
 * Provides the ability to create {@link C3Type}'s
 * Created by navid on 2019-06-13.
 */
export interface SchemaFactory {
    /**
     * Creates a {@link C3Type} for the given {@link Class}.
     * This method treats the class as a standard POJO or basic type.
     * If you need to convert a class that is a "service" use {@link SchemaFactory.createForService}.
     * @param clazz the class to create the schema for.
     * @return the newly created {@link C3Type}.
     */
    createForClass(clazz: ClassDeclaration): C3Type;

    /**
     * Creates a {@link NamespaceDefinition} for the given {@link Class}.
     * This method treats the class as a java "service".
     * @param clazz the class to create the schema for.
     * @return the newly created {@link NamespaceDefinition}.
     */
    createForService(clazz: ClassDeclaration): NamespaceDefinition;
}
