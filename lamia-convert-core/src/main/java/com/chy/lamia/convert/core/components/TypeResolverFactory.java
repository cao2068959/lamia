package com.chy.lamia.convert.core.components;

import com.chy.lamia.convert.core.entity.TypeDefinition;

public interface TypeResolverFactory {
    /**
     * 生成一个类型解析器
     */
    TypeResolver getTypeResolver(TypeDefinition targetType);
}
