package com.chy.lamia.convert.assemble;

import com.chy.lamia.convert.builder.MaterialStatementBuilder;
import com.chy.lamia.convert.builder.MaterialTypeConvertBuilder;
import com.chy.lamia.element.resolver.type.TypeResolver;
import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.Lists;
import com.sun.tools.javac.tree.JCTree;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 值对象组装器
 *
 * @author bignosecat
 */
public class ValueObjAssembleHandler extends CommonAssembleHandler {

    private final TypeResolver targetTypeResolver;
    private final Map<String, Setter> targetSetters;


    public ValueObjAssembleHandler(TypeDefinition targetType) {
        // 解析这个类型, 获取这个类型里面的 方法/变量 等
        this.targetTypeResolver = TypeResolver.getTypeResolver(targetType);

        // 获取这个类中所有的 setter方法
        this.targetSetters = targetTypeResolver.getInstantSetters();
    }


    /**
     * 选择一个最满足条件的构造器
     *
     * @return 构造器
     */
    public Constructor chooseConstructor() {
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
                if (!materialMap.contains(param.getVarName())) {
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

    /**
     * 创建一个新实例表达式,并返回对应的新实例名称
     *
     * @return
     */
    @Override
    protected String createNewInstantExpression() {
        // 选择一个合适的构造器
        Constructor constructor = chooseConstructor();
        String classPath = targetTypeResolver.getTypeDefinition().getClassPath();
        // 构造器所需要的所有入参
        List<MaterialTypeConvertBuilder> constructorParam = constructor.getParams().stream().map(varDefinition -> {
            MaterialTypeConvertBuilder materialTypeConvertBuilder = useMaterial(varDefinition);
            if (materialTypeConvertBuilder == null) {
                throw new RuntimeException("参数 [" + varDefinition.getVarName() + "] 可能已经被使用或者不存在");
            }
            return materialTypeConvertBuilder;
        }).collect(Collectors.toList());

        String instantName = genNewInstantName();

        // 表达式生成器
        MaterialStatementBuilder materialStatementBuilder = new MaterialStatementBuilder();

        materialStatementBuilder.setFunction((() -> {

            List<JCTree.JCExpression> expressions = constructorParam.stream().map(MaterialTypeConvertBuilder::convert)
                    .map(MaterialTypeConvertBuilder.ConvertResult::getVarExpression).collect(Collectors.toList());

            JCTree.JCStatement jcStatement = genNewInstance(newInstant, classPath, expressions);
            return Lists.of(jcStatement);

        }));

        materialStatementBuilders.add(materialStatementBuilder);
        return instantName;
    }


    /**
     * 生成对应的转换语句
     * 如: 生成 set赋值语句 如 : instantName.setName(xxxx)
     */
    @Override
    public void createConvertExpression() {
        // 遍历所有的 set方法， 如果能找到
        targetSetters.forEach((varName, setter) -> {
            MaterialTypeConvertBuilder material = useMaterial(setter.getType(), varName);
            // 没找到 就不处理了
            if (material == null) {
                return;
            }

            MaterialStatementBuilder materialStatementBuilder = new MaterialStatementBuilder();
            // 生成对应的 set的方法
            materialStatementBuilder.setFunction(() -> {
                JCTree.JCExpression expression = material.convert().getVarExpression();
                return Lists.of(JCUtils.instance.execMethod(newInstant, setter.getMethodName(), expression));
            });
            materialStatementBuilders.add(materialStatementBuilder);
        });
    }
}
