package com.chy.lamia.visitor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

public abstract class InstantMethodVisitor extends JCTree.Visitor {

    /**
     * 父类的类型
     */
    TypeDefinition parentType;

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl that) {
        //静态属性就不处理了
        if (that.sym == null || Flags.isStatic(that.sym)) {
            return;
        }
        visitInstanttMethod(that);
    }

    public abstract void visitInstanttMethod(JCTree.JCMethodDecl that);

    @Override
    public void visitClassDef(JCTree.JCClassDecl that) {
        JCTree.JCExpression extendsClause = that.getExtendsClause();
        //如果有继承的情况下先把父类保存了
        if (extendsClause != null) {
            this.parentType = new TypeDefinition(extendsClause.type.toString());
        }
        List<JCTree> members = that.getMembers();
        members.forEach(jcTree -> {
            if (jcTree instanceof JCTree.JCMethodDecl) {
                jcTree.accept(this);
            }
        });
    }

    public TypeDefinition getParentType() {
        return parentType;
    }
}
