package com.chy.lamia.convert.core.entity;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Constructor {

    @Getter
    private List<VarDefinition> params = new ArrayList<>();

    public void add(VarDefinition varDefinition) {
        params.add(varDefinition);
    }

    public void setParams(List<VarDefinition> params) {
        this.params = params;
    }
}