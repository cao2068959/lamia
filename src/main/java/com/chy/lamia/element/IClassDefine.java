package com.chy.lamia.element;

import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.entity.Var;

import java.util.List;
import java.util.Map;

public interface IClassDefine {


    AssembleFactory getAssembleFactory();

    Map<String, Var> getInstantVars();

    Map<String, Getter> getInstantGetters();

    Map<String, Setter> getInstantSetters();

    List<Constructor> getConstructors();
}
