package com.chy.lamia.convert.builder;

import com.sun.tools.javac.tree.JCTree;

/**
 * 变量表达式生成式
 *
 * @author bignosecat
 */
public interface VarExpressionFunction {

    /**
     * 根据变量名称生成对应的表达式
     * @param varName 变量名称
     * @return 表达式
     */
    JCTree.JCExpression run(String varName);

}
