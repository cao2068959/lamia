package com.chy.lamia.entity;

import com.sun.tools.javac.tree.JCTree;


public class Expression {

    JCTree.JCExpression expression;

    //可能需要对表达式进行一些处理
    ExpressionHandleFun expressionHandle;

    public Expression(JCTree.JCExpression expression, ExpressionHandleFun expressionHandle) {
        this.expression = expression;
        this.expressionHandle = expressionHandle;

    }

    public Expression(JCTree.JCExpression expression) {
        this.expression = expression;
    }

    /**
     * 通过类型, 可能会需要对表达式做一些转换
     *
     * @param parameterType
     * @return
     */
    public JCTree.JCExpression getExpressionByHandle(ParameterType parameterType) {
        if (expressionHandle == null) {
            return expression;
        }
        return expressionHandle.run(expression, parameterType);
    }

    public JCTree.JCExpression getExpression() {
        return expression;
    }


    public interface ExpressionHandleFun {
        JCTree.JCExpression run(JCTree.JCExpression expression, ParameterType parameterType);
    }

}
