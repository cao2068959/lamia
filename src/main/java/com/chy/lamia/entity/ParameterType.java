package com.chy.lamia.entity;

import com.sun.tools.javac.code.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public ParameterType(Type type) {
        this("", type.tsym.toString());
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

    public Optional<Class<?>> getTypeReflectClass() {
        return type.getReflectClass();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParameterType that = (ParameterType) o;

        if (!type.equals(that.type)) return false;
        return generic.equals(that.generic);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + generic.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String typePatch = getTypePatch();
        typePatch = typePatch == null ? "null" : typePatch;
        StringBuilder result = new StringBuilder(typePatch);
        if (generic != null && generic.size() != 0) {
            result.append("<");
            for (ParameterType parameterType : generic) {
                result.append(parameterType.toString());
            }
            result.append(">");
        }
        return result.toString();
    }


}
