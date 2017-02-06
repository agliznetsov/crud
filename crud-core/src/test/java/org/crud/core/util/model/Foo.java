package org.crud.core.util.model;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Optional;

class Foo extends ArrayList<Integer> {
    @Resource
    protected Optional<Integer> f2;
}