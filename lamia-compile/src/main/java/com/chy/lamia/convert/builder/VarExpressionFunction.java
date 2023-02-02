package com.chy.lamia.convert.builder;

import com.sun.tools.javac.tree.JCTree;

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
    JCTree.JCExpression run(JCTree.JCExpression expression);

}
