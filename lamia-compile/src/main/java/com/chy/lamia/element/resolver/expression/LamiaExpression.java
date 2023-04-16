package com.chy.lamia.element.resolver.expression;

import com.sun.tools.javac.tree.JCTree;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author bignosecat
 */
@Data
public class LamiaExpression {

    /**
     *  要设置的全部参数, 直接设置的字段对象以及需要字段映射的都在这里面
     */
    Set<String> allArgsNames = new LinkedHashSet<>();

    /**
     * 用于映射的参数名称
     */
    Set<String> mappingArgs = new HashSet<>();

    /**
     * 是否需要自定义配置
     */
    boolean buildConfig = false;


    JCTree.JCTypeCast typeCast;

    public void addArgs(Collection<String> args) {
        allArgsNames.addAll(args);
    }

    public void addSpreadArgs(Collection<String> args) {
        allArgsNames.addAll(args);
        mappingArgs.addAll(args);
    }

}
