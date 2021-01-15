package com.chy.lamia.entity;

import java.util.List;

public class ParameterType {

    final String name;
    final ClassType type;
    String methodName;
    List<ParameterType> generic;


    public ParameterType(String name, String typePath) {
        this.name = name;
        this.type = new ClassType(typePath);
    }

    public ParameterType(String name, ParameterType parameterType) {
        this.name = name;
        this.type = parameterType.getType();
        this.methodName = parameterType.methodName;
        this.generic = parameterType.generic;
    }

    public ParameterType(String name, String typePath, String methodName) {
        this(name, typePath);
        this.methodName = methodName;
    }

    public boolean matchType(ParameterType parameterType) {
        return type.matchType(parameterType.getType());
    }

    public List<ParameterType> getGeneric() {
        return generic;
    }

    public void setGeneric(List<ParameterType> generic) {
        this.generic = generic;
    }

    public ClassType getType() {
        return type;
    }

    public String getTypePatch(){
        if(type == null){
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



}
