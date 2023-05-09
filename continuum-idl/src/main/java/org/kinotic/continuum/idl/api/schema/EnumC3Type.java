package org.kinotic.continuum.idl.api.schema;

import lombok.*;
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
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EnumC3Type extends C3Type {

    List<String> values = new ArrayList<>();

    public EnumC3Type addValue(String value){
        values.add(value);
        return this;
    }

}
