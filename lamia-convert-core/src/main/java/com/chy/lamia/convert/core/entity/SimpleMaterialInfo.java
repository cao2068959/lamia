package com.chy.lamia.convert.core.entity;

import lombok.Data;

@Data
public class SimpleMaterialInfo {

    TypeDefinition type;

    ProtoMaterialInfo protoMaterialInfo;

    public SimpleMaterialInfo(ProtoMaterialInfo protoMaterialInfo, TypeDefinition type) {
        this.protoMaterialInfo = protoMaterialInfo;
        this.type = type;
    }
}
