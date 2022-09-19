package com.chy.lamia.entity.factory;

import com.chy.lamia.element.boxing.TypeBoxingHandle;
import com.chy.lamia.element.boxing.processor.TypeBoxingDefinition;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.utils.Lists;
import com.chy.lamia.utils.SymbolUtils;
import com.chy.lamia.utils.struct.Pair;
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
        if (unboxing == null) {
            return targetType;
        }

        return unboxing;
    }

    /**
     * 把2个type的类型 匹配到一致, 会不断解包,直到匹配到一致为止
     * 如: type--> Optional<A>  | targetType-->  Optional<Optional<A>>
     * 那么 Pair.left -->  TypeDefinition[Optional<A>] | Pair.right -->  TypeBoxingDefinition[Optional<A>]
     * <p>
     * 或者: type--> Optional<A>  | targetType--> A
     * 那么 Pair.left --> TypeBoxingDefinition[A] | Pair.right -->  TypeDefinition[A]
     * <p>
     * 如果 解包之后也无法匹配到一致, 返回 null
     */
    public static Pair<TypeDefinition, TypeDefinition> unPackageMatch(TypeDefinition type, TypeDefinition targetType) {
        // 类型本身就已经 匹配,直接返回
        if (type.matchType(targetType, true)) {
            return Pair.of(type, targetType);
        }
        List<? extends TypeDefinition> typeBoxChain = getBoxChain(unPackage(type));
        List<? extends TypeDefinition> targetBoxChain = getBoxChain(unPackage(targetType));

        for (TypeDefinition typeDefinition : typeBoxChain) {
            for (TypeDefinition target : targetBoxChain) {
                if (typeDefinition.matchType(target, true)) {
                    return Pair.of(typeDefinition, target);
                }
            }
        }

        // 类型完全不匹配
        return null;
    }

    /**
     * 获取 包装链路,只有 TypeBoxingDefinition 存在包装链路,如果非 TypeBoxingDefinition 类型返回  Lists.of(type)
     */
    private static List<? extends TypeDefinition> getBoxChain(TypeDefinition type) {
        if (type instanceof TypeBoxingDefinition) {
            return ((TypeBoxingDefinition) type).getBoxChain();
        }
        return Lists.of(type);
    }


}
