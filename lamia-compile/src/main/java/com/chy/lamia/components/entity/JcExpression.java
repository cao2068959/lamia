package com.chy.lamia.components.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.utils.struct.Pair;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import lombok.Setter;

public class JcExpression implements Expression {

    JCTree.JCExpression jcExpression;
    @Setter
    JCTree contextTree;


    public JcExpression(JCTree.JCExpression jcExpression) {
        this.jcExpression = jcExpression;
    }

    public JcExpression() {
    }

    @Override
    public Object get() {
        return jcExpression;
    }

    @Override
    public Pair<String, String> parseMethodReferenceOperator() {
        if (!(jcExpression instanceof JCTree.JCMemberReference)) {
            throw new RuntimeException("方法: parseMethodReferenceOperator 不支持类型[" + jcExpression.getClass().getName() + "]");
        }
        JCTree.JCMemberReference jcMemberReference = (JCTree.JCMemberReference) jcExpression;
        JCTree.JCExpression expr = jcMemberReference.expr;
        if (expr == null) {
            throw new RuntimeException("方法引用操作符的类名不能为空");
        }
        Type type = expr.type;
        if (type == null && contextTree != null) {
            type = JCUtils.instance.attribType(contextTree, expr.toString());
        }

        if (type == null) {
            throw new RuntimeException("无法解析类型[" + type + "]");
        }
        return new Pair<>(type.toString(), jcMemberReference.name.toString());

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
