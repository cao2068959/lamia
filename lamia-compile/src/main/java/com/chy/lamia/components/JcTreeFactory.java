package com.chy.lamia.components;

import com.chy.lamia.components.entity.JcExpression;
import com.chy.lamia.components.entity.JcStatement;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.Lists;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;

import java.util.List;
import java.util.stream.Collectors;

public class JcTreeFactory implements TreeFactory {

    private final JCUtils jcUtils;

    public JcTreeFactory(JCUtils jcUtils) {
        this.jcUtils = jcUtils;
    }

    @Override
    public Expression newClass(String classPath, List<Expression> newInstanceParam) {
        if (newInstanceParam == null || newInstanceParam.isEmpty()) {
            return new JcExpression(jcUtils.newClass(classPath, Lists.empty));
        }
        List<JCTree.JCExpression> param = newInstanceParam.stream().map(JcExpression::get)
                .collect(Collectors.toList());
        return new JcExpression(jcUtils.newClass(classPath, param));
    }

    @Override
    public Statement createVar(String instantName, String classPath, Expression expression) {
        JCTree.JCVariableDecl variableDecl = jcUtils.createVar(instantName, classPath, JcExpression.get(expression));
        return new JcStatement(variableDecl);
    }

    @Override
    public Statement createVar(String instantName, TypeDefinition type, Expression expression) {
        JCTree.JCTypeApply typeApply = jcUtils.toJCTypeApply(type);
        JCTree.JCVariableDecl variableDecl = jcUtils.createVar(instantName, typeApply, JcExpression.get(expression), Flags.PARAMETER);
        return new JcStatement(variableDecl);
    }


    @Override
    public Statement varAssign(String instantName, Expression newClass) {
        JCTree.JCStatement statement = jcUtils.varAssign(instantName, JcExpression.get(newClass));
        return new JcStatement(statement);
    }

    @Override
    public Expression toExpression(String newInstant) {
        return new JcExpression(jcUtils.memberAccess(newInstant));
    }

    @Override
    public Expression geStringExpression(String supplyName) {
        return new JcExpression(jcUtils.geStringExpression(supplyName));
    }

    @Override
    public Statement execMethod(String instant, String method, List<Expression> args) {
        return execMethod(toExpression(instant), method, args);
    }

    @Override
    public Statement execMethod(Expression expression, String method, List<Expression> args) {
        if (args == null || args.isEmpty()) {
            return new JcStatement(jcUtils.execMethod(JcExpression.get(expression), method, Lists.empty));
        }
        List<JCTree.JCExpression> param = args.stream().map(JcExpression::get)
                .collect(Collectors.toList());
        return new JcStatement(jcUtils.execMethod(JcExpression.get(expression), method, param));
    }

    @Override
    public Statement createReturn(String newInstantName) {
        return new JcStatement(jcUtils.createReturn(newInstantName));
    }

    @Override
    public Expression typeCast(String classPath, Expression expression) {
        return new JcExpression(jcUtils.typeCast(classPath, JcExpression.get(expression)));
    }

    @Override
    public Statement createIf(Expression judge, List<Statement> trueStatements, List<Statement> falseStatements) {
        return new JcStatement(jcUtils.createIf(JcExpression.get(judge), toJcList(trueStatements), toJcList(falseStatements)));
    }

    @Override
    public Expression createVarNotEqNull(Expression varExpression) {
        return new JcExpression(jcUtils.createVarNotEqNull(JcExpression.get(varExpression)));
    }

    private List<JCTree.JCStatement> toJcList(List<Statement> statements) {
        if (statements == null || statements.isEmpty()) {
            return Lists.empty;
        }
        return statements.stream().map(JcStatement::get).collect(Collectors.toList());
    }
}
