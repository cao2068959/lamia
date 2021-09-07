package com.chy.lamia.element;


import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.element.annotation.AnnotationProxyFactory;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.ParameterTypeMemberAnnotation;
import com.chy.lamia.log.Logger;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.SymbolUtils;
import com.chy.lamia.visitor.AbstractBlockVisitor;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Pair;

import java.util.*;


public class LooseBlockVisitor extends AbstractBlockVisitor {


    private final Map<String, ParameterTypeMemberAnnotation> vars;
    private final Set<NeedUpdateBlock> needUpdateBlocks;
    private NeedUpdateBlock needUpdateBlock;

    public LooseBlockVisitor() {
        vars = new HashMap<>();
        needUpdateBlocks = new HashSet<>();
    }

    public LooseBlockVisitor(Map<String, ParameterTypeMemberAnnotation> vars, Set<NeedUpdateBlock> needUpdateBlocks) {
        this.vars = new HashMap<>(vars);
        this.needUpdateBlocks = needUpdateBlocks;
    }


    /**
     * 如果 遇到了 代码块 if while for 等 都递归进去 再次扫描一次
     *
     * @param statement
     */
    @Override
    public void blockVisit(JCTree.JCBlock statement) {
        LooseBlockVisitor looseBlockVisitor = new LooseBlockVisitor(vars, needUpdateBlocks);
        //继续去扫描代码块里面的代码
        looseBlockVisitor.accept(statement, classTree);
        //analyzeResult(looseBlockVisitor);
    }


    @Override
    public boolean variableVisit(JCTree.JCVariableDecl statement, List<JCTree.JCStatement> enableUpdateStatements) {

        //去收集写了 Lamia.convert 的语句
        boolean updateStatements = lamiaConvertStatementCollect(statement.init, enableUpdateStatements, statement);

        //查找这一行语句上面有没有@MapMember
        Optional<MapMember> mapMemberOptional = AnnotationProxyFactory
                .createdAnnotation(classTree, statement.getModifiers().getAnnotations(), MapMember.class);


        Type type = JCUtils.instance.attribType(classTree, statement);
        ParameterTypeMemberAnnotation parameterType = new ParameterTypeMemberAnnotation(statement.getName().toString(),
                type.toString(), mapMemberOptional);
        parameterType.setGeneric(SymbolUtils.getGeneric(type));
        vars.put(parameterType.getFieldName(), parameterType);
        return updateStatements;
    }


    /**
     * 去收集写了 Lamia.convert 的语句
     *
     * @param jcExpression
     * @param enableUpdateStatements
     * @param variableDecl
     */
    private boolean lamiaConvertStatementCollect(JCTree.JCExpression jcExpression, List<JCTree.JCStatement> enableUpdateStatements, JCTree.JCVariableDecl variableDecl) {
        if (jcExpression == null || !(jcExpression instanceof JCTree.JCTypeCast)) {
            return true;
        }

        //获取强转
        JCTree.JCTypeCast typeCast = (JCTree.JCTypeCast) jcExpression;

        JCTree.JCExpression expr = typeCast.expr;
        if (expr == null || !(expr instanceof JCTree.JCMethodInvocation)) {
            return true;
        }
        JCTree.JCMethodInvocation methodInvocation = (JCTree.JCMethodInvocation) expr;
        String methName = methodInvocation.meth.toString();
        if (!methName.contains("Lamia.convert")) {
            return true;
        }

        //去收集Lamia.convert() 方法中传入进来的参数
        List<String> argsName = new LinkedList<>();
        for (JCTree.JCExpression arg : methodInvocation.args) {
            argsName.add(arg.toString());
        }
        //没传入参数可能有点不太对
        if (argsName.size() == 0) {
            return true;
        }


        ParameterType parameterType = JCUtils.instance.generateParameterType(classTree, typeCast.clazz);

        if (parameterType == null) {
            Logger.log("无法解析类型 [" + typeCast.clazz.toString() + "]");
            return true;
        }

        PendHighway pendHighway = new PendHighway(vars, argsName, parameterType, variableDecl);
        enableUpdateStatements.add(pendHighway);

        //这个代码块已经添加过了，那么就不再去添加了
        if (needUpdateBlock == null) {
            NeedUpdateBlock result = new NeedUpdateBlock(this.block, enableUpdateStatements);
            needUpdateBlocks.add(result);
        }

        return false;
    }


    @Override
    public boolean returnVisit(JCTree.JCReturn statement, List<JCTree.JCStatement> enableUpdateStatements) {
        return lamiaConvertStatementCollect(statement.expr, enableUpdateStatements, null);
    }

    public Set<NeedUpdateBlock> getNeedUpdateBlocks() {
        return needUpdateBlocks;
    }
}
