package com.chy.lamia.element.resolver.expression.configuration;

import com.chy.lamia.element.resolver.expression.ConfigParseContext;
import com.chy.lamia.element.resolver.expression.LamiaExpression;
import com.chy.lamia.element.resolver.expression.MethodWrapper;

/**
 * 配置项处理器的基础接口
 *
 * @author bignosecat
 */
public interface ConfigurationHandler {

    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context);

}
