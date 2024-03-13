package com.chy.lamia.convert.core.expression.parse.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.utils.struct.Pair;
import lombok.Data;

@Data
public abstract class ArgWrapper {

    Expression expression;

    String name;

    public ArgWrapper(Expression expression, String name) {
        this.expression = expression;
        this.name = name;
    }

    public void use() {

    }

    /**
     * 解析 lambda 表达式如: User:getName 解析成 com.chy.User 和 getName 两个部分
     *
     * @return
     */
    public abstract Pair<String, String> parseLambda();
}
