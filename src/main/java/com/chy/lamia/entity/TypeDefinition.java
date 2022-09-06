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

    public TypeDefinition(TypeDefinition typeDefinition) {
        classPath = typeDefinition.getClassPath();
        generic = new ArrayList<>(typeDefinition.getGeneric());
    }


    public void addGeneric(List<TypeDefinition> generic) {
        this.generic.addAll(generic);
    }

    @Override
    public String toString() {
        String typePatch = classPath;
        typePatch = typePatch == null ? "null" : typePatch;
        StringBuilder result = new StringBuilder(typePatch);
        if (generic != null && generic.size() != 0) {
            result.append("<");
            for (TypeDefinition typeDefinition : generic) {
                result.append(typeDefinition.toString());
            }
            result.append(">");
        }
        return result.toString();
    }
}
