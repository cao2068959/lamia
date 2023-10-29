package com.chy.lamia.element;

import com.chy.lamia.element.resolver.expression.RuleInfo;
import com.chy.lamia.entity.VarDefinition;
import lombok.Data;

/**
 * @author bignosecat
 */
@Data
public class ConvertVarInfo {
    VarDefinition varDefinition;
    RuleInfo ruleInfo;

    public ConvertVarInfo(VarDefinition varDefinition, RuleInfo ruleInfo) {
        this.varDefinition = varDefinition;
        this.ruleInfo = ruleInfo;
    }
}
