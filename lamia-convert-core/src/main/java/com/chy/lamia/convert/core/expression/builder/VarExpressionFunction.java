package com.chy.lamia.convert.core.expression.builder;


import com.chy.lamia.convert.core.components.entity.Expression;

/**
 * 变量表达式生成式
 *
 * @author bignosecat
 */
public interface VarExpressionFunction {

    /**
     * 生成对应的转换表达式
     *
     * @param  expression 变量原本的表达式
     * @return 表达式
     */
    Expression run(Expression expression);

}
