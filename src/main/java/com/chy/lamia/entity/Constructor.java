package com.chy.lamia.entity;

import java.util.ArrayList;
import java.util.List;

public class Constructor {

    private List<NameAndType> params = new ArrayList<>();

    public void add(String name, String typePath) {
        NameAndType nameAndType = new NameAndType(name, typePath);
        params.add(nameAndType);
    }


    public List<NameAndType> getParams() {
        return params;
    }
}
