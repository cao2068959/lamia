package com.chy.lamia.enums;


/**
 * Candidate 匹配的结果，具体查看 类 Candidate
 */
public enum MatchReuslt {

    /**
     * 匹配成功
     */
    HIT(0, "hit"),
    /**
     * 疑似成功，一般都是名称相同，但是类型不匹配， 有可能是 optional这样的包装类型
     */
    MAY(1, "may"),
    /**
     * 失败
     */
    MISS(1, "miss");

    private Integer code;
    private String name;

    MatchReuslt(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
