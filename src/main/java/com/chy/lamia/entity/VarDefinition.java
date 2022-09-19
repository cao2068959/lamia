package com.chy.lamia.entity;

import com.chy.lamia.annotation.MapMember;
import lombok.Data;

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

    @Override
    public String toString() {
        return type.toString() + " " + varRealName;
    }
}
