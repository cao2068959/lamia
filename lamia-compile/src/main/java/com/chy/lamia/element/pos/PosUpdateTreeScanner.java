package com.chy.lamia.element.pos;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import lombok.Setter;

public class PosUpdateTreeScanner extends TreeScanner {

    @Setter
    private int pos;

    @Override
    public void scan(JCTree tree) {
        if (tree != null) {
            tree.pos = pos;
        }
        super.scan(tree);
    }
}