package com.chy.lamia.entity;


import java.util.LinkedList;
import java.util.List;

public class SimpleMethod {

    private String name;
    /**
     * 返回值类型， null 就是 void
     */
    private ParameterType returnType;
    private List<ParameterType> params = new LinkedList<>();
    boolean isStatic = false;


    public SimpleMethod(String name, ParameterType returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParameterType getReturnType() {
        return returnType;
    }

    public void setReturnType(ParameterType returnType) {
        this.returnType = returnType;
    }

    public List<ParameterType> getParam() {
        return params;
    }

    public void addParam(ParameterType param) {
        params.add(param);
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public void setParams(List<ParameterType> params) {
        this.params = params;
    }
}
