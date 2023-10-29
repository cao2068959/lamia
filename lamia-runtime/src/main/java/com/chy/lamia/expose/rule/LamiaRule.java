package com.chy.lamia.expose.rule;

import com.chy.lamia.expose.LamiaBuilder;

/**
 * @author bignosecat
 */
public class LamiaRule {


    public LamiaRule filter(LamiaFilter filter) {
        return this;
    }


    public LamiaBuilder mapping(Object... param) {
        return new LamiaBuilder();
    }

    public LamiaBuilder setField(Object... param) {
        return new LamiaBuilder();
    }

}
