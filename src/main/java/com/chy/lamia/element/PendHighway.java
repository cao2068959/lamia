package com.chy.lamia.element;


import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.element.annotation.AnnotationProxyFactory;
import com.chy.lamia.element.assemble.AssembleFactoryHolder;
import com.chy.lamia.element.assemble.AssembleMaterial;
import com.chy.lamia.element.assemble.AssembleMaterialSource;
import com.chy.lamia.element.assemble.AssembleResult;
import com.chy.lamia.entity.Expression;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.ParameterTypeMemberAnnotation;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.SymbolUtils;
import com.sun.source.tree.TreeVisitor;
import com.sun.tools.javac.tree.JCTree;

import java.util.*;


/**
 * 需要处理生成对应代码的通路
 */
public class PendHighway extends JCTree.JCStatement {

    /**
     * 这个 block 能够访问到的所有变量
     */
    private final Map<String, ParameterTypeMemberAnnotation> blockVars;

    /**
     * 方法参数中的 vars
     */
    private Map<String, ParameterTypeMemberAnnotation> paramVars;

    /**
     * 要生成数据的类型
     */
    private final ClassDetails genClassDetails;
    private final JCUtils jcUtils;
    private final Optional<JCVariableDecl> variableDecl;
    /**
     * 手动指定需要使用的变量的名称
     */
    private final List<String> enableUseVarNames;
    private AssembleFactoryHolder genTypeFactory;


    public PendHighway(Map<String, ParameterTypeMemberAnnotation> blockVars,
                       List<String> enableUseVarNames,
                       ParameterType genType, JCVariableDecl variableDecl) {
        this.blockVars = blockVars;
        this.enableUseVarNames = enableUseVarNames;
        genClassDetails = new ClassDetails(genType);
        genTypeFactory = genClassDetails.getAssembleFactory();
        this.jcUtils = JCUtils.instance;
        this.variableDecl = Optional.ofNullable(variableDecl);
    }


    /**
     * 把所有需要的变量都放入聚合工厂中，让工厂来决定使用什么
     */
    public void addMaterials() {
        for (String enableUseVarName : enableUseVarNames) {
            ParameterTypeMemberAnnotation parameterTypeMemberAnnotation = paramVars.get(enableUseVarName);
            //把方法参数的放入
            doAddMaterials(parameterTypeMemberAnnotation, AssembleMaterialSource.PARAMETER);

            if (parameterTypeMemberAnnotation == null) {
                parameterTypeMemberAnnotation = blockVars.get(enableUseVarName);
                doAddMaterials(parameterTypeMemberAnnotation, AssembleMaterialSource.METHOD_VAR);
            }
            if (parameterTypeMemberAnnotation == null) {
                throw new RuntimeException("不能够找到变量属性：[" + enableUseVarName + "]");
            }
        }
    }

    private void doAddMaterials(ParameterTypeMemberAnnotation parameterTypeMemberAnnotation, AssembleMaterialSource source) {
        if (parameterTypeMemberAnnotation == null) {
            return;
        }
        AssembleMaterial assembleMaterial = new AssembleMaterial(parameterTypeMemberAnnotation,
                new Expression(jcUtils.memberAccess(parameterTypeMemberAnnotation.getFieldName())), source);
        assembleMaterial.setMapMember(parameterTypeMemberAnnotation.getMapMember());
        genTypeFactory.addMaterial(assembleMaterial);
    }


    /**
     * 聚合所有的条件去生成对应的代码
     *
     * @return AssembleResult
     */
    public AssembleResult assemble() {
        //获取 结果对象的 生成的语句
        return genTypeFactory.generate();
    }

    public Optional<JCVariableDecl> getVariableDecl() {
        return variableDecl;
    }

    public void setParamVars(Map<String, ParameterTypeMemberAnnotation> paramVars) {
        this.paramVars = paramVars;
    }

    @Override
    public Tag getTag() {
        return null;
    }

    @Override
    public void accept(Visitor v) {

    }

    @Override
    public Kind getKind() {
        return null;
    }

    @Override
    public <R, D> R accept(TreeVisitor<R, D> v, D d) {
        return null;
    }


}
