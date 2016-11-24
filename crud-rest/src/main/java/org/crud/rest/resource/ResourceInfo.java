package org.crud.rest.resource;

import lombok.Builder;
import lombok.Getter;
import org.crud.core.data.CrudRepository;

import java.util.Set;

@Builder
@Getter
public class ResourceInfo {
    private String name;
    private CrudRepository repository;
    private EntityListener entityListener;
    private Class entityClass;
    private Class idClass;
    private Class dtoClass;
    private Set<ResourceAction> actions;
}
