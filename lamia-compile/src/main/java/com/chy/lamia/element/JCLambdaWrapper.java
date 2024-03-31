package com.chy.lamia.element;

import com.chy.lamia.convert.core.entity.LazyTypeVarDefinition;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.entity.VarDefinition;
import com.chy.lamia.entity.ClassTreeWrapper;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;

import java.util.List;
import java.util.stream.Collectors;

public class JCLambdaWrapper {

    JCTree.JCLambda lambda;
    ClassTreeWrapper parentClass;

    public JCLambdaWrapper(JCTree.JCLambda lambda, ClassTreeWrapper classTree) {
        this.lambda = lambda;
        this.parentClass = classTree;
    }

    public Tree getBody() {
        return lambda.body;
    }

    public void setBody(JCTree tree) {
        lambda.body = tree;
    }

    public List<VarDefinition> params() {
        return lambda.params.stream().map(param -> {
            LazyTypeVarDefinition varDefinition = new LazyTypeVarDefinition(param.name.toString());
            varDefinition.setTypeSupplier(() -> {
                String typeClassPath = findType(param);
                return new TypeDefinition(typeClassPath);
            });
            return varDefinition;
        }).collect(Collectors.toList());
    }

    private String findType(JCTree.JCVariableDecl variableDecl) {
        // 进行类型推到
        parentClass.typeInference();
        JCTree type = variableDecl.getType();
        // 如果等于null 说明是一个 隐性变量，需要类型推导
        if (type == null) {
            throw new RuntimeException("无法推导出lambda:[" + lambda.toString() + "] 的变量类型");
        }
        return type.toString();
    }

}
