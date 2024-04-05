package com.chy.lamia.convert.core.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
import lombok.Data;

@Data
public class MethodParameterWrapper {

    /**
     * 参数的名称
     */
    String name;

    /**
     * 参数原本的文本信息
     */
    String text;

    /**
     * 参数的类型
     */
    TypeDefinition type;

    boolean isMethodInvoke = false;

    /**
     * 如果是方法调用的话，这里要存储对应的调用语句
     */
    Expression methodInvokeExpression;


    public MethodParameterWrapper(String name) {
        this.name = name;
        this.text = name;
    }


    public MethodParameterWrapper(TypeDefinition type, Expression methodInvokeExpression) {
        this.type = type;
        this.isMethodInvoke = true;
        this.methodInvokeExpression = methodInvokeExpression;
    }
}
