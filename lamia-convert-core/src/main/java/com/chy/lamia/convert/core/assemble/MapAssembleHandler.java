package com.chy.lamia.convert.core.assemble;


import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.NewlyStatementHolder;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.entity.VarDefinition;
import com.chy.lamia.convert.core.expression.imp.builder.MaterialStatementBuilder;
import com.chy.lamia.convert.core.expression.imp.builder.MaterialTypeConvertBuilder;
import com.chy.lamia.convert.core.utils.DefaultHashMap;
import com.chy.lamia.convert.core.utils.Lists;

import java.util.List;

/**
 * map的组装处理器
 *
 * @author bignosecat
 */
public class MapAssembleHandler extends CommonAssembleHandler {


    String classPath = "java.util.HashMap";
    private boolean valueIsString = false;

    public MapAssembleHandler(TypeDefinition type, VarDefinition target) {
        super(type);
        this.target = target;
        mapTypeHandle(type);
    }


    /**
     * 对map的类型进行检查，只支持Map<String, Object> 、Map<String, String> 两种类型，如果没有指定泛型，则默认为Map<String, Object>
     *
     * @param type
     */
    private void mapTypeHandle(TypeDefinition type) {
        List<TypeDefinition> generic = type.getGeneric();
        if (generic == null || generic.size() != 2) {
            TypeDefinition newType = new TypeDefinition(targetType.getClassPath());
            newType.addGeneric(new TypeDefinition("java.lang.String"));
            newType.addGeneric(new TypeDefinition("java.lang.Object"));
            super.targetType = newType;
            return;
        }

        TypeDefinition keyType = generic.get(0);
        if (!keyType.matchType(String.class)) {
            throw new RuntimeException("只支持 map<String, Object> 的泛型结构，而现在的 key类型为[" + keyType.toString() + "]");
        }

        TypeDefinition valueType = generic.get(1);
        if (valueType.matchType(String.class)) {
            valueIsString = true;
        } else if (!valueType.matchType(Object.class)) {
            throw new RuntimeException("只支持 map<String, Object> 的泛型结构，而现在的 value类型为[" + valueType.toString() + "]");
        }

    }


    @Override
    protected String createNewInstantExpression() {
        String instantName = genNewInstantName();

        // 表达式生成器
        MaterialStatementBuilder materialStatementBuilder = new MaterialStatementBuilder();

        materialStatementBuilder.setFunction((() -> {
            Statement jcStatement = genNewInstance(newInstant, targetType, classPath, Lists.of());
            return Lists.of(new NewlyStatementHolder(jcStatement));
        }));

        // 把转换语句给放进去
        super.addStatementBuilders(materialStatementBuilder);
        return instantName;
    }

    @Override
    public void createConvertExpression(DefaultHashMap<String, Material> materialMap) {
        // 找到所有的材料, 放入 map中,将生成对应的 put函数
        materialMap.forEach((key, material) -> {
            MaterialTypeConvertBuilder materialTypeConvertBuilder = toMaterialTypeConvertBuilder(material);

            MaterialStatementBuilder materialStatementBuilder = new MaterialStatementBuilder();
            materialStatementBuilder.setFunction(() -> materialTypeConvertBuilder.convert(expression -> {

                Expression putName = treeFactory.geStringExpression(material.getSupplyName());
                List<Expression> args = Lists.of(putName, putParamHandle(expression));
                Statement statement = treeFactory.execMethod(newInstant, "put", args);
                return Lists.of(new NewlyStatementHolder(statement));
            }).getConvertStatement());
            super.addStatementBuilders(materialStatementBuilder);
        });

    }

    private Expression putParamHandle(Expression expression) {
        if (valueIsString) {
            return treeFactory.execMethod(expression, "toString", Lists.of()).getExpression();
        }
        return expression;
    }

    protected MaterialTypeConvertBuilder toMaterialTypeConvertBuilder(Material material) {
        // 万能材料，适配一下
        if (material instanceof OmnipotentMaterial) {
            throw new RuntimeException("不允许map扩散转map, 入参map的名称为:[" + material.getProtoMaterialInfo().getMaterial().getText() + "]");
        }

        return new MaterialTypeConvertBuilder(material, material.supplyType);
    }


}
