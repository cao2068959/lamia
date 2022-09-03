package com.chy.lamia.entity.factory;

import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.utils.SymbolUtils;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;

import java.util.List;

public class TypeDefinitionFactory {


    /**
     * 把一个 变量表达式转成对应的 TypeDefinition
     *
     * @param varSymbol
     * @return
     */
    public static TypeDefinition create(Symbol.VarSymbol varSymbol) {
        // 类型的全路径
        String typeClassPath = varSymbol.type.tsym.toString();
        TypeDefinition result = new TypeDefinition(typeClassPath);
        //这个参数可能会有泛型
        List<TypeDefinition> generic = SymbolUtils.getGeneric(varSymbol);
        // 设置泛型
        result.setGeneric(generic);
        return result;
    }

    public static TypeDefinition create(Type type) {
        String typeClassPath = type.tsym.toString();
        TypeDefinition result = new TypeDefinition(typeClassPath);
        List<TypeDefinition> generic = SymbolUtils.getGeneric(type);
        result.setGeneric(generic);
        return result;
    }
}
