package com.chy.lamia.entity;


public class Var {
    String name;
    String classPath;

    public Var(String name, String classPath) {
        this.name = name;
        this.classPath = classPath;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }
}
