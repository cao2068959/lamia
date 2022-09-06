package com.chy.lamia.element.assemble;


import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.entity.Expression;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.chy.lamia.constant.PriorityConstant.CLASS_FIELD;

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
        String typePatch = material.getParameterType().getTypePatch();
        //java开头的包不会去进行扩散
        if (!typePatch.startsWith("java") && material.isSpread()) {
            doSpread(material);
            return;
        }
        doAddMaterial(material);
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

        Integer customPriority = assembleMaterial.getMapMember().map(MapMember::priority).orElse(-1);
        Integer priority = customPriority < 0 ? CLASS_FIELD : customPriority;

        ParameterTypeUtils.parameterGetterSpread(assembleMaterial.getParameterType(), (k, v) -> {
            //生成 a.getXX() 的表达式
            JCTree.JCExpressionStatement getterExpression =
                    JCUtils.instance.execMethod(assembleMaterial.getExpression().getExpression(), v.getSimpleName(), new LinkedList<>());
            ParameterType parameterType = new ParameterType(k, v.getParameterType());
            //将表达式放入 合成工厂去匹配
            AssembleMaterial childrenAssembleMaterial = new AssembleMaterial(parameterType, new Expression(getterExpression.expr));
            childrenAssembleMaterial.setPriority(priority);
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
