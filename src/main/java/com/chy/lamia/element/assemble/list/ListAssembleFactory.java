package com.chy.lamia.element.assemble.list;

import com.chy.lamia.element.assemble.AssembleFactoryChain;
import com.chy.lamia.element.assemble.AssembleResult;
import com.chy.lamia.element.assemble.IAssembleFactory;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.List;

/**
 * 生成 list的组装工厂
 */
public class ListAssembleFactory implements IAssembleFactory {

    private JCUtils jcUtils = JCUtils.instance;
    //list中泛型的类型
    private ParameterType childrenType;


    @Override
    public void addMaterial(ParameterType parameterType, JCTree.JCExpression expression,
                            Integer priority, AssembleFactoryChain chain) {



    }

    @Override
    public AssembleResult generate(AssembleFactoryChain chain) {
        return null;
    }

    @Override
    public void clear(AssembleFactoryChain chain) {

    }
}
