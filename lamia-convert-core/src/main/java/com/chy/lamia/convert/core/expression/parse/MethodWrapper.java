package com.chy.lamia.convert.core.expression.parse;

import com.chy.lamia.convert.core.components.entity.Expression;
import lombok.Data;

import java.util.List;

@Data
public class MethodWrapper {

    String name;

    List<Expression> args;

    public MethodWrapper(String name) {
        this.name = name;
    }


    public Expression getOnlyArgs() {
        if (args == null || args.isEmpty()) {
            return null;
        }
        return args.get(0);
    }

}
