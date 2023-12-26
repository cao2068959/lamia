package com.chy.lamia.convert.core.expression.parse.entity;

import com.chy.lamia.expose.rule.RuleType;
import lombok.Data;
import lombok.Getter;

@Data
public class RuleTypeArgWrapper extends ArgWrapper {

    @Getter
    RuleType ruleType;


    public RuleType getRuleType() {
        if (ruleType != null) {
            return ruleType;
        }

        this.ruleType = genRuleType();
        return ruleType;
    }

    private RuleType genRuleType() {
        String paramName = getName();
        if (paramName == null) {
            return null;
        }
        String ruleTypeName = getLastName(paramName);
        return RuleType.valueOf(ruleTypeName);
    }

    private String getLastName(String name) {
        if (!name.contains(".")) {
            return name;
        }
        return name.substring(name.lastIndexOf(".") + 1);
    }
}
