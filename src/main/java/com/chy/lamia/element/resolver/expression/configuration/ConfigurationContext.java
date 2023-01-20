package com.chy.lamia.element.resolver.expression.configuration;

import com.chy.lamia.element.resolver.expression.configuration.handler.ConfigurationStart;
import com.chy.lamia.element.resolver.expression.configuration.handler.DefaultSpreadHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置项上下文, 所有的配置处理器的实例都是他来维护
 *
 * @author bignosecat
 */
public class ConfigurationContext {

    static Map<String, ConfigurationHandler> data = new HashMap<>();

    static {
        data.put("config", new ConfigurationStart());
        data.put("config.defaultSpread", new DefaultSpreadHandler());

    }


    public static ConfigurationHandler getHandler(String key) {
        return data.get(key);
    }

}
