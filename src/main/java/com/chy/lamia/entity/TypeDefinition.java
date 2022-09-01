package com.chy.lamia.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 对整个java 类型的解析, 包括泛型等
 */
@Data
public class TypeDefinition {

    /**
     * 类型的全路径
     */
    String classPath;

    /**
     * 泛型
     */
    List<TypeDefinition> generic = new ArrayList<>();

    public TypeDefinition(String classPath) {
        this.classPath = classPath;
    }

    public void addGeneric(List<TypeDefinition> generic) {
        this.generic.addAll(generic);
    }
}
