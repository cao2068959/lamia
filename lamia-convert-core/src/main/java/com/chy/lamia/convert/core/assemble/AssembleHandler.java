package com.chy.lamia.convert.core.assemble;


import com.chy.lamia.convert.core.entity.LamiaConvertInfo;
import com.chy.lamia.convert.core.expression.imp.builder.MaterialStatementBuilder;

import java.util.List;
import java.util.Set;

/**
 * 组装处理器接口
 *
 * @author bignosecat
 */
public interface AssembleHandler {

    /**
     * 添加对应的组合材料
     *
     * @param materials
     */

    void addMaterial(List<Material> materials);

    /**
     * 运行整个组成器，生成对应的转换语句
     *
     * @return
     */
    List<MaterialStatementBuilder> run();

    /**
     * 设置 LamiaConvertInfo 的接口, 配置的一些信息都在里面
     *
     * @param lamiaConvertInfo
     */
    void setLamiaConvertInfo(LamiaConvertInfo lamiaConvertInfo);

    /**
     * 获取这次转换后的变量的名称
     *
     * @return
     */
    String getNewInstantName();

    /**
     * 获取映射上的所有字段的 名称
     *
     * @return
     */
    Set<String> getMappingVarName();

    /**
     * 使用 varName 来获取 对应的 Material
     *
     * @param name
     * @return
     */
    Material getMaterial(String name);

}
