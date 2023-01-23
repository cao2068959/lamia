package com.chy.lamia.convert.assemble;

import com.chy.lamia.convert.builder.MaterialStatementBuilder;
import com.chy.lamia.element.LamiaConvertInfo;

import java.util.List;

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

}
