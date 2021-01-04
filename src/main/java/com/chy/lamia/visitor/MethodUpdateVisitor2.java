package com.chy.lamia.visitor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

/**
 * 　　* @author hengyuan
 * 　　* @date $ $
 *
 */
public class MethodUpdateVisitor2 extends TreeTranslator {

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl tree) {
        super.visitMethodDef(tree);
    }

    @Override
    public void visitImport(JCTree.JCImport tree) {
        super.visitImport(tree);
    }
}
