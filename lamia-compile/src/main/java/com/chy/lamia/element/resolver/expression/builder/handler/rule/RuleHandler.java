package com.chy.lamia.element.resolver.expression.builder.handler.rule;

import com.chy.lamia.element.resolver.expression.ConfigParseContext;
import com.chy.lamia.element.resolver.expression.MethodWrapper;
import com.chy.lamia.element.resolver.expression.builder.BuilderHandler;
import com.chy.lamia.expose.rule.RuleType;
import com.sun.tools.javac.tree.JCTree;

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


    public List<RuleType> fetchRuleType(List<JCTree.JCExpression> args) {
        List<RuleType> result = new ArrayList<>();
        for (JCTree.JCExpression arg : args) {
            String argName = arg.toString();
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
