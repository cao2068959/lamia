package com.chy.lamia.element.assemble.list;

import com.chy.lamia.element.assemble.*;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.utils.CommonUtils;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.ParameterTypeUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.*;

/**
 * 生成 list的组装工厂
 */
public class ListAssembleFactory implements IAssembleFactory {

    private JCUtils jcUtils = JCUtils.instance;

    /**
     * 所有进入到 这个组装工厂的 list 变量都会被存入到这个 list中
     */
    private List<ListMaterial> allMaterial = new ArrayList<>();

    private ParameterType returnType;

    public ListAssembleFactory(ParameterType returnType) {
        this.returnType = returnType;
    }

    @Override
    public void addMaterial(AssembleMaterial material, AssembleFactoryChain chain) {

        doAddMaterial(material, chain);
        chain.addMaterial(material, chain);
    }

    public void doAddMaterial(AssembleMaterial material, AssembleFactoryChain chain) {
        ParameterType parameterType = material.getParameterType();
        JCTree.JCExpression expression = material.getExpression();
        Integer priority = material.getPriority();


        if (!isNeedDeal(parameterType)) {
            return;
        }

        List<ParameterType> generic = parameterType.getGeneric();
        if (generic == null || generic.size() != 1) {
            return;
        }
        ParameterType genericType = generic.get(0);


        String iterableVar = CommonUtils.generateVarName("iterable");
        //把list中的泛型中的类型的所有属性给塞入其他的组装工厂
        ParameterTypeUtils.parameterGetterSpread(genericType, (methodName, getter) -> {
            AssembleFactoryChain mirrorChain = chain.mirror();
            JCTree.JCExpressionStatement jcExpressionStatement = jcUtils.execMethod(iterableVar,
                    getter.getSimpleName(), new LinkedList<>());
            AssembleMaterial assembleMaterial = new AssembleMaterial(new ParameterType(methodName, getter.getParameterType()),
                    jcExpressionStatement.expr, AssembleMaterialSource.OTHER);
            mirrorChain.addMaterial(assembleMaterial, mirrorChain);
        });
        ListMaterial listMaterial = new ListMaterial(parameterType, genericType, expression, iterableVar);
        allMaterial.add(listMaterial);
    }


    public static boolean isNeedDeal(ParameterType parameterType) {
        if (parameterType.getType().matchType("java.util.List")) {
            return true;
        }
        //尝试反射获取一下
        Optional<Class<?>> typeReflectClass = parameterType.getTypeReflectClass();
        if (!typeReflectClass.isPresent()) {
            return false;
        }
        return typeReflectClass.get().isAssignableFrom(Collection.class);
    }

    @Override
    public AssembleResult generate(AssembleFactoryChain chain) {
        AssembleResult assembleResult = chain.generate(chain);
        List<JCTree.JCStatement> statements = assembleResult.getStatements();
        List<ListMaterial> listMaterials = chooseMaterial(statements);
        return generateAssembleResult(listMaterials, statements, assembleResult);
    }

    private List<ListMaterial> chooseMaterial(List<JCTree.JCStatement> statements) {
        List<ListMaterial> result = new ArrayList<>(allMaterial.size());

        for (ListMaterial material : allMaterial) {
            for (JCTree.JCStatement statement : statements) {
                if (statement.toString().contains(material.iterableVar)) {
                    result.add(material);
                    break;
                }
            }
        }

        return result;
    }

    private AssembleResult generateAssembleResult(List<ListMaterial> listMaterials, List<JCTree.JCStatement> statements, AssembleResult assembleResult) {
        List<JCTree.JCStatement> resultBlock = new LinkedList<>();

        String returnImpTypePatch = returnType.getTypePatch();
        //如果返回类型写的是 List 那么就换成对应的实现，这里用的 ArrayList
        if ("java.util.List".equals(returnImpTypePatch)) {
            returnImpTypePatch = "java.util.ArrayList";
        }
        String returnName = CommonUtils.generateVarName("result");
        JCTree.JCNewClass jcNewClass = jcUtils.newClass(returnImpTypePatch, new LinkedList<>());
        JCTree.JCVariableDecl newVar = jcUtils.createVar(returnName, returnType.getTypePatch(), jcNewClass);

        //把创建 result的语句塞入body中
        resultBlock.add(newVar);

        //没有使用过
        if (listMaterials.size() == 0) {
            resultBlock.addAll(statements);
            return new AssembleResult(resultBlock, returnName, assembleResult.getDependentClassPath());
        }


        if (listMaterials.size() == 1) {
            ListMaterial material = listMaterials.get(0);
            //添加 result.add(item) 这一行代码
            JCTree.JCExpressionStatement addExecMethod = jcUtils.execMethod(returnName, "add",
                    List.of(jcUtils.memberAccess(material.iterableVar)));
            statements.add(addExecMethod);
            //把代码放入 for循环中
            JCTree.JCEnhancedForLoop foreachLoop = jcUtils.createForeachLoop(material.expression,
                    material.genericType, material.iterableVar, statements);
            resultBlock.add(foreachLoop);
            return new AssembleResult(resultBlock, returnName, assembleResult.getDependentClassPath());
        }
        //TODO
        throw new RuntimeException("暂时不支持多list转换");
    }


    @Override
    public void clear(AssembleFactoryChain chain) {
        chain.clear(chain);
        allMaterial = new ArrayList<>();

    }

    class ListMaterial {
        ParameterType parameterType;
        ParameterType genericType;
        JCTree.JCExpression expression;
        String iterableVar;

        public ListMaterial(ParameterType parameterType, ParameterType genericType, JCTree.JCExpression expression, String iterableVar) {
            this.parameterType = parameterType;
            this.genericType = genericType;
            this.expression = expression;
            this.iterableVar = iterableVar;
        }

        public ParameterType getParameterType() {
            return parameterType;
        }

        public JCTree.JCExpression getExpression() {
            return expression;
        }
    }

}
