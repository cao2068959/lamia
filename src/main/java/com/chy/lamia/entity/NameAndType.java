package com.chy.lamia.entity;

public class NameAndType {

    String name;
    String typePath;
    String methodName;


    public NameAndType(String name, String typePath) {
        this.name = name;
        this.typePath = typePath;
    }

    public NameAndType(String name, String typePath, String methodName) {
        this.name = name;
        this.typePath = typePath;
        this.methodName = methodName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypePath() {
        return typePath;
    }

    public void setTypePath(String typePath) {
        this.typePath = typePath;
    }

    public String getMethodName() {
        return methodName;
    }
}
