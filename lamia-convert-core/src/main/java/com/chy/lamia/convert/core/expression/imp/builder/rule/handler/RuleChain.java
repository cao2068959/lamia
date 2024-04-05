package com.chy.lamia.convert.core.expression.imp.builder.rule.handler;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.NewlyStatementHolder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class RuleChain {

    @Getter
    List<NewlyStatementHolder> result = new ArrayList<>();

    List<IRuleHandler> allHandler = new ArrayList<>();

    int index = 0;

    @Getter
    boolean isReRefVar = false;

    public RuleChain() {
    }

    private RuleChain(List<IRuleHandler> allHandler) {
        this.allHandler = allHandler;
    }

    public void addStatement(NewlyStatementHolder statement) {
        result.add(statement);
    }

    public void addStatement(List<NewlyStatementHolder> statement) {
        result.addAll(statement);
    }


    public List<NewlyStatementHolder> continueCallAndReturn(Expression var) {
        RuleChain copy = copy();
        copy.continueCall(var);

        this.index = copy.index;

        return copy.result;
    }

    public void continueCall(Expression var) {
        // 这个调用链到头了
        if (index >= allHandler.size()) {
            return;
        }
        IRuleHandler ruleHandler = allHandler.get(index++);
        ruleHandler.run(var, this);
    }

    public RuleChain copy() {
        RuleChain result = new RuleChain(allHandler);
        result.index = index;
        result.isReRefVar = isReRefVar;
        return result;
    }


    public int getCurrentDataIndex() {
        return result.size();
    }

    public void addFirstRule(IRuleHandler ruleHandler) {
        addRuleHandle(ruleHandler);
        allHandler.add(0, ruleHandler);
    }

    public void addRule(IRuleHandler ruleHandler) {
        addRuleHandle(ruleHandler);
        allHandler.add(ruleHandler);
    }

    private void addRuleHandle(IRuleHandler ruleHandler) {
        if (ruleHandler.isReRefVar()) {
            isReRefVar = true;
        }
    }

    public int size() {
        return allHandler.size();
    }
}
