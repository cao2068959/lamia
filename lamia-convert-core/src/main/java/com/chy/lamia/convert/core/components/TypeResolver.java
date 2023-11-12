package com.chy.lamia.convert.core.components;

import com.chy.lamia.convert.core.entity.Constructor;
import com.chy.lamia.convert.core.entity.Getter;
import com.chy.lamia.convert.core.entity.Setter;
import com.chy.lamia.convert.core.entity.TypeDefinition;

import java.util.List;
import java.util.Map;

public interface TypeResolver {

    /**
     * 获取所有的 set方法
     *
     * @return
     */
    Map<String, Setter> getInstantSetters();

    /**
     * 获取所有的构造器
     *
     * @return
     */
    List<Constructor> getConstructors();

    /**
     * 获取所有的 getter语句
     *
     * @return
     */
    Map<String, Getter> getInstantGetters();

    TypeDefinition getTypeDefinition();


}
