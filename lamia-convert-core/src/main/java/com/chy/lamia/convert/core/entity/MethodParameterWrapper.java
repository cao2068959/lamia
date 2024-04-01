package com.chy.lamia.convert.core.entity;

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



    public MethodParameterWrapper(String name) {
        this.name = name;
        this.text = name;
    }


    public MethodParameterWrapper(TypeDefinition type) {
        this.type = type;
        isMethodInvoke = true;
    }
}
