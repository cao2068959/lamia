package com.chy.lamia.convert.assemble;

import com.chy.lamia.convert.builder.MaterialStatementBuilder;
import com.chy.lamia.element.LamiaConvertInfo;

import java.util.List;

/**
 * map的组装处理器
 *
 * @author bignosecat
 */
public class MapAssembleHandler implements AssembleHandler {


    private LamiaConvertInfo lamiaConvertInfo;

    @Override
    public void addMaterial(List<Material> materials) {

    }

    @Override
    public List<MaterialStatementBuilder> run() {
        return null;
    }

    @Override
    public void setLamiaConvertInfo(LamiaConvertInfo lamiaConvertInfo) {
        this.lamiaConvertInfo = lamiaConvertInfo;
    }
}
