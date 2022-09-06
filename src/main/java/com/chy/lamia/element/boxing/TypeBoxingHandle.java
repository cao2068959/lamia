package com.chy.lamia.element.boxing;


import com.chy.lamia.element.boxing.processor.ITypeBoxingProcessor;
import com.chy.lamia.element.boxing.processor.OptionalBoxingProcessor;
import com.chy.lamia.element.boxing.processor.TypeBoxingDefinition;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.TypeProcessorResult;

import java.util.HashMap;
import java.util.Map;

/**
 * 类型 拆箱/装箱 处理器
 *
 * @author bignosecat
 */
public class TypeBoxingHandle {

    public static TypeBoxingHandle instance = new TypeBoxingHandle();

    Map<String, ITypeBoxingProcessor> processorMap = new HashMap<>();

    public TypeBoxingHandle() {
        addTypeBoxingProcessor(new OptionalBoxingProcessor());
    }

    public void addTypeBoxingProcessor(ITypeBoxingProcessor typeProcessor) {
        processorMap.put(typeProcessor.handleClassName(), typeProcessor);
    }

    /**
     * 对应的类型拆包，这里并不是基本数据类型那种拆包，比如 Optioanl<String> -> String
     * 并且返回修改后的拆包表达式，比如 Optioanl<String> myop ; 传入的表达式 应该是 myop， 那么解包后的表达式 应该是 myop.get()
     *
     * @param typeDefinition
     */
    public TypeProcessorResult unboxing(TypeDefinition typeDefinition) {



        ExpressionFunction autoboxingExpression = typeProcessor::autoboxingExpression;
        ExpressionFunction unboxingExpression = typeProcessor::unboxingExpression;
        return new TypeProcessorResult(parameterType, newParameterType, unboxingExpression, autoboxingExpression);
    }

    private TypeBoxingDefinition doUnboxing(TypeDefinition typeDefinition) {
        String typePath = typeDefinition.getClassPath();
        ITypeBoxingProcessor typeProcessor = processorMap.get(typePath);
        if (typeProcessor == null) {
            return null;
        }
        TypeBoxingDefinition parent = new TypeBoxingDefinition(typeDefinition);

        TypeDefinition children = typeProcessor.unboxingType(typeDefinition);





    }


}
