package com.chy.lamia.element.tree;


import com.chy.lamia.entity.Var;
import com.chy.lamia.visitor.InstantVarVisitor;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;

import java.util.HashMap;
import java.util.Map;

/**
 * 属性采集器,把一个类里面所有的 类属性给保存下来
 * 内部类不在采集范围
 *
 *
 */
public class VarCollect extends InstantVarVisitor {

    private Map<String,Var> data = new HashMap<>();


    @Override
    public void visitInstantVar(JCTree.JCVariableDecl tree) {
        Type type= tree.vartype.type;
        if(type == null){
            return;
        }
        String typePath = tree.vartype.type.toString();
        String varName = tree.name.toString();
        Var var = new Var(varName, typePath);
        data.put(varName,var);
    }

    public Map<String, Var> getData() {
        return data;
    }
}
