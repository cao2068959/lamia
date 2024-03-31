package com.chy.lamia.convert.core.entity;

import lombok.Data;

@Data
public class BuildInfo {

    /**
     * 当前是否设置规则, 如果不为 null 说明是 rule() 之后构建的
     */
    RuleInfo ruleInfo;

    /**
     * 通过 builder() 的方式构建的
     */
    boolean isBuilder = false;


    Object holder;

    public void merge(BuildInfo buildInfo) {
        if (buildInfo.ruleInfo != null) {
            if (ruleInfo == null) {
                ruleInfo = buildInfo.ruleInfo;
            } else {
                ruleInfo.merge(buildInfo.ruleInfo);
            }
        }
    }

    public boolean isIgnoreField(String classPath, String fieldName) {
        if (ruleInfo == null) {
            return false;
        }
        return ruleInfo.isIgnoreField(classPath, fieldName);
    }
}
