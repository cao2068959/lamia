package com.chy.lamia.convert.core.components.entity;

import com.chy.lamia.convert.core.entity.MethodParameterWrapper;
import com.chy.lamia.convert.core.utils.struct.Pair;

public interface Expression {

    public Object get();

    /**
     * 解析方法引用操作符，返回一个Pair，第一个元素是方法引用的类名，第二个元素是方法名
     * 如: user::getName 返回 Pair("com.chy.user", "getName")
     *
     * @return
     */
    public Pair<String, String> parseMethodReferenceOperator();


    /**
     * 将一个 Expression 转成一个 MethodParameterWrapper（如果是的话） 否则会抛出异常
     */
    public MethodParameterWrapper toMethodParameterWrapper();

}
