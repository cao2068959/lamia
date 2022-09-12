package com.chy.lamia.convert;

import com.chy.lamia.convert.assemble.AssembleHandler;
import com.chy.lamia.convert.assemble.MapAssembleHandler;
import com.chy.lamia.convert.assemble.ValueObjAssembleHandler;
import com.chy.lamia.element.LamiaConvertInfo;
import com.chy.lamia.element.resolver.TypeResolver;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;

import java.util.Map;

/**
 * 转换工厂
 *
 * @author bignosecat
 */
public class ConvertFactory {

    public static ConvertFactory INSTANCE = new ConvertFactory();


    /**
     * 开始生成对应的转换代码
     *
     * @param lamiaConvertInfo 表达式信息
     */
    public void make(LamiaConvertInfo lamiaConvertInfo) {

        // 可能存在包装类型,把包装类型解包 如: Optional<A> ---> A
        TypeDefinition targetType = TypeDefinitionFactory.unPackage(lamiaConvertInfo.getTargetType());





        // Optional / list / map / obj


    }


    /**
     * 获取一个合适的组装处理器
     *
     * @param targetType 要组装的对象
     * @return 组装器
     */
    private AssembleHandler getAssembleHandler(TypeDefinition targetType) {
        // 如果要组装的是 map, 则用map的组装器
        if (targetType.matchType(Map.class)){
            return new MapAssembleHandler();
        }
        return new ValueObjAssembleHandler(targetType);

    }
}
