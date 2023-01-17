package com.chy.lamia.expose;


public class LamiaConfiguration {

    public Object convert(Object... param) {
        throw new RuntimeException("转换失败，无效的表达式");
    }


    public LamiaConfiguration defaultSpread(Boolean b) {
        return this;
    }


    public LamiaConfiguration spreadArgs(Object... param){
        return this;
    }





}
