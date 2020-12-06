package com.chy.lamia.visitor;


import com.chy.lamia.element.Modify;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;


public abstract class AbstractBlockVisitor {

    public JCTree.JCBlock block;

    public void accept(JCTree.JCBlock block) {
        if (block == null) {
            return;
        }
        List<JCTree.JCStatement> statements = block.getStatements();
        if (statements == null) {
            return;
        }
        this.block = block;
        doVisitorAllBlock(statements);
    }

    private void doVisitorAllBlock(List<JCTree.JCStatement> statements) {
        for (JCTree.JCStatement statement : statements) {
            //如果是 if 语句
            if (statement instanceof JCTree.JCIf) {
                JCTree.JCIf jcif = (JCTree.JCIf) statement;
                JCTree.JCBlock elseBlock = (JCTree.JCBlock) jcif.elsepart;
                JCTree.JCBlock thenBlock = (JCTree.JCBlock) jcif.thenpart;
                ifVisit(jcif, thenBlock, elseBlock);
                blockVisit(thenBlock);
                blockVisit(elseBlock);
                continue;
            }


            //如果是 while 语句
            if (statement instanceof JCTree.JCWhileLoop) {
                JCTree.JCWhileLoop jcWhileLoop = (JCTree.JCWhileLoop) statement;
                whileLoopVisit(jcWhileLoop, (JCTree.JCBlock) jcWhileLoop.body);
                blockVisit((JCTree.JCBlock) jcWhileLoop.body);
                continue;
            }

            //如果是 return 语句
            if (statement instanceof JCTree.JCReturn) {
                JCTree.JCReturn jCReturn = (JCTree.JCReturn) statement;
                returnVisit(jCReturn);
                continue;
            }

            //变量申明语句
            if (statement instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) statement;
                variableVisit(jcVariableDecl);
                continue;
            }

            //如果是 代码块
            if (statement instanceof JCTree.JCBlock) {
                JCTree.JCBlock jcBlock = (JCTree.JCBlock) statement;
                innerBlockVisit(jcBlock);
                blockVisit(jcBlock);
                continue;
            }
        }
    }


    public void ifVisit(JCTree.JCIf statement, JCTree.JCBlock thenBlock, JCTree.JCBlock elseBlock) {
    }

    public void innerBlockVisit(JCTree.JCBlock statement) {
    }

    public void blockVisit(JCTree.JCBlock statement) {
    }

    public void whileLoopVisit(JCTree.JCWhileLoop statement, JCTree.JCBlock whileBlock) {
    }

    public void returnVisit(JCTree.JCReturn statement) {
    }

    public void variableVisit(JCTree.JCVariableDecl statement) {
    }

}
