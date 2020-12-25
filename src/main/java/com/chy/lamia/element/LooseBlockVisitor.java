package com.chy.lamia.element;


import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.visitor.AbstractBlockVisitor;
import com.sun.tools.javac.tree.JCTree;

import java.util.LinkedList;
import java.util.List;


public class LooseBlockVisitor extends AbstractBlockVisitor {

    private boolean haveReturn = false;
    private final List<ParameterType> vars;
    private final List<LooseBlock> result;

    public LooseBlockVisitor() {
        vars = new LinkedList<>();
        result = new LinkedList<>();
    }

    public LooseBlockVisitor(List<ParameterType> vars, List<LooseBlock> result) {
        this.vars = new LinkedList<>(vars);
        this.result = result;
    }


    /**
     * 如果 遇到了 代码块 if while for 等 都递归进去 再次扫描一次
     *
     * @param statement
     */
    @Override
    public void blockVisit(JCTree.JCBlock statement) {
        LooseBlockVisitor looseBlockVisitor = new LooseBlockVisitor(vars, result);
        //继续去扫描代码块里面的代码
        looseBlockVisitor.accept(statement);
        analyzeResult(looseBlockVisitor);
    }

    /**
     * 去解析 对应的 LooseBlockVisitor 是不是符合要求的数据， 如果是 那么保存下相关的数据
     *
     * @param looseBlockVisitor
     */
    private void analyzeResult(LooseBlockVisitor looseBlockVisitor) {
        // 对应的 模块里面没有 return，那么 不是目标对象，就不进行处理了
        if (!looseBlockVisitor.haveReturn) {
            return;
        }
        LooseBlock looseBlock = new LooseBlock(looseBlockVisitor.getVars(), looseBlockVisitor.block);
        result.add(looseBlock);
    }


    public List<LooseBlock> getResult() {
        analyzeResult(this);
        return result;
    }


    @Override
    public void variableVisit(JCTree.JCVariableDecl statement) {
        String typePath = statement.vartype.toString();
        String name = statement.getName().toString();
        ParameterType parameterType = new ParameterType(name, typePath);
        vars.add(parameterType);
    }

    @Override
    public void returnVisit(JCTree.JCReturn statement) {
        haveReturn = true;
    }


    public List<ParameterType> getVars() {
        return vars;
    }
}
