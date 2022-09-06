package com.chy.lamia.element.resolver;

import com.chy.lamia.element.IClassDefine;
import com.chy.lamia.entity.*;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 类型解析器, 可以把 TypeDefinition 转成可解析的类型, 从而获取 type中的 方法
 * <p>
 * 根据文件类型不同分成 class解析 以及 java文件解析, 会自动判断对应的文件类型选择对应的解析器
 * @author bignosecat
 */
public class TypeResolver {

    IClassDefine classDefine;
    TypeDefinition typeDefinition;
    boolean canUpdate = true;
    List<TypeResolver> generic = new LinkedList<>();

    private static Map<String, TypeResolver> cache = new HashMap<>();


    /**
     * 使用 getTypeResolver() 来获取 TypeResolver 对象, 因为有缓存,不建议直接 new
     *
     * @param typeDefinition
     */
    private TypeResolver(TypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
        List<TypeDefinition> generics = typeDefinition.getGeneric();
        if (generics != null) {
            generic = generics.stream().map(TypeResolver::getTypeResolver)
                    .collect(Collectors.toList());
        }
        JCUtils jcUtils = JCUtils.instance;
        String classPath = typeDefinition.getClassPath();
        JCTree tree = jcUtils.getTree(classPath);
        if (tree != null) {
            classDefine = new TreeClassDefine(jcUtils, tree);
            return;
        }
        // 上面tree没找到说明不是 通过java文件进行编译的, 下面将解析 class, 所以改文件不可修改了
        canUpdate = false;
        //使用 ASM  解析class文件
        Class<?> classForReflect = getClassForReflect(classPath);
        if (classForReflect != null) {
            classDefine = new AsmClassDefine(jcUtils, classForReflect);
            return;
        }
        throw new RuntimeException("无法解析类： " + classPath);

    }


    /**
     * 通过反射去获取对应的 class对象
     *
     * @param classPath
     * @return
     */
    private Class<?> getClassForReflect(String classPath) {
        try {
            return Class.forName(classPath);
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    /**
     * 获取这个类中所有的类变量
     *
     * @return
     */
    public Map<String, Var> getInstantVarName() {
        return classDefine.getInstantVars();
    }


    /**
     * 获取这个 类中所有的 get方法
     *
     * @return
     */
    public Map<String, Getter> getInstantGetters() {
        return classDefine.getInstantGetters();
    }

    /**
     * 获取这个类中所有的 set方法
     *
     * @return
     */
    public Map<String, Setter> getInstantSetters() {
        return classDefine.getInstantSetters();
    }


    /**
     * 获取这个类中所有的方法
     *
     * @return
     */
    public List<SimpleMethod> getAllMethod() {
        return classDefine.getAllMethod();
    }

    /**
     * 获取对应的 类型处理器, 这里做一层缓存
     *
     * @param typeDefinition
     * @return
     */
    public static TypeResolver getTypeResolver(TypeDefinition typeDefinition) {
        String key = typeDefinition.toString();
        TypeResolver result = cache.get(key);
        if (result != null) {
            return result;
        }
        result = new TypeResolver(typeDefinition);
        cache.put(key, result);
        return result;
    }

}
