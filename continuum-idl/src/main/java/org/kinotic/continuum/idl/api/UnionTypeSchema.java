package org.kinotic.continuum.idl.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a union type in the IDL.
 * Union types are a way to represent a type that can be one of many types.
 * Created by Navíd Mitchell 🤪 on 4/13/23.
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UnionTypeSchema extends TypeSchema {

    /**
     * This is the field that will be used to determine which type is being used.
     * The field must exist on all types in this union.
     */
    private String discriminator;

    /**
     * The types that are part of this union
     * All types must have a field with the name of the discriminator
     */
    private List<ObjectTypeSchema> types = new ArrayList<>();

}
