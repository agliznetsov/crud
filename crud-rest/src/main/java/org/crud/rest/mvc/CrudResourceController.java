package org.crud.rest.mvc;

import lombok.extern.slf4j.Slf4j;
import org.crud.core.data.DataQuery;
import org.crud.core.data.Identifiable;
import org.crud.core.transform.TransformService;
import org.crud.core.util.MapUtils;
import org.crud.core.util.ReflectUtils;
import org.crud.rest.resource.EntityListener;
import org.crud.rest.resource.ResourceAction;
import org.crud.rest.resource.ResourceInfo;
import org.crud.rest.resource.ResourceInfoSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController
@RequestMapping(value = "${org.crud.resources.path:/resources}")
public class CrudResourceController {

    @Autowired
    protected ResourceInfoSupplier resourceInfoSupplier;
    @Autowired
    protected TransformService transformService;

    protected void checkResourceAction(ResourceInfo resourceInfo, ResourceAction action) {
        if (!resourceInfo.getActions().contains(action)) {
            throw new IllegalArgumentException("Action is not supported: " + action);
        }
    }

    @RequestMapping(value = "", method = {GET})
    public Collection<Map> listResources() {
        return resourceInfoSupplier.get().values().stream()
                .map(it -> MapUtils.map("name", it.getName(), "actions", it.getActions(), "uri", getResourceURI(it.getName())))
                .collect(Collectors.toList());
    }

    private String getResourceURI(String name) {
        return MvcUriComponentsBuilder.fromMethodName(getClass(), "list", name, null).buildAndExpand().encode().toUriString();
    }

    @RequestMapping(value = "{resourceName}", method = {GET})
    public ResponseEntity list(@PathVariable("resourceName") String resourceName, @RequestParam MultiValueMap<String, String> params) {
        ResourceInfo resourceInfo = getResource(resourceName);
        DataQuery query = QueryStringParser.parse(params);
        checkResourceAction(resourceInfo, ResourceAction.READ);
        return query(resourceInfo, query);
    }

    @RequestMapping(value = "/{resourceName}/search", method = {POST})
    public ResponseEntity search(@PathVariable("resourceName") String resourceName, @RequestBody DataQuery query) {
        ResourceInfo resourceInfo = getResource(resourceName);
        checkResourceAction(resourceInfo, ResourceAction.READ);
        return query(resourceInfo, query);
    }

    @RequestMapping(value = "/{resourceName}/{id}", method = {GET})
    public Object getById(@PathVariable("resourceName") String resourceName, @PathVariable("id") String id) {
        ResourceInfo resourceInfo = getResource(resourceName);
        Serializable entityId = (Serializable) transformService.transform(id, resourceInfo.getIdClass());
        Object entity = resourceInfo.getRepository().getOne(entityId);
        if (resourceInfo.getDtoClass() != null) {
            entity = transformService.transform(entity, resourceInfo.getDtoClass());
        }
        return entity;
    }

    @RequestMapping(value = "/{resourceName}", method = POST)
    public ResponseEntity create(@PathVariable("resourceName") String resourceName, @RequestBody byte[] body) {
        ResourceInfo resourceInfo = getResource(resourceName);
        checkResourceAction(resourceInfo, ResourceAction.CREATE);

        Object entity = parseEntity(body, resourceInfo);
        entity = saveEntity(resourceInfo, entity);

        if (entity instanceof Identifiable) {
            Object id = ((Identifiable) entity).getId();
            String uri = getResourceURI(resourceName) + "/" + id;
            return ResponseFactory.created(id, uri);
        } else {
            return ResponseFactory.created();
        }
    }

    @RequestMapping(value = "/{resourceName}/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("resourceName") String resourceName, @PathVariable("id") String id) {
        ResourceInfo resourceInfo = getResource(resourceName);
        checkResourceAction(resourceInfo, ResourceAction.DELETE);
        Serializable entityId = (Serializable) transformService.transform(id, resourceInfo.getIdClass());
        resourceInfo.getRepository().deleteOne(entityId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{resourceName}/{id}", method = PUT)
    public void update(@PathVariable("resourceName") String resourceName, @PathVariable("id") String id, @RequestBody byte[] body) {
        ResourceInfo resourceInfo = getResource(resourceName);
        checkResourceAction(resourceInfo, ResourceAction.UPDATE);
        Object entity = parseEntity(body, resourceInfo);
        if (entity instanceof Identifiable) {
            Object entityId = transformService.transform(id, resourceInfo.getIdClass());
            ((Identifiable) entity).setId(entityId);
        }
        saveEntity(resourceInfo, entity);
    }

    @RequestMapping(value = "/{resourceName}/{id}", method = PATCH)
    public void patch(@PathVariable("resourceName") String resourceName, @PathVariable("id") String id, @RequestBody JsonPatchOperation[] operations) {
        ResourceInfo resourceInfo = getResource(resourceName);
        checkResourceAction(resourceInfo, ResourceAction.PATCH);
        Serializable entityId = (Serializable) transformService.transform(id, resourceInfo.getIdClass());
        Object entity = resourceInfo.getRepository().getOne(entityId);
        patchEntity(entity, operations);
        saveEntity(resourceInfo, entity);
    }


    protected ResourceInfo getResource(String resourceName) {
        ResourceInfo resourceInfo = resourceInfoSupplier.get().get(resourceName);
        Assert.notNull(resourceInfo, "Resource not found: " + resourceName);
        return resourceInfo;
    }

    protected ResponseEntity query(ResourceInfo resourceInfo, DataQuery query) {
        List<?> items = resourceInfo.getRepository().find(query);
        Long count = null;
        if (query.isCount()) {
            count = resourceInfo.getRepository().count(query);
        }
        if (resourceInfo.getDtoClass() != null) {
            items = transformService.transformList(items, resourceInfo.getDtoClass());
        }
        return ResponseFactory.list(items, count);
    }

    protected Object parseEntity(@RequestBody byte[] body, ResourceInfo resourceInfo) {
        Object entity;
        if (resourceInfo.getDtoClass() != null) {
            Object dto = transformService.transform(body, resourceInfo.getDtoClass());
            entity = transformService.transform(dto, resourceInfo.getEntityClass());
        } else {
            entity = transformService.transform(body, resourceInfo.getEntityClass());
        }
        return entity;
    }

    protected Object saveEntity(ResourceInfo resourceInfo, Object entity) {
        EntityListener entityListener = resourceInfo.getEntityListener();
        if (entityListener != null) {
            entity = entityListener.beforeSave(entity);
        }
        entity = resourceInfo.getRepository().save(entity);
        if (entityListener != null) {
            entityListener.afterSave(entity);
        }
        return entity;
    }

    protected void patchEntity(Object entity, JsonPatchOperation[] operations) {
        for (JsonPatchOperation oper : operations) {
            switch (oper.op) {
                case add:
                case replace:
                    ReflectUtils.setField(entity, oper.path, oper.value);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported JSON PATCH operation: " + oper.op);
            }
        }
    }

}


