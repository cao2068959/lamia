package com.chy.lamia.element;


import com.chy.lamia.entity.SunList;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.Lists;
import com.sun.tools.javac.tree.JCTree;

import java.util.List;

public class NeedUpdateBlock {

    JCTree.JCBlock block;
    List<JCTree.JCStatement> enableUpdateStatements;

    public NeedUpdateBlock(JCTree.JCBlock block, List<JCTree.JCStatement> enableUpdateStatements) {
        this.block = block;
        this.enableUpdateStatements = enableUpdateStatements;
    }

    public JCTree.JCBlock getBlock() {
        return block;
    }

    public List<JCTree.JCStatement> getEnableUpdateStatements() {
        return enableUpdateStatements;
    }


    public void modifyMethodBody(List<JCTree.JCStatement> newStatement) {
        block.stats = Lists.toSunList(newStatement);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NeedUpdateBlock that = (NeedUpdateBlock) o;

        return block.equals(that.block);
    }

    @Override
    public int hashCode() {
        return block.hashCode();
    }


}
