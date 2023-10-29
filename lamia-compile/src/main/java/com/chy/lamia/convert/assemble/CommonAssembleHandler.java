package com.chy.lamia.convert.assemble;

import com.chy.lamia.convert.builder.MaterialStatementBuilder;
import com.chy.lamia.convert.builder.MaterialTypeConvertBuilder;
import com.chy.lamia.element.LamiaConvertInfo;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.utils.CommonUtils;
import com.chy.lamia.utils.DefaultHashMap;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 通用的 对象组装处理器
 *
 * @author bignosecat
 */
public abstract class CommonAssembleHandler implements AssembleHandler {

    @Getter
    private final DefaultHashMap<String, Material> materialMap = new DefaultHashMap<>();
    /**
     * 已经使用过的 material，用于防止重复使用
     */
    protected final Set<String> useMaterial = new HashSet<>();

    /**
     * 生成新实例的名称
     */
    protected String newInstant;

    /**
     * 生成的表达式器列表, 最终将使用这些 builder来生成对应的转换语句
     */
    private List<MaterialStatementBuilder> materialStatementBuilders = new ArrayList<>();
    protected LamiaConvertInfo lamiaConvertInfo;


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
        // 生成一个新的实例,返回对应的实例名称
        this.newInstant = createNewInstantExpression();

        // 生成对应的 set 赋值语句
        createConvertExpression(materialMap);
        return materialStatementBuilders;
    }

    /**
     * 生成新实例的表达式
     *
     * @param instantName      实例名称
     * @param classPath        实例类型
     * @param newInstanceParam 新实例的参数
     * @return
     */
    protected JCTree.JCStatement genNewInstance(String instantName, String classPath, List<JCTree.JCExpression> newInstanceParam) {
        JCTree.JCNewClass jcNewClass = JCUtils.instance.newClass(classPath, newInstanceParam);

        // 变量是否已经存在,是否需要去创建类型
        if (lamiaConvertInfo.isCreatedType()) {
            JCTree.JCVariableDecl newVar = JCUtils.instance.createVar(instantName, classPath, jcNewClass);
            return newVar;
        }
        return JCUtils.instance.varAssign(instantName, jcNewClass);
    }


    /**
     * 创建一个新的实例, 返回新实例的名称
     *
     * @return
     */
    protected abstract String createNewInstantExpression();


    /**
     * 生成对应的转换语句
     * 如: 生成 set赋值语句 如 : instantName.setName(xxxx)
     */
    public abstract void createConvertExpression(DefaultHashMap<String, Material> materialMap);


    @Override
    public void setLamiaConvertInfo(LamiaConvertInfo lamiaConvertInfo) {
        this.lamiaConvertInfo = lamiaConvertInfo;
    }

    @Override
    public String getNewInstantName() {
        return newInstant;
    }

    public String genNewInstantName() {
        String oldResultName = lamiaConvertInfo.getVarName();
        String varName;
        if (oldResultName == null) {
            // 新实例的名称生成
            varName = CommonUtils.generateVarName("result");
        } else {
            varName = oldResultName;
        }
        return varName;
    }

    /**
     * 使用 material
     *
     * @param varDefinition 当使用material 的时候,需要这个材料转成 什么变量 (名称 + 类型)
     * @return
     */
    protected MaterialTypeConvertBuilder useMaterial(VarDefinition varDefinition) {
        return useMaterial(varDefinition.getType(), varDefinition.getVarName());
    }


    protected MaterialTypeConvertBuilder useMaterial(TypeDefinition typeDefinition, String varName) {
        // 这个材料已经被使用过，不在返回了
        if (useMaterial.contains(varName)) {
            return null;
        }
        useMaterial.add(varName);
        Material material = materialMap.get(varName);
        if (material == null) {
            return null;
        }

        // 万能材料，适配一下
        if (material instanceof OmnipotentMaterial) {
            material = ((OmnipotentMaterial) material).adapter(typeDefinition, varName);
        }
        return new MaterialTypeConvertBuilder(material, typeDefinition);
    }


    protected void addStatementBuilders(MaterialStatementBuilder materialStatementBuilder) {
        materialStatementBuilders.add(materialStatementBuilder);
    }
}
