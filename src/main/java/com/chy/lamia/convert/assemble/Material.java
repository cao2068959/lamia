package com.chy.lamia.convert.assemble;

import com.chy.lamia.convert.builder.VarExpressionFunction;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.utils.JCUtils;
import lombok.Getter;
import lombok.Setter;


/**
 * @author bignosecat
 */
@Getter
@Setter
public class Material {


    /**
     * 该材料提供的 名称是什么，用于和真正要转换字段的名称匹配
     */
    String supplyName;

    /**
     * 该材料的变量本身， 如果该材料提供的是 a.getName(), 那这里 varDefinition 指的是 A a; 这个变量
     */
    VarDefinition varDefinition;

    /**
     *  真正去执行的类型是什么, 如果 var 是 Optional<A> 类型, 但是真正参与表达式的是 a.getXX() 那么这里execType 是 A类型
     */
    TypeDefinition execType;

    /**
     * 这个材料提供的类型是什么, 可以理解成 getXX() 返回的参数类型
     */
    TypeDefinition supplyType;

    /**
     * 表达式生成函数, 传入真实的变量名,生成执行的表达式, 如果普通对象就是变量名称,如果是map/扩散 则是 map.get("") , 或者 vobj.getVar();
     */
    VarExpressionFunction varExpressionFunction;


    public static Material simpleMaterial(VarDefinition varDefinition) {
        Material result = new Material();
        result.setSupplyName(varDefinition.getVarName());
        result.setVarDefinition(varDefinition);
        // 包装成一个表达式
        result.setVarExpressionFunction(name -> JCUtils.instance.memberAccess(name));
        return result;
    }



}