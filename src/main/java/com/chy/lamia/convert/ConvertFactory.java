package com.chy.lamia.convert;

import com.chy.lamia.convert.assemble.*;
import com.chy.lamia.convert.builder.MaterialExpressionBuilder;
import com.chy.lamia.element.LamiaConvertInfo;
import com.chy.lamia.element.resolver.TypeResolver;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
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
     */
    public void make(LamiaConvertInfo lamiaConvertInfo) {

        // 可能存在包装类型,把包装类型解包 如: Optional<A> ---> A
        TypeDefinition targetType = TypeDefinitionFactory.unPackage(lamiaConvertInfo.getTargetType());
        // 寻找适合的组成器
        AssembleHandler assembleHandler = getAssembleHandler(targetType);
        // 获取所有可能参与组合结果对象的材料
        List<Material> materials = createdMaterials(lamiaConvertInfo);
        // 在组装器中添加所有的材料
        assembleHandler.addMaterial(materials);
        // 生成所有组装对象 语句的构建器, 每一个builder 将会生成一行 语句, 如 result.setXXX(var.getVVV());
        List<MaterialExpressionBuilder> materialExpressionBuilders = assembleHandler.run();
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
            if (varDefinition.isSpread()) {
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
        // 如果这个类型是有包装的类型
        TypeDefinition typeDefinition = TypeDefinitionFactory.unPackage(type);
        // 解析对应的类型
        TypeResolver typeResolver = TypeResolver.getTypeResolver(typeDefinition);
        Map<String, Getter> instantGetters = typeResolver.getInstantGetters();

        List<Material> result = new ArrayList<>();

        instantGetters.forEach((fieldName, getter) -> {
            Material material = new Material();
            material.setVarDefinition(varDefinition);
            material.setSupplyType(typeDefinition);
            material.setSupplyName(fieldName);
            // 生成对应的 var.getXX()
            material.setExpression((varName -> {
                JCTree.JCExpressionStatement statement = JCUtils.instance.execMethod(varName, getter.getMethodName(), Lists.of());
                return statement.getExpression();
            }));
            result.add(material);
        });
        return result;
    }


    /**
     * 获取一个合适的组装处理器
     *
     * @param targetType 要组装的对象
     * @return 组装器
     */
    private AssembleHandler getAssembleHandler(TypeDefinition targetType) {
        // 如果要组装的是 map, 则用map的组装器
        if (targetType.matchType(Map.class)) {
            return new MapAssembleHandler();
        }
        return new ValueObjAssembleHandler(targetType);

    }
}
