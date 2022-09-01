package com.chy.lamia.entity.factory;

import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.element.annotation.AnnotationProxyFactory;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.utils.SymbolUtils;
import com.sun.tools.javac.code.Symbol;

import java.util.List;
import java.util.Optional;

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
}
