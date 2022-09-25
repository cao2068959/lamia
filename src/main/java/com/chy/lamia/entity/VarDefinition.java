package com.chy.lamia.entity;

import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.convert.builder.BoxingExpressionBuilder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 对变量 的描述
 * <p>
 * 这里凡是 {类型 名称} 的数据都可以用 VarDefinition 不仅仅是传统意义上的 "变量" ， 所以常量也行
 */
@Data
public class VarDefinition {
    /**
     * 变量的真实名称
     */
    String varRealName;

    /**
     * 变量的名称，正常情况下和 varRealName 保持一致，也有可能不同，实际去匹配变量名的时候使用的名称
     */
    String varName;

    /**
     * 变量的类型
     */
    TypeDefinition type;

    /**
     * 这个边上是否标注了注解 @MapMember
     */
    private Optional<MapMember> mapMember = Optional.empty();

    private Map<String, BoxingExpressionBuilder> boxingExpressionBuilderCache = new HashMap<>();


    public VarDefinition(String name, TypeDefinition type) {
        this.varName = name;
        this.varRealName = name;
        this.type = type;
    }


    public void setMapMember(Optional<MapMember> mapMember) {
        this.mapMember = mapMember;
        String name = mapMember.map(MapMember::value).orElse("");
        if ("".equals(name)) {
            return;
        }
        this.varName = name;
    }

    /**
     * 获取 变量的优先级, 取自 @MapMember 中的值,如果没标准注解则 -1
     *
     * @return
     */
    public int getPriority() {
        return mapMember.map(MapMember::priority).orElse(-1);
    }

    public boolean isSpread() {
        return mapMember.map(MapMember::spread).orElse(false);
    }


    /**
     * 将这个变量转成其他类型, 生成对应的表达式, 如果已经转换过,那么存在对应的缓存, 就不会返回对应的转换过程表达式, 只会返回转后的变量名称
     * <p>
     * 只有存在泛型父子关系的才能转换
     *
     * @param targetType
     * @return
     */
    public ExpressionWrapper convert(TypeDefinition targetType) {
        String key = targetType.toString();
        BoxingExpressionBuilder builder = boxingExpressionBuilderCache.computeIfAbsent(key, k -> new BoxingExpressionBuilder(type, varRealName, targetType));
        return builder.getVarExpression();
    }

    @Override
    public String toString() {
        return type.toString() + " " + varRealName;
    }

}
