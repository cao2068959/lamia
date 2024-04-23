package com.chy.lamia.convert.core.entity;

import lombok.Data;

/**
 * 在lamia表达式中 mapping或者setFiled 中的参数称为 ProtoMaterialInfo
 */
@Data
public class ProtoMaterialInfo {

    /**
     * 材料的id,自动生成，主要为了去重
     */
    String id;

    MethodParameterWrapper material;

    /**
     * 材料的构建信息
     */
    BuildInfo buildInfo;


    boolean spread = false;

    int priority;


    public ProtoMaterialInfo(String id) {
        this.id = id;
    }


    public static ProtoMaterialInfo simpleMaterialInfo(String name) {
        ProtoMaterialInfo result = new ProtoMaterialInfo(name);
        MethodParameterWrapper methodParameterWrapper = new MethodParameterWrapper(name);
        result.setMaterial(methodParameterWrapper);
        return result;
    }


    public void merge(ProtoMaterialInfo protoMaterialInfo) {
        if (protoMaterialInfo.isSpread()) {
            spread = true;
        }
        if (buildInfo == null) {
            buildInfo = protoMaterialInfo.getBuildInfo();
        } else {
            buildInfo.merge(protoMaterialInfo.getBuildInfo());
        }
    }


    public boolean isSpread() {
        if (!spread) {
            return false;
        }
        // 系统的基础类型不进行扩散
        if (material.getType().isBaseTypeOrSystemType()) {
            return false;
        }
        return spread;
    }

    public String getName() {
        return material.getName();
    }
}
