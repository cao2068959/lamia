package com.chy.lamia.element;


import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.entity.ParameterType;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Pair;

import java.util.List;

public class LooseBlock {

    //这个 block 能够访问到的所有变量
    private final List<Pair<ParameterType, MapMember>> vars;

    private final JCTree.JCBlock block;

    public LooseBlock(List<Pair<ParameterType, MapMember>> vars, JCTree.JCBlock block) {
        this.vars = vars;
        this.block = block;
    }


    public List<Pair<ParameterType, MapMember>> getVars() {
        return vars;
    }


    public JCTree.JCBlock getBlock() {
        return block;
    }

    public void modifyBody(JCTree.JCBlock newBlock) {
        block.stats = newBlock.stats;
    }


}
