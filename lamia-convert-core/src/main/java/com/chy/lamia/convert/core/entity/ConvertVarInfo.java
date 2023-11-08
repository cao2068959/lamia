package com.chy.lamia.convert.core.entity;

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
