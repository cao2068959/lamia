package com.chy.lamia.expose.rule;

import com.chy.lamia.expose.LamiaBuilder;

import java.util.function.Function;

/**
 * @author bignosecat
 */
public class LamiaRule {


    public LamiaRule filter(LamiaFilter filter) {
        return this;
    }


    @SafeVarargs
    public final <T>LamiaRule ignoreField(Function<T, ?>... field) {
        return this;
    }


    public LamiaBuilder mapping(Object... param) {
        return new LamiaBuilder();
    }

    public LamiaBuilder setField(Object... param) {
        return new LamiaBuilder();
    }

}
