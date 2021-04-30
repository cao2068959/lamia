package com.chy.lamia.element.type;


import com.chy.lamia.element.type.processor.ITypeProcessor;
import com.chy.lamia.element.type.processor.OptionalProcessor;
import com.chy.lamia.entity.ClassType;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.TypeProcessorResult;
import com.sun.tools.javac.tree.JCTree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TypeProcessorFactory {

    public static TypeProcessorFactory instance = new TypeProcessorFactory();

    Map<String, ITypeProcessor> processorMap = new HashMap<>();

    public TypeProcessorFactory() {
        ITypeProcessor[] typeProcessors = new ITypeProcessor[]{new OptionalProcessor()};
        Arrays.stream(typeProcessors).forEach(typeProcessor -> {
            String[] indexs = typeProcessor.indexs();
            if (indexs == null) {
                return;
            }
            Arrays.stream(indexs).forEach(index -> {
                processorMap.put(index, typeProcessor);
            });
        });

    }

    /**
     * 对应的类型拆包，这里并不是基本数据类型那种拆包，比如 Optioanl<String> -> String
     * 并且返回修改后的拆包表达式，比如 Optioanl<String> myop ; 传入的表达式 应该是 myop， 那么解包后的表达式 应该是 myop.get()
     *
     * @param parameterType
     */
    public TypeProcessorResult unpack(ParameterType parameterType) {
        String typePath = parameterType.getType().getTypePath();
        ITypeProcessor typeProcessor = processorMap.get(typePath);
        if (typeProcessor == null) {
            return null;
        }

        ParameterType newParameterType = typeProcessor.unboxingType(parameterType);
        ExpressionFunction autoboxingExpression = typeProcessor::autoboxingExpression;
        ExpressionFunction unboxingExpression = typeProcessor::unboxingExpression;
        return new TypeProcessorResult(parameterType, newParameterType, unboxingExpression, autoboxingExpression);
    }


}
