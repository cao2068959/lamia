package com.chy.lamia.element.assemble;


import com.chy.lamia.entity.ParameterType;
import com.sun.tools.javac.tree.JCTree;

public interface IAssembleFactory {

    /**
     * 添加原料
     *
     * @param assembleMaterial assembleMaterial
     * @param chain chain
     */
    void addMaterial(AssembleMaterial assembleMaterial, AssembleFactoryChain chain);

    /**
     * 生成结果
     *
     * @return
     */
    AssembleResult generate(AssembleFactoryChain chain);

    /**
     * 清空整个工厂
     */
    void clear(AssembleFactoryChain chain);
}
