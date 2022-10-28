package com.chy.lamia.convert.assemble;

import com.chy.lamia.convert.builder.MaterialStatementBuilder;
import com.chy.lamia.convert.builder.MaterialTypeConvertBuilder;
import com.chy.lamia.element.resolver.TypeResolver;
import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.utils.CommonUtils;
import com.chy.lamia.utils.DefaultHashMap;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.Lists;
import com.sun.tools.javac.tree.JCTree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 值对象组装器
 *
 * @author bignosecat
 */
public class ValueObjAssembleHandler implements AssembleHandler {

    private final TypeResolver targetTypeResolver;
    private final Map<String, Setter> targetSetters;

    private final DefaultHashMap<String, Material> materialMap = new DefaultHashMap<>();
    /**
     * 已经使用过的 material，用于防止重复使用
     */
    private final Set<String> useMaterial = new HashSet<>();

    /**
     * 生成新实例的名称
     */
    private String newInstant;

    /**
     * 生成的表达式器列表, 最终将使用这些 builder来生成对应的转换语句
     */
    List<MaterialStatementBuilder> materialStatementBuilders = new ArrayList<>();


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
            if (material instanceof OmnipotentMaterial) {
                materialMap.setDefaultValue(material);
                return;
            }
            materialMap.put(material.getSupplyName(), material);
        });
    }

    /**
     * 生成对应的转换语句
     *
     * @return
     */
    @Override
    public List<MaterialStatementBuilder> run() {
        // 选择一个合适的构造器
        Constructor constructor = chooseConstructor();

        // 生成一个新的实例,返回对应的实例名称
        this.newInstant = createNewInstantExpression(constructor);

        // 生成对应的 set 赋值语句
        createSetterExpression();
        return materialStatementBuilders;
    }

    /**
     * 生成 set赋值语句 如 : instantName.setName(xxxx)
     */
    private void createSetterExpression() {
        // 遍历所有的 set方法， 如果能找到
        targetSetters.forEach((varName, setter) -> {
            MaterialTypeConvertBuilder material = useMaterial(setter.getType(), varName);
            // 没找到 就不处理了
            if (material == null) {
                return;
            }

            MaterialStatementBuilder materialStatementBuilder = new MaterialStatementBuilder();
            String id = materialStatementBuilder.addMaterial(material);
            // 生成对应的 set的方法
            materialStatementBuilder.setFunction(builder -> {
                JCTree.JCExpression expression = builder.getExpression(id);
                return Lists.of(JCUtils.instance.execMethod(newInstant, setter.getMethodName(), expression));
            });
            materialStatementBuilders.add(materialStatementBuilder);
        });
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
     * @param constructor
     * @return
     */
    private String createNewInstantExpression(Constructor constructor) {
        String classPath = targetTypeResolver.getTypeDefinition().getClassPath();
        // 构造器所需要的所有入参
        List<MaterialTypeConvertBuilder> constructorParam = constructor.getParams().stream().map(this::useMaterial)
                .collect(Collectors.toList());

        // 新实例的名称生成
        String varName = CommonUtils.generateVarName("result");

        // 表达式生成器
        MaterialStatementBuilder materialStatementBuilder = new MaterialStatementBuilder();
        // 把所有的参数都放入构造器中, 返回对应顺序的id
        List<String> materialIds = materialStatementBuilder.addMaterial(constructorParam);

        materialStatementBuilder.setFunction((builder -> {
            // 用 materialId 去换取 真正的表达式
            List<JCTree.JCExpression> paramsExpression = builder.getExpression(materialIds);
            JCTree.JCNewClass jcNewClass = JCUtils.instance.newClass(classPath, paramsExpression);
            JCTree.JCVariableDecl newVar = JCUtils.instance.createVar(varName, classPath, jcNewClass);
            return Lists.of(newVar);
        }));

        materialStatementBuilders.add(materialStatementBuilder);
        return varName;
    }

    private MaterialTypeConvertBuilder useMaterial(VarDefinition varDefinition) {
        return useMaterial(varDefinition.getType(), varDefinition.getVarName());
    }


    private MaterialTypeConvertBuilder useMaterial(TypeDefinition typeDefinition, String varName) {
        // 这个材料已经被使用过，不在返回了
        if (useMaterial.contains(varName)) {
            return null;
        }
        useMaterial.add(varName);
        Material material = materialMap.get(varName);
        // 万能材料，适配一下
        if (material instanceof OmnipotentMaterial) {
            material = ((OmnipotentMaterial) material).adapter(typeDefinition, varName);
        }
        return new MaterialTypeConvertBuilder(material, typeDefinition);
    }


}
