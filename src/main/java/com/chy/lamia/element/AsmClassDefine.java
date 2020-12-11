package com.chy.lamia.element;


import com.chy.lamia.element.reflect.GetSetCollect;
import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.entity.Var;
import com.chy.lamia.utils.JCUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AsmClassDefine implements IClassDefine {

    private final JCUtils jcUtils;
    private final String classPath;
    private final Class<?> leadClass;




    public AsmClassDefine(JCUtils jcUtils, Class<?> leadClass) {
        this.jcUtils = jcUtils;
        this.classPath = leadClass.getName();
        this.leadClass = leadClass;
    }

    @Override
    public AssembleFactory getAssembleFactory() {
        return null;
    }

    @Override
    public Map<String, Var> getInstantVars() {
        return null;
    }

    @Override
    public Map<String, Getter> getInstantGetters() {
        return null;
    }

    @Override
    public Map<String, Setter> getInstantSetters() {
        return null;
    }

    @Override
    public List<Constructor> getConstructors() {
        return null;
    }
}
