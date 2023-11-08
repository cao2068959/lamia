package com.chy.lamia.convert.core;

import com.chy.lamia.convert.assemble.*;
import com.chy.lamia.convert.builder.MaterialStatementBuilder;
import com.chy.lamia.element.ConvertVarInfo;
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
        // 在组装器中添加所有的所有可能参与组合结果对象的材料
        assembleHandler.addMaterial(createdMaterials(lamiaConvertInfo));
        // 生成所有组装对象 语句的构建器, 每一个builder 将会生成一行 语句, 如 result.setXXX(var.getVVV())
        List<MaterialStatementBuilder> materialStatementBuilders = assembleHandler.run();
        // 生成真正的 java语句
        return createdStatement(materialStatementBuilders, assembleHandler, lamiaConvertInfo);

    }

    /**
     * 使用 MaterialExpressionBuilder 去生成真正的 statement
     * <p>
     * 每一个 MaterialStatementBuilder 理论上是对应了一行 java语句，但是这一行java语句可能依赖的入参需要有一些前置转换，所以
     * 一个 MaterialStatementBuilder 能够生成出多行 java语句
     *
     * @param expressionBuilders
     * @param assembleHandler
     * @param lamiaConvertInfo
     * @return
     */
    private List<JCTree.JCStatement> createdStatement(List<MaterialStatementBuilder> expressionBuilders, AssembleHandler assembleHandler, LamiaConvertInfo lamiaConvertInfo) {
        List<JCTree.JCStatement> result = new ArrayList<>();
        expressionBuilders.forEach(expressionBuilder -> {
            List<JCTree.JCStatement> jcStatements = expressionBuilder.build();
            result.addAll(jcStatements);
        });

        // 如果是return 后面直接接上的 转换语句,那么 还需要把return 语句补上
        if (lamiaConvertInfo.isReturn()) {
            String newInstantName = assembleHandler.getNewInstantName();
            JCTree.JCReturn aReturn = JCUtils.instance.createReturn(newInstantName);
            result.add(aReturn);
        }

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
        List<ConvertVarInfo> args = lamiaConvertInfo.getArgsByPriority();


        List<Material> result = new ArrayList<>();
        args.forEach(convertVarInfo -> {
            VarDefinition varDefinition = convertVarInfo.getVarDefinition();
            // 判断这个参数是否需要扩散开
            if (lamiaConvertInfo.isSpread(varDefinition)) {
                List<Material> materials = spreadVarDefinition(convertVarInfo);
                result.addAll(materials);
                return;
            }
            // 不需要扩散那 直接封装了
            Material material = Material.simpleMaterial(convertVarInfo);
            result.add(material);
        });
        return result;
    }

    /**
     * 扩散 varDefinition 转成对应的 Material
     *
     * @param convertVarInfo ConvertVarInfo
     * @return
     */
    private List<Material> spreadVarDefinition(ConvertVarInfo convertVarInfo) {
        VarDefinition varDefinition = convertVarInfo.getVarDefinition();
        TypeDefinition type = varDefinition.getType();
        // 如果扩散的是一个 map
        if (type.matchType(Map.class)) {
            Material material = new OmnipotentMaterial(varDefinition);
            return Lists.of(material);
        }

        // 是系统类型或者基础数据类型,不允许扩散
        if (type.isBaseTypeOrSystemType()) {
            throw new RuntimeException("变量 [" + varDefinition + "] 是系统类型不允许扩散, 可以使用 Lamia.convert(不扩散的类型) 方法或者 Lamia.config().spreadArgs(需要扩散的类型).convert(不扩散的类型) 来指定");
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
            material.setRuleInfo(convertVarInfo.getRuleInfo());
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
            result = new MapAssembleHandler(lamiaConvertInfo.getTarget());
        } else {
            result = new ValueObjAssembleHandler(targetType, lamiaConvertInfo.getTarget());
        }
        result.setLamiaConvertInfo(lamiaConvertInfo);
        return result;

    }
}
