package com.chy.lamia.element;

import com.sun.tools.javac.tree.JCTree;

/**
 * 修改 block的接口
 *
 */
public interface Modify {

    void run(JCTree.JCBlock data);

}
