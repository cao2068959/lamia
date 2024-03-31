package com.chy.lamia.convert.core.entity;


import com.chy.lamia.convert.core.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
     * 要转换成的变量
     */
    @Getter
    VarDefinition target;

    /**
     * 参与转换的参数
     * key: 参与转换变量的名称, 如果使用 @MapMember 修改过名称 , 这里存储的是没改名前的名称
     * value: 对应的变量
     */
    @Getter
    Map<String, VarDefinition> args = new HashMap<>();

    @Getter
    @Setter
    LamiaExpression lamiaExpression;

    /**
     * 转换的结果变量名称
     */
    @Getter
    @Setter
    String resultVarName;

    /**
     * 是否需要定义结果变量的类型，如果不需要说明这个结果变量是一个已经定义好的变量
     */
    @Getter
    @Setter
    boolean declareResultVarType = true;

    @Getter
    @Setter
    boolean isReturn = false;


    public LamiaConvertInfo(LamiaExpression lamiaExpression) {
        this.lamiaExpression = lamiaExpression;
    }

    public LamiaConvertInfo() {
    }

    public void addVarArgs(VarDefinition varDefinition) {
        String varName = varDefinition.getVarRealName();
        VarDefinition existVd = args.get(varName);
        if (existVd == null) {
            args.put(varName, varDefinition);
            return;
        }
        // 如果有重复的, 那么使用优先级判断
        if (varDefinition.getPriority() > existVd.getPriority()) {
            args.put(varName, varDefinition);
        }
    }

    public Set<String> getAllArgsName() {
        return lamiaExpression.getAllArgs().keySet();
    }

    public Map<String, ProtoMaterialInfo> getAllArgs() {
        return lamiaExpression.getAllArgs();
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
