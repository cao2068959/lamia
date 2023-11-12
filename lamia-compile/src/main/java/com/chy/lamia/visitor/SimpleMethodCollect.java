package com.chy.lamia.visitor;


import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.entity.SimpleMethod;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
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
        TypeDefinition returnParameterType = null;
        if (returnType != null && returnType.type != null && returnType.type.getTag() != TypeTag.VOID) {
            returnParameterType = TypeDefinitionFactory.create(returnType.type);
        }

        SimpleMethod simpleMethod = new SimpleMethod(that.getName().toString(), returnParameterType);
        simpleMethod.setStatic(isStatic);
        List<TypeDefinition> parameterTypes = that.getParameters().stream()
                .map(variableDecl -> TypeDefinitionFactory.create(variableDecl.type))
                .collect(Collectors.toList());

        simpleMethod.setParams(parameterTypes);
        data.add(simpleMethod);
    }


    public List<SimpleMethod> getData() {
        return data;
    }
}
