package com.chy.lamia.convert.assemble;

import com.chy.lamia.entity.Expression;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import lombok.Getter;


/**
 * @author bignosecat
 */
@Getter
public class Material {


    /**
     * 要转换对应目标的名称
     */
    String targetName;

    /**
     * 要转换成的类型是什么
     */
    TypeDefinition targetType;

    /**
     * 参与转换的变量
     */
    VarDefinition varDefinition;

    /**
     * 表达式, 如果普通对象就是变量名称,如果是map/扩散 则是 map.get("") , 或者 vobj.getVar();
     */
    Expression expression;

}
