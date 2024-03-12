package com.chy.lamia.convert.core.entity;


import lombok.Setter;

import java.util.function.Supplier;

/**
 * VarDefinition 的子类，类型将会懒加载
 */

public class LazyTypeVarDefinition extends VarDefinition {

    @Setter
    Supplier<TypeDefinition> typeSupplier;

    public LazyTypeVarDefinition(String name) {
        super(name, null);
    }

    @Override
    public TypeDefinition getType() {
        if (super.getType() == null) {
            setType(typeSupplier.get());
        }
        return type;
    }
}
