package com.chy.lamia.element.boxing;


import com.chy.lamia.element.boxing.processor.ITypeBoxingProcessor;
import com.chy.lamia.element.boxing.processor.OptionalBoxingProcessor;
import com.chy.lamia.element.boxing.processor.TypeBoxingDefinition;
import com.chy.lamia.entity.TypeDefinition;

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
     * 拆到不能拆位置,并且返回最终的元素
     *
     * @param typeDefinition
     */
    public TypeBoxingDefinition unboxing(TypeDefinition typeDefinition) {
        TypeBoxingDefinition boxingDefinition = new TypeBoxingDefinition(typeDefinition);
        TypeBoxingDefinition result = null;
        while (true) {
            // 解包,直至解到最后一层
            TypeBoxingDefinition typeBoxingDefinition = doUnboxing(boxingDefinition);
            if (typeBoxingDefinition == null) {
                break;
            }
            boxingDefinition = typeBoxingDefinition;
            result = typeBoxingDefinition;
        }
        return result;
    }

    private TypeBoxingDefinition doUnboxing(TypeDefinition typeDefinition) {
        String typePath = typeDefinition.getClassPath();
        ITypeBoxingProcessor typeProcessor = processorMap.get(typePath);
        if (typeProcessor == null) {
            return null;
        }
        // 给父类解包,如果存在的话
        return typeProcessor.unboxing(typeDefinition);
    }


    public TypeBoxingDefinition toTypeBoxingDefinition(TypeDefinition typeDefinition) {
        if (typeDefinition instanceof TypeBoxingDefinition) {
            return (TypeBoxingDefinition) typeDefinition;
        }
        return new TypeBoxingDefinition(typeDefinition);
    }

}
