package com.chy.lamia.element.assemble;

import com.sun.tools.javac.tree.JCTree;

import java.util.List;

public class AssembleResult {
    /**
     * 生成的代码块
     */
    List<JCTree.JCStatement> statements;
    /**
     * 如果是组装一个值对象后这就是生成的值对象的名称
     */
    String newInstantName;

    public AssembleResult(List<JCTree.JCStatement> statements, String newInstantName) {
        this.statements = statements;
        this.newInstantName = newInstantName;
    }

    public List<JCTree.JCStatement> getStatements() {
        return statements;
    }

    public String getNewInstantName() {
        return newInstantName;
    }
}
