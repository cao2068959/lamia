package com.chy.lamia.convert.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bignosecat
 */
@Target({ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.SOURCE)
public @interface MapMember {
    /**
     * 优先级
     */
    int priority() default -1;
    /**
     * 要转换到的属性的名称
     *
     * @return
     */
    String value() default "";

    /**
     * 这个属性是否扩散, 扩散的含义在于 如果是对象将把这个对象中的字段映射到新对象中, 如果是map将调用 map.get("key") 去映射
     * @return
     */
    boolean spread() default false;
}
