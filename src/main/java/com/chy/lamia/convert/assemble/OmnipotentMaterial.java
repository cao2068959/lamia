package com.chy.lamia.convert.assemble;

import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;

/**
 * 万能的 Material 可以代替所有的 Material
 */
public class OmnipotentMaterial extends Material {


    public Material adapter(TypeDefinition typeDefinition, String name) {
        Material result = new Material();
        result.supplyName = name;
        result.supplyType = typeDefinition;
        result.varDefinition = super.varDefinition;

        return result;
    }

    public OmnipotentMaterial(VarDefinition varDefinition) {
        super.varDefinition = varDefinition;
    }
}
