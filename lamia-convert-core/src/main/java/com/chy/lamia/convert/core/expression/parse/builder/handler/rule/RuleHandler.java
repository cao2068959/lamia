package com.chy.lamia.convert.core.expression.parse.builder.handler.rule;

import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.entity.RuleInfo;
import com.chy.lamia.convert.core.expression.parse.ConfigParseContext;
import com.chy.lamia.convert.core.expression.parse.builder.BuilderHandler;
import com.chy.lamia.convert.core.expression.parse.entity.ArgWrapper;
import com.chy.lamia.convert.core.expression.parse.entity.MethodWrapper;
import com.chy.lamia.convert.core.expression.parse.entity.RuleTypeArgWrapper;
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
    public void config(LamiaExpression lamiaExpression,
                       MethodWrapper methodWrapper,
                       ConfigParseContext context) {
        context.intoScope("rule");

        List<ArgWrapper> argWrappers = methodWrapper.useAllArgs();
        List<RuleType> ruleTypes = fetchRuleType(argWrappers);

        RuleInfo ruleInfo = new RuleInfo();
        ruleInfo.setRuleTypes(ruleTypes);
        lamiaExpression.getBuildInfo().setRuleInfo(ruleInfo);
    }


    public List<RuleType> fetchRuleType(List<ArgWrapper> args) {
        if (args == null || args.isEmpty()) {
            return null;
        }

        List<RuleType> result = new ArrayList<>();
        for (ArgWrapper arg : args) {
            if (arg instanceof RuleTypeArgWrapper) {
                RuleTypeArgWrapper ruleTypeArgWrapper = (RuleTypeArgWrapper) arg;
                result.add(ruleTypeArgWrapper.getRuleType());
                continue;
            }

            String ruleTypeName = getLastName(arg.getName());
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
