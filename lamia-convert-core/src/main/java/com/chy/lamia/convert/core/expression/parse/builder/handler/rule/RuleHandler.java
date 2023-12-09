package com.chy.lamia.convert.core.expression.parse.builder.handler.rule;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.entity.RuleInfo;
import com.chy.lamia.convert.core.expression.parse.ConfigParseContext;
import com.chy.lamia.convert.core.expression.parse.MethodWrapper;
import com.chy.lamia.convert.core.expression.parse.builder.BuilderHandler;
import com.chy.lamia.expose.rule.RuleType;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩散参数自定义的配置处理器
 *
 * @author bignosecat
 */
public class RuleHandler implements BuilderHandler {
    @Override
    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context) {
        context.intoScope("rule");
        List<RuleType> ruleTypes = fetchRuleType(methodWrapper.getArgs());

        RuleInfo ruleInfo = new RuleInfo();
        ruleInfo.setRuleTypes(ruleTypes);


        lamiaExpression.setRuleInfos(ruleInfo);
    }


    public List<RuleType> fetchRuleType(List<Expression> args) {
        List<RuleType> result = new ArrayList<>();
        for (Expression arg : args) {
            String argName = String.valueOf(arg.get());
            String ruleTypeName = getLastName(argName);
            result.add(RuleType.valueOf(ruleTypeName));
        }
        return result;
    }

    private String getLastName(String name) {
        if (!name.contains(".")) {
            return name;
        }
        return name.substring(name.lastIndexOf(".") + 1);
    }

}
