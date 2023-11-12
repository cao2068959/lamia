package com.chy.lamia.entity;


import com.chy.lamia.convert.core.annotation.MapMember;

import java.util.Optional;

public class ParameterTypeMemberAnnotation extends ParameterType {

    private Optional<MapMember> mapMember;

    public ParameterTypeMemberAnnotation(String name, String typePath, Optional<MapMember> mapMember) {
        super(name, typePath);
        this.mapMember = mapMember;
    }


    public Optional<MapMember> getMapMember() {
        return mapMember;
    }
}
