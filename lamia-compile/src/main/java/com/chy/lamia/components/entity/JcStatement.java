package com.chy.lamia.components.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.sun.tools.javac.tree.JCTree;

public class JcStatement implements Statement {

    JCTree.JCStatement data;

    public JcStatement(JCTree.JCStatement data) {
        this.data = data;
    }

    public JcStatement() {
    }

    @Override
    public Object get() {
        return data;
    }

    @Override
    public Expression getExpression() {
        if (data instanceof JCTree.JCExpressionStatement) {
            JCTree.JCExpressionStatement expressionStatement = (JCTree.JCExpressionStatement) data;
            JCTree.JCExpression expr = expressionStatement.expr;
            if (expr != null) {
                return new JcExpression(expr);
            }
        }
        return null;
    }

    public static JCTree.JCStatement get(Statement statement) {
        if (!(statement instanceof JcStatement)) {
            throw new RuntimeException("不支持类型[" + statement.getClass().getName() + "]");
        }
        return (JCTree.JCStatement) statement.get();
    }
}
