package com.chy.lamia.convert;

import com.chy.lamia.convert.assemble.*;
import com.chy.lamia.convert.builder.MaterialStatementBuilder;
import com.chy.lamia.element.LamiaConvertInfo;
import com.chy.lamia.element.resolver.type.TypeResolver;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.Lists;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 转换工厂
 *
 * @author bignosecat
 */
public class ConvertFactory {

    public static ConvertFactory INSTANCE = new ConvertFactory();


    /**
     * 开始生成对应的转换代码
     *
     * @param lamiaConvertInfo 表达式信息
     * @return
     */
    public List<JCTree.JCStatement> make(LamiaConvertInfo lamiaConvertInfo) {
        // 寻找适合的组成器
        AssembleHandler assembleHandler = getAssembleHandler(lamiaConvertInfo);
        // 获取所有可能参与组合结果对象的材料
        List<Material> materials = createdMaterials(lamiaConvertInfo);
        // 在组装器中添加所有的材料
        assembleHandler.addMaterial(materials);
        // 生成所有组装对象 语句的构建器, 每一个builder 将会生成一行 语句, 如 result.setXXX(var.getVVV())
        List<MaterialStatementBuilder> materialStatementBuilders = assembleHandler.run();
        // 生成真正的 java语句
        return createdStatement(materialStatementBuilders, lamiaConvertInfo);

    }

    /**
     * 使用 MaterialExpressionBuilder 去生成真正的 statement
     * <p>
     * 每一个 MaterialStatementBuilder 理论上是对应了一行 java语句，但是这一行java语句可能依赖的入参需要有一些前置转换，所以
     * 一个 MaterialStatementBuilder 能够生成出多行 java语句
     *
     * @param expressionBuilders
     * @param lamiaConvertInfo
     * @return
     */
    private List<JCTree.JCStatement> createdStatement(List<MaterialStatementBuilder> expressionBuilders, LamiaConvertInfo lamiaConvertInfo) {
        List<JCTree.JCStatement> result = new ArrayList<>();
        expressionBuilders.forEach(expressionBuilder -> {
            List<JCTree.JCStatement> jcStatements = expressionBuilder.build();
            result.addAll(jcStatements);
        });
        return result;
    }

    /**
     * 整合所有的入参
     *
     * @param lamiaConvertInfo
     * @return
     */
    private List<Material> createdMaterials(LamiaConvertInfo lamiaConvertInfo) {
        // 根据优先级 获取出所有的参数, 高优先级的放队尾
        List<VarDefinition> args = lamiaConvertInfo.getArgsByPriority();


        List<Material> result = new ArrayList<>();
        args.forEach(varDefinition -> {
            // 判断这个参数是否需要扩散开
            if (lamiaConvertInfo.isSpread(varDefinition)) {
                List<Material> materials = spreadVarDefinition(varDefinition);
                result.addAll(materials);
                return;
            }
            // 不需要扩散那 直接封装了
            Material material = Material.simpleMaterial(varDefinition);
            result.add(material);
        });
        return result;
    }

    /**
     * 扩散 varDefinition 转成对应的 Material
     *
     * @param varDefinition varDefinition
     * @return
     */
    private List<Material> spreadVarDefinition(VarDefinition varDefinition) {
        TypeDefinition type = varDefinition.getType();
        // 如果扩散的是一个 map
        if (type.matchType(Map.class)) {
            Material material = new OmnipotentMaterial(varDefinition);
            return Lists.of(material);
        }

        // 解析对应的类型
        TypeResolver typeResolver = TypeResolver.getTypeResolver(type);
        Map<String, Getter> instantGetters = typeResolver.getInstantGetters();

        List<Material> result = new ArrayList<>();

        instantGetters.forEach((fieldName, getter) -> {
            Material material = new Material();
            material.setVarDefinition(varDefinition);
            material.setSupplyType(getter.getType());
            material.setSupplyName(fieldName);
            // 生成对应的 var.getXX()
            material.setVarExpressionFunction((varExpression -> {
                JCTree.JCExpressionStatement statement = JCUtils.instance.execMethod(varExpression, getter.getMethodName(), Lists.of());
                return statement.getExpression();
            }));
            result.add(material);
        });
        return result;
    }


    /**
     * 获取一个合适的组装处理器
     *
     * @return 组装器
     */
    private AssembleHandler getAssembleHandler(LamiaConvertInfo lamiaConvertInfo) {

        TypeDefinition targetType = lamiaConvertInfo.getTargetType();
        AssembleHandler result;
        // 如果要组装的是 map, 则用map的组装器
        if (targetType.matchType(Map.class)) {
            result = new MapAssembleHandler();
        } else {
            result = new ValueObjAssembleHandler(targetType);
        }
        result.setLamiaConvertInfo(lamiaConvertInfo);
        return result;

    }
}
