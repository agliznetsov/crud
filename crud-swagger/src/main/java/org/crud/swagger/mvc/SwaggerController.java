package org.crud.swagger.mvc;

import io.swagger.models.Swagger;
import lombok.SneakyThrows;
import org.crud.core.util.MapUtils;
import org.crud.swagger.SwaggerSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "${org.crud.swagger.path:/swagger}")
public class SwaggerController {
    @Autowired
    List<SwaggerSupplier> swaggerSuppliers;
    @Value("${org.crud.swagger.path:/swagger}")
    String swaggerRoot;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Swagger swagger(@RequestParam(name = "title", required = false) String title) {
        return swaggerSuppliers.stream()
                .map(Supplier::get)
                .filter(it -> title == null || title.equals(it.getInfo().getTitle()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Title not found: " + title));
    }

    @RequestMapping(value = "/titles", method = RequestMethod.GET)
    public List apiTitles() {
        return swaggerSuppliers.stream()
                .map(it -> apiTitle(it.get()))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private Map apiTitle(Swagger swagger) {
        String title = swagger.getInfo().getTitle();
        String url = swaggerRoot + "?title=" + URLEncoder.encode(title, StandardCharsets.UTF_8.name());
        return MapUtils.map("title", title, "url", url);
    }
}
