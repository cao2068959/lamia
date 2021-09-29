package com.chy.lamia.element.assemble;


import com.chy.lamia.entity.ParameterType;
import com.sun.tools.javac.tree.JCTree;

import java.util.Optional;

public class AssembleFactoryChain implements IAssembleFactory {

    AssembleFactoryHolder holder;
    int index;


    public AssembleFactoryChain(AssembleFactoryHolder holder) {
        this.holder = holder;
    }


    public AssembleFactoryChain(AssembleFactoryHolder holder, int index) {
        this.holder = holder;
        this.index = index;
    }

    @Override
    public void addMaterial(AssembleMaterial assembleMaterial, AssembleFactoryChain chain) {
        Optional<IAssembleFactory> iAssembleFactory = holder.getIAssembleFactory(index);
        index++;
        iAssembleFactory.ifPresent(assembleFactory -> {
            Optional<AssembleMaterial> parent = assembleMaterial.getParent();
            assembleFactory.addMaterial(assembleMaterial, this);});
    }


    @Override
    public AssembleResult generate(AssembleFactoryChain chain) {

        Optional<IAssembleFactory> iAssembleFactory = holder.getIAssembleFactory(index);
        index++;
        if (iAssembleFactory.isPresent()) {
            return iAssembleFactory.get().generate(this);
        }
        return null;
    }

    @Override
    public void clear(AssembleFactoryChain chain) {
        Optional<IAssembleFactory> iAssembleFactory = holder.getIAssembleFactory(index);
        index++;

        iAssembleFactory.ifPresent(assembleFactory -> assembleFactory.clear(this));

    }


    protected void resetIndex() {
        index = 0;
    }

    public AssembleFactoryChain mirror() {
        return new AssembleFactoryChain(holder, index);
    }
}
