package com.chy.lamia.element;


import com.chy.lamia.entity.NameAndType;
import com.chy.lamia.entity.SunList;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.LinkedList;
import java.util.List;

public class LooseBlock {

    //这个 block 能够访问到的所有变量
    private final List<NameAndType> vars;

    private final JCTree.JCBlock block;

    public LooseBlock(List<NameAndType> vars, JCTree.JCBlock block) {
        this.vars = vars;
        this.block = block;
    }


    public List<NameAndType> getVars() {
        return vars;
    }


    public JCTree.JCBlock getBlock() {
        return block;
    }

    public void modifyBody(JCTree.JCBlock newBlock){
        block.stats = newBlock.stats;
    }


}
