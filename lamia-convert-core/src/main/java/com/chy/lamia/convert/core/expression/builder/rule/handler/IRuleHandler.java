package com.chy.lamia.convert.core.expression.builder.rule.handler;

import com.sun.tools.javac.tree.JCTree;

/**
 * @author bignosecat
 */
public interface IRuleHandler {


    void run(JCTree.JCExpression varExpression, RuleChain chain);
}