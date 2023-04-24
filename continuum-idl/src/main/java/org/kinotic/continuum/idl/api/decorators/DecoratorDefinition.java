package org.kinotic.continuum.idl.api.decorators;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Decorators provide a way to add both annotations and a meta-programming syntax for class declarations and members.
 * The {@link DecoratorDefinition} provides a way to define the available decorators, as well as the data needed for each.
 * Created by Navíd Mitchell 🤪 on 4/23/23.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NotNullDecoratorDefinition.class, name = "NotNull"),
})
@JsonInclude(JsonInclude.Include.NON_EMPTY) // do not include any empty or null fields
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class DecoratorDefinition {

    private List<DecoratorTarget> targets;

}
