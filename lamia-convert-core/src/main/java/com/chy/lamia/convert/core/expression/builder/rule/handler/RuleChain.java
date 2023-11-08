package com.chy.lamia.convert.core.expression.builder.rule.handler;

import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class RuleChain {

    @Getter
    List<JCTree.JCStatement> result = new ArrayList<>();

    List<IRuleHandler> allHandler = new ArrayList<>();

    int index = 0;


    public RuleChain(List<IRuleHandler> allHandler) {
        this.allHandler = allHandler;
    }


    public void addStatement(JCTree.JCStatement statement) {
        result.add(statement);
    }

    public void addStatement(List<JCTree.JCStatement> statement) {
        result.addAll(statement);
    }


    public List<JCTree.JCStatement> continueCallAndReturn(JCTree.JCExpression var) {
        RuleChain copy = copy();
        copy.continueCall(var);

        this.index = copy.index;

        return copy.result;
    }

    public void continueCall(JCTree.JCExpression var) {
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
