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

}
