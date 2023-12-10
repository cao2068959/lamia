package com.chy.lamia.convert.core.expression.parse.builder.handler;

import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.expression.parse.ConfigParseContext;
import com.chy.lamia.convert.core.expression.parse.entity.MethodWrapper;
import com.chy.lamia.convert.core.expression.parse.builder.BuilderHandler;


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
