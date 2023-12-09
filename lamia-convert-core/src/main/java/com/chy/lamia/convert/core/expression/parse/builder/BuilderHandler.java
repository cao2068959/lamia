package com.chy.lamia.convert.core.expression.parse.builder;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.expression.parse.ConfigParseContext;
import com.chy.lamia.convert.core.expression.parse.MethodWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置项处理器的基础接口
 *
 * @author bignosecat
 */
public interface BuilderHandler {

    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context);


    default List<String> fetchArgsName(List<Expression> args) {
        List<String> result = new ArrayList<>();
        for (Expression arg : args) {
            result.add(arg.get().toString());
        }
        return result;
    }
}
