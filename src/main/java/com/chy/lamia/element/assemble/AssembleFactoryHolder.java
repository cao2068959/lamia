package com.chy.lamia.element.assemble;


import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.ParameterTypeUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.chy.lamia.constant.PriorityConstant.PARAMETERS_IN_VAR;

public class AssembleFactoryHolder {

    /**
     * 所有需要执行assembleFactory的列表, 排在前面的是最外层的 assembleFactory
     */
    List<IAssembleFactory> assembleFactories = new ArrayList<>();
    AssembleFactoryChain assembleFactoryChain;


    public AssembleFactoryHolder(List<IAssembleFactory> assembleFactories) {
        this.assembleFactories = assembleFactories;
        this.assembleFactoryChain = new AssembleFactoryChain(this);
    }

    public void addMaterial(AssembleMaterial material) {
        if (isSpread(material)) {
            doSpread(material);
        }
        doAddMaterial(material);
    }

    private boolean isSpread(AssembleMaterial material) {
        return material.getMapMember().map(MapMember::spread)
                .orElseGet(() -> {
                    if (material.getSource() == AssembleMaterialSource.PARAMETER) {
                        return true;
                    }
                    return false;
                });
    }


    public void doAddMaterial(AssembleMaterial material) {
        assembleFactoryChain.resetIndex();
        assembleFactoryChain.addMaterial(material, assembleFactoryChain);
    }

    /**
     * 扩散这个类中的所有属性, 只会扩散拥有getter的字段
     *
     * @param assembleMaterial
     */
    private void doSpread(AssembleMaterial assembleMaterial) {

        ParameterTypeUtils.parameterGetterSpread(assembleMaterial.getParameterType(), (k, v) -> {
            //生成 a.getXX() 的表达式
            JCTree.JCExpressionStatement getterExpression =
                    JCUtils.instance.execMethod(assembleMaterial.getExpression(), v.getSimpleName(), new LinkedList<>());
            ParameterType parameterType = new ParameterType(k, v.getParameterType());
            //将表达式放入 合成工厂去匹配
            AssembleMaterial childrenAssembleMaterial = new AssembleMaterial(parameterType, getterExpression.expr);
            childrenAssembleMaterial.setPriority(PARAMETERS_IN_VAR);
            childrenAssembleMaterial.setParent(assembleMaterial);
            this.addMaterial(childrenAssembleMaterial);
        });

    }


    public AssembleResult generate() {
        assembleFactoryChain.resetIndex();
        return assembleFactoryChain.generate(assembleFactoryChain);
    }


    public void clear() {
        assembleFactoryChain.resetIndex();
        assembleFactoryChain.clear(assembleFactoryChain);
    }

    public void addAssembleFactorie(IAssembleFactory iAssembleFactory) {
        assembleFactories.add(iAssembleFactory);
    }

    protected Optional<IAssembleFactory> getIAssembleFactory(int index) {
        if (getIAssembleFactoryLen() <= index) {
            return Optional.empty();
        }
        return Optional.of(assembleFactories.get(index));
    }

    protected int getIAssembleFactoryLen() {
        return assembleFactories.size();

    }


}
