package com.chy.lamia.convert.assemble;

import com.chy.lamia.convert.ExpressionBuilder;
import com.chy.lamia.element.resolver.TypeResolver;
import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.utils.CommonUtils;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.Lists;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 值对象组装器
 *
 * @author bignosecat
 */
public class ValueObjAssembleHandler implements AssembleHandler {

    private final TypeResolver targetTypeResolver;
    private final Map<String, Setter> targetSetters;

    private Map<String, Material> materialMap = new HashMap<>();

    /**
     * 生成的表达式器列表, 最终将使用这些 builder来生成对应的转换语句
     */
    List<ExpressionBuilder> expressionBuilders = new ArrayList<>();


    public ValueObjAssembleHandler(TypeDefinition targetType) {
        // 解析这个类型, 获取这个类型里面的 方法/变量 等
        this.targetTypeResolver = TypeResolver.getTypeResolver(targetType);

        // 获取这个类中所有的 setter方法
        this.targetSetters = targetTypeResolver.getInstantSetters();
    }

    /**
     * 添加参与组装的耗材, 可以是变量,或者对象的get方法
     */
    @Override
    public void addMaterial(List<Material> materials) {
        materials.forEach(material -> {
            materialMap.put(material.getTargetName(), material);
        });
    }

    /**
     * 生成对应的转换语句
     */
    public void run() {
        // 选择一个合适的构造器
        Constructor constructor = chooseConstructor();


    }

    /**
     * 选择一个最满足条件的构造器
     *
     * @return 构造器
     */
    private Constructor chooseConstructor() {
        // 获取所有的构造器
        List<Constructor> constructors = targetTypeResolver.getConstructors();
        if (constructors == null || constructors.size() == 0) {
            throw new RuntimeException("类:[" + targetTypeResolver.getTypeDefinition().getClassPath() + "] 无法获取构造器");
        }
        Constructor result = null;

        constructorLoop:
        for (Constructor constructor : constructors) {

            List<VarDefinition> params = constructor.getParams();
            // 去寻找构造器中每一个参数是不是可以找到
            for (VarDefinition param : params) {
                //如果有字段无法匹配上就直接跳过了
                if (!materialMap.containsKey(param.getVarName())) {
                    continue constructorLoop;
                }
            }

            if (result == null) {
                result = constructor;
                continue;
            }
            // 选择参数最多的一个构造器
            if (result.getParams().size() < params.size()) {
                result = constructor;
            }
        }

        if (result == null) {
            throw new RuntimeException("类: [" + targetTypeResolver.getTypeDefinition().getClassPath() + "] 没有适合的构造器, 参与的参数为: [" + String.join(",", materialMap.keySet()) + "]");
        }
        return result;
    }

    private String createNewInstant(Constructor constructor) {
        String classPath = targetTypeResolver.getTypeDefinition().getClassPath();
        // 构造器所需要的所有入参
        List<Material> constructorParam = constructor.getParams().stream().map(param -> materialMap.get(param.getVarName())).collect(Collectors.toList());
        // 新实例的名称生成
        String varName = CommonUtils.generateVarName("result");

        // 表达式生成器
        ExpressionBuilder expressionBuilder = new ExpressionBuilder();
        // 把所有的参数都放入构造器中, 返回对应顺序的id
        List<String> materialIds = expressionBuilder.addMaterial(constructorParam);

        expressionBuilder.setFunction((builder -> {
            // 用 materialId 去换取 真正的表达式
            List<JCTree.JCExpression> paramsExpression = builder.getExpression(materialIds);
            JCTree.JCNewClass jcNewClass = JCUtils.instance.newClass(classPath, paramsExpression);
            JCTree.JCVariableDecl newVar = JCUtils.instance.createVar(varName, classPath, jcNewClass);
            return Lists.of(newVar);
        }));

        expressionBuilders.add(expressionBuilder);
        return varName;
    }

}
