package com.chy.lamia.visitor;


import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.element.NeedUpdateBlock;
import com.chy.lamia.element.PendHighway;
import com.chy.lamia.element.LooseBlockVisitor;
import com.chy.lamia.element.annotation.AnnotationProxyFactory;
import com.chy.lamia.element.assemble.AssembleResult;
import com.chy.lamia.element.funicle.FunicleFactory;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.ParameterTypeMemberAnnotation;
import com.chy.lamia.entity.SunList;
import com.chy.lamia.expose.Lamia;
import com.chy.lamia.processor.marked.MarkedMethods;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.SymbolUtils;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Pair;

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

        Map<String, ParameterTypeMemberAnnotation> paramMap = new HashMap<>();
        //处理方法入参，解析泛型，类型转换
        new SunList<>(methodSymbolDecl.sym.getParameters()).forEach(varSymbol -> {
            ParameterTypeMemberAnnotation parameterType = toParameterType(varSymbol);
            paramMap.put(parameterType.getFieldName(), parameterType);
        });

        //解析原来方法中的方法体,计算出所有需要去修改的 方法体
        Set<NeedUpdateBlock> needUpdateBlocks = untieBlock(methodSymbolDecl);
        for (NeedUpdateBlock needUpdateBlock : needUpdateBlocks) {
            updateBlock(needUpdateBlock, paramMap);
        }

    }

    private ParameterTypeMemberAnnotation toParameterType(Symbol.VarSymbol varSymbol) {
        String paramsTypeClassPath = varSymbol.type.tsym.toString();
        Optional<MapMember> mapMember = AnnotationProxyFactory
                .createdAnnotation(varSymbol.type.getAnnotationMirrors(), MapMember.class);
        String name = mapMember.map(_mapMember -> {
            String result = _mapMember.value();
            if ("".equals(result)) {
                return varSymbol.name.toString();
            }
            return result;
        }).orElse(varSymbol.name.toString());

        ParameterTypeMemberAnnotation parameterType = new ParameterTypeMemberAnnotation(name, paramsTypeClassPath, mapMember);
        //把这个字段原本的字段名称塞进去
        parameterType.setFieldName(varSymbol.name.toString());
        //这个参数可能会有泛型
        List<ParameterType> generic = SymbolUtils.getGeneric(varSymbol);
        parameterType.setGeneric(generic);
        return parameterType;
    }


    /**
     * 去修改方法体中的代码
     *
     * @param needUpdateBlock
     * @param paramMap
     */
    private void updateBlock(NeedUpdateBlock needUpdateBlock, Map<String, ParameterTypeMemberAnnotation> paramMap) {
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
            variableDecl.init = jcUtils.memberAccess(assembleResult.getNewInstantName());
            return (JCTree.JCStatement) variableDecl;
        }).orElseGet(() -> jcUtils.createReturn(assembleResult.getNewInstantName()));

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
    private Set<NeedUpdateBlock> untieBlock(JCTree.JCMethodDecl methodSymbolDecl) {
        JCTree.JCBlock originalBody = methodSymbolDecl.body;
        LooseBlockVisitor looseBlockVisitor = new LooseBlockVisitor();
        looseBlockVisitor.accept(originalBody, classTree);
        return looseBlockVisitor.getNeedUpdateBlocks();
    }


}
