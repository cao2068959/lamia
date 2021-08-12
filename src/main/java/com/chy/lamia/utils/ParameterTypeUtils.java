package com.chy.lamia.utils;


import com.chy.lamia.element.ClassDetails;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.ParameterType;

import java.util.Map;

public class ParameterTypeUtils {

    /**
     * 去解析Parameter 中的 getter方法
     *
     * @param type
     */
    public static void parameterGetterSpread(ParameterType type, MethodFun<Getter> getterMethodFun) {
        //先把 类型转成 ClassElement 方便获取 getter setter 等一系列的方法
        ClassDetails classDetails = ClassDetails.getClassElement(type);
        //获取这个类里面所有的 getter 方法
        Map<String, Getter> getters = classDetails.getInstantGetters();
        getters.forEach(getterMethodFun::exec);
    }

    public static interface MethodFun<T> {
        void exec(String methodName, T t);

    }

}
