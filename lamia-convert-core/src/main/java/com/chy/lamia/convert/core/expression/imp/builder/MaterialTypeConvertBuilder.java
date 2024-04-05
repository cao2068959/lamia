package com.chy.lamia.convert.core.expression.imp.builder;


import com.chy.lamia.convert.core.assemble.Material;
import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.NameHandler;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.NewlyStatementHolder;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.chy.lamia.convert.core.entity.MethodParameterWrapper;
import com.chy.lamia.convert.core.entity.RuleInfo;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.expression.imp.builder.rule.RuleHandlerContext;
import com.chy.lamia.convert.core.expression.imp.builder.rule.handler.RuleChain;
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

    boolean typeMatch = true;

    public MaterialTypeConvertBuilder(Material material, TypeDefinition targetType) {
        treeFactory = ComponentFactory.getComponent(TreeFactory.class);
        this.material = material;
        this.targetType = targetType;
    }

    /**
     * 将这个类型 转换成 指定的类型
     */
    public ConvertResult convert(Function<Expression, List<NewlyStatementHolder>> callBack) {
        return ruleConvert(callBack);
    }


    /**
     * 根据固定的规则进行转换
     *
     * @param callBack
     */
    private ConvertResult ruleConvert(Function<Expression, List<NewlyStatementHolder>> callBack) {

        RuleHandlerContext ruleHandlerContext = RuleHandlerContext.INSTANCE;
        RuleInfo ruleInfo = material.getProtoMaterialInfo().getBuildInfo().getRuleInfo();
        RuleChain ruleChain = ruleHandlerContext.getRuleChain(ruleInfo);

        // 添加一个规则，用来给外面生成真正的赋值语句
        ruleChain.addRule((varExpression, chain) -> {
            List<NewlyStatementHolder> allStatement = callBack.apply(varExpression);
            chain.addStatement(allStatement);
            chain.continueCall(varExpression);
        });

        // 如果存在规则要处理，那么就先是用一个变量取承接再去执行后续的规则
        if (ruleChain.isReRefVar()) {
            // 里面有规则处理，用一个变量承接一下
            ruleChain.addFirstRule(((lastExpression, chain) -> {
                String name = ComponentFactory.getComponent(NameHandler.class).generateTempName(material.getSupplyName());
                TypeDefinition type = material.getSupplyType();
                // 先生成一个变量承接一下

                Statement var = treeFactory.createVar(name, type.getClassPath(), lastExpression);
                chain.addStatement(new NewlyStatementHolder(var));

                // 继续下面的执行
                chain.continueCall(treeFactory.toExpression(name));
            }));
        }

        // 添加一个规则，如果 material 中的是一个表达式那么给他生成对应的 赋值语句
        ruleChain.addFirstRule((__, chain) -> {
            MethodParameterWrapper parameterWrapper = material.getProtoMaterialInfo().getMaterial();
            String name = parameterWrapper.getName();
            // 这个材料没有name，那么说明可能是一个方法调用表达式，需要给他随机生成一个名字
            if (name == null) {
                if (!parameterWrapper.isMethodInvoke()) {
                    throw new RuntimeException("参数:[" + parameterWrapper.getText() + "] 中缺少对应的name, 并且不是方法调用表达式");
                }
                name = ComponentFactory.getComponent(NameHandler.class).generateTempName("m" + parameterWrapper.getType().simpleClassName());
                Statement statement = treeFactory.createVar(name, parameterWrapper.getType().getClassPath(), parameterWrapper.getMethodInvokeExpression());
                chain.addStatement(new NewlyStatementHolder(statement));
                parameterWrapper.setName(name);
            }

            Expression materialExpression = treeFactory.toExpression(name);
            // 把 material 转成真正执行表达式, 也就是拿到 material.getA() 这样的表达式
            Expression expression = material.getVarExpressionFunction().run(materialExpression);
            chain.continueCall(expression);
        });


        // 执行规则
        ruleChain.continueCall(null);
        return new ConvertResult(ruleChain.getResult());
    }

    /**
     * 将这个类型 转换成 指定的类型
     */
    public Expression convertSimple() {

        String name = material.getProtoMaterialInfo().getName();
        Expression materialExpression = treeFactory.toExpression(name);
        return material.getVarExpressionFunction().run(materialExpression);
    }

}
