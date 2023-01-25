package com.chy.lamia.element.resolver.expression;

import com.sun.tools.javac.tree.JCTree;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author bignosecat
 */
@Data
public class LamiaExpression {
    Set<String> allArgsNames = new LinkedHashSet<>();

    Set<String> spreadArgs = new HashSet<>();

    boolean defaultSpread = false;

    JCTree.JCTypeCast typeCast;

    public void addArgs(Collection<String> args) {
        allArgsNames.addAll(args);
    }

    public void addSpreadArgs(Collection<String> args) {
        allArgsNames.addAll(args);
        spreadArgs.addAll(args);
    }

}
