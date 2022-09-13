package com.chy.lamia.convert.assemble;

import com.chy.lamia.convert.ExpressionBuilder;

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
    List<ExpressionBuilder> run();
}
