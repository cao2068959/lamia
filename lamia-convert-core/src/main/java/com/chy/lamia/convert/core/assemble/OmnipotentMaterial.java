package com.chy.lamia.convert.core.assemble;


import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.entity.VarDefinition;
import com.chy.lamia.convert.core.utils.Lists;

/**
 * 万能的 Material 可以代替所有的 Material
 */
public class OmnipotentMaterial extends Material {


    public Material adapter(TypeDefinition typeDefinition, String name) {
        Material result = new Material();
        result.supplyName = name;
        result.supplyType = typeDefinition;
        result.varDefinition = super.varDefinition;

        result.varExpressionFunction = expression -> {
            JCTree.JCExpression jcExpression = JCUtils.instance.geStringExpression(result.getSupplyName());
            // 生成对应的 map.get("xx") 表达式
            JCTree.JCExpressionStatement expressionStatement = JCUtils.instance.execMethod(expression, "get", Lists.of(jcExpression));

            // 生成的表达式类型需要强转
            return JCUtils.instance.typeCast(result.getSupplyType().getClassPath(), expressionStatement.expr);
        };

        return result;
    }

    public OmnipotentMaterial(VarDefinition varDefinition) {
        super.varDefinition = varDefinition;
    }
}
