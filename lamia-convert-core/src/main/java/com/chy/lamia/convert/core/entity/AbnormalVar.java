package com.chy.lamia.convert.core.entity;

import lombok.Data;

@Data
public class AbnormalVar {


    /**
     * 异常字段的名称
     */
    String varName;


    /**
     * 字段所属实例的类型
     */
    TypeDefinition instanceType;

    /**
     * 字段所属实例的名词
     */
    String instanceName;

    /**
     * 这个字段的类型
     */
    TypeDefinition type;


    /**
     * 错误的 材料信息
     */
    SimpleMaterialInfo errorMaterial;

    public AbnormalVar(String varName) {
        this.varName = varName;
    }


}
