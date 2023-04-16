package com.chy.lamia.expose;


public class LamiaBuilder {


    public Object build(){
        throw new RuntimeException("转换失败，无效的表达式");
    }

    public LamiaBuilder convert(Object... param) {
        return this;
    }

    public LamiaBuilder setArgs(Object... param){
        return this;
    }





}
