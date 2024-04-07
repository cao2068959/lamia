package com.chy.lamia.convert.core.assemble;


import com.chy.lamia.convert.core.ConvertFactory;
import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.TypeResolver;
import com.chy.lamia.convert.core.components.TypeResolverFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.NewlyStatementHolder;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.chy.lamia.convert.core.entity.*;
import com.chy.lamia.convert.core.expression.imp.builder.ConvertResult;
import com.chy.lamia.convert.core.expression.imp.builder.MaterialStatementBuilder;
import com.chy.lamia.convert.core.expression.imp.builder.MaterialTypeConvertBuilder;
import com.chy.lamia.convert.core.utils.DefaultHashMap;
import com.chy.lamia.convert.core.utils.Lists;

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


    public ValueObjAssembleHandler(ConvertFactory convertFactory, TypeDefinition targetType, VarDefinition target) {
        super(targetType);
        TypeResolverFactory typeResolverFactory = ComponentFactory.getInstanceComponent(convertFactory, TypeResolverFactory.class);

        // 解析这个类型, 获取这个类型里面的 方法/变量 等
        this.targetTypeResolver = typeResolverFactory.getTypeResolver(targetType);

        // 获取这个类中所有的 setter方法
        this.targetSetters = targetTypeResolver.getInstantSetters();
        super.target = target;
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
        DefaultHashMap<String, Material> materialMap = getMaterialMap();
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

            List<Expression> expressions = constructorParam.stream()
                    .map(MaterialTypeConvertBuilder::convertSimple).collect(Collectors.toList());

            Statement jcStatement = genNewInstance(instantName, targetType, classPath, expressions);
            return Lists.of(new NewlyStatementHolder(jcStatement));

        }));

        addStatementBuilders(materialStatementBuilder);
        return instantName;
    }


    /**
     * 生成对应的转换语句
     * 如: 生成 set赋值语句 如 : instantName.setName(xxxx)
     */
    @Override
    public void createConvertExpression(DefaultHashMap<String, Material> materialMap) {
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
                ConvertResult convert = material.convert(jcExpression -> {
                    Statement statement = treeFactory.execMethod(newInstant, setter.getMethodName(), Lists.of(jcExpression));
                    NewlyStatementHolder newlyStatementHolder = new NewlyStatementHolder(statement);
                    // 类型无法匹配，标注一下，并且给出异常的字段
                    if (!material.isTypeMatch()) {
                        Material materialMate = material.getMaterial();
                        newlyStatementHolder.setTypeMatch(false);
                        AbnormalVar abnormalVar = new AbnormalVar(setter.getVarName());
                        abnormalVar.setInstanceName(newInstant);
                        abnormalVar.setInstanceType(targetType);
                        abnormalVar.setType(setter.getType());
                        SimpleMaterialInfo materialInfo = new SimpleMaterialInfo(materialMate.getProtoMaterialInfo(), materialMate.getSupplyType());
                        abnormalVar.setErrorMaterial(materialInfo);
                        abnormalVar.setReturn(lamiaConvertInfo.isReturn());
                        newlyStatementHolder.addAbnormalVar(abnormalVar);
                    }
                    return Lists.of(newlyStatementHolder);
                });
                return convert.getConvertStatement();
            });
            addStatementBuilders(materialStatementBuilder);
        });
    }
}
