package com.chy.lamia.entity;

import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import lombok.Data;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Data
public class ClassTreeWrapper {

    public JCTree classTree;

    private boolean isTypeInference = false;

    private TypeDefinition typeDefinition;

    public ClassTreeWrapper(JCTree classTree) {
        this.classTree = classTree;
    }

    public void typeInference() {
        if (!isTypeInference) {
            JCUtils.instance.attrib(classTree);
            isTypeInference = true;
        }
    }

    public TypeDefinition getTypeDefinition() {
        if (typeDefinition != null) {
            return typeDefinition;
        }
        if (classTree instanceof JCTree.JCClassDecl) {
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) classTree;
            String classPath = Optional.ofNullable(classDecl.sym).map(Objects::toString).orElse(null);
            if (classPath != null) {
                this.typeDefinition = new TypeDefinition(classPath);
                return typeDefinition;
            }
        }
        // 上面解析不行，直接类型推断
        typeInference();
        Type type = classTree.type;
        this.typeDefinition = TypeDefinitionFactory.create(type);
        return this.typeDefinition;
    }

    /**
     * 使用一个简写的类名 获取一个完整的类型
     *
     * @param string
     * @return
     */
    public Type getFullType(String string) {
        return JCUtils.instance.attribType(classTree, string);
    }

    public Type getFullType(JCTree.JCExpression expression) {
        Type type = expression.type;
        if (type != null) {
            return type;
        }
        // 类型推断
        typeInference();
        // 类型推断之后去再获取一下
        type = expression.type;
        if (type != null) {
            return type;
        }
        // 类型推断之后还是没有 那么去解析这个表达式
        return JCUtils.instance.attribType(classTree, expression);
    }

    public <T> T getByAfterTypeInference(Supplier<T> fun) {
        T t = fun.get();
        if (t == null) {
            typeInference();
            t = fun.get();
            if (t == null) {
                throw new RuntimeException("无法获取到类型信息");
            }
        }
        return t;
    }
}
