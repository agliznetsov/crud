package org.crud.rest.resource;

import java.util.EnumSet;
import java.util.Set;

public enum ResourceAction {
    CREATE, READ, UPDATE, DELETE, PATCH;

    public static final Set<ResourceAction> CRUD = EnumSet.of(CREATE, READ, UPDATE, DELETE, PATCH);
}
