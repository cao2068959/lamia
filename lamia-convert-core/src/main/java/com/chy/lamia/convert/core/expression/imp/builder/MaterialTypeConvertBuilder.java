package com.chy.lamia.convert.core.expression.imp.builder;


import com.chy.lamia.convert.core.assemble.Material;
import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.chy.lamia.convert.core.entity.RuleInfo;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.entity.VarDefinition;
import com.chy.lamia.convert.core.expression.imp.builder.rule.RuleHandlerContext;
import com.chy.lamia.convert.core.expression.imp.builder.rule.handler.RuleChain;
import com.chy.lamia.convert.core.utils.CommonUtils;
import lombok.Data;

import java.util.List;
import java.util.function.Function;


/**
 * 类型转换生成器, 把 material 对象转成适合的类型(如果需要) 并生成对应的转换语句,以及返回转换后的 对象名
 * 如: material--> A a , target---> B, 会生成表达式 B b = a.getB()
 *
 * @author bignosecat
 */
@Data
public class MaterialTypeConvertBuilder {

    Material material;
    TypeDefinition targetType;

    TreeFactory treeFactory;

    public MaterialTypeConvertBuilder(Material material, TypeDefinition targetType) {
        treeFactory = ComponentFactory.getComponent(TreeFactory.class);
        this.material = material;
        this.targetType = targetType;
    }

    /**
     * 将这个类型 转换成 指定的类型
     */
    public ConvertResult convert(Function<Expression, List<Statement>> callBack) {
        VarDefinition materialVarDefinition = material.getVarDefinition();

        Expression materialExpression = treeFactory.toExpression(materialVarDefinition.getVarRealName());
        // 把 material 转成真正执行表达式, 也就是拿到 material.getA() 这样的表达式
        Expression expression = material.getVarExpressionFunction().run(materialExpression);

        return ruleConvert(expression, callBack);
    }



    /**
     * 根据固定的规则进行转换
     *
     * @param expression
     * @param callBack
     */
    private ConvertResult ruleConvert(Expression expression,
                                      Function<Expression, List<Statement>> callBack) {
        RuleHandlerContext ruleHandlerContext = RuleHandlerContext.INSTANCE;


        RuleInfo ruleInfo = material.getRuleInfo();
        RuleChain ruleChain = ruleHandlerContext.getRuleChain(ruleInfo);

        boolean noRule = true;
        // 没有任何的规则要处理
        if (ruleChain.size() > 0) {
            noRule = false;
            // 里面有规则处理，用一个变量承接一下
            ruleChain.addFirstRule(((__, chain) -> {
                String name = CommonUtils.tempName(material.getSupplyName());
                TypeDefinition type = material.getSupplyType();
                // 先生成一个变量承接一下

                Statement var = treeFactory.createVar(name, type.getClassPath(), expression);
                chain.addStatement(var);

                // 继续下面的执行
                chain.continueCall(treeFactory.toExpression(name));
            }));
        }

        // 添加一个规则，用来给外面生成真正的赋值语句
        ruleChain.addRule((varExpression, chain) -> {
            List<Statement> allStatement = callBack.apply(varExpression);
            chain.addStatement(allStatement);
            chain.continueCall(varExpression);
        });

        if (noRule) {
            ruleChain.continueCall(expression);
        } else {
            // 开始执行规则链路
            ruleChain.continueCall(null);
        }


        return new ConvertResult(ruleChain.getResult());
    }

    /**
     * 将这个类型 转换成 指定的类型
     */
    public Expression convertSimple() {

        VarDefinition materialVarDefinition = material.getVarDefinition();
        Expression materialExpression = treeFactory.toExpression(materialVarDefinition.getVarRealName());
        return material.getVarExpressionFunction().run(materialExpression);
    }

}
