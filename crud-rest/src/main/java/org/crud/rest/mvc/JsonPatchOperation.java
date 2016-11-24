package org.crud.rest.mvc;

import static org.crud.rest.mvc.JsonPatchOperation.OperationType.*;

public class JsonPatchOperation {
    public enum OperationType {add, remove, replace, copy, move, test}

    public OperationType op;

    public String path;

    public Object value;

    public String from;

    public JsonPatchOperation() {
    }

    public JsonPatchOperation(OperationType op, String path, Object value, String from) {
        this.op = op;
        this.path = path;
        this.value = value;
        this.from = from;
    }

    public static JsonPatchOperation add(String path, Object value) {
        return new JsonPatchOperation(add, path, value, null);
    }

    public static JsonPatchOperation remove(String path) {
        return new JsonPatchOperation(remove, path, null, null);
    }

    public static JsonPatchOperation replace(String path, Object value) {
        return new JsonPatchOperation(replace, path, value, null);
    }

    public static JsonPatchOperation copy(String path, String from) {
        return new JsonPatchOperation(copy, path, null, from);
    }

    public static JsonPatchOperation move(String path, String from) {
        return new JsonPatchOperation(move, path, null, from);
    }

    public static JsonPatchOperation test(String path, Object value) {
        return new JsonPatchOperation(test, path, value, null);
    }
}
