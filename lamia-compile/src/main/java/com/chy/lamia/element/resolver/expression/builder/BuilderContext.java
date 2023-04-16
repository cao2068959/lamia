package com.chy.lamia.element.resolver.expression.builder;

import com.chy.lamia.element.resolver.expression.builder.handler.BuilderStart;
import com.chy.lamia.element.resolver.expression.builder.handler.ConvertHandler;
import com.chy.lamia.element.resolver.expression.builder.handler.SetArgsHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置项上下文, 所有的配置处理器的实例都是他来维护
 *
 * @author bignosecat
 */
public class BuilderContext {

    static Map<String, BuilderHandler> data = new HashMap<>();

    static {
        data.put("builder", new BuilderStart());
        data.put("builder.convert", new ConvertHandler());
        data.put("builder.setArgs", new SetArgsHandler());

    }


    public static BuilderHandler getHandler(String key) {
        return data.get(key);
    }

}
