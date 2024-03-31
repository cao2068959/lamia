package com.chy.lamia.entity;

import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import lombok.Data;

import java.util.function.Supplier;

@Data
public class ClassTreeWrapper {

    public JCTree classTree;

    private boolean isTypeInference = false;

    public ClassTreeWrapper(JCTree classTree) {
        this.classTree = classTree;
    }

    public void typeInference() {
        if (!isTypeInference) {
            JCUtils.instance.attrib(classTree);
            isTypeInference = true;
        }
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
