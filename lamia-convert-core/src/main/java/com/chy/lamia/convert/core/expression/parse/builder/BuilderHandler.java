package com.chy.lamia.convert.core.expression.parse.builder;

import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.entity.MethodParameterWrapper;
import com.chy.lamia.convert.core.entity.ProtoMaterialInfo;
import com.chy.lamia.convert.core.expression.parse.ConfigParseContext;
import com.chy.lamia.convert.core.expression.parse.entity.ArgWrapper;
import com.chy.lamia.convert.core.expression.parse.entity.MethodWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置项处理器的基础接口
 *
 * @author bignosecat
 */
public interface BuilderHandler {

    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context);


    default List<String> fetchArgsName(List<Expression> args) {
        List<String> result = new ArrayList<>();
        for (Expression arg : args) {
            result.add(arg.get().toString());
        }
        return result;
    }

    default ProtoMaterialInfo toProtoMaterialInfo(ArgWrapper arg) {
        MethodParameterWrapper methodParameter = arg.getExpression().toMethodParameterWrapper();
        String id = methodParameter.getName();
        if (methodParameter.isMethodInvoke()) {
            id = "methodInvocation-" + methodParameter.getText();
        }
        ProtoMaterialInfo protoMaterialInfo = new ProtoMaterialInfo(id);
        protoMaterialInfo.setMaterial(methodParameter);
        return protoMaterialInfo;
    }

}
