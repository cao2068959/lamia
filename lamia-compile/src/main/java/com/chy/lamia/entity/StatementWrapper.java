package com.chy.lamia.entity;

import com.sun.source.tree.TreeVisitor;
import com.sun.tools.javac.tree.JCTree;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StatementWrapper extends JCTree.JCStatement {
    String id;

    @Override
    public Tag getTag() {
        return null;
    }

    @Override
    public void accept(Visitor visitor) {

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