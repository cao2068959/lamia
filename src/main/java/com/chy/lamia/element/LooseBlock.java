package com.chy.lamia.element;


import com.chy.lamia.entity.NameAndType;

import java.util.List;

public class LooseBlock {

    //这个 block 能够访问到的所有变量
    private final List<NameAndType> vars;

    //用于修改 block的函数指针
    private final Modify modify;

    public LooseBlock(List<NameAndType> vars, Modify modify) {
        this.vars = vars;
        this.modify = modify;
    }
}
