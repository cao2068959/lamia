package com.chy.lamia.convert.core.expression.parse.builder.handler.rule;

import com.chy.lamia.convert.core.components.ComponentFactory;
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
            expression

        });
        System.out.println("IgnoreField");

    }

    private Pair<String,String> parseLambda(Expression expression) {
       if (expression == null) {
           return null;
       }
        ComponentFactory.
    }

}
