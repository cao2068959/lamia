package com.chy.lamia.convert.core.expression.builder;


import com.chy.lamia.convert.assemble.Material;
import com.chy.lamia.convert.builder.rule.RuleHandlerContext;
import com.chy.lamia.convert.builder.rule.handler.RuleChain;
import com.chy.lamia.convert.core.utils.CommonUtils;
import com.chy.lamia.element.resolver.expression.RuleInfo;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;
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

    public MaterialTypeConvertBuilder(Material material, TypeDefinition targetType) {
        this.material = material;
        this.targetType = targetType;
    }

    /**
     * 将这个类型 转换成 指定的类型
     */
    public ConvertResult convert(Function<JCTree.JCExpression, List<JCTree.JCStatement>> callBack) {

        VarDefinition materialVarDefinition = material.getVarDefinition();
        JCTree.JCExpression materialExpression = JCUtils.instance.memberAccess(materialVarDefinition.getVarRealName());
        // 把 material 转成真正执行表达式, 也就是拿到 material.getA() 这样的表达式
        JCTree.JCExpression expression = material.getVarExpressionFunction().run(materialExpression);

        return ruleConvert(expression, callBack);
    }


    /**
     * 将这个类型 转换成 指定的类型
     */
    public JCTree.JCExpression convertSimple() {

        VarDefinition materialVarDefinition = material.getVarDefinition();
        JCTree.JCExpression materialExpression = JCUtils.instance.memberAccess(materialVarDefinition.getVarRealName());
        return material.getVarExpressionFunction().run(materialExpression);
    }

    /**
     * 根据固定的规则进行转换
     *
     * @param expression
     * @param callBack
     */
    private ConvertResult ruleConvert(JCTree.JCExpression expression,
                                      Function<JCTree.JCExpression, List<JCTree.JCStatement>> callBack) {
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
                JCTree.JCVariableDecl var = JCUtils.instance.createVar(name, type.getClassPath(), expression);
                chain.addStatement(var);

                // 继续下面的执行
                chain.continueCall(JCUtils.instance.memberAccess(name));
            }));
        }

        // 添加一个规则，用来给外面生成真正的赋值语句
        ruleChain.addRule((varExpression, chain) -> {
            List<JCTree.JCStatement> allStatement = callBack.apply(varExpression);
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


}
