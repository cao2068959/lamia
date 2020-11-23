package com.chy.lamia.element.tree;

import com.chy.lamia.entity.Constructor;
import com.chy.lamia.visitor.InstantMethodVisitor;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

import java.util.ArrayList;

public class ConstructorCollect extends InstantMethodVisitor {

    private java.util.List<Constructor> data = new ArrayList();

    @Override
    public void visitInstanttMethod(JCTree.JCMethodDecl that) {
        if (!"<init>".equals(that.name.toString())) {
            return;
        }

        Constructor constructor = new Constructor();
        List<JCTree.JCVariableDecl> params = that.params;
        for (JCTree.JCVariableDecl param : params) {
            String name = param.name.toString();
            String typeName = param.vartype.type.toString();
            constructor.add(name, typeName);
        }

        data.add(constructor);
    }


    public java.util.List<Constructor> getData() {
        return data;
    }

}
