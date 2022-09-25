package com.chy.lamia.entity;

import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 表达式包装, 保存了当前的表达式以及对应 可能依赖的前置语句
 *
 * @author bignosecat
 */
@Getter
@Setter
public class ExpressionWrapper {

    /**
     * 前置表达式
     */
    List<JCTree.JCStatement> before = new ArrayList<>();

    /**
     * 生成的变量表达式
     */
    JCTree.JCExpression expression;


    /**
     * 如果有返回类型 返回的类型是什么
     */
    Optional<TypeDefinition> returnType = Optional.empty();

    public ExpressionWrapper() {
    }

    public ExpressionWrapper(JCTree.JCExpression expression, TypeDefinition returnType) {
        this(expression, Optional.ofNullable(returnType));
    }

    public ExpressionWrapper(JCTree.JCExpression expression, Optional<TypeDefinition> returnType) {
        this.returnType = returnType;
        this.expression = expression;
    }

    public void addBeforeStatement(JCTree.JCStatement statement) {
        before.add(statement);
    }


    public void addBeforeStatement(List<JCTree.JCStatement> befores) {
        for (JCTree.JCStatement jcStatement : befores) {
            addBeforeStatement(jcStatement);
        }

    }
}
