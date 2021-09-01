package com.chy.lamia.visitor;


import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.SimpleMethod;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleMethodCollect extends AllMethodVisitor {

    List<SimpleMethod> data = new ArrayList<>();

    @Override
    public void visitMethod(JCTree.JCMethodDecl that) {

        if ("<init>".equals(that.name.toString())) {
            return;
        }


        boolean isStatic = that.sym != null && Flags.isStatic(that.sym);


        JCTree returnType = that.getReturnType();
        ParameterType returnParameterType = null;
        if (returnType != null && returnType.type != null && returnType.type.getTag() != TypeTag.VOID) {
            returnParameterType = new ParameterType(returnType.type.toString());
        }

        SimpleMethod simpleMethod = new SimpleMethod(that.getName().toString(), returnParameterType);
        simpleMethod.setStatic(isStatic);
        List<ParameterType> parameterTypes = that.getParameters().stream()
                .map(variableDecl -> new ParameterType(variableDecl.type.toString()))
                .collect(Collectors.toList());
        simpleMethod.setParams(parameterTypes);
        data.add(simpleMethod);
    }

    public List<SimpleMethod> getData() {
        return data;
    }
}
