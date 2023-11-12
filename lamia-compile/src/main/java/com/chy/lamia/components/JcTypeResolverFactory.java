package com.chy.lamia.components;

import com.chy.lamia.convert.core.components.TypeResolver;
import com.chy.lamia.convert.core.components.TypeResolverFactory;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.element.resolver.type.JcTypeResolver;

public class JcTypeResolverFactory implements TypeResolverFactory {

    @Override
    public TypeResolver getTypeResolver(TypeDefinition targetType) {
       return JcTypeResolver.getTypeResolver(targetType);
    }
}
