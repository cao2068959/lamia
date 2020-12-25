package com.chy.lamia.utils;


import com.chy.lamia.entity.ParameterType;
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
    public static java.util.List<ParameterType> getGeneric(Symbol varSymbol) {
        return getGeneric(varSymbol.type);
    }

    public static java.util.List<ParameterType> getGeneric(Type type) {
        List<Type> typeArguments = type.getTypeArguments();
        ArrayList<ParameterType> result = new ArrayList();
        if (typeArguments == null) {
            return result;
        }
        for (Type typeArgument : typeArguments) {
            ParameterType parameterType = new ParameterType("", typeArgument.toString());
            result.add(parameterType);
            parameterType.setGeneric(getGeneric(typeArgument));
        }
        return result;
    }


}
