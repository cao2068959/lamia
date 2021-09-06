package com.chy.lamia.element;


import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.element.annotation.AnnotationProxyFactory;
import com.chy.lamia.element.assemble.AssembleFactoryHolder;
import com.chy.lamia.element.assemble.AssembleMaterial;
import com.chy.lamia.element.assemble.AssembleMaterialSource;
import com.chy.lamia.element.assemble.AssembleResult;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.SunList;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.SymbolUtils;
import com.sun.source.tree.TreeVisitor;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * 需要处理生成对应代码的通路
 */
public class PendHighway extends JCTree.JCStatement {

    //这个 block 能够访问到的所有变量
    private final List<Pair<ParameterType, MapMember>> vars;
    //要生成数据的类型
    private final ClassDetails genClassDetails;
    private final JCUtils jcUtils;
    private final Optional<JCVariableDecl> variableDecl;
    private AssembleFactoryHolder genTypeFactory;




    public PendHighway(List<Pair<ParameterType, MapMember>> vars, ParameterType genType, JCVariableDecl variableDecl) {
        this.vars = new ArrayList<>(vars);
        genClassDetails = new ClassDetails(genType);
        genTypeFactory = genClassDetails.getAssembleFactory();
        this.jcUtils = JCUtils.instance;
        this.variableDecl = Optional.ofNullable(variableDecl);
    }


    public List<Pair<ParameterType, MapMember>> getVars() {
        return vars;
    }

    public ClassDetails getGenClassDetails() {
        return genClassDetails;
    }

    public AssembleFactoryHolder getGenTypeFactory() {
        return genTypeFactory;
    }

    public void addMaterialsFromMethodBodyVar() {
        if (vars == null || vars.size() == 0) {
            return;
        }
        vars.forEach(methodBodyVarPair -> {
            ParameterType methodBodyVar = methodBodyVarPair.fst;
            MapMember mapMember = methodBodyVarPair.snd;
            AssembleMaterial assembleMaterial = new AssembleMaterial(methodBodyVar,
                    jcUtils.memberAccess(methodBodyVar.getFieldName()), AssembleMaterialSource.METHOD_VAR);
            assembleMaterial.setMapMember(mapMember);
            genTypeFactory.addMaterial(assembleMaterial);
        });
    }


    public void addMaterialsFromParameters(SunList<Symbol.VarSymbol> params) {
        if (params == null || params.size() == 0) {
            return;
        }
        params.forEach(varSymbol -> {
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

            ParameterType parameterType = new ParameterType(name, paramsTypeClassPath);
            //把这个字段原本的字段名称塞进去
            parameterType.setFieldName(varSymbol.name.toString());
            //这个参数可能会有泛型
            List<ParameterType> generic = SymbolUtils.getGeneric(varSymbol);
            parameterType.setGeneric(generic);
            //先把 这个参数本身给塞入工厂
            AssembleMaterial assembleMaterial = new AssembleMaterial(parameterType,
                    jcUtils.memberAccess(parameterType.getFieldName()), AssembleMaterialSource.PARAMETER);
            assembleMaterial.setMapMember(mapMember);
            genTypeFactory.addMaterial(assembleMaterial);
        });
    }

    /**
     * 聚合所有的条件去生成对应的代码
     */
    public AssembleResult assemble() {
        //获取 结果对象的 生成的语句
        return genTypeFactory.generate();
    }

    public Optional<JCVariableDecl> getVariableDecl() {
        return variableDecl;
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
