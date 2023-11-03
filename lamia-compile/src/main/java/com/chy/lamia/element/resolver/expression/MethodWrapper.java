package com.chy.lamia.element.resolver.expression;

import com.sun.tools.javac.tree.JCTree;
import lombok.Data;

import java.util.List;

@Data
public class MethodWrapper {

    String name;

    List<JCTree.JCExpression> args;

    public MethodWrapper(String name) {
        this.name = name;
    }


    public JCTree.JCExpression getOnlyArgs() {
        if (args == null || args.isEmpty()) {
            return null;
        }
        return args.get(0);
    }

}
