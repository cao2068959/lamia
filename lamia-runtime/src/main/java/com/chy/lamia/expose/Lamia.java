package com.chy.lamia.expose;


public class Lamia {

    public static Object convert(Object... param) {
        throw new RuntimeException("转换失败，无效的表达式");
    }

    public static Object mapping(Object... param) {
        throw new RuntimeException("转换失败，无效的表达式");
    }

    public static LamiaConfiguration config() {
        return new LamiaConfiguration();
    }

}
