package com.chy.lamia.element;


import com.sun.tools.javac.tree.JCTree;

import java.util.List;

/**
 * 每一个 Lamia.convert 表达式能够使用到的作用域
 *
 */
public class LamiaConvertInfo {

    String id;



    JCTree.JCBlock block;
    List<JCTree.JCStatement> enableUpdateStatements;


}
