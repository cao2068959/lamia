package com.chy.lamia.element.resolver.expression;

import com.chy.lamia.expose.rule.RuleType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RuleInfo {

    List<RuleType> ruleTypes;

    public void merge(RuleInfo ruleInfo) {
        List<RuleType> srcRuleTypes = ruleInfo.getRuleTypes();
        if (srcRuleTypes != null) {
            if (ruleInfo == null) {
                this.ruleTypes = new ArrayList<>();
            }
            ruleTypes.addAll(srcRuleTypes);
        }


    }
}
