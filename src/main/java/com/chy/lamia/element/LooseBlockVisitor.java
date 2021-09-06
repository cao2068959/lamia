package com.chy.lamia.element;


import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.element.annotation.AnnotationProxyFactory;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.log.Logger;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.SymbolUtils;
import com.chy.lamia.visitor.AbstractBlockVisitor;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Pair;

import java.util.*;


public class LooseBlockVisitor extends AbstractBlockVisitor {


    private final List<Pair<ParameterType, MapMember>> vars;
    private final Set<NeedUpdateBlock> needUpdateBlocks;
    private NeedUpdateBlock needUpdateBlock;

    public LooseBlockVisitor() {
        vars = new LinkedList<>();
        needUpdateBlocks = new HashSet<>();
    }

    public LooseBlockVisitor(List<Pair<ParameterType, MapMember>> vars, Set<NeedUpdateBlock> needUpdateBlocks) {
        this.vars = new LinkedList<>(vars);
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

        //没标记注解的就不处理了
        if (mapMemberOptional.isEmpty()) {
            return updateStatements;
        }

        Type type = JCUtils.instance.attribType(classTree, statement);
        ParameterType parameterType = new ParameterType(statement.getName().toString(), type.toString());
        MapMember mapMember = mapMemberOptional.get();
        parameterType.setGeneric(SymbolUtils.getGeneric(type));
        vars.add(Pair.of(parameterType, mapMember));

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

        Type type = JCUtils.instance.attribType(classTree, typeCast.clazz.toString());
        if (type == null) {
            Logger.log("无法解析泛型对象 [" + typeCast.clazz.toString() + "]");
            return true;
        }

        PendHighway pendHighway = new PendHighway(vars, new ParameterType(type.toString()), variableDecl);
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


    public List<Pair<ParameterType, MapMember>> getVars() {
        return vars;
    }

    public Set<NeedUpdateBlock> getNeedUpdateBlocks() {
        return needUpdateBlocks;
    }
}
