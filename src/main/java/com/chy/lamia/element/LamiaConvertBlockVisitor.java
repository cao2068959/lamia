package com.chy.lamia.element;


import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.element.annotation.AnnotationProxyFactory;
import com.chy.lamia.element.resolver.expression.LamiaExpression;
import com.chy.lamia.element.resolver.expression.LamiaExpressionResolver;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
import com.chy.lamia.utils.JCUtils;
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

    private LamiaExpressionResolver lamiaExpressionResolver = new LamiaExpressionResolver();


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
    public boolean expressionStatementVisit(JCTree.JCExpressionStatement expressionStatement) {
        JCTree.JCExpression expression = expressionStatement.expr;
        if (!(expression instanceof JCTree.JCAssign)) {
            return false;
        }
        JCTree.JCAssign assign = (JCTree.JCAssign) expression;

        //去收集写了 Lamia.convert 的语句
        LamiaConvertInfo lamiaConvertInfo = lamiaConvertStatementCollect(assign.rhs);

        String name = Optional.ofNullable(assign.lhs).map(Objects::toString).orElse(null);
        lamiaConvertInfo.setVarName(name);

        return lamiaConvertInfo == null;
    }


    @Override
    public boolean variableVisit(JCTree.JCVariableDecl statement) {

        //查找这一行语句上面有没有@MapMember
        Optional<MapMember> mapMember = AnnotationProxyFactory
                .createdAnnotation(classTree, statement.getModifiers().getAnnotations(), MapMember.class);

        // 解析这个变量把他转成 VarDefinition
        Type type = JCUtils.instance.attribType(classTree, statement);
        TypeDefinition typeDefinition = TypeDefinitionFactory.create(type);
        VarDefinition varDefinition = new VarDefinition(statement.getName().toString(), typeDefinition);
        varDefinition.setMapMember(mapMember);

        // 将这个变量放入 容器
        vars.put(varDefinition.getVarRealName(), varDefinition);

        //去收集写了 Lamia.convert 的语句
        LamiaConvertInfo lamiaConvertInfo = lamiaConvertStatementCollect(statement.init);

        // 如果该语句是对应的表达式,那么就不记录, 已经替换成新的表达式
        return lamiaConvertInfo == null;
    }


    /**
     * 去收集写了 Lamia.convert 的语句
     *
     * @param jcExpression
     * @return 是否收集到了 转换表达式
     */
    private LamiaConvertInfo lamiaConvertStatementCollect(JCTree.JCExpression jcExpression) {

        LamiaExpression lamiaExpression = lamiaExpressionResolver.resolving(jcExpression);

        if (lamiaExpression == null) {
            return null;
        }

        LamiaConvertInfo lamiaConvertInfo = new LamiaConvertInfo(lamiaExpression);
        // 要转换成的目标类型
        TypeDefinition convertTargetType = JCUtils.instance.toTypeDefinition(classTree, lamiaExpression.getTypeCast().clazz);
        lamiaConvertInfo.setTargetType(convertTargetType);

        Set<String> allArgsName = lamiaConvertInfo.getAllArgsName();
        allArgsName.stream().map(vars::get).filter(Objects::nonNull).forEach(lamiaConvertInfo::addVarArgs);

        // 替换当前的 statement
        getCurrentBlock().replaceStatement(lamiaConvertInfo);

        return lamiaConvertInfo;
    }


    @Override
    public boolean returnVisit(JCTree.JCReturn statement) {
        LamiaConvertInfo lamiaConvertInfo = lamiaConvertStatementCollect(statement.expr);

        return lamiaConvertInfo == null;
    }


    @Override
    public void visitorEnd() {
        if (currentBlock != null) {
            lamiaConvertHolderBlock.add(currentBlock);
        }
    }

    public LamiaConvertHolderBlock getCurrentBlock() {
        if (currentBlock != null) {
            return currentBlock;
        }
        currentBlock = new LamiaConvertHolderBlock(getProcessedFinishStatement(), getBlock());
        return currentBlock;
    }

    public List<LamiaConvertHolderBlock> getResult() {
        return lamiaConvertHolderBlock;
    }

}
