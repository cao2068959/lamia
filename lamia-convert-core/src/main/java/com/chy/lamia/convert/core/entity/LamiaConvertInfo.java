package com.chy.lamia.convert.core.entity;


import com.chy.lamia.convert.core.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 每一个 Lamia.convert(setFiled/mapping) 表达式能够使用到的作用域
 *
 * @author bignosecat
 */
public class LamiaConvertInfo {

    @Getter
    String id = CommonUtils.getRandomString(16);

    /**
     * 要转换成的类型
     */
    @Getter
    @Setter
    TypeDefinition targetType;

    /**
     * 转换的结果变量名称, 会用这个名称去接收 new A() 的对象, 如果没有的话，就随机生成一个变量名
     */
    @Getter
    @Setter
    String resultVarName;

    /**
     * 如果存在target则不会去 new一个新对象，而是直接使用 target 对象来生成转换语句
     */
    @Getter
    VarDefinition target;

    /**
     * 在指定作用域中可以使用的变量
     */
    @Getter
    Map<String, VarDefinition> scopeVar = new HashMap<>();

    /**
     * lamia表达式的解析对象
     */
    @Getter
    @Setter
    LamiaExpression lamiaExpression;

    /**
     * 是否需要定义结果变量的类型，如果不需要说明这个结果变量是一个已经定义好的变量
     */
    @Getter
    @Setter
    boolean declareResultVarType = true;

    @Getter
    @Setter
    boolean isReturn = false;

    /**
     * Material 类型初始化 有的protoMaterial 的类型需要获取到所有scopeVar之后才能确定，所以在确认所有的scopeVar之后再进行初始化
     */
    private boolean isProtoMaterialTypeInit = false;

    public LamiaConvertInfo(LamiaExpression lamiaExpression) {
        this.lamiaExpression = lamiaExpression;
    }

    public LamiaConvertInfo() {
    }

    public void addScopeVar(VarDefinition varDefinition) {
        String varName = varDefinition.getVarRealName();
        VarDefinition existVd = scopeVar.get(varName);
        if (existVd == null) {
            scopeVar.put(varName, varDefinition);
            return;
        }
        // 如果有重复的, 那么使用优先级判断
        if (varDefinition.getPriority() > existVd.getPriority()) {
            scopeVar.put(varName, varDefinition);
        }
    }


    public Map<String, ProtoMaterialInfo> getAllProtoMaterial() {
        protoMaterialTypeInit(false);
        return lamiaExpression.getAllArgs();
    }

    public void protoMaterialTypeInit(boolean force) {
        if (!force && isProtoMaterialTypeInit) {
            return;
        }

        lamiaExpression.getAllArgs().forEach((key, protoMaterialInfo) -> {
            MethodParameterWrapper parameterWrapper = protoMaterialInfo.material;
            // 方法调用的参数先不处理
            if (parameterWrapper.isMethodInvoke) {
                return;
            }
            String fieldName = parameterWrapper.getName();
            VarDefinition varDefinition = scopeVar.get(fieldName);
            if (varDefinition == null) {
                throw new RuntimeException("找不到对应的变量 [" + fieldName + "] 请检查是否在作用域中定义了这个变量");
            }
            parameterWrapper.setType(varDefinition.getType());
        });


        isProtoMaterialTypeInit = true;
    }

    /**
     * 判断是否是完整的转换语句
     *
     * @return
     */
    public boolean isCompleteConvert() {
        // 先检查表达式是否完整，如果不完整直接返回
        boolean complete = lamiaExpression.isComplete();
        if (!complete) {
            return false;
        }
        // 没有设置返回值也是直接返回
        if (target == null && targetType == null) {
            return false;
        }
        // 看这个表达式是否有需要转换的数据
        return lamiaExpression.hasConvertData();
    }

    public void setTarget(VarDefinition target) {
        this.target = target;
        this.targetType = target.getType();
    }
}
