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
     */
    Map<String, BuildInfo> allArgs = new LinkedHashMap<>();

    /**
     * 用于映射的参数名称
     */
    Set<String> mappingArgs = new LinkedHashSet<>();

    /**
     * 构建的信息会存放到这个里面
     */
    BuildInfo buildInfo;

    TypeDefinition targetType;

    Expression target;

    /**
     * 是否已经解析完成
     */
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
        return !allArgs.isEmpty() || !mappingArgs.isEmpty();
    }

    public void setParseComplete(boolean parseComplete) {
        this.parseComplete = parseComplete;
    }

    public void addArgs(Collection<String> args) {
        for (String arg : args) {
            BuildInfo oldBuildInfo = allArgs.get(arg);
            if (oldBuildInfo == null) {
                allArgs.put(arg, buildInfo);
            } else {
                oldBuildInfo.merge(buildInfo);
            }
        }
        // 这一批 buildInfo 用完了，需要重新创建一个


    }

    public void addSpreadArgs(Collection<String> args) {
        addArgs(args);
        mappingArgs.addAll(args);
    }

    public BuildInfo updateBuild() {
        this.buildInfo = new BuildInfo();
        return buildInfo;
    }
}
