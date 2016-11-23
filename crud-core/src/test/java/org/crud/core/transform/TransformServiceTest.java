package org.crud.core.transform;

import lombok.EqualsAndHashCode;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class TransformServiceTest {
    TransformService transformService;

    @Before
    public void setUp() throws Exception {
        transformService = new TransformServiceImpl();
    }

    @Test
    public void test_transform_list() throws Exception {
        List<String> res = transformService.transformList(Arrays.asList(111, 222), String.class);
        assertEquals(2, res.size());
        assertEquals("111", res.get(0));
        assertEquals("222", res.get(1));
    }

    @Test
    public void test_to_string() throws Exception {
        assertEquals("555", transformService.transform(555, String.class));
    }

    @Test
    public void test_string_to_int() throws Exception {
        assertEquals(new Integer(555), transformService.transform("555", Integer.class));
    }

    @Test
    public void test_number() throws Exception {
        assertEquals(1.0, transformService.transform(1, Double.class));
    }

    @Test
    public void test_primitive() throws Exception {
        Object res = transformService.transform(Boolean.TRUE, boolean.class);
        assertEquals(true, res);
    }

    @Test
    public void test_bean() throws Exception {
        transformService.registerBeanPair(Circle.class, CircleDTO.class);
        Circle circle = new Circle();
        circle.color = Color.BLUE;
        circle.radius = 1.0;

        CircleDTO dto = transformService.transform(circle, CircleDTO.class);
        assertEquals("BLUE", dto.color);
        assertEquals("1.0", dto.radius);

        Circle circle2 = transformService.transform(dto, Circle.class);
        assertEquals(circle, circle2);
    }

    @Test
    public void test_pair() throws Exception {
        transformService.registerBeanPair(Circle.class, Circle2DTO.class, new CircleTransformer());
        Circle circle = new Circle();
        circle.color = Color.BLUE;
        circle.radius = 1.0;

        Circle2DTO dto = transformService.transform(circle, Circle2DTO.class);
        assertEquals("BLUE", dto.color);
        assertEquals(2.0, dto.diameter);

        Circle circle2 = transformService.transform(dto, Circle.class);
        assertEquals(circle, circle2);
    }

    public enum Color {
        RED, BLUE
    }

    public static class Figure {
        public Color color;
    }

    @EqualsAndHashCode
    public static class Circle extends Figure {
        public double radius;
    }

    public static class CircleDTO {
        public String color;
        public String radius;
    }

    public static class Circle2DTO {
        public String color;
        public double diameter;
    }

    public class CircleTransformer implements PairTransformer<Circle, Circle2DTO> {

        @Override
        public Circle2DTO forward(Circle source, Circle2DTO target, TransformationContext context) {
            target.diameter = source.radius * 2;
            return target;
        }

        @Override
        public Circle backward(Circle2DTO source, Circle target, TransformationContext context) {
            target.radius = source.diameter / 2;
            return target;
        }
    }
}
