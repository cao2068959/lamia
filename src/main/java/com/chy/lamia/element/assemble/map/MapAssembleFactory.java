package com.chy.lamia.element.assemble.map;


import com.chy.lamia.element.assemble.AssembleFactoryChain;
import com.chy.lamia.element.assemble.AssembleMaterial;
import com.chy.lamia.element.assemble.AssembleResult;
import com.chy.lamia.element.assemble.IAssembleFactory;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.utils.CommonUtils;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.Lists;
import com.sun.tools.javac.tree.JCTree;

import java.util.*;

public class MapAssembleFactory implements IAssembleFactory {

    private JCUtils jcUtils = JCUtils.instance;

    private Map<String, MaterialCache> materialCaches = new HashMap<>();

    private ParameterType returnType;

    public MapAssembleFactory(ParameterType returnType) {
        this.returnType = returnType;
    }

    @Override
    public void addMaterial(AssembleMaterial material, AssembleFactoryChain chain) {
        doAddMaterial(material);
        chain.addMaterial(material, chain);
    }


    public void doAddMaterial(AssembleMaterial material) {
        if (!material.hasParent()) {
            return;
        }
        ParameterType parameterType = material.getParameterType();
        JCTree.JCExpression expression = material.getExpression().getExpression();
        Integer priority = material.getPriority();

        String name = parameterType.getName();
        MaterialCache materialCache = materialCaches.get(name);

        if (materialCache != null && materialCache.getPriority() > priority) {
            return;
        }
        materialCache = new MaterialCache(parameterType,
                material.getTopParent().map(AssembleMaterial::getParameterType), expression, priority);
        materialCaches.put(parameterType.getName(), materialCache);
    }

    public static boolean isNeedDeal(ParameterType parameterType) {
        if (parameterType.getType().matchType("java.util.Map")) {
            return true;
        }
        //尝试反射获取一下
        Optional<Class<?>> typeReflectClass = parameterType.getTypeReflectClass();
        if (!typeReflectClass.isPresent()) {
            return false;
        }
        return Map.class.isAssignableFrom(typeReflectClass.get());
    }

    @Override
    public AssembleResult generate(AssembleFactoryChain chain) {
        //先判断要生成的结果是什么类型
        String returnImpTypePatch = returnType.getTypePatch();
        //如果设置的就是一个map 那么设置成对应的 hashMap
        if ("java.util.Map".equals(returnImpTypePatch)) {
            returnImpTypePatch = "java.util.HashMap";
        }
        String returnName = CommonUtils.generateVarName("result");
        JCTree.JCNewClass jcNewClass = jcUtils.newClass(returnImpTypePatch, new LinkedList<>());
        JCTree.JCVariableDecl newVar = jcUtils.createVar(returnName, returnType.getTypePatch(), jcNewClass);
        List<JCTree.JCStatement> statements = new ArrayList<>();
        statements.add(newVar);
        Set<String> dependentClassPath = new HashSet<>();
        materialCaches.forEach((k, v) -> {
            JCTree.JCExpression expression = v.getExpression();
            //生成代码 result.put("name",xxxx)
            JCTree.JCExpressionStatement expressionStatement = jcUtils.execMethod(returnName,
                    "put", Lists.of(jcUtils.geStringExpression(k), expression));

            v.getTopParentParameterType().ifPresent(parentParameter -> dependentClassPath.add(parentParameter.getTypePatch()));
            statements.add(expressionStatement);
        });
        return new AssembleResult(statements, returnName, dependentClassPath);
    }


    /**
     * 收集依赖到了什么 对象
     *
     * @param assembleMaterial
     */
    private void gatherDependent(AssembleMaterial assembleMaterial, Set<String> result) {
        assembleMaterial.getTopParent().ifPresent(am -> {
            result.add(am.getParameterType().getTypePatch());
        });
    }


    @Override
    public void clear(AssembleFactoryChain chain) {
        materialCaches = new HashMap<>();
        chain.clear(chain);
    }
}
