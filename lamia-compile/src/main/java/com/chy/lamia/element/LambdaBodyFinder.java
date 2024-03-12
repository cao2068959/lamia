package com.chy.lamia.element;

import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class LambdaBodyFinder extends TreeScanner<Void, Void> {

    private final JCTree classTree;
    @Getter
    List<JCLambdaWrapper> result;

    public LambdaBodyFinder(JCTree classTree) {
        this.classTree = classTree;
    }

    @Override
    public Void visitLambdaExpression(LambdaExpressionTree node, Void aVoid) {
        if (node instanceof JCTree.JCLambda) {
            JCTree.JCLambda lambda = (JCTree.JCLambda) node;
            addResult(lambda);
        }
        return null;
    }

    private void addResult(JCTree.JCLambda node) {
        if (result == null) {
            result = new ArrayList<>();
        }
        result.add(new JCLambdaWrapper(node, classTree));
    }

}