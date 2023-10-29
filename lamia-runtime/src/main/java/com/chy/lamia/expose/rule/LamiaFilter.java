package com.chy.lamia.expose.rule;

import java.util.function.Consumer;

/**
 * @author bignosecat
 */
public interface LamiaFilter {


    /**
     * 让用户自定义 过滤接口, 可以 在set值之设置一些通用逻辑
     *
     * @param data
     * @param setFun
     */
    void filter(Object data, Consumer<Object> setFun);

}
