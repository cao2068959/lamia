package com.chy.lamia.convert.core.entity;


import com.chy.lamia.convert.core.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

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
     * 要转的实例
     */
    @Getter
    @Setter
    VarDefinition target;

    /**
     * 参与转换的参数
     * key: 参与转换变量的名称, 如果使用 @MapMember 修改过名称 , 这里存储的是没改名前的名称
     * value: 对应的变量
     */
    @Getter
    Map<String, VarDefinition> args = new HashMap<>();

    LamiaExpression lamiaExpression;

    @Getter
    @Setter
    String varName;

    @Getter
    @Setter
    boolean createdType = true;

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

    /**
     * 根据优先级 来获取 对应的 参数
     * 优先级 数字越大 则越优先
     * 低优先级的放队头，高优先级放队尾
     * <p>
     * <p>
     * 1.根据 varDefinition.getPriority() 来获取优先级，不设置 则是 -1
     * 2.如果获取的优先级是一样的，那么 根据所在 allArgsNames 中的位置判定
     *
     * @return 对应的 var列表
     */
    public List<ConvertVarInfo> getArgsByPriority() {
        List<ConvertVarInfo> result = new ArrayList<>();
        getAllArgs().forEach((name, rule) -> {
            VarDefinition varDefinition = args.get(name);
            result.add(new ConvertVarInfo(varDefinition, rule));
        });
        // 排序
        result.sort(Comparator.comparingInt(cvi -> cvi.getVarDefinition().getPriority()));

        return result;
    }

    public Set<String> getAllArgsName() {
        return lamiaExpression.getAllArgs().keySet();
    }

    public Map<String, RuleInfo> getAllArgs() {
        return lamiaExpression.getAllArgs();
    }

    public boolean isSpread(VarDefinition varDefinition) {
        // 系统的基础类型不进行扩散
        if (varDefinition.getType().isBaseTypeOrSystemType()) {
            return false;
        }
        // 变量的注解上面标注了一定进行扩散
        boolean spread = varDefinition.isSpread();
        if (spread) {
            return true;
        }
        if (lamiaExpression.getMappingArgs().contains(varDefinition.getVarRealName())) {
            return true;
        }
        return false;
    }

}
