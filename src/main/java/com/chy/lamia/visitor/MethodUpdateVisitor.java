package com.chy.lamia.visitor;


import com.chy.lamia.element.AssembleFactory;
import com.chy.lamia.element.ClassElement;
import com.chy.lamia.element.LooseBlock;
import com.chy.lamia.element.LooseBlockVisitor;
import com.chy.lamia.entity.AssembleResult;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.NameAndType;
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

import static com.chy.lamia.constant.PriorityConstant.*;

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
        //获取方法中所有的入参
        SunList<Symbol.VarSymbol> paramList = new SunList<>(methodSymbolDecl.sym.getParameters());
        //解析原来方法中的方法体,计算出 所有的通路
        List<LooseBlock> looseBlocks = untieBlock(methodSymbolDecl);
        for (LooseBlock looseBlock : looseBlocks) {
            assemble(looseBlock, paramList, assembleFactory);
        }
    }

    private void assemble(LooseBlock looseBlock, SunList<Symbol.VarSymbol> paramList, AssembleFactory assembleFactory) {
        //先清空
        assembleFactory.clear();
        //先把方法入参当做材料添加进入 工厂中
        addMaterialsFromParameters(paramList, assembleFactory);
        //把方法体中能访问到的所有参数当做材料添加进入 工厂中
        addMaterialsFromMethodBodyVar(looseBlock.getVars(), assembleFactory);
        //去修改老的方法的 方法体
        modifyMethodBody(looseBlock, assembleFactory);
    }

    /**
     * 去修改 方法体里的内容
     *
     * @param looseBlock
     * @param assembleFactory
     */
    private void modifyMethodBody(LooseBlock looseBlock, AssembleFactory assembleFactory) {

        //获取 结果对象的 生成的语句
        AssembleResult assembleResult = assembleFactory.generateTree();

        //要去修改之前的方法,要先把之前的方法给拿出来
        JCTree.JCBlock oldBlock = looseBlock.getBlock();
        List<JCTree.JCStatement> newStatement = new LinkedList<>();
        for (JCTree.JCStatement oldStatement : oldBlock.getStatements()) {
            // 先把老的方法 到 return之前的语句都复制一份
            if (!(oldStatement instanceof JCTree.JCReturn)) {
                newStatement.add(oldStatement);
                continue;
            }
            //复制到 return语句了, 抛弃老的 return 语句, 把新生成的语句加上去
            for (JCTree.JCStatement treeStatement : assembleResult.getStatements()) {
                newStatement.add(treeStatement);
            }

            //生成 新的 return 语句
            String newInstantName = assembleResult.getNewInstantName();
            JCTree.JCReturn aReturn = jcUtils.createReturn(newInstantName);
            newStatement.add(aReturn);
        }

        JCTree.JCBlock block = jcUtils.createBlock(newStatement);
        looseBlock.modifyBody(block);
    }


    /**
     * 去解析 原来的方法体, 每到一个 return  都算一个 通路, 计算出代码有可能经过的所有通路, 并且把每一个通路中 可以访问到 变量给保存下来
     * func(A a,B b){
     *   C c = ...
     *   if(..){
     *       D d = ..
     *       return
     *   }
     *   E e = ..
     *   return
     * }
     * 上述代码 可以解析出 2条 通路
     * 1 . 进入到 if 中 return结束 , 可以访问的 变量有 c,d
     * 2 . 不经过 if 直到方法结束 , 可以访问到的 变量有 c,e
     * 这里仅仅只解析 方法体中的变量, 方法的入参并不计算在内
     *
     * @param methodSymbolDecl
     * @return
     */
    private List<LooseBlock> untieBlock(JCTree.JCMethodDecl methodSymbolDecl) {
        JCTree.JCBlock originalBody = methodSymbolDecl.body;
        LooseBlockVisitor looseBlockVisitor = new LooseBlockVisitor();
        looseBlockVisitor.accept(originalBody);
        List<LooseBlock> looseBlocks = looseBlockVisitor.getResult();
        return looseBlocks;
    }

    private void addMaterialsFromMethodBodyVar(List<NameAndType> methodBodyVars, AssembleFactory assembleFactory) {
        if (methodBodyVars == null || methodBodyVars.size() == 0) {
            return;
        }
        methodBodyVars.forEach(methodBodyVar -> {
            assembleFactory.match(methodBodyVar, jcUtils.memberAccess(methodBodyVar.getName()), METHOD_BODY_VAR);
        });
    }

    private void addMaterialsFromParameters(SunList<Symbol.VarSymbol> params, AssembleFactory assembleFactory) {
        if (params == null || params.size() == 0) {
            return;
        }
        params.forEach(varSymbol -> {
            NameAndType nameAndType = new NameAndType(varSymbol.name.toString(), varSymbol.type.toString());
            //先把 这个参数本身给塞入工厂
            assembleFactory.match(nameAndType, jcUtils.memberAccess(nameAndType.getName()), PARAMETERS);

            //解析这个类里面所有的 getter setter 塞入构造工厂中
            anatomyClassToAssembleFactory(varSymbol.type.toString(), varSymbol.name.toString(),
                    assembleFactory, jcUtils, PARAMETERS_IN_VAR);
        });
    }


    /**
     * 解析类里面所有的 getter方法，把这些 getter方法放入 AssembleFactory 去匹配
     *
     * @param classpath
     * @param instanceName
     * @param assembleFactory
     * @param jcUtils
     */
    private void anatomyClassToAssembleFactory(String classpath, String instanceName,
                                               AssembleFactory assembleFactory, JCUtils jcUtils,
                                               Integer priority) {
        //先把 类型转成 ClassElement 方便获取 getter setter 等一系列的方法
        ClassElement classElement = ClassElement.getClassElement(classpath, jcUtils);
        //获取这个类里面所有的 getter 方法
        Map<String, Getter> getters = classElement.getInstantGetters();
        getters.forEach((k, v) -> {
            //生成 a.getXX() 的表达式
            JCTree.JCExpressionStatement getterExpression = jcUtils.execMethod(instanceName, v.getSimpleName(),
                    new LinkedList<>());
            NameAndType nameAndType = new NameAndType(k, v.getTypePath());
            //将表达式放入 合成工厂去匹配
            assembleFactory.match(nameAndType, getterExpression.expr, priority);
        });
    }


}
