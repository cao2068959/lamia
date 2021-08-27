package com.chy.lamia.element.assemble;

import com.sun.tools.javac.tree.JCTree;

import java.util.List;
import java.util.Set;

public class AssembleResult {
    /**
     * 生成的代码块
     */
    List<JCTree.JCStatement> statements;
    /**
     * 如果是组装一个值对象后这就是生成的值对象的名称
     */
    String newInstantName;

    Set<String> dependentClassPath;

    public AssembleResult(List<JCTree.JCStatement> statements, String newInstantName, Set<String> dependentClassPath) {
        this.statements = statements;
        this.newInstantName = newInstantName;
        this.dependentClassPath = dependentClassPath;
    }

    public List<JCTree.JCStatement> getStatements() {
        return statements;
    }

    public String getNewInstantName() {
        return newInstantName;
    }
}
