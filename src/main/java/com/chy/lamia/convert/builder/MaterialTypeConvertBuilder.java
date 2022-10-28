package com.chy.lamia.convert.builder;


import com.chy.lamia.convert.assemble.Material;
import com.chy.lamia.entity.ExpressionWrapper;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.struct.Pair;
import com.sun.tools.javac.tree.JCTree;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 类型转换生成器, 把 material 对象转成适合的类型(如果需要) 并生成对应的转换语句,以及返回转换后的 对象名
 *
 * @author bignosecat
 */
@Data
public class MaterialTypeConvertBuilder {

    Material material;
    TypeDefinition targetType;

    public MaterialTypeConvertBuilder(Material material, TypeDefinition targetType) {
        this.material = material;
        this.targetType = targetType;
    }

    /**
     * 将这个类型 转换成 指定的类型
     */
    public ConvertResult convert() {
        ConvertResult result = new ConvertResult();
        VarDefinition materialVarDefinition = material.getVarDefinition();

        // 变量真正提供的类型
        TypeDefinition realType = materialVarDefinition.getType();
        // 执行的时候 get方法所在对象的类型
        TypeDefinition execType = material.getExecType();


        JCTree.JCExpression materialExpression;
        // 如果提供的类型和实际参与转换的类型不同，则转换类型
        if (!realType.matchType(execType, true)) {
            // 将变量转换成对应的类型, 只有存在 泛型关系的类型才能转换
            ExpressionWrapper expressionWrapper = materialVarDefinition.convert(execType);
            result.addConvertStatement(expressionWrapper.getBefore());
            materialExpression = expressionWrapper.getExpression();
        } else {
            materialExpression = JCUtils.instance.memberAccess(materialVarDefinition.getVarRealName());
        }


        // set表达式需要的类型
        TypeDefinition targetType = this.targetType;
        // get表达式 返回的类型
        TypeDefinition supplyType = material.getSupplyType();


        // 需要的类型和转换材料提供的材料是否相同, 如果不相同则需要 转相同
        if (!targetType.matchType(supplyType, true)) {
            // 解包匹配到合适的 类型
            Pair<TypeDefinition, TypeDefinition> unPackagePair = TypeDefinitionFactory.unPackageMatch(execType, targetType);
            if (unPackagePair == null) {
                throw new RuntimeException(" 变量[" + material.getVarDefinition() + "] 参与转换失败 类型 [" + execType + "] 无法转换成 [" + targetType + "]");
            }
            execType = unPackagePair.getLeft();
            targetType = unPackagePair.getRight();
        }
    }

    @Data
    public static class ConvertResult {
        List<JCTree.JCStatement> convertStatement = new ArrayList<>();
        JCTree.JCExpression varExpression;

        public void addConvertStatement(List<JCTree.JCStatement> statements) {
            convertStatement.addAll(statements);
        }
    }


}
