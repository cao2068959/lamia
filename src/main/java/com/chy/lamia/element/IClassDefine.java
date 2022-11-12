package com.chy.lamia.element;

import com.chy.lamia.entity.*;

import java.util.List;
import java.util.Map;

public interface IClassDefine {



    Map<String, Var> getInstantVars();

    Map<String, Getter> getInstantGetters();

    Map<String, Setter> getInstantSetters();

    List<Constructor> getConstructors();

    List<SimpleMethod> getAllMethod();

}
