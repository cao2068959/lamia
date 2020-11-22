package com.chy.lamia.visitor;


import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

public class ParameterVisitor extends TreeTranslator {

    @Override
    public void visitTree(JCTree tree) {
        super.visitTree(tree);
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl tree) {
        super.visitMethodDef(tree);
    }
}
