package com.chy.lamia.convert.core.entity;

import com.chy.lamia.expose.rule.RuleType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class RuleInfo {

    List<RuleType> ruleTypes;

    Set<String> ignoreFields;


    public void merge(RuleInfo ruleInfo) {
        List<RuleType> srcRuleTypes = ruleInfo.getRuleTypes();
        if (srcRuleTypes != null) {
            if (this.ruleTypes == null) {
                this.ruleTypes = new ArrayList<>();
            }
            ruleTypes.addAll(srcRuleTypes);
        }

        Set<String> targetIgnoreFields = ruleInfo.getIgnoreFields();
        if (targetIgnoreFields != null) {
            if (ignoreFields == null) {
                ignoreFields = new HashSet<>();
            }
            ignoreFields.addAll(targetIgnoreFields);
        }

    }

    public boolean isIgnoreField(String classPath, String fieldName) {
        if (ignoreFields == null) {
            return false;
        }
        String fullFieldName = getFullFieldName(classPath, fieldName);
        return ignoreFields.contains(fullFieldName);
    }

    private String getFullFieldName(String classPath, String fieldName) {
        return classPath + "#" + fieldName;
    }

    public void addIgnoreField(String classPath, String fieldName) {
        if (ignoreFields == null) {
            ignoreFields = new HashSet<>();
        }
        ignoreFields.add(getFullFieldName(classPath, fieldName));
    }

    public void init() {

    }
}
