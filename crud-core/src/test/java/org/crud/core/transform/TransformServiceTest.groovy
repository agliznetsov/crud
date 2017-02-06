package org.crud.core.transform

import org.crud.core.beans.*
import spock.lang.Specification

import static java.lang.Boolean.TRUE

/**
 *
 */
class TransformServiceTest extends Specification {
    def transformService = new TransformServiceImpl();

    def "Can transform lists"() {
        expect:
        transformService.transformList([111, 222], String.class) == ["111", "222"]
    }

    def "Can transform simple types"(input, clazz, result) {
        expect:
        transformService.transform(input, clazz) == result

        where:
        input      | clazz         || result
        // to string
        555        | String.class  || "555"
        1.6        | String.class  || "1.6"
        1.6f       | String.class  || "1.6"
        1234L      | String.class  || "1234"
        TRUE       | String.class  || "true"
        // to integer
        "0"        | Integer.class || 0
        "555"      | Integer.class || 555
        // to double
        "56.65"    | Double.class  || 56.65d
        1          | Double.class  || 1.0d
        // primitives
        TRUE       | boolean.class || true
        // Enums
        Color.BLUE | Color2.class  || Color2.BLUE
    }

    def "Can transform beans"() {
        given:
        transformService.registerBeanPair(Circle.class, CircleDTO.class);

        and:
        def circle = new Circle();
        circle.color = Color.BLUE;
        circle.radius = 1.0;

        when:
        def dto = transformService.transform(circle, CircleDTO.class);

        then:
        dto.color == "BLUE"
        dto.radius == "1.0"

        when:
        def circle2 = transformService.transform(dto, Circle.class);

        then:
        circle2 == circle

    }

    def "Can transform by pairs"() {
        given:
        transformService.registerBeanPair(Circle.class, Circle2DTO.class, new CircleTransformer());

        and:
        def circle = new Circle();
        circle.color = Color.BLUE;
        circle.radius = 1.0;

        when:
        def dto = transformService.transform(circle, Circle2DTO.class);

        then:
        dto.color == "BLUE"
        dto.diameter == 2.0

        when:
        def circle2 = transformService.transform(dto, Circle.class);

        then:
        circle2 == circle
    }


}
