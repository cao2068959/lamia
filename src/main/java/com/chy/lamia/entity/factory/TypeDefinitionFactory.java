package com.chy.lamia.entity.factory;

import com.chy.lamia.element.boxing.TypeBoxingHandle;
import com.chy.lamia.element.boxing.processor.TypeBoxingDefinition;
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


    /**
     * 解包, 解到 最基础的 value object 或者 map
     *
     * @param targetType 要解包的包装类型
     * @return 解包后的类如  Optional<A> ----> 返回的是 A
     */
    public static TypeDefinition unPackage(TypeDefinition targetType) {
        TypeBoxingDefinition unboxing = TypeBoxingHandle.instance.unboxing(targetType);
        // 不是包装类型,直接返回
        if (unboxing == null){
            return targetType;
        }

        return unboxing;
    }
}
