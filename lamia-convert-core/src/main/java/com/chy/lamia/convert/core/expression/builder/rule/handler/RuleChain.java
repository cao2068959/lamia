package com.chy.lamia.convert.core.expression.builder.rule.handler;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class RuleChain {

    @Getter
    List<Statement> result = new ArrayList<>();

    List<IRuleHandler> allHandler = new ArrayList<>();

    int index = 0;


    public RuleChain(List<IRuleHandler> allHandler) {
        this.allHandler = allHandler;
    }


    public void addStatement(Statement statement) {
        result.add(statement);
    }

    public void addStatement(List<Statement> statement) {
        result.addAll(statement);
    }


    public List<Statement> continueCallAndReturn(Expression var) {
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
        return result;
    }


    public int getCurrentDataIndex() {
        return result.size();
    }

    public void addFirstRule(IRuleHandler ruleHandler) {
        allHandler.add(0, ruleHandler);
    }

    public void addRule(IRuleHandler ruleHandler) {
        allHandler.add(ruleHandler);
    }

    public int size() {
        return allHandler.size();
    }
}
