package com.chy.lamia.entity;


public class ClassType {

    final String typePath;
    String simpleTypeName = "";

    public ClassType(String typePath) {
        this.typePath = typePath;
        this.simpleTypeName = generaterSimpleTypeName(typePath);
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

    public boolean matchType(ClassType type){
        return matchType(type.getTypePath());
    }


    public String getTypePath() {
        return typePath;
    }


}
