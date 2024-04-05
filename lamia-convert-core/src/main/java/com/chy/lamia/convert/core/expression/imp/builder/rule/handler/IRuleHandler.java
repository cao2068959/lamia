package com.chy.lamia.convert.core.expression.imp.builder.rule.handler;


import com.chy.lamia.convert.core.components.entity.Expression;

/**
 * @author bignosecat
 */
public interface IRuleHandler {

    void run(Expression varExpression, RuleChain chain);

    /**
     * 是否是一个重新引用的变量
     *
     */
    default boolean isReRefVar() {
        return false;
    }
}
