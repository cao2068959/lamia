package com.chy.lamia.entity;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClassType {

    static Map<String, String> boxTypes = new HashMap<>();

    static {
        boxTypes.put("boolean", Boolean.class.getName());
        boxTypes.put("int", Integer.class.getName());
        boxTypes.put("char", Character.class.getName());
        boxTypes.put("byte", Byte.class.getName());
        boxTypes.put("double", Double.class.getName());
        boxTypes.put("long", Long.class.getName());
        boxTypes.put("float", Float.class.getName());
        boxTypes.put("short", Short.class.getName());
    }

    final String typePath;
    private Optional<Class<?>> reflectClass;

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

        String targetBoxType = boxTypes.get(type);
        if (targetBoxType == null) {
            targetBoxType = type;
        }

        String thisBoxType = boxTypes.get(typePath);
        if (thisBoxType == null) {
            thisBoxType = typePath;
        }

        return thisBoxType.equals(targetBoxType);
    }

    public boolean matchType(ClassType type) {
        return matchType(type.getTypePath());
    }

    public String getTypePath() {
        return typePath;
    }

    public Optional<Class<?>> getReflectClass() {
        if (reflectClass == null) {
            reflectClass = tryGenReflectClass();
        }
        return reflectClass;
    }

    private Optional<Class<?>> tryGenReflectClass() {
        try {
            return Optional.of(Class.forName(typePath));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }


}
