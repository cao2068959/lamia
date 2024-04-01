package com.chy.lamia.convert.core.assemble;

import com.chy.lamia.convert.core.entity.*;
import com.chy.lamia.convert.core.expression.imp.builder.VarExpressionFunction;
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
     * 这个材料提供的类型是什么, 可以理解成 getXX() 返回的参数类型
     */
    TypeDefinition supplyType;


    /**
     * 材料的原始信息，如 这个材料是 a.getName , 这里 protoMaterialInfo 指代的就是 A a 这个信息
     */
    ProtoMaterialInfo protoMaterialInfo;

    /**
     * 表达式生成函数, 传入真实的变量名,生成执行的表达式, 如果普通对象就是变量名称,如果是map/扩散 则是 map.get("") , 或者 vobj.getVar();
     */
    VarExpressionFunction varExpressionFunction;


    public Material(ProtoMaterialInfo protoMaterialInfo) {
        this.protoMaterialInfo = protoMaterialInfo;
    }

    public static Material simpleMaterial(ProtoMaterialInfo protoMaterialInfo) {
        if (protoMaterialInfo.getMaterial().isMethodInvoke()) {
            throw new RuntimeException("方法[simpleMaterial] 不支持参数是调用表达式的情况");
        }
        Material result = new Material(protoMaterialInfo);
        MethodParameterWrapper parameterWrapper = protoMaterialInfo.getMaterial();
        result.setSupplyName(parameterWrapper.getName());
        result.setSupplyType(parameterWrapper.getType());
        // 包装成一个表达式
        result.setVarExpressionFunction(expression -> expression);
        return result;
    }

    public boolean isIgnoreField(String classPath, String fieldName) {
        return protoMaterialInfo.getBuildInfo().isIgnoreField(classPath, fieldName);
    }

}
