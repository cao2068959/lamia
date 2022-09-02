package com.chy.lamia.element;


import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.element.annotation.AnnotationProxyFactory;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.ParameterTypeMemberAnnotation;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.log.Logger;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.SymbolUtils;
import com.chy.lamia.visitor.AbstractBlockVisitor;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;

import java.util.*;


/**
 * @author bignosecat
 */
public class LamiaConvertBlockVisitor extends AbstractBlockVisitor {

    /**
     * 这个 block 中能够遇到的所有变量
     */
    private final Map<String, VarDefinition> vars;


    /**
     * 持有了 所有 lamia.convert 语句 的代码块, 这些代码块也就是后续需要去修改内容的
     */
    private final List<LamiaConvertHolderBlock> lamiaConvertHolderBlock;

    private LamiaConvertHolderBlock currentBlock;


    public LamiaConvertBlockVisitor() {
        lamiaConvertHolderBlock = new ArrayList<>();
        vars = new HashMap<>();
    }

    public LamiaConvertBlockVisitor(Map<String, VarDefinition> vars, List<LamiaConvertHolderBlock> lamiaConvertHolderBlock) {
        // 镜像拷贝
        this.vars = new HashMap<>(vars);
        this.lamiaConvertHolderBlock = lamiaConvertHolderBlock;
    }

    /**
     * 如果 遇到了 代码块 if while for 等 都递归进去 再次扫描一次
     *
     * @param statement statement
     */
    @Override
    public void blockVisit(JCTree.JCBlock statement) {
        LamiaConvertBlockVisitor lamiaConvertScopeBlockVisitor = new LamiaConvertBlockVisitor(vars, lamiaConvertHolderBlock);
        //继续去扫描代码块里面的代码
        lamiaConvertScopeBlockVisitor.accept(statement, classTree);
    }


    @Override
    public boolean variableVisit(JCTree.JCVariableDecl statement) {

        //查找这一行语句上面有没有@MapMember
        Optional<MapMember> mapMemberOptional = AnnotationProxyFactory
                .createdAnnotation(classTree, statement.getModifiers().getAnnotations(), MapMember.class);

        Type type = JCUtils.instance.attribType(classTree, statement);
        ParameterTypeMemberAnnotation parameterType = new ParameterTypeMemberAnnotation(statement.getName().toString(),
                type.toString(), mapMemberOptional);
        parameterType.setGeneric(SymbolUtils.getGeneric(type));
        vars.put(parameterType.getFieldName(), parameterType);


        //去收集写了 Lamia.convert 的语句
        boolean isLamiaConvert = lamiaConvertStatementCollect(statement.init, statement);
        // 如果该语句是对应的表达式,那么就不记录, 已经替换成新的表达式
        return !isLamiaConvert;
    }


    /**
     * 去收集写了 Lamia.convert 的语句
     *
     * @param jcExpression
     * @param variableDecl
     * @return 是否收集到了 转换表达式
     */
    private boolean lamiaConvertStatementCollect(JCTree.JCExpression jcExpression, JCTree.JCVariableDecl variableDecl) {
        if (!(jcExpression instanceof JCTree.JCTypeCast)) {
            return false;
        }

        //获取强转
        JCTree.JCTypeCast typeCast = (JCTree.JCTypeCast) jcExpression;

        JCTree.JCExpression expr = typeCast.expr;
        if (!(expr instanceof JCTree.JCMethodInvocation)) {
            return false;
        }
        JCTree.JCMethodInvocation methodInvocation = (JCTree.JCMethodInvocation) expr;
        String methName = methodInvocation.meth.toString();
        if (!methName.contains("Lamia.convert")) {
            return false;
        }

        //去收集Lamia.convert() 方法中传入进来的参数
        List<String> argsName = new LinkedList<>();
        for (JCTree.JCExpression arg : methodInvocation.args) {
            argsName.add(arg.toString());
        }
        //没传入参数可能有点不太对
        if (argsName.size() == 0) {
            return false;
        }

        // 解析强转成为什么类型
        ParameterType parameterType = JCUtils.instance.generateParameterType(classTree, typeCast.clazz);


        if (parameterType == null) {
            Logger.log("无法解析类型 [" + typeCast.clazz.toString() + "]");
            return false;
        }


        PendHighway pendHighway = new PendHighway(vars, argsName, parameterType, variableDecl);
        enableUpdateStatements.add(pendHighway);


        //这个代码块已经添加过了，那么就不再去添加了
        if (needUpdateBlock == null) {
            NeedUpdateBlock result = new NeedUpdateBlock(this.block, enableUpdateStatements);
            needUpdateBlocks.add(result);
        }

        return true;
    }


    @Override
    public boolean returnVisit(JCTree.JCReturn statement) {
        return lamiaConvertStatementCollect(statement.expr, null);
    }

    public LamiaConvertHolderBlock getCurrentBlock() {
        if (currentBlock != null) {
            return currentBlock;
        }
        currentBlock = new LamiaConvertHolderBlock();
        return currentBlock;
    }

    public Set<NeedUpdateBlock> getNeedUpdateBlocks() {
        return needUpdateBlocks;
    }
}
