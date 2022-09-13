package com.chy.lamia.entity;

import lombok.Data;

@Data
public class Setter {

    /**
     * set方法 设置的字段的名称
     */
    private String varName;
    /**
     * 方法的名称
     */
    private String methodName;

    /**
     * set方法 设置的字段的类型
     */
    private TypeDefinition type;

}
