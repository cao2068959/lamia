package com.chy.lamia.entity;


public class ClassType {

    final String typePath;

    public ClassType(String typePath) {
        typePath = typePathPurify(typePath);
        this.typePath = typePath;
    }

    /**
     * 净化 类路径，如果后面带了泛型，将会把泛型给抹去
     *
     * @param typePath
     * @return
     */
    private String typePathPurify(String typePath) {
        int index = typePath.indexOf("<");
        if (index == -1) {
            return typePath;
        }
        return typePath.substring(0, index);
    }


    public boolean matchType(String type) {
        if (type == null) {
            return false;
        }
        if (type.equals(typePath)) {
            return true;
        }
        return false;
    }

    public boolean matchType(ClassType type) {
        return matchType(type.getTypePath());
    }


    public String getTypePath() {
        return typePath;
    }


}
