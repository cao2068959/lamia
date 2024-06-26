package com.chy.lamia.visitor;

import com.chy.lamia.context.LamiaContext;
import com.chy.lamia.convert.core.ConvertFactory;
import com.chy.lamia.convert.core.annotation.MapMember;
import com.chy.lamia.convert.core.components.entity.NewlyStatementHolder;
import com.chy.lamia.convert.core.entity.LamiaConvertInfo;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.entity.VarDefinition;
import com.chy.lamia.element.LamiaConvertBlockVisitor;
import com.chy.lamia.element.LamiaConvertHolderBlock;
import com.chy.lamia.element.annotation.AnnotationProxyFactory;
import com.chy.lamia.element.pos.PosUpdateTreeScanner;
import com.chy.lamia.entity.ClassTreeWrapper;
import com.chy.lamia.entity.StatementWrapper;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
import com.chy.lamia.processor.marked.MarkedMethods;
import com.chy.lamia.reporter.Reporter;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

import java.util.*;

public class MethodUpdateVisitor extends TreeTranslator {


    private final MarkedMethods markedMethods;
    private final String className;
    private final ClassTreeWrapper classTree;

    public MethodUpdateVisitor(MarkedMethods markedMethods, JCTree tree, String className) {
        this.markedMethods = markedMethods;
        this.classTree = new ClassTreeWrapper(tree);
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
        LamiaContext.currentElement = methodSymbol;
        // 方法所有的入参
        Map<String, VarDefinition> paramMap = new HashMap<>();
        //处理方法入参，解析泛型，类型转换
        methodSymbolDecl.sym.getParameters().forEach(varSymbol -> {
            // 把方法中的参数表达式转成 VarDefinition 对象
            VarDefinition varDefinition = toVarDefinition(varSymbol);
            paramMap.put(varDefinition.getVarName(), varDefinition);
        });


        //解析原来方法中的方法体,计算出所有需要去修改的 方法体
        List<LamiaConvertHolderBlock> needUpdateBlocks = findLamiaConvertBlock(methodSymbolDecl);
        for (LamiaConvertHolderBlock lamiaConvertHolderBlock : needUpdateBlocks) {
            updateBlock(lamiaConvertHolderBlock, paramMap);
        }

    }

    private VarDefinition toVarDefinition(Symbol.VarSymbol varSymbol) {
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


    private void updateBlock(LamiaConvertHolderBlock lamiaConvertHolderBlock, Map<String, VarDefinition> paramMap) {

        // 获取这个代码块中 所有的代码
        List<JCTree.JCStatement> statements = lamiaConvertHolderBlock.getContents();
        List<JCTree.JCStatement> newStatement = new LinkedList<>();

        PosUpdateTreeScanner statementScanner = new PosUpdateTreeScanner();

        for (JCTree.JCStatement statement : statements) {
            // 如果是 LamiaConvertInfo.Statement 说明这段代码本身就需要修改的
            if (statement instanceof StatementWrapper) {
                StatementWrapper wrapper = (StatementWrapper) statement;

                try {
                    LamiaConvertInfo lamiaConvertInfo = lamiaConvertHolderBlock.getLamiaConvertInfo(wrapper);
                    // 合并所有参数 之前只添加了 方法体中参与转换的参数, 现在把入参中的也添加进去
                    paramMap.forEach((__, value) -> lamiaConvertInfo.addScopeVar(value));
                    // 生成对应的转换语句
                    List<NewlyStatementHolder> makeResult = ConvertFactory.INSTANCE.make(lamiaConvertInfo);
                    makeResult.stream().map(s -> {
                        JCTree.JCStatement item = (JCTree.JCStatement) s.getStatement().get();
                        statementScanner.setPos(wrapper.getOldExpression().getStartPosition());
                        statementScanner.scan(item);
                        return item;
                    }).forEach(newStatement::add);

                } catch (RuntimeException e) {
                    Reporter.reportException(e, wrapper.getOldExpression());
                    return;
                }
            } else {
                newStatement.add(statement);
            }
        }
        //替换原来的老代码
        lamiaConvertHolderBlock.modifyMethodBody(newStatement);
    }


    /**
     * 去解析 原来的方法体, 每到一个 Lamia.convert() 就保存在 LamiaConvertHolderBlock 对象中,
     * <p>
     * 每一个 LamiaConvertHolderBlock 代表着存在 Lamia.convert 表达式的代码块, 如果一个代码块中同时出现了多个  Lamia.convert 表达式 那么将多个表达式存到一个 LamiaConvertHolderBlock 中
     *
     * @param methodSymbolDecl
     * @return
     */
    private List<LamiaConvertHolderBlock> findLamiaConvertBlock(JCTree.JCMethodDecl methodSymbolDecl) {
        JCTree.JCBlock originalBody = methodSymbolDecl.body;
        LamiaConvertBlockVisitor lamiaConvertBlockVisitor = new LamiaConvertBlockVisitor();
        lamiaConvertBlockVisitor.accept(originalBody, classTree);
        return lamiaConvertBlockVisitor.getResult();
    }

}



