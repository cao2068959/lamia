package com.chy.lamia.entity;

import com.sun.tools.javac.tree.JCTree;

import java.util.List;

public class AssembleResult {

    List<JCTree.JCStatement> statements;
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
