package com.chy.lamia.convert.core.components;

/**
 * 名称处理器
 */
public interface NameHandler {

    /**
     * 生成一个 名称
     *
     * @param type 要生成的类型
     */
    public String generateName(String type);

    /**
     * 生成一个临时的名称
     *
     * @param name 原本的名称
     */
    public String generateTempName(String name);

}
