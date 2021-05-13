package com.chy.lamia.element.annotation;


import sun.reflect.annotation.ExceptionProxy;

import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SimpleAnnotationInvocationHandler implements InvocationHandler {



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String member = method.getName();
        int parameterCount = method.getParameterCount();

        if (parameterCount == 1 && member == "equals" &&
                method.getParameterTypes()[0] == Object.class) {
            return equalsImpl(proxy, args[0]);
        }
        if (parameterCount != 0) {
            throw new AssertionError("Too many parameters for an annotation method");
        }

        if (member == "toString") {
            return toStringImpl();
        } else if (member == "hashCode") {
            return hashCodeImpl();
        } else if (member == "annotationType") {
            return type;
        }

        // Handle annotation member accessors
        Object result = memberValues.get(member);

        if (result == null)
            throw new IncompleteAnnotationException(type, member);


        if (result.getClass().isArray() && Array.getLength(result) != 0)
            result = cloneArray(result);

        return result;
    }
}
