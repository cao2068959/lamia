package com.chy.lamia.convert.core.entity;

import lombok.Data;

/**
 * @author bignosecat
 */
@Data
public class ConvertVarInfo2 {
    VarDefinition varDefinition;
    BuildInfo buildInfo;

    public ConvertVarInfo2(VarDefinition varDefinition, BuildInfo buildInfo) {
        this.varDefinition = varDefinition;
        this.buildInfo = buildInfo;
    }
}
