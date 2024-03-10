package com.chy.lamia.visitor;

import com.chy.lamia.utils.Lists;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.TreeVisitor;
import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;

import java.util.List;

public class SimpleBlockTree implements BlockTree {

    @Getter
    JCTree.JCStatement statement;

    public SimpleBlockTree(JCTree.JCStatement statement) {
        this.statement = statement;
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
