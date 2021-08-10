package com.chy.lamia.element.assemble;


import com.chy.lamia.entity.ParameterType;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public void addMaterial(ParameterType parameterType, JCTree.JCExpression expression, Integer priority) {
        assembleFactoryChain.resetIndex();
        assembleFactoryChain.addMaterial(parameterType, expression, priority, assembleFactoryChain);
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


    interface Function<A, B, C> {
        C apply(A a, B b);
    }

}
