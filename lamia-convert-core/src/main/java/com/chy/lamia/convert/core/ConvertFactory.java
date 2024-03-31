package com.chy.lamia.convert.core;


import com.chy.lamia.convert.core.assemble.*;
import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.TypeResolver;
import com.chy.lamia.convert.core.components.TypeResolverFactory;
import com.chy.lamia.convert.core.components.entity.NewlyStatementHolder;
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
    public List<NewlyStatementHolder> make(LamiaConvertInfo lamiaConvertInfo) {
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
    private List<NewlyStatementHolder> createdStatement(List<MaterialStatementBuilder> expressionBuilders, AssembleHandler assembleHandler, LamiaConvertInfo lamiaConvertInfo) {
        List<NewlyStatementHolder> result = new ArrayList<>();
        expressionBuilders.forEach(expressionBuilder -> {
            List<NewlyStatementHolder> jcStatements = expressionBuilder.build();
            result.addAll(jcStatements);
        });

        // 如果是return 后面直接接上的 转换语句,那么 还需要把return 语句补上
        if (lamiaConvertInfo.isReturn()) {
            String newInstantName = assembleHandler.getNewInstantName();
            Statement aReturn = treeFactory.createReturn(newInstantName);
            result.add(new NewlyStatementHolder(aReturn));
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

        List<Material> result = new ArrayList<>();
        List<Material> highPriority = new ArrayList<>();

        lamiaConvertInfo.getAllArgs().forEach((__, protoMaterialInfo) -> {
            // 判断这个参数是否需要扩散开
            if (protoMaterialInfo.isSpread()) {
                List<Material> materials = spreadVarDefinition(protoMaterialInfo);
                result.addAll(materials);
                return;
            }
            // 不需要扩散那 直接封装了
            Material material = Material.simpleMaterial(protoMaterialInfo);
            highPriority.add(material);
        });
        result.addAll(highPriority);
        return result;
    }

    /**
     * 扩散 ProtoMaterialInfo 转成对应的 Material
     *
     * @param  protoMaterialInfo
     * @return
     */
    private List<Material> spreadVarDefinition(ProtoMaterialInfo protoMaterialInfo) {
        TypeDefinition type = protoMaterialInfo.getMaterial().getType();
        // 如果扩散的是一个 map
        if (type.matchType(Map.class)) {
            Material material = new OmnipotentMaterial(protoMaterialInfo);
            return Lists.of(material);
        }

        // 是系统类型或者基础数据类型,不允许扩散
        if (type.isBaseTypeOrSystemType()) {
            throw new RuntimeException("参数 [" + protoMaterialInfo.getMaterial().getText() + "] 是系统类型不允许扩散, 可以使用 Lamia.setField(不扩散的类型) 方法来指定");
        }

        // 解析对应的类型
        TypeResolver typeResolver = ComponentFactory.getInstanceComponent(this, TypeResolverFactory.class)
                .getTypeResolver(type);
        Map<String, Getter> instantGetters = typeResolver.getInstantGetters();

        List<Material> result = new ArrayList<>();

        instantGetters.forEach((fieldName, getter) -> {
            if (protoMaterialInfo.getBuildInfo().isIgnoreField(type.getClassPath(), fieldName)) {
                return;
            }
            Material material = new Material(protoMaterialInfo);
            material.setSupplyType(getter.getType());
            material.setSupplyName(fieldName);
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
