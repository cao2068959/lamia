package com.chy.lamia.element.assemble.map;


import com.chy.lamia.element.assemble.AssembleFactoryChain;
import com.chy.lamia.element.assemble.AssembleResult;
import com.chy.lamia.element.assemble.IAssembleFactory;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.HashMap;
import java.util.Map;

public class MapAssembleFactory implements IAssembleFactory {

    private JCUtils jcUtils = JCUtils.instance;

    private Map<String, MaterialCache> materialCaches = new HashMap<>();


    @Override
    public void addMaterial(ParameterType parameterType, JCTree.JCExpression expression, Integer priority, AssembleFactoryChain chain) {
        doAddMaterial(parameterType, expression, priority);
        chain.addMaterial(parameterType, expression, priority, chain);
    }


    public void doAddMaterial(ParameterType parameterType, JCTree.JCExpression expression, Integer priority) {
        String name = parameterType.getName();
        MaterialCache materialCache = materialCaches.get(name);
        if (materialCache == null) {
            materialCache = new MaterialCache(parameterType, expression, priority);
        }
        Integer lastPriority = materialCache.getPriority();
        if (lastPriority >= priority) {
            return;
        }
        materialCaches.put(parameterType.getName(), materialCache);
    }


    @Override
    public AssembleResult generate(AssembleFactoryChain chain) {


        return null;
    }

    @Override
    public void clear(AssembleFactoryChain chain) {
        materialCaches = new HashMap<>();
        chain.clear(chain);
    }
}
