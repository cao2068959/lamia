package com.chy.lamia.utils;


import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.TypeDefinition;
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
            TypeDefinition typeDefinition = new TypeDefinition(typeArgument.toString());
            result.add(typeDefinition);
            // 这个泛型类型本身可能还继续有泛型
            typeDefinition.setGeneric(getGeneric(typeArgument));
        }
        return result;
    }
}
