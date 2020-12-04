package com.chy.lamia.visitor;


import com.chy.lamia.element.AssembleFactory;
import com.chy.lamia.element.ClassElement;
import com.chy.lamia.element.LooseBlock;
import com.chy.lamia.element.LooseBlockVisitor;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.SunList;
import com.chy.lamia.processor.marked.MarkedMethods;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MethodUpdateVisitor extends TreeTranslator {


    private final MarkedMethods markedMethods;
    private final JCUtils jcUtils;

    public MethodUpdateVisitor(MarkedMethods markedMethods, JCUtils jcUtils) {
        this.markedMethods = markedMethods;
        this.jcUtils = jcUtils;

    }


    @Override
    public void visitMethodDef(JCTree.JCMethodDecl methodSymbolDecl) {
        updateMethod(methodSymbolDecl);
        super.visitMethodDef(methodSymbolDecl);
    }

    private void updateMethod(JCTree.JCMethodDecl methodSymbolDecl) {
        Symbol.MethodSymbol methodSymbol = methodSymbolDecl.sym;
        //确认这个方法是不是应该被修改 及打了注解 @SmartReturn
        if (!markedMethods.contains(methodSymbol.toString())) {
            return;
        }

        //解析这个方法的返回值
        Type returnType = methodSymbol.getReturnType();
        //返回值不是一个对象就不进行处理了
        if (returnType.getTag() != TypeTag.CLASS) {
            return;
        }
        //解析返回值 的类结构
        ClassElement returnClassElement = ClassElement.getClassElement(returnType.toString(), jcUtils);
        //根据不同的策略获取 返回值的生成工厂
        AssembleFactory assembleFactory = returnClassElement.getAssembleFactory();
        untieBlock(methodSymbolDecl);

        //把所有的方法变量以及入参变量 都传入 组装工厂里, 让工厂自己判断到底应该如何去生成结果对象
        assembleReady(methodSymbolDecl, assembleFactory);
        //获取 结果对象的 生成的语句
        List<JCTree.JCStatement> treeStatements = assembleFactory.generateTree();
        //把生成的语句插入到原来的代码中
        doUpdateMethod(methodSymbolDecl, treeStatements);

    }


    private void untieBlock(JCTree.JCMethodDecl methodSymbolDecl) {
        JCTree.JCBlock originalBody = methodSymbolDecl.body;
        LooseBlockVisitor looseBlockVisitor = new LooseBlockVisitor();
        looseBlockVisitor.accept(originalBody);
        List<LooseBlock> looseBlocks = looseBlockVisitor.getResult();
        System.out.println(looseBlocks);
    }


    private void doUpdateMethod(JCTree.JCMethodDecl methodSymbolDecl, List<JCTree.JCStatement> treeStatements) {
        JCTree.JCBlock oldBody = methodSymbolDecl.getBody();
        //把新的代码加在 自定义的代码之后， return 之前
        oldBody.getStatements().forEach(satement -> {
            treeStatements.add(satement);
        });

        methodSymbolDecl.body = jcUtils.createBlock(treeStatements);
    }

    private void assembleReady(JCTree.JCMethodDecl methodSymbolDecl, AssembleFactory assembleFactory) {
        SunList<Symbol.VarSymbol> paramList = new SunList<>(methodSymbolDecl.sym.getParameters());
        //先把 方法入参的所有变量给 传入到工厂里
        assembleForParameters(paramList, assembleFactory);
    }


    private void assembleForParameters(SunList<Symbol.VarSymbol> params, AssembleFactory assembleFactory) {
        if (params == null || params.size() == 0) {
            return;
        }
        params.forEach(varSymbol -> {
            //先把 类型转成 ClassElement 方便获取 getter setter 等一系列的方法
            ClassElement classElement = ClassElement.getClassElement(varSymbol.type.toString(), jcUtils);
            //获取这个类里面所有的 getter 方法
            Map<String, Getter> getters = classElement.getInstantGetters();
            getters.forEach((k, v) -> {
                JCTree.JCExpressionStatement getterExpression = jcUtils.execMethod(varSymbol.name.toString(),
                        v.getSimpleName(), new LinkedList<>());
                assembleFactory.match(k, v.getTypePath(), getterExpression.expr);
            });
        });
    }

}
