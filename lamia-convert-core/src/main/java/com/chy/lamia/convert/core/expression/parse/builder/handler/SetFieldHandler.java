package com.chy.lamia.convert.core.expression.parse.builder.handler;

import com.chy.lamia.convert.core.entity.BuildInfo;
import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.entity.ProtoMaterialInfo;
import com.chy.lamia.convert.core.expression.parse.ConfigParseContext;
import com.chy.lamia.convert.core.expression.parse.builder.BuilderHandler;
import com.chy.lamia.convert.core.expression.parse.entity.MethodWrapper;

/**
 * 扩散参数自定义的配置处理器
 *
 * @author bignosecat
 */
public class SetFieldHandler implements BuilderHandler,BuilderArgsUse {
    @Override
    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context) {

        methodWrapper.useAllArgs().forEach(arg -> {
            ProtoMaterialInfo protoMaterialInfo = toProtoMaterialInfo(arg);
            lamiaExpression.addArgs(protoMaterialInfo);
        });
        BuildInfo updatedBuild = lamiaExpression.updateBuild();
        updatedBuild.setBuilder(false);
    }

}
