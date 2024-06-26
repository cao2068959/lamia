package com.chy.lamia.element.tree;


import com.chy.lamia.convert.core.entity.Getter;
import com.chy.lamia.convert.core.entity.Setter;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
import com.chy.lamia.visitor.InstantMethodVisitor;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

import java.util.HashMap;
import java.util.Map;

public class GetSetCollect extends InstantMethodVisitor {

    private Map<String, Getter> getterData = new HashMap<>();
    private Map<String, Setter> setterData = new HashMap<>();


    @Override
    public void visitInstanttMethod(JCTree.JCMethodDecl that) {
        String name = that.getName().toString();
        //不是 get/set  开头直接弃坑
        if (name.length() < 4) {
            return;
        }

        if (name.startsWith("get")) {
            getterHandle(name, that);
        } else if (name.startsWith("set")) {
            setterHandle(name, that);
        }


    }

    private void setterHandle(String name, JCTree.JCMethodDecl that) {

        List<JCTree.JCVariableDecl> parameters = that.getParameters();
        if (parameters.length() != 1) {
            return;
        }

        //拿到set后面的名字
        String varName = varNameHandle(name.substring(3));
        if (varName == null) {
            return;
        }
        // 获取方法的第一个参数
        JCTree.JCVariableDecl fparam = parameters.get(0);
        Type paramType = fparam.vartype.type;
        TypeDefinition typeDefinition = TypeDefinitionFactory.create(paramType);

        Setter setter = new Setter();
        setter.setType(typeDefinition);
        setter.setVarName(varName);
        setter.setMethodName(name);
        setterData.put(varName, setter);
    }


    private void getterHandle(String name, JCTree.JCMethodDecl that) {
        List<JCTree.JCVariableDecl> parameters = that.getParameters();
        //如果方法的参数大于0 说明根本就不是 getter方法
        if (parameters.length() > 0) {
            return;
        }

        JCTree returnType = that.getReturnType();
        //返回值等于 Null也不是一个合格的 getter
        if (returnType.type.getTag() == TypeTag.VOID) {
            return;
        }

        //拿到get后面的名字
        String varName = varNameHandle(name.substring(3));
        if (varName == null) {
            return;
        }

        Type type = returnType.type;
        TypeDefinition typeDefinition = TypeDefinitionFactory.create(type);

        Getter getter = new Getter();
        getter.setType(typeDefinition);
        getter.setVarName(varName);
        getter.setMethodName(name);
        getterData.put(varName, getter);
    }


    /**
     * 处理一下 var的名字
     * 把开头字母给小写
     *
     * @param data
     * @return
     */
    private String varNameHandle(String data) {
        if (data == null || data.length() < 1) {
            return null;
        }

        char[] chars = data.toCharArray();
        chars[0] = toLow(chars[0]);
        return new String(chars);
    }

    private char toLow(char c) {
        if (c >= 'A' && c <= 'Z') {
            c += 32;
        }
        return c;
    }

    public Map<String, Getter> getGetterData() {
        return getterData;
    }

    public Map<String, Setter> getSetterData() {
        return setterData;
    }
}
