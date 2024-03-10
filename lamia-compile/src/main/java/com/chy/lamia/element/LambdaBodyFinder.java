package com.chy.lamia.element;

import com.chy.lamia.utils.JCUtils;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class LambdaBodyFinder extends TreeScanner<Void, Void> {

    private final JCTree classTree;
    @Getter
    List<JCTree.JCLambda> result;

    public LambdaBodyFinder(JCTree classTree) {
        this.classTree = classTree;
    }

    @Override
    public Void visitLambdaExpression(LambdaExpressionTree node, Void aVoid) {
        JCUtils jcUtils = JCUtils.instance;
        if (node instanceof JCTree.JCLambda) {
            JCTree.JCLambda lambda = (JCTree.JCLambda) node;
            Env<AttrContext> env = jcUtils.attr.lambdaEnv(lambda, jcUtils.enter.getClassEnv(((JCTree.JCClassDecl) classTree).sym));
            jcUtils.attr.attrib(env);
            System.out.println(lambda);
        }
        return null;
    }

    private void addResult(JCTree.JCLambda node) {
        if (result == null) {
            result = new ArrayList<>();
        }
        result.add(node);
    }

}