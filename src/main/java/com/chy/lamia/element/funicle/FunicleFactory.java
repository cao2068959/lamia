package com.chy.lamia.element.funicle;

import com.chy.lamia.element.ClassDetails;
import com.chy.lamia.entity.ClassType;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.SimpleMethod;
import com.chy.lamia.log.Logger;
import com.chy.lamia.utils.CommonUtils;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.*;

/**
 * 用来和依赖类之间去建立脐带关系， 其实也就是生成一个方法去相互调用
 * 用来解决idea增量编译的问题
 */
public class FunicleFactory {

    public static String flag = "Funicle";

    /**
     * key: 被lamia操作的类路径
     * value: 依赖的所有类路径
     */
    private static Map<String, Set<String>> dependents = new HashMap<>();

    private static Map<String, Optional<SimpleMethod>> funicleMethodCache = new HashMap<>();

    public static void addDependent(String classpath, Set<String> data) {
        if (data == null) {
            return;
        }

        Set<String> oldData = dependents.get(classpath);
        if (oldData == null) {
            dependents.put(classpath, data);
            return;
        }
        oldData.addAll(data);
    }


    public static void createFunicleMethod(JCTree jcTree, String className) {
        try {
            doCreateFunicleMethod(jcTree, className);
        } catch (Exception e) {
            Logger.log("createFunicleMethod 执行失败 --------------------");
            Logger.throwableLog(e);
        }
    }

    private static void doCreateFunicleMethod(JCTree jcTree, String className) {
        Set<String> dependentClassPaths = dependents.get(className);
        Map<String, SimpleMethod> classPathAndMethod = new HashMap<>();

        //寻找对应的依赖类中有没有脐带方法，如果没有那么就去创建一个
        dependentClassPaths.forEach(dependentClassPath -> {
            Optional<SimpleMethod> funicleMethodByCache = findFunicleMethodByCache(dependentClassPath);
            if (funicleMethodByCache.isPresent()) {
                classPathAndMethod.put(dependentClassPath, funicleMethodByCache.get());
            }
        });

        //去调用类中生成方法，完成脐带的调用
        FunicleMethodCreateVisitor visitor = new FunicleMethodCreateVisitor(classPathAndMethod);
        jcTree.accept(visitor);

    }


    /**
     * 从缓存中获取某个脐带方法，如果没有就创建一个
     *
     * @param classpath
     * @return
     */
    private static Optional<SimpleMethod> findFunicleMethodByCache(String classpath) {
        Optional<SimpleMethod> simpleMethod = funicleMethodCache.get(classpath);
        if (simpleMethod != null) {
            return simpleMethod;
        }
        simpleMethod = Optional.ofNullable(findOrCreateFunicleMethod(classpath));
        funicleMethodCache.put(classpath, simpleMethod);
        return simpleMethod;
    }

    private static SimpleMethod findOrCreateFunicleMethod(String classpath) {

        ClassDetails classElement = ClassDetails.getClassElement(new ParameterType(classpath));
        List<SimpleMethod> allMethod = classElement.getAllMethod();
        //先去这个类中的所有方法看看，有没有对应的脐带方法
        return findFunicleMethodByClass(allMethod).orElseGet(() -> {
            //没有就去生成一个 脐带方法
            Optional<String> randomMethodName = JCUtils.instance.genStaticRandomMethod(classpath, flag);
            if (!randomMethodName.isPresent()) {
                Logger.log("[" + classpath + "] 生成 FunicleMethod 失败.....................");
                return null;
            }
            return new SimpleMethod(randomMethodName.get(), null);
        });
    }


    private static Optional<SimpleMethod> findFunicleMethodByClass(List<SimpleMethod> allMethod) {
        String funicleFlag = CommonUtils.lamiaPrefix + flag + "$$";

        //脐带方法放到末尾的可能性更大，这里倒叙扫描
        for (int i = allMethod.size() - 1; i >= 0; i++) {
            SimpleMethod simpleMethod = allMethod.get(i);

            if (!simpleMethod.isStatic() || !simpleMethod.getParam().isEmpty()) {
                continue;
            }
            if (simpleMethod.getName().contains(funicleFlag)) {
                return Optional.of(simpleMethod);
            }

        }
        return Optional.empty();
    }


}
