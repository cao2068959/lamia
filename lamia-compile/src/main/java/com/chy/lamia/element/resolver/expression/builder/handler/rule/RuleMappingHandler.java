package com.chy.lamia.element.resolver.expression.builder.handler.rule;

import com.chy.lamia.element.resolver.expression.ConfigParseContext;
import com.chy.lamia.element.resolver.expression.LamiaExpression;
import com.chy.lamia.element.resolver.expression.MethodWrapper;
import com.chy.lamia.element.resolver.expression.builder.BuilderHandler;

import java.util.List;

/**
 * 扩散参数自定义的配置处理器
 *
 * @author bignosecat
 */
public class RuleMappingHandler implements BuilderHandler {
    @Override
    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context) {

        List<String> argsName = fetchArgsName(methodWrapper.getArgs());
        lamiaExpression.addSpreadArgs(argsName, lamiaExpression.getRuleInfos());
        context.outScope("rule");
    }


}
