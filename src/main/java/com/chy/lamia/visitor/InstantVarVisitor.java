package com.chy.lamia.visitor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

public abstract class InstantVarVisitor extends JCTree.Visitor {


    @Override
    public void visitVarDef(JCTree.JCVariableDecl that) {
        //静态属性就处理了
        if(Flags.isStatic(that.sym)){
            return;
        }
        visitInstantVar(that);
    }

    public abstract void visitInstantVar(JCTree.JCVariableDecl that);


    @Override
    public void visitClassDef(JCTree.JCClassDecl that) {
        List<JCTree> members = that.getMembers();
        members.forEach(jcTree -> {
            if(jcTree instanceof JCTree.JCVariableDecl){
                jcTree.accept(this);
            }
        });
    }




}
