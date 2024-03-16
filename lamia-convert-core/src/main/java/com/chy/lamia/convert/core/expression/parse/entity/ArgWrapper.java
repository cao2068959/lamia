package com.chy.lamia.convert.core.expression.parse.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
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

}
