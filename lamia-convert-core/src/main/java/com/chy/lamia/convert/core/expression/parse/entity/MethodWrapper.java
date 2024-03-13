package com.chy.lamia.convert.core.expression.parse.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class MethodWrapper {

    @Getter
    @Setter
    String name;

    @Setter
    List<ArgWrapper> args;

    public MethodWrapper(String name) {
        this.name = name;
    }

    public List<ArgWrapper> useAllArgs() {
        args.forEach(ArgWrapper::use);
        return args;
    }

    public List<String> useAllArgsToName() {
        List<String> result = new ArrayList<>();
        for (ArgWrapper arg : args) {
            arg.use();
            result.add(arg.getName());
        }
        return result;
    }


    public Expression useOnlyArgs() {
        if (args == null || args.isEmpty()) {
            return null;
        }
        ArgWrapper argWrapper = args.get(0);
        argWrapper.use();
        return argWrapper.getExpression();
    }


    @Override
    public String toString() {
        return "MethodWrapper{" +
                "name='" + name + '\'' +
                '}';
    }
}
