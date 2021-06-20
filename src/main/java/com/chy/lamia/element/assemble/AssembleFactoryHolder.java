package com.chy.lamia.element.assemble;


import com.chy.lamia.entity.ParameterType;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;

public class AssembleFactoryHolder implements IAssembleFactory {

    /**
     * 所有需要执行assembleFactory的列表, 排在前面的是最外层的 assembleFactory
     */
    List<IAssembleFactory> assembleFactories = new ArrayList<>();

    public AssembleFactoryHolder(List<IAssembleFactory> assembleFactories) {
        this.assembleFactories = assembleFactories;
    }

    @Override
    public void addMaterial(ParameterType parameterType, JCTree.JCExpression expression, Integer priority) {
        foreachBackward((assembleFactory, Void) -> {
            assembleFactory.addMaterial(parameterType, expression, priority);
            return null;
        });
    }

    @Override
    public AssembleResult generate(AssembleResult assembleResult) {
        return foreachBackward(((assembleFactory, lastResult) -> {
            AssembleResult generate = assembleFactory.generate(lastResult);
            return generate;
        }));
    }

    private <T> T foreachBackward(Function<IAssembleFactory, T, T> consumer) {
        T result = null;
        for (int i = assembleFactories.size(); i > 0; i--) {
            IAssembleFactory assembleFactory = assembleFactories.get(i - 1);
            result = consumer.apply(assembleFactory, result);
        }
        return result;
    }

    @Override
    public void clear() {
        foreachBackward((assembleFactory, Void) -> {
            assembleFactory.clear();
            return null;
        });
    }

    public void addAssembleFactorie(IAssembleFactory iAssembleFactory){
        assembleFactories.add(iAssembleFactory);
    }

    interface Function<A, B, C> {
        C apply(A a, B b);
    }

}
