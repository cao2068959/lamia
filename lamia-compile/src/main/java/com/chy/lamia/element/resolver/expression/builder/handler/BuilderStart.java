package com.chy.lamia.element.resolver.expression.builder.handler;

import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.element.resolver.expression.ConfigParseContext;
import com.chy.lamia.element.resolver.expression.MethodWrapper;
import com.chy.lamia.element.resolver.expression.builder.BuilderHandler;


/**
 * 配置开始
 *
 * @author bignosecat
 */
public class BuilderStart implements BuilderHandler {

    @Override
    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context) {
        context.intoScope("builder");
    }
}
