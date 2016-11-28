package org.crud.swagger;

import io.swagger.converter.ModelConverterContextImpl;
import io.swagger.jackson.ModelResolver;
import io.swagger.models.*;
import io.swagger.models.parameters.*;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.refs.RefType;
import io.swagger.util.Json;
import org.crud.core.annotations.Filterable;
import org.crud.core.data.CompositeOperator;
import org.crud.core.data.DataQuery;
import org.crud.core.data.EntityProxy;
import org.crud.core.util.ReflectUtils;
import org.crud.rest.mvc.JsonPatchOperation;
import org.crud.rest.resource.ResourceAction;
import org.crud.rest.resource.ResourceInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.crud.rest.mvc.QueryStringParser.*;

public class SwaggerBuilder {
    public static final String X_URI_TEMPLATE = "x-uri-template";

    Swagger swagger;
    String resourcesPath = "/resources";
    Tag tag;
    Path path;
    Parameter parameter;
    Operation operation;
    ModelConverterContextImpl modelConverterContext;

    public SwaggerBuilder() {
        modelConverterContext = new ModelConverterContextImpl(new ModelResolver(Json.mapper()));
        swagger = new Swagger();
        swagger.setBasePath(resourcesPath);
        swagger.setInfo(new Info());
        swagger.getInfo().setTitle("Resources");
    }

    public void setResourcesPath(String resourcesPath) {
        this.resourcesPath = resourcesPath;
    }

    public void setModelConverterContext(ModelConverterContextImpl modelConverterContext) {
        this.modelConverterContext = modelConverterContext;
    }

    public Swagger build() {
        swagger.setDefinitions(modelConverterContext.getDefinedModels());
        if (swagger.getPaths() != null) {
            for (Map.Entry<String, Path> e : swagger.getPaths().entrySet()) {
                for (Operation op : e.getValue().getOperations()) {
                    if (op.getVendorExtensions().get(X_URI_TEMPLATE) == null) {
                        String template = "";
                        List<String> names = op.getParameters().stream().filter(it -> it instanceof QueryParameter).map(it -> it.getName()).collect(Collectors.toList());
                        if (names.size() > 0) {
                            template = "{?" + String.join(",", names) + "}";
                        }
                        op.getVendorExtensions().put(X_URI_TEMPLATE, resourcesPath + e.getKey() + template);
                    }
                }
            }
        }
        return swagger;
    }

    public SwaggerBuilder tag(String name) {
        return tag(name, null);
    }

    public SwaggerBuilder tag(String name, String description) {
        tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);
        swagger.tag(tag);
        return this;
    }

    public SwaggerBuilder path(String key) {
        path = new Path();
        swagger.path(key, path);
        return this;
    }

    public SwaggerBuilder GET(String id) {
        operation("get", id);
        return this;
    }

    public SwaggerBuilder POST(String id) {
        operation("post", id);
        return this;
    }

    public SwaggerBuilder PUT(String id) {
        operation("put", id);
        return this;
    }

    public SwaggerBuilder PATCH(String id) {
        operation("patch", id);
        return this;
    }

    public SwaggerBuilder DELETE(String id) {
        operation("delete", id);
        return this;
    }

    public SwaggerBuilder operation(String method, String id) {
        return operation(method, id, null);
    }

    public SwaggerBuilder operation(String method, String id, String description) {
        operation = new Operation();
        if (tag != null) {
            operation.tag(tag.getName());
        }
        operation.setOperationId(id);
        operation.setSummary(id);
        operation.setDescription(description);
        path.set(method.toLowerCase(), operation);
        return this;
    }

    public SwaggerBuilder pathParam(String name) {
        addParameter(name, new PathParameter());
        return this;
    }

    public SwaggerBuilder queryParam(String name) {
        addParameter(name, new QueryParameter());
        return this;
    }

    public SwaggerBuilder body(Class clazz) {
        ModelImpl model = (ModelImpl) modelConverterContext.resolve(clazz);
        BodyParameter parameter = new BodyParameter();
        parameter.setName("body");
        parameter.setRequired(true);
        parameter.schema(new RefModel(RefType.DEFINITION.getInternalPrefix() + model.getName()));
        operation.parameter(parameter);
        this.parameter = parameter;
        return this;
    }

    public SwaggerBuilder bodyList(Class clazz) {
        ModelImpl model = (ModelImpl) modelConverterContext.resolve(clazz);
        BodyParameter parameter = new BodyParameter();
        parameter.setName("body");
        parameter.setRequired(true);
        ArrayModel array = new ArrayModel();
        array.setItems(new RefProperty(RefType.DEFINITION.getInternalPrefix() + model.getName()));
        parameter.schema(array);
        operation.parameter(parameter);
        this.parameter = parameter;
        return this;
    }

    public SwaggerBuilder type(Class clazz) {
        Property property = modelConverterContext.resolveProperty(clazz, null);
        SerializableParameter sparameter = (SerializableParameter) parameter;
        sparameter.setType(property.getType());
        sparameter.setFormat(property.getFormat());
        return this;
    }

    public SwaggerBuilder response(Class clazz) {
        return response(200, clazz);
    }

    public SwaggerBuilder response(int code, Class clazz) {
        Property property = modelConverterContext.resolveProperty(clazz, null);
        Response response = new Response();
        response.schema(property);
        operation.response(code, response);
        return this;
    }

    public SwaggerBuilder responseList(Class clazz) {
        Property property = modelConverterContext.resolveProperty(clazz, null);
        Response response = new Response();
        ArrayProperty array = new ArrayProperty();
        array.setItems(property);
        response.schema(array);
        operation.response(200, response);
        return this;
    }

    private void addParameter(String name, AbstractSerializableParameter parameter) {
        parameter.setName(name);
        parameter.setType("string");
        this.parameter = parameter;
        operation.parameter(parameter);
    }

    public SwaggerBuilder enumValues(Object... values) {
        ((SerializableParameter) parameter).setEnum(Arrays.stream(values).map(Object::toString).collect(Collectors.toList()));
        return this;
    }

    private SwaggerBuilder listParameters(Class dtoClass) {
        queryParam(SKIP).type(Integer.class);
        queryParam(MAX).type(Integer.class);
        queryParam(SORT).type(String.class);
        queryParam(OP).type(String.class).enumValues(CompositeOperator.values());
        ReflectUtils.getInstanceFields(dtoClass).values().forEach(f -> {
            Filterable filterable = f.getAnnotation(Filterable.class);
            if (filterable != null) {
                String name = filterable.name().isEmpty() ? f.getName() : filterable.name();
                Class type = filterable.type().equals(Void.class) ? f.getType() : filterable.type();
                if (type.equals(EntityProxy.class)) {
                    type = (Class) ReflectUtils.findGenericTypes(f)[0];
                    if (filterable.name().isEmpty()) {
                        name = f.getName() + ".id";
                    }
                }
                queryParam(name).type(type);
            }
        });
        return this;
    }

    public void crudResource(ResourceInfo resource) {
        String name = resource.getName();
        Class dtoClass = resource.getDtoClass() != null ? resource.getDtoClass() : resource.getEntityClass();
        tag(name);

        path(join("", name, "{id}"));

        if (resource.getActions().contains(ResourceAction.READ)) {
            GET("Get" + name).pathParam("id").type(resource.getIdClass()).response(dtoClass);
        }
        if (resource.getActions().contains(ResourceAction.UPDATE)) {
            PUT("Update" + name).pathParam("id").type(resource.getIdClass()).body(dtoClass);
        }
        if (resource.getActions().contains(ResourceAction.PATCH)) {
            PATCH("Patch" + name).pathParam("id").type(resource.getIdClass()).bodyList(JsonPatchOperation.class);
        }
        if (resource.getActions().contains(ResourceAction.DELETE)) {
            DELETE("Delete" + name).pathParam("id").type(resource.getIdClass());
        }

        path(join("", name));

        if (resource.getActions().contains(ResourceAction.READ)) {
            GET("List" + name).listParameters(dtoClass).responseList(dtoClass);
        }
        if (resource.getActions().contains(ResourceAction.CREATE)) {
            POST("Create" + name).body(dtoClass).response(201, String.class);
        }

        if (resource.getActions().contains(ResourceAction.READ)) {
            path(join("", name, "search")).POST("Search" + name).body(DataQuery.class).responseList(dtoClass);
        }
    }

    private String join(CharSequence... parts) {
        return String.join("/", parts);
    }

}
