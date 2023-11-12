package com.chy.lamia.convert.core.components;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;

import java.util.List;

public interface TreeFactory {
    /**
     * 生成语句 new Xxx()
     *
     * @param classPath
     * @param newInstanceParam
     * @return
     */
    Expression newClass(String classPath, List<Expression> newInstanceParam);

    /**
     * 创建一个变量
     *
     * @param instantName
     * @param classPath
     * @param newClass
     * @return
     */
    Statement createVar(String instantName, String classPath, Expression newClass);

    /**
     * 给一个变量赋值
     *
     * @param instantName
     * @param newClass
     * @return
     */
    Statement varAssign(String instantName, Expression newClass);

    /**
     * 把字符转成一个表达式
     *
     * @param newInstant
     * @return
     */
    Expression toExpression(String newInstant);

    /**
     * 把一个字符转成 string表达式如： abc -> "abc"
     *
     * @param supplyName
     * @return
     */
    Expression geStringExpression(String supplyName);

    /**
     * 方法执行
     */
    Statement execMethod(String instant, String method, List<Expression> args);

    Statement execMethod(Expression expression, String method, List<Expression> args);

    /**
     * 生成一个 return 语句
     */
    Statement createReturn(String newInstantName);

    /**
     * 生成强转类型的表达式
     *
     * @param classPath
     * @param expression
     * @return
     */
    Expression typeCast(String classPath, Expression expression);

    /**
     * 生成对应的if语句
     */
    Statement createIf(Expression judge, List<Statement> trueStatements, List<Statement> falseStatements);

    /**
     * 生成 varExpression != null 这样的表达式
     *
     * @param varExpression
     * @return
     */
    Expression createVarNotEqNull(Expression varExpression);
}
