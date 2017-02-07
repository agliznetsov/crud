package org.crud.core.beans;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Optional;

public class Foo extends ArrayList<Integer> {
    @Resource
    protected Optional<Integer> f2;
}