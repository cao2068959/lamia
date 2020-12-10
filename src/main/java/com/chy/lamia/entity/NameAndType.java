package com.chy.lamia.entity;

public class NameAndType {

    final String name;
    final String typePath;
    String simpleTypeName = "";
    String methodName;


    public NameAndType(String name, String typePath) {
        this.name = name;
        this.typePath = typePath;
        this.simpleTypeName = generaterSimpleTypeName(typePath);

    }

    public NameAndType(String name, String typePath, String methodName) {
        this(name, typePath);
        this.methodName = methodName;
    }

    private String generaterSimpleTypeName(String typePath) {
        if (typePath == null) {
            return "";
        }
        int index = typePath.lastIndexOf(".");
        if (index == -1) {
            return typePath;
        }

        if (index + 1 >= typePath.length()) {
            return "";
        }

        return typePath.substring(index + 1);
    }


    public String getName() {
        return name;
    }

    public String getMethodName() {
        return methodName;
    }


    public boolean matchType(String type) {
        if (type == null) {
            return false;
        }

        if (type.equals(typePath)) {
            return true;
        }

        if (simpleTypeName.equals(type)) {
            return true;
        }
        return false;
    }

    public boolean matchType(NameAndType nameAndType) {
        return matchType(nameAndType.typePath);
    }

}
