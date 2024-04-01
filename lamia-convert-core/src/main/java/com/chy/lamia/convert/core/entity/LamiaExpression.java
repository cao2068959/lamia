package com.chy.lamia.convert.core.entity;

import com.chy.lamia.convert.core.components.entity.Expression;
import lombok.Data;

import java.util.*;

/**
 * @author bignosecat
 */
@Data
public class LamiaExpression {

    /**
     * 要设置的全部参数, 直接设置的字段对象以及需要字段映射的都在这里面
     * key 是这个字段的id
     */
    Map<String, ProtoMaterialInfo> allArgs = new HashMap<>();

    /**
     * 构建的信息会存放到这个里面
     */
    BuildInfo buildInfo;

    TypeDefinition targetType;

    Expression target;

    /**
     * 是否已经解析完成
     */
    @lombok.Setter
    private boolean parseComplete = true;


    /**
     * 判断这一个表达式是否完整
     *
     * @return
     */
    public boolean isComplete() {
        return parseComplete;
    }

    /**
     * 是否 有转换数据
     *
     * @return
     */
    public boolean hasConvertData() {
        return !allArgs.isEmpty();
    }

    public void addArgs(Collection<ProtoMaterialInfo> args) {
        for (ProtoMaterialInfo arg : args) {
            addArgs(arg);
        }
    }

    public void addArgs(ProtoMaterialInfo data) {
        String id = data.getId();
        data.setBuildInfo(buildInfo);
        ProtoMaterialInfo protoMaterialInfo = allArgs.get(id);
        if (protoMaterialInfo == null) {
            allArgs.put(id, data);
        } else {
            protoMaterialInfo.merge(data);
        }
    }


    public BuildInfo updateBuild() {
        this.buildInfo = new BuildInfo();
        return buildInfo;
    }
}
