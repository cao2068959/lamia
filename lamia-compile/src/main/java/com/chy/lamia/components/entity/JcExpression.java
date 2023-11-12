package com.chy.lamia.components.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.sun.tools.javac.tree.JCTree;

public class JcExpression implements Expression {

    JCTree.JCExpression jcExpression;

    public JcExpression(JCTree.JCExpression jcExpression) {
        this.jcExpression = jcExpression;
    }

    public JcExpression() {
    }

    @Override
    public Object get() {
        return jcExpression;
    }

    public JCTree.JCExpression getByType() {
        return jcExpression;
    }

    public static JCTree.JCExpression get(Expression jcExpression) {
        if (!(jcExpression instanceof JcExpression)) {
            throw new RuntimeException("不支持类型[" + jcExpression.getClass().getName() + "]");
        }
        return (JCTree.JCExpression) jcExpression.get();
    }
}
