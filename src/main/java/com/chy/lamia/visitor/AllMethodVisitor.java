package com.chy.lamia.visitor;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

public abstract class AllMethodVisitor extends JCTree.Visitor {


    @Override
    public void visitMethodDef(JCTree.JCMethodDecl that) {
        visitMethod(that);
    }


    public abstract void visitMethod(JCTree.JCMethodDecl that);

    @Override
    public void visitClassDef(JCTree.JCClassDecl that) {
        List<JCTree> members = that.getMembers();
        members.forEach(jcTree -> {
            if (jcTree instanceof JCTree.JCMethodDecl) {
                jcTree.accept(this);
            }
        });
    }


}
