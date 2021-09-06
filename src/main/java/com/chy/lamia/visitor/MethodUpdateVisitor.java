package com.chy.lamia.visitor;


import com.chy.lamia.element.NeedUpdateBlock;
import com.chy.lamia.element.PendHighway;
import com.chy.lamia.element.LooseBlockVisitor;
import com.chy.lamia.element.assemble.AssembleResult;
import com.chy.lamia.element.funicle.FunicleFactory;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.SunList;
import com.chy.lamia.expose.Lamia;
import com.chy.lamia.processor.marked.MarkedMethods;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

import java.util.*;

public class MethodUpdateVisitor extends TreeTranslator {


    private final MarkedMethods markedMethods;
    private final JCUtils jcUtils;
    private final String className;
    private JCTree classTree;

    public MethodUpdateVisitor(MarkedMethods markedMethods, JCUtils jcUtils, JCTree tree, String className) {
        this.markedMethods = markedMethods;
        this.jcUtils = jcUtils;
        this.classTree = tree;
        this.className = className;
    }


    @Override
    public void visitMethodDef(JCTree.JCMethodDecl methodSymbolDecl) {
        updateMethod(methodSymbolDecl);
        super.visitMethodDef(methodSymbolDecl);
    }

    private void updateMethod(JCTree.JCMethodDecl methodSymbolDecl) {
        Symbol.MethodSymbol methodSymbol = methodSymbolDecl.sym;
        //确认这个方法是不是应该被修改 及打了注解 @Mapping
        if (!markedMethods.contains(methodSymbol.toString())) {
            return;
        }

        //解析这个方法的返回值
        Type returnType = methodSymbol.getReturnType();
        //返回值不是一个对象就不进行处理了
        if (returnType.getTag() != TypeTag.CLASS) {
            return;
        }

        ParameterType returnParameterType = new ParameterType(returnType);
        Set<String> dependent = Set.of(returnParameterType.getTypePatch());
        FunicleFactory.addDependent(className, dependent);

        //获取方法中所有的入参
        SunList<Symbol.VarSymbol> paramList = new SunList<>(methodSymbolDecl.sym.getParameters());
        //解析原来方法中的方法体,计算出所有需要去修改的 方法体
        Set<NeedUpdateBlock> needUpdateBlocks = untieBlock(methodSymbolDecl);
        for (NeedUpdateBlock needUpdateBlock : needUpdateBlocks) {
            updateBlock(needUpdateBlock, paramList);
        }

    }

    /**
     * 去修改方法体中的代码
     *
     * @param needUpdateBlock
     * @param paramList
     */
    private void updateBlock(NeedUpdateBlock needUpdateBlock, SunList<Symbol.VarSymbol> paramList) {
        List<JCTree.JCStatement> enableUpdateStatements = needUpdateBlock.getEnableUpdateStatements();

        List<JCTree.JCStatement> newStatement = new LinkedList<>();
        for (JCTree.JCStatement enableUpdateStatement : enableUpdateStatements) {
            if (enableUpdateStatement instanceof PendHighway) {
                generateNewStatement((PendHighway) enableUpdateStatement, newStatement, paramList);
            } else {
                newStatement.add(enableUpdateStatement);
            }
        }
        //替换原来的老代码
        needUpdateBlock.modifyMethodBody(newStatement);
    }

    private void generateNewStatement(PendHighway pendHighway, List<JCTree.JCStatement> newStatement, SunList<Symbol.VarSymbol> paramList) {
        //先把方法入参当做材料添加进入 工厂中
        pendHighway.addMaterialsFromParameters(paramList);
        //把方法体中能访问到的所有参数当做材料添加进入 工厂中
        pendHighway.addMaterialsFromMethodBodyVar();
        //生成最终的转换代码
        AssembleResult assembleResult = pendHighway.assemble();

        //将生成的代码都放入结果集中
        newStatement.addAll(assembleResult.getStatements());

        //存在variableDecl 说明是一个 A a = Lamia.convert() 的形式，那么去替换 Lamia.convert()为新生成的对象
        //如果不存在，说明是 return Lamia.convert() 的形式，也生成对应的语句
        JCTree.JCStatement statement = pendHighway.getVariableDecl().map((variableDecl) -> {
            variableDecl.init = jcUtils.memberAccess(assembleResult.getNewInstantName());
            return (JCTree.JCStatement) variableDecl;
        }).orElseGet(() -> jcUtils.createReturn(assembleResult.getNewInstantName()));

        //放入最后的一条接收语句
        newStatement.add(statement);
    }


    /**
     * 去解析 原来的方法体, 每到一个 Lamia.convert()  都算一个 通路, 计算出代码有可能经过的所有通路, 并且把每一个通路中 可以访问到 变量给保存下来
     *
     * @param methodSymbolDecl
     * @return
     */
    private Set<NeedUpdateBlock> untieBlock(JCTree.JCMethodDecl methodSymbolDecl) {
        JCTree.JCBlock originalBody = methodSymbolDecl.body;
        LooseBlockVisitor looseBlockVisitor = new LooseBlockVisitor();
        looseBlockVisitor.accept(originalBody, classTree);
        return looseBlockVisitor.getNeedUpdateBlocks();
    }


}
