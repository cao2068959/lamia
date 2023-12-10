package com.chy.lamia.convert.core.expression.parse.builder.handler.rule;

import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.expression.parse.ConfigParseContext;
import com.chy.lamia.convert.core.expression.parse.entity.MethodWrapper;
import com.chy.lamia.convert.core.expression.parse.builder.BuilderHandler;

import java.util.List;

/**
 * 扩散参数自定义的配置处理器
 *
 * @author bignosecat
 */
public class RuleSetFieldHandler implements BuilderHandler {
    @Override
    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context) {
        List<String> argsName = methodWrapper.useAllArgsToName();

        lamiaExpression.addArgs(argsName, lamiaExpression.getRuleInfos());

        context.outScope("rule");
    }

}
