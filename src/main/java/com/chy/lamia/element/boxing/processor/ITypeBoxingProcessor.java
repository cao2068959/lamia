package com.chy.lamia.element.boxing.processor;


import com.chy.lamia.entity.TypeDefinition;

public interface ITypeBoxingProcessor {

    /**
     * 该处理器处理的 class的全路径是什么
     *
     * @return
     */
    String handleClassName();


    /**
     * 拆箱
     *
     * @param typeDefinition
     * @return 返回拆箱后的类型
     */
    TypeBoxingDefinition unboxing(TypeDefinition typeDefinition);

}
