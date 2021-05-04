package com.chy.lamia.entity;

public class Setter {

    private String simpleName;
    //setter 方法的类型, 也就是入参的类型是什么
    private ParameterType parameterType;

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public ParameterType getParameterType() {
        return parameterType;
    }

    public void setParameterType(ParameterType parameterType) {
        this.parameterType = parameterType;
    }

    public void setTypePath(String parameterTypeName) {
        parameterType = new ParameterType(parameterTypeName);
    }
}
