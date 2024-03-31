package com.chy.lamia.components.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.entity.MethodParameterWrapper;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.utils.struct.Pair;
import com.chy.lamia.entity.ClassTreeWrapper;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import lombok.Setter;

public class JcExpression implements Expression {

    JCTree.JCExpression jcExpression;
    @Setter
    ClassTreeWrapper contextTree;


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
            type = contextTree.getFullType(expr.toString());
        }

        if (type == null) {
            throw new RuntimeException("无法解析类型[" + type + "]");
        }
        return new Pair<>(type.toString(), jcMemberReference.name.toString());

    }

    @Override
    public MethodParameterWrapper toMethodParameterWrapper() {
        if (jcExpression instanceof JCTree.JCIdent) {
            JCTree.JCIdent jcIdent = (JCTree.JCIdent) jcExpression;
            return new MethodParameterWrapper(jcIdent.name.toString());
        }

        if (jcExpression instanceof JCTree.JCMethodInvocation) {
            JCTree.JCMethodInvocation methodInvocation = (JCTree.JCMethodInvocation) jcExpression;
            Type type = contextTree.getByAfterTypeInference(() -> methodInvocation.type);
            MethodParameterWrapper parameterWrapper = new MethodParameterWrapper(new TypeDefinition(type.toString()));
            parameterWrapper.setText(jcExpression.toString());
            return parameterWrapper;
        }

        throw new RuntimeException("不支持类型[" + jcExpression.getClass().getName() + "] 进行toMethodParameterWrapper 转换");
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
