package com.chy.lamia.convert.core.expression.parse.builder.handler.rule;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.entity.RuleInfo;
import com.chy.lamia.convert.core.expression.parse.ConfigParseContext;
import com.chy.lamia.convert.core.expression.parse.builder.BuilderHandler;
import com.chy.lamia.convert.core.expression.parse.entity.ArgWrapper;
import com.chy.lamia.convert.core.expression.parse.entity.MethodWrapper;
import com.chy.lamia.convert.core.utils.struct.Pair;

import java.util.List;

public class IgnoreField implements BuilderHandler {

    @Override
    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper,
                       ConfigParseContext context) {

        List<ArgWrapper> allArgs = methodWrapper.useAllArgs();
        if (allArgs == null || allArgs.isEmpty()) {
            return;
        }
        RuleInfo ruleInfos = lamiaExpression.getRuleInfos();
        allArgs.forEach(arg -> {
            Expression expression = arg.getExpression();
            Pair<String, String> typeAndVar = parseLambda(expression);
            ruleInfos.addIgnoreField(typeAndVar.getLeft(), typeAndVar.getRight());
        });
    }

    private Pair<String, String> parseLambda(Expression expression) {
        if (expression == null) {
            return null;
        }
        Pair<String, String> result = expression.parseMethodReferenceOperator();
        // 这里获取到的value是 方法名称如: getName , 这边把他转换 name，如果不是 get 开头的话，就直接返回
        String value = result.getRight();
        value = getterToVar(value);
        result.setRight(value);
        return result;
    }

    private String getterToVar(String value) {
        if (value.startsWith("get")) {
            return value.substring(3, 4).toLowerCase() + value.substring(4);
        }
        return value;
    }


}
