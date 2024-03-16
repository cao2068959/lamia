package com.chy.lamia.convert.core;


import com.chy.lamia.convert.core.assemble.*;
import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.TypeResolver;
import com.chy.lamia.convert.core.components.TypeResolverFactory;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.chy.lamia.convert.core.entity.*;
import com.chy.lamia.convert.core.expression.imp.builder.MaterialStatementBuilder;
import com.chy.lamia.convert.core.utils.Lists;

import java.util.*;

/**
 * 转换工厂
 *
 * @author bignosecat
 */
public class ConvertFactory {

    public static ConvertFactory INSTANCE = new ConvertFactory();
    private final TreeFactory treeFactory;

    public ConvertFactory() {
        treeFactory = ComponentFactory.getComponent(TreeFactory.class);
    }

    /**
     * 开始生成对应的转换代码
     *
     * @param lamiaConvertInfo 表达式信息
     * @return
     */
    public List<Statement> make(LamiaConvertInfo lamiaConvertInfo) {
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
     * 将会执行表达式，并获取参与转换的所有变量， 包括 target set 的var 也会算入其中
     *
     * @param lamiaConvertInfo
     * @return key:类全路径， value：这个类中用到的所有 var
     */
    public Map<String, Set<String>> getParticipateVar(LamiaConvertInfo lamiaConvertInfo) {
        // 寻找适合的组成器
        AssembleHandler assembleHandler = getAssembleHandler(lamiaConvertInfo);
        // 在组装器中添加所有的所有可能参与组合结果对象的材料
        assembleHandler.addMaterial(createdMaterials(lamiaConvertInfo));
        // 执行转换
        assembleHandler.run();

        Map<String, Set<String>> result = new HashMap<>();
        Set<String> mappingVarName = assembleHandler.getMappingVarName();
        for (String varName : mappingVarName) {
            Material material = assembleHandler.getMaterial(varName);
            if (material == null) {
                continue;
            }

            if (material instanceof OmnipotentMaterial) {
                continue;
            }
            if (material.getSupplyType() == null) {
                continue;
            }
            String supplyName = material.getSupplyName();
            String classPath = material.getVarDefinition().getType().getClassPath();
            result.computeIfAbsent(classPath, __ -> new HashSet<>()).add(supplyName);
        }

        if (assembleHandler instanceof MapAssembleHandler) {
            return result;
        }

        String classPath = lamiaConvertInfo.getTargetType().getClassPath();
        result.put(classPath, mappingVarName);

        return result;
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
    private List<Statement> createdStatement(List<MaterialStatementBuilder> expressionBuilders, AssembleHandler assembleHandler, LamiaConvertInfo lamiaConvertInfo) {
        List<Statement> result = new ArrayList<>();
        expressionBuilders.forEach(expressionBuilder -> {
            List<Statement> jcStatements = expressionBuilder.build();
            result.addAll(jcStatements);
        });

        // 如果是return 后面直接接上的 转换语句,那么 还需要把return 语句补上
        if (lamiaConvertInfo.isReturn()) {
            String newInstantName = assembleHandler.getNewInstantName();
            Statement aReturn = treeFactory.createReturn(newInstantName);
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
        List<Material> highPriority = new ArrayList<>();

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
            highPriority.add(material);
        });
        result.addAll(highPriority);
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
        TypeResolver typeResolver = ComponentFactory.getInstanceComponent(this, TypeResolverFactory.class)
                .getTypeResolver(type);
        Map<String, Getter> instantGetters = typeResolver.getInstantGetters();

        List<Material> result = new ArrayList<>();

        RuleInfo ruleInfo = convertVarInfo.getRuleInfo();
        instantGetters.forEach((fieldName, getter) -> {
            if (ruleInfo != null && ruleInfo.isIgnoreField(type.getClassPath(), fieldName)) {
                return;
            }
            Material material = new Material();
            material.setVarDefinition(varDefinition);
            material.setSupplyType(getter.getType());
            material.setSupplyName(fieldName);
            material.setRuleInfo(convertVarInfo.getRuleInfo());
            // 生成对应的 var.getXX()
            material.setVarExpressionFunction((varExpression -> {
                Statement statement = treeFactory.execMethod(varExpression, getter.getMethodName(), Lists.of());
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
            result = new ValueObjAssembleHandler(this, targetType, lamiaConvertInfo.getTarget());
        }
        result.setLamiaConvertInfo(lamiaConvertInfo);
        return result;

    }
}
