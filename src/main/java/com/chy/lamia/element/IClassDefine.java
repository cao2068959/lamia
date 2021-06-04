package com.chy.lamia.element;

import com.chy.lamia.element.assemble.valobj.ValueObjectAssembleFactory;
import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.entity.Var;

import java.util.List;
import java.util.Map;

public interface IClassDefine {


    ValueObjectAssembleFactory getAssembleFactory();

    Map<String, Var> getInstantVars();

    Map<String, Getter> getInstantGetters();

    Map<String, Setter> getInstantSetters();

    List<Constructor> getConstructors();
}
