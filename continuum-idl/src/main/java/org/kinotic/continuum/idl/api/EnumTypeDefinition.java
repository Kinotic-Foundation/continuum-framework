package org.kinotic.continuum.idl.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Navíd Mitchell 🤪 on 4/13/23.
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class EnumTypeDefinition extends TypeDefinition {

    List<String> values = new ArrayList<>();

    public EnumTypeDefinition addValue(String value){
        values.add(value);
        return this;
    }

}
