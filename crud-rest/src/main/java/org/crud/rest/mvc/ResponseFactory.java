package org.crud.rest.mvc;

import org.crud.core.util.MapUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_RANGE;
import static org.springframework.http.HttpHeaders.LOCATION;


public class ResponseFactory {
    public static ResponseEntity created() {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public static ResponseEntity created(Object id, String uri) {
        HttpHeaders headers = new HttpHeaders();
        if (uri != null) {
            headers.put(LOCATION, Collections.singletonList(uri));
        }
        Map body = MapUtils.map("id", id);
        return new ResponseEntity<>(body, headers, HttpStatus.CREATED);
    }

    public static ResponseEntity list(List list, Long count) {
        HttpHeaders headers = new HttpHeaders();
        if (count != null) {
            headers.put(CONTENT_RANGE, Collections.singletonList("count " + count));
        }
        return new ResponseEntity<>(list, headers, HttpStatus.CREATED);
    }
}
