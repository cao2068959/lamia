package com.chy.lamia.visitor;


import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.element.LamiaConvertScope;
import com.chy.lamia.element.NeedUpdateBlock;
import com.chy.lamia.element.PendHighway;
import com.chy.lamia.element.LamiaConvertScopeBlockVisitor;
import com.chy.lamia.element.annotation.AnnotationProxyFactory;
import com.chy.lamia.element.assemble.AssembleResult;
import com.chy.lamia.element.funicle.FunicleFactory;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
import com.chy.lamia.processor.marked.MarkedMethods;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.code.Symbol;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

import java.util.*;

public class MethodUpdateVisitor extends TreeTranslator {


    private final MarkedMethods markedMethods;
    private final String className;
    private final JCTree classTree;

    public MethodUpdateVisitor(MarkedMethods markedMethods, JCTree tree, String className) {
        this.markedMethods = markedMethods;
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
        // 方法所有的入参
        Map<String, VarDefinition> paramMap = new HashMap<>();
        //处理方法入参，解析泛型，类型转换
        methodSymbolDecl.sym.getParameters().forEach(varSymbol -> {
            // 把方法中的参数表达式转成 VarDefinition 对象
            VarDefinition varDefinition = toVarDefinition(varSymbol);
            paramMap.put(varDefinition.getVarName(), varDefinition);
        });

        //解析原来方法中的方法体,计算出所有需要去修改的 方法体
        Set<NeedUpdateBlock> needUpdateBlocks = findLamiaConvertScope(methodSymbolDecl);
        for (NeedUpdateBlock needUpdateBlock : needUpdateBlocks) {
            updateBlock(needUpdateBlock, paramMap);
        }

    }

    private VarDefinition toVarDefinition(Symbol.VarSymbol varSymbol) {
        String paramsTypeClassPath = varSymbol.type.tsym.toString();
        // 解析表达式，转成对应的类型描述对象，方便后续操作
        TypeDefinition typeDefinition = TypeDefinitionFactory.create(varSymbol);

        // 寻找他是否有注解 @MapMember
        Optional<MapMember> mapMember = AnnotationProxyFactory
                .createdAnnotation(varSymbol.type.getAnnotationMirrors(), MapMember.class);
        String varRealName = varSymbol.name.toString();

        // 名称 + 类型 组成一个变量
        VarDefinition result = new VarDefinition(varRealName, typeDefinition);
        result.setMapMember(mapMember);
        return result;
    }


    /**
     * 去修改方法体中的代码
     *
     * @param needUpdateBlock
     * @param paramMap
     */
    private void updateBlock(NeedUpdateBlock needUpdateBlock, Map<String, VarDefinition> paramMap) {

        List<JCTree.JCStatement> enableUpdateStatements = needUpdateBlock.getEnableUpdateStatements();

        List<JCTree.JCStatement> newStatement = new LinkedList<>();
        for (JCTree.JCStatement enableUpdateStatement : enableUpdateStatements) {
            if (enableUpdateStatement instanceof PendHighway) {
                PendHighway pendHighway = (PendHighway) enableUpdateStatement;
                //把方法的入参放进去
                pendHighway.setParamVars(paramMap);
                generateNewStatement(pendHighway, newStatement);
            } else {
                newStatement.add(enableUpdateStatement);
            }
        }
        //替换原来的老代码
        needUpdateBlock.modifyMethodBody(newStatement);
    }

    private void generateNewStatement(PendHighway pendHighway, List<JCTree.JCStatement> newStatement) {

        //把所有的材料添加进工厂
        pendHighway.addMaterials();
        //生成最终的转换代码
        AssembleResult assembleResult = pendHighway.assemble();

        //将生成的代码都放入结果集中
        newStatement.addAll(assembleResult.getStatements());

        //存在variableDecl 说明是一个 A a = Lamia.convert() 的形式，那么去替换 Lamia.convert()为新生成的对象
        //如果不存在，说明是 return Lamia.convert() 的形式，也生成对应的语句
        JCTree.JCStatement statement = pendHighway.getVariableDecl().map((variableDecl) -> {
            variableDecl.init = JCUtils.instance.memberAccess(assembleResult.getNewInstantName());
            return (JCTree.JCStatement) variableDecl;
        }).orElseGet(() -> JCUtils.instance.createReturn(assembleResult.getNewInstantName()));

        //放入最后的一条接收语句
        newStatement.add(statement);

        Set<String> dependentClassPath = assembleResult.getDependentClassPath();
        dependentClassPath.add(pendHighway.genTypePath());
        //设置对应的脐带
        FunicleFactory.addDependent(className, dependentClassPath);
    }

    /**
     * 去解析 原来的方法体, 每到一个 Lamia.convert()  都算一个 通路, 计算出代码有可能经过的所有通路, 并且把每一个通路中 可以访问到 变量给保存下来
     *
     * @param methodSymbolDecl
     * @return
     */
    private Set<LamiaConvertScope> findLamiaConvertScope(JCTree.JCMethodDecl methodSymbolDecl) {
        JCTree.JCBlock originalBody = methodSymbolDecl.body;
        LamiaConvertScopeBlockVisitor lamiaConvertScopeBlockVisitor = new LamiaConvertScopeBlockVisitor();
        lamiaConvertScopeBlockVisitor.accept(originalBody, classTree);
        return lamiaConvertScopeBlockVisitor.getNeedUpdateBlocks();
    }


}
