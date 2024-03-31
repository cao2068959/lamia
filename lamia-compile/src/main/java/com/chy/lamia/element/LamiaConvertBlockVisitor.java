package com.chy.lamia.element;


import com.chy.lamia.convert.core.annotation.MapMember;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.entity.LamiaConvertInfo;
import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.entity.VarDefinition;
import com.chy.lamia.element.annotation.AnnotationProxyFactory;
import com.chy.lamia.element.resolver.expression.LamiaExpressionResolver;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.visitor.AbstractBlockVisitor;
import com.chy.lamia.visitor.LambdaLineBlockTree;
import com.sun.source.tree.Tree;
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
     * 需要去更新的块
     * 持有了 所有 lamia.convert 语句 的代码块, 这些代码块也就是后续需要去修改内容的
     */
    private List<LamiaConvertHolderBlock> updateBlocks = new ArrayList<>();

    /**
     * 当前块中的内容，这个块里面存在 lamia表达式才会去记录，否则是null
     */
    private LamiaConvertHolderBlock currentBlock;

    private LamiaExpressionResolver lamiaExpressionResolver = new LamiaExpressionResolver();


    public LamiaConvertBlockVisitor() {
        this.updateBlocks = new ArrayList<>();
        vars = new HashMap<>();
    }

    public LamiaConvertBlockVisitor(Map<String, VarDefinition> vars, List<LamiaConvertHolderBlock> lamiaConvertHolderBlock) {
        // 镜像拷贝
        this.vars = new HashMap<>(vars);
        this.updateBlocks = lamiaConvertHolderBlock;
    }


    /**
     * 如果 遇到了 代码块 if while for 等 都递归进去 再次扫描一次
     *
     * @param block statement
     */
    @Override
    public void blockVisit(JCTree.JCBlock block) {
        //继续去扫描代码块里面的代码
        getNewVisitor().accept(block, classTree);
    }

    @Override
    public boolean expressionStatementVisit(JCTree.JCExpressionStatement expressionStatement) {
        JCTree.JCExpression expression = expressionStatement.expr;
        if (expression instanceof JCTree.JCAssign) {
            return assignExpressionHandle((JCTree.JCAssign) expression);
        }

        if (expression instanceof JCTree.JCMethodInvocation) {
            return methodInvocationExpressionHandle((JCTree.JCMethodInvocation) expression);
        }

        return true;

    }

    private boolean methodInvocationExpressionHandle(JCTree.JCMethodInvocation expression) {
        LamiaConvertInfo lamiaConvertInfo = lamiaConvertStatementCollect(expression);
        if (lamiaConvertInfo == null) {
            scanLambdaExpression(expression);
            return true;
        }
        return false;
    }

    private boolean assignExpressionHandle(JCTree.JCAssign assign) {
        //去收集写了 Lamia.convert 的语句
        LamiaConvertInfo lamiaConvertInfo = lamiaConvertStatementCollect(assign.rhs);
        if (lamiaConvertInfo != null) {
            String name = Optional.ofNullable(assign.lhs).map(Objects::toString).orElse(null);
            lamiaConvertInfo.setResultVarName(name);
            lamiaConvertInfo.setDeclareResultVarType(false);
            // 这一行就是 lamia表达式，就不记录了
            return false;
        }

        scanLambdaExpression(assign);
        return true;
    }


    @Override
    public boolean variableVisit(JCTree.JCVariableDecl statement) {

        //查找这一行语句上面有没有@MapMember
        Optional<MapMember> mapMember = AnnotationProxyFactory
                .createdAnnotation(classTree, statement.getModifiers().getAnnotations(), MapMember.class);

        // 解析这个变量把他转成 VarDefinition
        Type type = classTree.getFullType(statement.vartype);
        TypeDefinition typeDefinition = TypeDefinitionFactory.create(type);
        VarDefinition varDefinition = new VarDefinition(statement.getName().toString(), typeDefinition);
        varDefinition.setMapMember(mapMember);

        // 将这个变量放入 容器
        vars.put(varDefinition.getVarRealName(), varDefinition);

        //去收集写了 Lamia.convert 的语句
        LamiaConvertInfo lamiaConvertInfo = lamiaConvertStatementCollect(statement.init);
        if (lamiaConvertInfo != null) {
            lamiaConvertInfo.setResultVarName(varDefinition.getVarRealName());
            // 这一行就是 lamia表达式，就不记录了
            return false;
        }
        scanLambdaExpression(statement);
        return true;
    }

    /**
     * 扫描lambda 表达式
     *
     * @param tree
     */
    private void scanLambdaExpression(Tree tree) {
        LambdaBodyFinder lambdaBodyFinder = new LambdaBodyFinder(classTree);
        lambdaBodyFinder.scan(tree, null);
        List<JCLambdaWrapper> lambdaExpressionTrees = lambdaBodyFinder.getResult();
        if (lambdaExpressionTrees == null) {
            return;
        }

        for (JCLambdaWrapper lambdaExpressionTree : lambdaExpressionTrees) {
            lambdaExpressionHandle(lambdaExpressionTree);
        }
    }

    private void lambdaExpressionHandle(JCLambdaWrapper lambdaExpressionTree) {
        Tree body = lambdaExpressionTree.getBody();

        if (body instanceof JCTree.JCBlock) {
            getLambdaParamVisitor(lambdaExpressionTree).accept((JCTree.JCBlock) body, classTree);
            return;
        }
        // 没有 {} 的单纯的lambda 的形式
        if (body instanceof JCTree.JCExpression) {
            JCTree.JCExpression expression = (JCTree.JCExpression) body;
            // 如果不是块lambda那么默认以 return的方式来处理
            JCTree.JCStatement statement = JCUtils.instance.createReturn(expression);
            LambdaLineBlockTree blockTree = new LambdaLineBlockTree(statement, lambdaExpressionTree);
            //继续去扫描代码块里面的代码
            getLambdaParamVisitor(lambdaExpressionTree).accept(blockTree, classTree);
            return;
        }
    }

    private LamiaConvertBlockVisitor getLambdaParamVisitor(JCLambdaWrapper lambdaWrapper) {
        LamiaConvertBlockVisitor newVisitor = getNewVisitor();
        // 把lambda 表达式的参数也放进去
        lambdaWrapper.params().forEach(param -> newVisitor.vars.put(param.getVarRealName(), param));
        return newVisitor;
    }


    /**
     * 去收集写了 Lamia.convert 的语句
     *
     * @param jcExpression
     * @return 是否收集到了 转换表达式
     */
    private LamiaConvertInfo lamiaConvertStatementCollect(JCTree.JCExpression jcExpression) {

        LamiaExpression lamiaExpression = lamiaExpressionResolver.resolving(classTree, jcExpression);

        if (lamiaExpression == null) {
            return null;
        }

        LamiaConvertInfo lamiaConvertInfo = new LamiaConvertInfo(lamiaExpression);

        Expression targetExpression = lamiaExpression.getTarget();

        // 如果在表达式 设置了 build(*) ，那么就根据Build的参数去找对应的变量, 如果没有就根据强转类型去找最终要转换的类型
        if (targetExpression != null) {
            parseBuildTarget(lamiaConvertInfo, targetExpression, jcExpression);
        } else {
            TypeDefinition targetType = lamiaExpression.getTargetType();
            if (targetType == null) {
                throw new RuntimeException("表达式[" + jcExpression.toString() + "] 没有设置要转换的类型，可以设置对应的强转类型，或者在 build() 方法中指定要转换的类型或对象");
            }
            lamiaConvertInfo.setTargetType(targetType);
        }


        Set<String> allArgsName = lamiaConvertInfo.getAllArgsName();
        allArgsName.stream().map(vars::get).filter(Objects::nonNull).forEach(lamiaConvertInfo::addVarArgs);

        // 替换当前的 statement
        getCurrentBlock().replaceStatement(lamiaConvertInfo);

        return lamiaConvertInfo;
    }

    private void parseBuildTarget(LamiaConvertInfo lamiaConvertInfo, Expression buildParam,
                                  JCTree.JCExpression jcExpression) {
        Object buildParamJcExpression = buildParam.get();
        // 直接在build() 中写了一个变量名引用的，直接去上下文中去找这个变量是什么
        if (buildParamJcExpression instanceof JCTree.JCIdent) {
            String targetName = buildParamJcExpression.toString();
            VarDefinition varDefinition = vars.get(targetName);
            if (varDefinition == null) {
                throw new RuntimeException("表达式[" + jcExpression.toString() + "] 设置的target 实例有误[" + targetName + "]");
            }
            lamiaConvertInfo.setTarget(varDefinition);
            return;
        }

        // 在build中写的是 变量.属性 的形式， 如 build(User.class), 这里只支持 .class 的方式，不支持 build(map.getUserVO())
        if (buildParamJcExpression instanceof JCTree.JCFieldAccess) {
            JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) buildParamJcExpression;
            String targetName = fieldAccess.name.toString();
            if ("class".equals(targetName)) {
                JCTree.JCExpression typeSimpleName = fieldAccess.selected;
                Type type = classTree.getFullType(typeSimpleName);
                lamiaConvertInfo.setTargetType(new TypeDefinition(type.toString()));
            } else {
                throw new RuntimeException("表达式[" + jcExpression.toString() + "] 设置的target 实例有误[" + fieldAccess + "] 请使用 .class 的形式来设置对应的类型");
            }
        }


    }

    private LamiaConvertBlockVisitor getNewVisitor() {
        return new LamiaConvertBlockVisitor(vars, updateBlocks);
    }

    @Override
    public boolean returnVisit(JCTree.JCReturn statement) {
        LamiaConvertInfo lamiaConvertInfo = lamiaConvertStatementCollect(statement.expr);
        if (lamiaConvertInfo != null) {
            lamiaConvertInfo.setReturn(true);
            return false;
        }

        scanLambdaExpression(statement);
        return true;
    }


    @Override
    public void visitorEnd() {
        if (currentBlock != null) {
            updateBlocks.add(currentBlock);
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
        return updateBlocks;
    }

}
