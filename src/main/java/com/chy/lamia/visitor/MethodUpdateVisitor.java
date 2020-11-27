package com.chy.lamia.visitor;


import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

import java.util.List;

public class MethodUpdateVisitor extends TreeTranslator {

    private final List<JCTree.JCStatement> update;
    private final JCUtils jcUtils;


    public MethodUpdateVisitor(List<JCTree.JCStatement> treeStatements, JCUtils jcUtils) {
        this.update = treeStatements;
        this.jcUtils = jcUtils;
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl tree) {
        updateMethod(tree);
        super.visitMethodDef(tree);
    }

    private void updateMethod(JCTree.JCMethodDecl tree){
        if(!"test1".equals(tree.getName().toString())){
            return;
        }

        JCTree.JCBlock oldBody = tree.getBody();
        oldBody.getStatements().forEach(satement->{
            update.add(satement);
        });

        tree.body = jcUtils.createBlock(update);
    }


}
