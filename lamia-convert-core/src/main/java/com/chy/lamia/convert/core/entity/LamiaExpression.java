package com.chy.lamia.convert.core.entity;

import lombok.Data;

import java.util.*;

/**
 * @author bignosecat
 */
@Data
public class LamiaExpression {

    /**
     * 要设置的全部参数, 直接设置的字段对象以及需要字段映射的都在这里面
     */
    Map<String, RuleInfo> allArgs = new LinkedHashMap<>();

    /**
     * 用于映射的参数名称
     */
    Set<String> mappingArgs = new LinkedHashSet<>();


    /**
     * 当前是否设置规则，如果设置了最终会转移后  allArgsNames , 这个变量只是一个临时的中转
     */
    RuleInfo ruleInfos;


    /**
     * 是否需要自定义配置
     */
    boolean buildConfig = false;


    TypeDefinition targetType;

    ExpressionItem target;


    public void addArgs(Collection<String> args) {

        for (String arg : args) {
            if (!allArgs.containsKey(arg)) {
                allArgs.put(arg, null);
            }
        }
    }

    public void addArgs(Collection<String> args, RuleInfo ruleInfo) {
        for (String arg : args) {
            RuleInfo oldRule = allArgs.get(arg);
            if (oldRule == null) {
                allArgs.put(arg, ruleInfo);
                continue;
            }
            oldRule.merge(ruleInfo);
        }
    }

    public void addSpreadArgs(Collection<String> args, RuleInfo ruleInfo) {
        if (ruleInfo == null) {
            addArgs(args);
        } else {
            addArgs(args, ruleInfo);
        }

        mappingArgs.addAll(args);
    }

}
