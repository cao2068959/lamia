package com.chy.lamia.visitor;

import com.chy.lamia.element.JCLambdaWrapper;
import com.chy.lamia.utils.Lists;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.TreeVisitor;
import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;

import java.util.List;

public class LambdaLineBlockTree implements BlockTree {
    @Getter
    private final JCLambdaWrapper jcLambdaWrapper;
    @Getter
    JCTree.JCStatement statement;

    public LambdaLineBlockTree(JCTree.JCStatement statement, JCLambdaWrapper jcLambdaWrapper) {
        this.statement = statement;
        this.jcLambdaWrapper = jcLambdaWrapper;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public List<? extends StatementTree> getStatements() {
        return Lists.of(statement);
    }

    @Override
    public Kind getKind() {
        return null;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> treeVisitor, D d) {
        return null;
    }
}
