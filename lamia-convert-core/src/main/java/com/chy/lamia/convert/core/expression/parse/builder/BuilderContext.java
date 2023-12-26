package com.chy.lamia.convert.core.expression.parse.builder;


import com.chy.lamia.convert.core.expression.parse.builder.handler.BuilderEnd;
import com.chy.lamia.convert.core.expression.parse.builder.handler.BuilderStart;
import com.chy.lamia.convert.core.expression.parse.builder.handler.MappingHandler;
import com.chy.lamia.convert.core.expression.parse.builder.handler.SetFieldHandler;
import com.chy.lamia.convert.core.expression.parse.builder.handler.rule.RuleHandler;
import com.chy.lamia.convert.core.expression.parse.builder.handler.rule.RuleMappingHandler;
import com.chy.lamia.convert.core.expression.parse.builder.handler.rule.RuleSetFieldHandler;

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
        data.put("builder.build", new BuilderEnd());
        data.put("builder.mapping", new MappingHandler());
        data.put("builder.setField", new SetFieldHandler());
        data.put("builder.rule", new RuleHandler());
        data.put("builder.rule.mapping", new RuleMappingHandler());
        data.put("builder.rule.setField", new RuleSetFieldHandler());

    }


    public static BuilderHandler getHandler(String key) {
        return data.get(key);
    }

}
