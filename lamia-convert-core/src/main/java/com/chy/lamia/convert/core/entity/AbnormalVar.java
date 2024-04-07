package com.chy.lamia.convert.core.entity;

import lombok.Data;

@Data
public class AbnormalVar {


    /**
     * 异常字段的名称 a.setB() 这里是b的名称
     */
    String varName;

    /**
     * 这个字段的类型 a.setB() 这里是B的类型
     */
    TypeDefinition type;

    /**
     * 字段所属实例的类型 a.getB() 这里是A的类型
     */
    TypeDefinition instanceType;

    /**
     * 字段所属实例的名称，a.getB() 这里是a的名称
     */
    String instanceName;

    /**
     * 错误的 材料信息
     */
    SimpleMaterialInfo errorMaterial;

    boolean isReturn = false;

    public AbnormalVar(String varName) {
        this.varName = varName;
    }


}
