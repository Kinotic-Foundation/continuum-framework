package org.kinotic.continuum.idl.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents a compute type.
 * Created by Navíd Mitchell 🤪 on 4/14/23.
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ComputeTypeSchema extends TypeSchema{

    private String name;

}
