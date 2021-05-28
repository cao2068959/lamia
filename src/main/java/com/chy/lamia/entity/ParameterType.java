package com.chy.lamia.entity;

import java.util.ArrayList;
import java.util.List;

public class ParameterType {

    String name;
    String fieldName;
    final ClassType type;
    String methodName;
    List<ParameterType> generic;

    public ParameterType(String name, String typePath) {
        this.name = name;
        this.fieldName = name;
        this.type = new ClassType(typePath);
    }


    public ParameterType(String typePath) {
        this.name = "";
        this.fieldName = "";
        this.type = new ClassType(typePath);
    }


    public ParameterType(String name, ParameterType parameterType) {
        this.name = name;
        this.fieldName = name;
        this.type = parameterType.getType();
        this.methodName = parameterType.methodName;
        this.generic = parameterType.generic;
    }

    public ParameterType(String name, ParameterType parameterType, String methodName) {
        this(name, parameterType);
        this.methodName = methodName;
    }


    public boolean matchType(ParameterType parameterType) {
        return type.matchType(parameterType.getType());
    }


    public List<ParameterType> getGeneric() {
        return generic;
    }

    public void addGeneric(ParameterType parameterType) {
        if (generic == null) {
            generic = new ArrayList<>();
        }
        generic.add(parameterType);
    }

    public void setGeneric(List<ParameterType> generic) {
        this.generic = generic;
    }

    public ClassType getType() {
        return type;
    }

    public String getTypePatch() {
        if (type == null) {
            return null;
        }
        return type.typePath;

    }

    public String getName() {
        return name;
    }

    public String getMethodName() {
        return methodName;
    }


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setName(String name) {
        this.name = name;
    }
}
