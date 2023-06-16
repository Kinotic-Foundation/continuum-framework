package org.kinotic.continuum.idl.api.schema;

import lombok.*;
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
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UnionC3Type extends C3Type {

    /**
     * The namespace that this {@link UnionC3Type} belongs to
     */
    private String namespace = null;

    /**
     * This is the name of the {@link UnionC3Type} such as "Animal"
     */
    private String name = null;

    /**
     * This is the field that will be used to determine which type is being used.
     * The discriminator value must be a property that must exist on all types in this union.
     * The {@link ObjectC3Type} property with this name must be a {@link StringC3Type}
     */
    private String discriminator;

    /**
     * The types that are part of this union
     * All types must have a field with the name of the discriminator
     */
    private List<ObjectC3Type> types = new ArrayList<>();

}
