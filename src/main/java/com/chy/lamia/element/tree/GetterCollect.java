package com.chy.lamia.element.tree;


import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.Var;
import com.chy.lamia.visitor.InstantMethodVisitor;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Name;

import java.util.HashMap;
import java.util.Map;

public class GetterCollect extends InstantMethodVisitor {

    private Map<String, Getter> data = new HashMap<>();


    @Override
    public void visitInstanttMethod(JCTree.JCMethodDecl that) {
        String name = that.getName().toString();
        //不是 get 开头直接弃坑
        if (name.length() < 4 || !name.startsWith("get")) {
            return;
        }

        //拿到get后面的名字
        String varName = varNameHandle(name.substring(3));
        if (varName == null) {
            return;
        }

        Getter getter = new Getter();
        getter.setSimpleName(name);
        data.put(varName, getter);
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


    public Map<String, Getter> getData() {
        return data;
    }


}
