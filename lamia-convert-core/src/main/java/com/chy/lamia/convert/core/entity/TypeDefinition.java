package com.chy.lamia.convert.core.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 对整个java 类型的解析, 包括泛型等
 */
@Data
public class TypeDefinition {

    /**
     * 类型的全路径
     */
    String classPath;

    /**
     * 反射的类型,不一定存在
     */
    Optional<Class<?>> reflectClass;

    /**
     * 泛型
     */
    List<TypeDefinition> generic = new ArrayList<>();

    public TypeDefinition(String classPath) {
        this.classPath = classPath;
    }

    public TypeDefinition(TypeDefinition typeDefinition) {
        classPath = typeDefinition.getClassPath();
        generic = new ArrayList<>(typeDefinition.getGeneric());
    }


    public void addGeneric(List<TypeDefinition> generic) {
        this.generic.addAll(generic);
    }

    public void addGeneric(TypeDefinition generic) {
        this.generic.add(generic);
    }

    /**
     * 判断是否是指定的 类型
     *
     * @return true/false
     */
    public boolean matchType(Class<?> type) {
        String typeName = type.getTypeName();
        if (classPath.equals(typeName)) {
            return true;
        }
        // 获取反射类型, 使用反射的方式去判断是否是对应的类型
        Optional<Class<?>> reflectClass = getReflectClass();
        return reflectClass.filter(type::isAssignableFrom).isPresent();
    }

    /**
     * 类型匹配
     *
     * @param type           要对比的类型
     * @param isMatchGeneric 是否匹配泛型, true: 泛型也需要完全相同才能算相同
     * @return 是否比较通过
     */
    public boolean matchType(TypeDefinition type, boolean isMatchGeneric) {
        if (type == null) {
            return false;
        }
        if (type == this) {
            return true;
        }
        if (isMatchGeneric) {
            return toString().equals(type.toString());
        }
        return classPath.equals(type.getClassPath());
    }


    public Optional<Class<?>> getReflectClass() {
        if (reflectClass == null) {
            reflectClass = tryGenReflectClass();
        }
        return reflectClass;
    }

    private Optional<Class<?>> tryGenReflectClass() {
        try {
            return Optional.of(Class.forName(classPath));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        String typePatch = classPath;
        typePatch = typePatch == null ? "null" : typePatch;
        StringBuilder result = new StringBuilder(typePatch);


        if (generic != null && generic.size() != 0) {
            result.append("<");
            for (int i = 0; i < generic.size(); i++) {
                TypeDefinition typeDefinition = generic.get(i);
                result.append(typeDefinition.toString());
                if (i != generic.size() - 1) {
                    result.append(", ");
                }
            }
            result.append(">");
        }
        return result.toString();
    }

    public String simpleClassName() {
        String[] split = classPath.split("\\.");
        return split[split.length - 1];
    }

    /**
     * 目标类型是否是 当前类型的 子泛型
     *
     * @param targetType
     * @return
     */
    public boolean isChildrenGeneric(TypeDefinition targetType) {
        String current = toString();
        return current.contains(targetType.toString());
    }

    /**
     * 是否是基础数据类型
     *
     * @return
     */
    public boolean isBaseTypeOrSystemType() {
        if (classPath.contains("java.lang")) {
            return true;
        }
        return false;
    }
}
