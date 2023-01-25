package com.chy.lamia.convert.assemble;

import com.chy.lamia.convert.builder.MaterialStatementBuilder;
import com.chy.lamia.convert.builder.MaterialTypeConvertBuilder;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.Lists;
import com.sun.tools.javac.tree.JCTree;

import java.util.List;

/**
 * map的组装处理器
 *
 * @author bignosecat
 */
public class MapAssembleHandler extends CommonAssembleHandler {


    String classPath = "java.util.HashMap";

    @Override
    protected String createNewInstantExpression() {
        String instantName = genNewInstantName();

        // 表达式生成器
        MaterialStatementBuilder materialStatementBuilder = new MaterialStatementBuilder();

        materialStatementBuilder.setFunction((() -> {
            JCTree.JCStatement jcStatement = genNewInstance(newInstant, classPath, Lists.of());
            return Lists.of(jcStatement);
        }));
        materialStatementBuilders.add(materialStatementBuilder);
        return instantName;
    }

    @Override
    public void createConvertExpression() {
        // 找到所有的材料, 放入 map中,将生成对应的 put函数
        materialMap.forEach((key, material) -> {
            MaterialTypeConvertBuilder materialTypeConvertBuilder = toMaterialTypeConvertBuilder(material);

            MaterialStatementBuilder materialStatementBuilder = new MaterialStatementBuilder();
            materialStatementBuilder.setFunction(() -> {
                JCTree.JCExpression expression = materialTypeConvertBuilder.convert().getVarExpression();
                JCTree.JCExpression putName = JCUtils.instance.geStringExpression(material.getSupplyName());
                List<JCTree.JCExpression> args = Lists.of(putName, expression);

                return Lists.of(JCUtils.instance.execMethod(newInstant, "put", args));
            });
            materialStatementBuilders.add(materialStatementBuilder);
        });

    }

    protected MaterialTypeConvertBuilder toMaterialTypeConvertBuilder(Material material) {
        // 万能材料，适配一下
        if (material instanceof OmnipotentMaterial) {
            throw new RuntimeException("不允许map扩散转map, 入参map的名称为:[" + material.varDefinition.getVarName() + "]");
        }

        return new MaterialTypeConvertBuilder(material, material.supplyType);
    }

}
