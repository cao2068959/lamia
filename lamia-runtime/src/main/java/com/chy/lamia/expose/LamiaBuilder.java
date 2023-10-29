package com.chy.lamia.expose;


import com.chy.lamia.expose.rule.LamiaRule;
import com.chy.lamia.expose.rule.RuleType;

public class LamiaBuilder {


    public Object build() {
        throw new RuntimeException("转换失败，无效的表达式");
    }


    /**
     * 设置一些转换规则，紧接着规则后面的  mapping、setField 将会生效
     *
     * @return
     */
    public LamiaRule rule(RuleType type) {
        return new LamiaRule();
    }

    /**
     * 把对象中所有的值都映射进去
     *
     * @param param
     * @return
     */
    public LamiaBuilder mapping(Object... param) {
        return this;
    }


    /**
     * 直接对字段进行设置
     *
     * @param param
     * @return
     */
    public LamiaBuilder setField(Object... param) {
        return this;
    }


}
