package com.chy.lamia.convert.core.entity;

import lombok.Data;

@Data
public class SimpleMaterialInfo {

    String name;
    TypeDefinition type;

    BuildInfo buildInfo;

    public SimpleMaterialInfo(String name, TypeDefinition type) {
        this.name = name;
        this.type = type;
    }
}
