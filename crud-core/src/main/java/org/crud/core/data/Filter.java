package org.crud.core.data;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CompositeFilter.class, name = "CompositeFilter"),
        @JsonSubTypes.Type(value = PropertyFilter.class, name = "PropertyFilter")
})
public abstract class Filter {
}
