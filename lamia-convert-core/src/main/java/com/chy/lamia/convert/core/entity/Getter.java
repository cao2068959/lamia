package com.chy.lamia.convert.core.entity;

import lombok.Data;

@Data
public class Getter {


    /**
     * get 字段的名称
     */
    private String varName;
    /**
     * 方法的名称
     */
    private String methodName;

    /**
     * get出来的类型
     */
    private TypeDefinition type;

    /**
     *  get方法所在类的类型
     */
    private TypeDefinition parentClassType;


}