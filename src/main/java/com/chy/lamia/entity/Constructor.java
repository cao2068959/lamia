package com.chy.lamia.entity;

import java.util.ArrayList;
import java.util.List;

public class Constructor {

    private List<ParameterType> params = new ArrayList<>();

    public void add(String name, String typePath) {
        ParameterType parameterType = new ParameterType(name, typePath);
        params.add(parameterType);
    }

    public void add(ParameterType parameterType) {
        params.add(parameterType);
    }


    public List<ParameterType> getParams() {
        return params;
    }
}
