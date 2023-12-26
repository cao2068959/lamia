package com.chy.lamia.components;

import com.chy.lamia.convert.core.components.NameHandler;
import com.chy.lamia.convert.core.utils.CommonUtils;

public class CommonNameHandler implements NameHandler {
    @Override
    public String generateName(String type) {
        return CommonUtils.generateVarName(type);
    }

    @Override
    public String generateTempName(String name) {
        return CommonUtils.tempName(name);
    }
}
