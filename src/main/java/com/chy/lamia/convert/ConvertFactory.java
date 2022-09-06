package com.chy.lamia.convert;

import com.chy.lamia.element.LamiaConvertInfo;
import com.chy.lamia.element.resolver.TypeResolver;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;

/**
 * 转换工厂
 *
 * @author bignosecat
 */
public class ConvertFactory {

    public static ConvertFactory INSTANCE = new ConvertFactory();


    /**
     * 开始生成对应的转换代码
     * @param lamiaConvertInfo 表达式信息
     */
    public void make(LamiaConvertInfo lamiaConvertInfo) {
        // 解析要转换成的目标类型
        TypeResolver targetTypeResolver = TypeResolver.getTypeResolver(lamiaConvertInfo.getTargetType());
        // 可能存在包装类型,把包装类型解包 如: Optional<A> ---> A
        TypeDefinition targetTypeDefinition =  TypeDefinitionFactory.unPackage(lamiaConvertInfo.getTargetType());
        


        // Optional / list / map / obj


    }
}
