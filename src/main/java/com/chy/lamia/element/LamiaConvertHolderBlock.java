package com.chy.lamia.element;

import lombok.Getter;

import java.util.Map;

/**
 * 持有了 所有 lamia.convert 语句 的代码块, 这些代码块也就是后续需要去修改内容的
 *
 * @author bignosecat
 */
@Getter
public class LamiaConvertHolderBlock {

    /**
     * 在代码中的所有 lamia.convert 语句, 以及对应这个语句能够访问到所有的变量
     *
     * key : lamia.convert 语句 在代码块中的id, 用于标识这个转换语句在代码块中的位置
     * value: 对应的 LamiaConvertScope 对象
     *
     */
    private Map<String, LamiaConvertInfo> lamiaConvertScopes;



}
