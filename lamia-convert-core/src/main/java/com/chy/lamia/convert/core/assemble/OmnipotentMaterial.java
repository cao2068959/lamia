package com.chy.lamia.convert.core.assemble;


import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.entity.VarDefinition;
import com.chy.lamia.convert.core.utils.Lists;

/**
 * 万能的 Material 可以代替所有的 Material
 */
public class OmnipotentMaterial extends Material {


    private  TreeFactory treeFactory;

    public OmnipotentMaterial() {
        this.treeFactory = ComponentFactory.getComponent(TreeFactory.class);
    }

    public OmnipotentMaterial(VarDefinition varDefinition) {
        this();
        super.varDefinition = varDefinition;
    }

    public Material adapter(TypeDefinition typeDefinition, String name) {
        Material result = new Material();
        result.supplyName = name;
        result.supplyType = typeDefinition;
        result.varDefinition = super.varDefinition;

        result.varExpressionFunction = expression -> {
            Expression jcExpression = treeFactory.geStringExpression(result.getSupplyName());
            // 生成对应的 map.get("xx") 表达式
            Statement statement = treeFactory.execMethod(expression, "get", Lists.of(jcExpression));

            // 生成的表达式类型需要强转
            return treeFactory.typeCast(result.getSupplyType().getClassPath(), statement.getExpression());
        };

        return result;
    }


}
