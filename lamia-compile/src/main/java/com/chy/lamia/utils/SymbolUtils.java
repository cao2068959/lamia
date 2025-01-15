package com.chy.lamia.utils;


import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.List;

import java.util.ArrayList;

public class SymbolUtils {

    /**
     * 解析泛型生成 ParameterType
     *
     * @return
     */
    public static java.util.List<TypeDefinition> getGeneric(Symbol varSymbol) {
        return getGeneric(varSymbol.type);
    }

    public static java.util.List<TypeDefinition> getGeneric(Type type) {
        List<Type> typeArguments = type.getTypeArguments();
        ArrayList<TypeDefinition> result = new ArrayList();
        if (typeArguments == null) {
            return result;
        }
        for (Type typeArgument : typeArguments) {
            TypeDefinition typeDefinition = TypeDefinitionFactory.create(typeArgument);
            result.add(typeDefinition);
        }
        return result;
    }
}
