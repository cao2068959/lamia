package com.chy.lamia.element.annotation;


import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SimpleAnnotationInvocationHandler implements InvocationHandler {


    private final Class<? extends Annotation> annotationClass;
    private final Map<String, String> argsMap;

    public SimpleAnnotationInvocationHandler(Class<? extends Annotation> annotationClass, Map<String, String> argsMap) {
        if (argsMap == null) {
            argsMap = new HashMap<>();
        }
        this.annotationClass = annotationClass;
        this.argsMap = argsMap;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String member = method.getName();
        if (member.equals("toString")) {
            return toStringImpl();
        }
        if (member.equals("hashCode")) {
            return hashCodeImpl();
        }

        if (member.equals("annotationType")) {
            return annotationClass;
        }

        String result = argsMap.get(member);
        if (result == null) {
            return method.getDefaultValue();
        }

        Class<?> returnType = method.getReturnType();
        return transformType(result, returnType);
    }

    private Object transformType(String strType, Class type) {
        if (type == String.class) {
            return strType;
        }

        if (type == Boolean.class || boolean.class == type) {
            if ("true".equals(strType)) {
                return true;
            } else {
                return false;
            }
        }
        return strType;
    }


    private int hashCodeImpl() {
        int simpleNameHashCode = annotationClass.getSimpleName().hashCode();
        int argsMapHashCode = argsMap.hashCode();
        return argsMapHashCode + simpleNameHashCode;
    }

    private String toStringImpl() {
        String simpleName = annotationClass.getSimpleName();
        return simpleName + ":[" + argsMap + "]";
    }


}
