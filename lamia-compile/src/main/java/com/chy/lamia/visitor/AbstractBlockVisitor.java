package com.chy.lamia.visitor;


import com.chy.lamia.entity.ClassTreeWrapper;
import com.chy.lamia.reporter.Reporter;
import com.chy.lamia.utils.Lists;
import com.sun.source.tree.BlockTree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import lombok.Getter;

import java.util.ArrayList;


public abstract class AbstractBlockVisitor {

    /**
     * 要遍历的 block
     */
    @Getter
    public BlockTree block;

    /**
     * 这个 block所属的 class
     */
    @Getter
    public ClassTreeWrapper classTree;

    /**
     * 已经 迭代遍历后的语句
     */
    @Getter
    public java.util.List<JCTree.JCStatement> processedFinishStatement;


    public void accept(JCTree.JCBlock block, ClassTreeWrapper classTree) {
        if (block == null) {
            return;
        }
        List<JCTree.JCStatement> statements = block.getStatements();
        if (statements == null) {
            return;
        }
        initParam(block, classTree);
        visitorAllBlock(statements);
        visitorEnd();
    }

    public void accept(LambdaLineBlockTree block, ClassTreeWrapper classTree) {
        if (block == null) {
            return;
        }
        JCTree.JCStatement statement = block.getStatement();
        if (statement == null) {
            return;
        }
        initParam(block, classTree);
        visitorAllBlock(Lists.ofSunList(statement));
        visitorEnd();
    }


    private void initParam(BlockTree block, ClassTreeWrapper classTree) {
        this.block = block;
        this.classTree = classTree;
        this.processedFinishStatement = new ArrayList<>();
        subInitParam(block, classTree);
    }


    private void visitorAllBlock(List<JCTree.JCStatement> statements) {

        for (JCTree.JCStatement statement : statements) {
            try {
                if (doVisitorAllBlock(statement)) {
                    processedFinishStatement.add(statement);
                }
            } catch (RuntimeException e) {
                Reporter.reportException(e, statement);
            }
        }
    }


    private boolean doVisitorAllBlock(JCTree.JCStatement statement) {
        //如果是 if 语句
        if (statement instanceof JCTree.JCIf) {
            JCTree.JCIf jcif = (JCTree.JCIf) statement;
            JCTree.JCBlock elseBlock = (JCTree.JCBlock) jcif.elsepart;
            JCTree.JCBlock thenBlock = (JCTree.JCBlock) jcif.thenpart;
            ifVisit(jcif, thenBlock, elseBlock);
            blockVisit(thenBlock);
            blockVisit(elseBlock);
            return true;
        }


        //如果是 while 语句
        if (statement instanceof JCTree.JCWhileLoop) {
            JCTree.JCWhileLoop jcWhileLoop = (JCTree.JCWhileLoop) statement;
            whileLoopVisit(jcWhileLoop, (JCTree.JCBlock) jcWhileLoop.body);
            blockVisit((JCTree.JCBlock) jcWhileLoop.body);
            return true;
        }

        //如果是 return 语句
        if (statement instanceof JCTree.JCReturn) {
            JCTree.JCReturn jCReturn = (JCTree.JCReturn) statement;
            return returnVisit(jCReturn);
        }

        //变量申明语句
        if (statement instanceof JCTree.JCVariableDecl) {
            JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) statement;
            return variableVisit(jcVariableDecl);
        }

        //如果是 代码块
        if (statement instanceof JCTree.JCBlock) {
            JCTree.JCBlock jcBlock = (JCTree.JCBlock) statement;
            innerBlockVisit(jcBlock);
            blockVisit(jcBlock);
            return true;
        }

        // 普通的执行表达式
        if (statement instanceof JCTree.JCExpressionStatement) {
            JCTree.JCExpressionStatement expressionStatement = (JCTree.JCExpressionStatement) statement;
            return expressionStatementVisit(expressionStatement);
        }
        return true;
    }

    public boolean expressionStatementVisit(JCTree.JCExpressionStatement expressionStatement) {
        return true;
    }


    public void ifVisit(JCTree.JCIf statement, JCTree.JCBlock thenBlock, JCTree.JCBlock elseBlock) {
    }

    public void innerBlockVisit(JCTree.JCBlock statement) {
    }

    public void blockVisit(JCTree.JCBlock statement) {
    }

    public void whileLoopVisit(JCTree.JCWhileLoop statement, JCTree.JCBlock whileBlock) {
    }

    public boolean returnVisit(JCTree.JCReturn statement) {
        return true;
    }

    public boolean variableVisit(JCTree.JCVariableDecl statement) {
        return true;
    }

    public void visitorEnd() {
    }

    protected abstract void subInitParam(BlockTree block, ClassTreeWrapper classTree);

}
