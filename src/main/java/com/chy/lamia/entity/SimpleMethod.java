package com.chy.lamia.entity;


import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class SimpleMethod {

    private String name;
    /**
     * 返回值类型， null 就是 void
     */
    private TypeDefinition returnType;
    private List<TypeDefinition> params = new LinkedList<>();
    boolean isStatic = false;


    public SimpleMethod(String name, TypeDefinition returnType) {
        this.name = name;
        this.returnType = returnType;
    }

}
