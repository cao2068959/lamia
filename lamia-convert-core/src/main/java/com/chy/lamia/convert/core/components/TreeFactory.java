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
     *
     * @param newInstant
     * @param put
     * @param args
     * @return
     */
    Statement execMethod(String newInstant, String put, List<Expression> args);
}
