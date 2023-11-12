package com.chy.lamia.element.class_define;

import com.chy.lamia.convert.core.entity.Constructor;
import com.chy.lamia.convert.core.entity.Getter;
import com.chy.lamia.convert.core.entity.Setter;
import com.chy.lamia.entity.SimpleMethod;
import com.chy.lamia.entity.Var;

import java.util.List;
import java.util.Map;

public interface IClassDefine {



    Map<String, Var> getInstantVars();

    Map<String, Getter> getInstantGetters();

    Map<String, Setter> getInstantSetters();

    List<Constructor> getConstructors();

    List<SimpleMethod> getAllMethod();

}
