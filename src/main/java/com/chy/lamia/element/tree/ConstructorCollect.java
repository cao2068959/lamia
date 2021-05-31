package com.chy.lamia.element.tree;

import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.utils.SymbolUtils;
import com.chy.lamia.visitor.InstantMethodVisitor;
import com.sun.tools.javac.code.Type;
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
            Type paramType = param.vartype.type;
            ParameterType parameterType = new ParameterType(name, paramType.toString());
            //去解析一下这个类型里面有没泛型
            java.util.List<ParameterType> generic = SymbolUtils.getGeneric(paramType);
            parameterType.setGeneric(generic);
            constructor.add(parameterType);
        }

        data.add(constructor);
    }


    public java.util.List<Constructor> getData() {
        return data;
    }

}
