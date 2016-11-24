package org.crud.rest.resource;

import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class ResourceInfoRegistry implements ResourceInfoSupplier {
    private final Map<String, ResourceInfo> map = new HashMap<>();

    @Override
    public Map<String, ResourceInfo> get() {
        return map;
    }

    public void add(ResourceInfo resourceInfo) {
        Assert.notNull(resourceInfo.getName());
        map.put(resourceInfo.getName(), resourceInfo);
    }
}
