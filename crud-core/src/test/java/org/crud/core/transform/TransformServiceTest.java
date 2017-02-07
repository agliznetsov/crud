package org.crud.core.transform;

import org.crud.core.beans.*;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TransformServiceTest {
    TransformService transformService = new TransformServiceImpl();

    @Test
    public void transformList() throws Exception {
        assertEquals(Arrays.asList("1", "2"), transformService.transformList(Arrays.asList(1, 2), String.class));
    }

    @Test
    public void transform_simple_type() throws Exception {
        assertEquals("555", transformService.transform(555, String.class));
        assertEquals("1.5", transformService.transform(1.5, String.class));
        assertEquals("1.5", transformService.transform(1.5f, String.class));
        assertEquals("123", transformService.transform(123L, String.class));
        assertEquals("true", transformService.transform(true, String.class));

        assertEquals(555, transformService.transform("555", Integer.class).intValue());

        assertEquals(true, transformService.transform(Boolean.TRUE, boolean.class));

        assertEquals(1.5d, transformService.transform("1.5", Double.class).doubleValue(), 0);
        assertEquals(1.0d, transformService.transform(1, Double.class).doubleValue(), 0);

        assertEquals(Color2.BLUE, transformService.transform(Color.BLUE, Color2.class));
    }

    @Test
    public void transform_bean() throws Exception {
        transformService.registerBeanPair(Circle.class, CircleDTO.class);

        Circle circle = new Circle();
        circle.color = Color.BLUE;
        circle.radius = 1.0;

        CircleDTO dto = transformService.transform(circle, CircleDTO.class);
        assertEquals("BLUE", dto.color);
        assertEquals("1.0", dto.radius);

        Circle circle2 = transformService.transform(dto, Circle.class);
        assertEquals(circle.color, circle2.color);
        assertEquals(circle.radius, circle2.radius, 0);
    }

    @Test
    public void transform_pair() throws Exception {
        transformService.registerBeanPair(Circle.class, Circle2DTO.class, new CircleTransformer());
        Circle circle = new Circle();
        circle.color = Color.BLUE;
        circle.radius = 1.0;

        Circle2DTO dto = transformService.transform(circle, Circle2DTO.class);
        assertEquals("BLUE", dto.color);
        assertEquals(2.0, dto.diameter, 0);

        Circle circle2 = transformService.transform(dto, Circle.class);
        assertEquals(circle, circle2);
    }

}
