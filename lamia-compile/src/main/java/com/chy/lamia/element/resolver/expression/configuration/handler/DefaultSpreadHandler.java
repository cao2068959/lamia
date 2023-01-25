package com.chy.lamia.element.resolver.expression.configuration.handler;

import com.chy.lamia.element.resolver.expression.ConfigParseContext;
import com.chy.lamia.element.resolver.expression.LamiaExpression;
import com.chy.lamia.element.resolver.expression.MethodWrapper;
import com.chy.lamia.element.resolver.expression.configuration.ConfigurationHandler;

/**
 * @author bignosecat
 */
public class DefaultSpreadHandler implements ConfigurationHandler {

    @Override
    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context) {

        Boolean isDefaultSpread = getBaseTypeArgs(methodWrapper, 0, Boolean.class);
        lamiaExpression.setDefaultSpread(isDefaultSpread);
    }




}
