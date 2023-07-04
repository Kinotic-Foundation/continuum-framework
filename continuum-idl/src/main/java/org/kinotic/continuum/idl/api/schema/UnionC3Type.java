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
     * The types that are part of this union
     */
    private List<ObjectC3Type> types = new ArrayList<>();

}
