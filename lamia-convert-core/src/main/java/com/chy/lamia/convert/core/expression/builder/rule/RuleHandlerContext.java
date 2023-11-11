package com.chy.lamia.convert.core.expression.builder.rule;


import com.chy.lamia.convert.core.entity.RuleInfo;
import com.chy.lamia.convert.core.expression.builder.rule.handler.IRuleHandler;
import com.chy.lamia.convert.core.expression.builder.rule.handler.IgnoreNullRuleHandler;
import com.chy.lamia.convert.core.expression.builder.rule.handler.RuleChain;
import com.chy.lamia.expose.rule.RuleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bignosecat
 */
public class RuleHandlerContext {

    public static RuleHandlerContext INSTANCE = new RuleHandlerContext();

    private Map<RuleType, IRuleHandler> handlerMap = new HashMap<>();


    RuleHandlerContext() {
        handlerMap.put(RuleType.IGNORE_NULL, new IgnoreNullRuleHandler());
    }


    public RuleChain getRuleChain(RuleInfo ruleInfo) {
        if (ruleInfo == null) {
            return new RuleChain(new ArrayList<>());
        }

        List<RuleType> ruleTypes = ruleInfo.getRuleTypes();

        List<IRuleHandler> ruleHandlers = new ArrayList<>();
        if (ruleTypes != null) {
            ruleTypes.forEach(value -> {
                IRuleHandler iRuleHandler = handlerMap.get(value);
                if (iRuleHandler == null) {
                    throw new RuntimeException("无效的 rule:[" + value + "]");
                }
                ruleHandlers.add(iRuleHandler);
            });
        }

        return new RuleChain(ruleHandlers);
    }
}
