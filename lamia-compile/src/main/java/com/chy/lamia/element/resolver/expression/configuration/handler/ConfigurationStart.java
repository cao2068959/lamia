package com.chy.lamia.element.resolver.expression.configuration.handler;

import com.chy.lamia.element.resolver.expression.ConfigParseContext;
import com.chy.lamia.element.resolver.expression.LamiaExpression;
import com.chy.lamia.element.resolver.expression.MethodWrapper;
import com.chy.lamia.element.resolver.expression.configuration.ConfigurationHandler;


/**
 * 配置开始
 *
 * @author bignosecat
 */
public class ConfigurationStart implements ConfigurationHandler {

    @Override
    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context) {
        context.intoScope("config");
    }
}
