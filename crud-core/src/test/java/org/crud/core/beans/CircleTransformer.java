package org.crud.core.beans;

import org.crud.core.transform.PairTransformer;
import org.crud.core.transform.TransformationContext;

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