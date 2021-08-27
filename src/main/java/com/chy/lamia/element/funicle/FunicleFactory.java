package com.chy.lamia.element.funicle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用来和依赖类之间去建立脐带关系， 其实也就是生成一个方法去相互调用
 * 用来解决idea增量编译的问题
 */
public class FunicleFactory {

    /**
     * key: 被lamia操作的类路径
     * value: 依赖的所有类路径
     */
    private static Map<String, Set<String>> dependents = new HashMap<>();


    public static void addDependent(String classpath, Set<String> data) {
        Set<String> oldData = dependents.get(classpath);
        if (oldData == null) {
            dependents.put(classpath, data);
            return;
        }
        oldData.addAll(data);
    }


}
