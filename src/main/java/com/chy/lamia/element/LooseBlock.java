package com.chy.lamia.element;


import com.chy.lamia.entity.ParameterType;
import com.sun.tools.javac.tree.JCTree;

import java.util.List;

public class LooseBlock {

    //这个 block 能够访问到的所有变量
    private final List<ParameterType> vars;

    private final JCTree.JCBlock block;

    public LooseBlock(List<ParameterType> vars, JCTree.JCBlock block) {
        this.vars = vars;
        this.block = block;
    }


    public List<ParameterType> getVars() {
        return vars;
    }


    public JCTree.JCBlock getBlock() {
        return block;
    }

    public void modifyBody(JCTree.JCBlock newBlock){
        block.stats = newBlock.stats;
    }


}
