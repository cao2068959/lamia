package com.chy.lamia.expose;


public class Lamia {

    /**
     * 把对象中所有的值都映射进去
     *
     * @param param
     * @return
     */
    public static Object mapping(Object... param) {
        throw new RuntimeException("转换失败，无效的表达式");
    }


    /**
     * 直接对字段进行设置
     *
     * @param param
     * @return
     */
    public static Object setField(Object... param) {
        throw new RuntimeException("转换失败，无效的表达式");
    }

    /**
     * 自定义用更灵活的方式进行配置
     * @return
     */
    public static LamiaBuilder builder() {
        return new LamiaBuilder();
    }

}
