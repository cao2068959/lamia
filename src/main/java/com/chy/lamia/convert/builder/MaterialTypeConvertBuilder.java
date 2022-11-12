package com.chy.lamia.convert.builder;


import com.chy.lamia.convert.assemble.Material;
import com.chy.lamia.entity.ExpressionWrapper;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 类型转换生成器, 把 material 对象转成适合的类型(如果需要) 并生成对应的转换语句,以及返回转换后的 对象名
 * 如: material--> A a , target---> B, 会生成表达式 B b --> a.getB()
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

        // 把 material 转成真正执行表达式, 也就是拿到 material.getA() 这样的表达式
        JCTree.JCExpression expression = material.getVarExpressionFunction().run(materialExpression);

        // 获取到 get() 表达式之后, 需要把这个表达式放入到 set(get()) 中去, 但是可能存在 两者类型不匹配的问题,需要转换
        return resultTypeHandle(expression, result);
    }

    /**
     * 对转换出来的结果进行类型转换
     *
     * @param materialConvertExpression get() 出来的表达式
     * @param result                    返回结果
     * @return
     */
    private ConvertResult resultTypeHandle(JCTree.JCExpression materialConvertExpression, ConvertResult result) {
        // set表达式需要的类型
        TypeDefinition targetType = this.targetType;
        // get表达式 返回的类型
        TypeDefinition supplyType = material.getSupplyType();

        // 类型匹配直接返回即可
        if (targetType.matchType(supplyType, true)) {
            result.setVarExpression(materialConvertExpression);
            return result;
        }
        // 转换2个的类型
        ExpressionWrapper varExpression = new BoxingExpressionBuilder(supplyType, materialConvertExpression, targetType).getVarExpression();
        // 两个类型不是 泛型的父子关系, 直接强转
        if (varExpression == null) {
            // 强转
            result.setVarExpression(JCUtils.instance.typeCast(targetType.getClassPath(), materialConvertExpression).getExpression());
            return result;
        }
        result.addConvertStatement(varExpression.getBefore());
        result.setVarExpression(result.getVarExpression());
        return result;
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
