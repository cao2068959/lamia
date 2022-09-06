package com.chy.lamia.element.boxing.processor;

import com.chy.lamia.element.boxing.ExpressionFunction;
import com.chy.lamia.entity.TypeDefinition;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 具有自动装包和拆包能力的 TypeDefinition
 *
 * @author bignosecat
 */
public class TypeBoxingDefinition extends TypeDefinition {

    /**
     * 包装链路 如: List<Optional<A>> 那么链路就是 List --> Optional --> A
     */
    List<TypeBoxingDefinition> boxChain = new ArrayList<>();

    /**
     * 自身在 boxChain 中的位置
     */
    int index = -1;

    /**
     * 装箱函数, 执行后可以获得 boxChain[index-1] 位置类型的变量
     */
    @Setter
    ExpressionFunction boxingExpression;

    /**
     * 拆箱函数, 执行后可以获得 boxChain[index+1] 位置类型的变量
     */
    @Setter
    ExpressionFunction unboxingExpression;


    public TypeBoxingDefinition(TypeDefinition typeDefinition) {
        super(typeDefinition);
    }

    /**
     * 添加一个 解包的子类型
     *
     * @param definition
     */
    public void addChildrenBoxType(TypeBoxingDefinition definition) {
        if (definition.boxChain.size() != 0) {
            // 解包链路中已经有数据,并且不是最外层那么 无法将其挂载到子解包类型上面
            if (definition.index != 0) {
                throw new RuntimeException("解包类型 [" + definition + "] 无法挂载到 类型 [" + this + "] 中");
            }
        }
        // 如果自身还没挂载过的初始化一遍
        init();
        // 先获取要挂载子类型以前的 解包链路, 因为后续就会替换这个链路
        List<TypeBoxingDefinition> childrenBoxChain = definition.boxChain;
        // 把要挂载的子类型给挂载上去,同时刷新他的 boxChain 以及 index
        addChain(definition);
        // 如果 子类型 中已经存有值,那么把他原本 boxChain 中的值都刷一遍
        for (int i = 0; i < definition.boxChain.size(); i++) {
            TypeBoxingDefinition typeBoxingDefinition = childrenBoxChain.get(i);
            addChain(typeBoxingDefinition);
        }
    }


    private void addChain(TypeBoxingDefinition typeBoxingDefinition) {
        boxChain.add(typeBoxingDefinition);
        typeBoxingDefinition.index = boxChain.size() - 1;
    }

    private void init() {
        if (index != -1) {
            return;
        }
        addChain(this);
    }
}
