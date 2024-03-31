package com.chy.lamia.element.annotation;


import com.chy.lamia.entity.ClassTreeWrapper;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AnnotationProxyFactory {


    public static <T extends Annotation> Optional<T> createdAnnotation(List<Attribute.TypeCompound> typeCompounds,
                                                                       Class<T> annotationClass) {

        if (typeCompounds == null) {
            return Optional.empty();
        }
        for (Attribute.TypeCompound typeCompound : typeCompounds) {
            if (!annotationClass.getName().equals(typeCompound.type.toString())) {
                continue;
            }

            Map<String, String> argsMap = typeCompoundToMap(typeCompound.values);
            SimpleAnnotationInvocationHandler invocationHandler =
                    new SimpleAnnotationInvocationHandler(annotationClass, argsMap);
            T proxyInstance = (T) Proxy.newProxyInstance(annotationClass.getClassLoader(),
                    new Class<?>[]{annotationClass}, invocationHandler);
            return Optional.of(proxyInstance);
        }
        return Optional.empty();
    }

    private static Map<String, String> typeCompoundToMap(List<Pair<Symbol.MethodSymbol, Attribute>> values) {
        Map<String, String> result = new HashMap<>();
        if (values == null || values.isEmpty()) {
            return result;
        }
        for (Pair<Symbol.MethodSymbol, Attribute> attribute : values) {
            Object value = attribute.snd.getValue();
            if (value == null) {
                continue;
            }
            result.put(attribute.fst.name.toString(), value.toString());
        }
        return result;
    }


    public static <T extends Annotation> Optional<T> createdAnnotation(ClassTreeWrapper classTree, List<JCTree.JCAnnotation> annotations,
                                                                       Class<T> annotationClass) {
        if (annotations == null) {
            return Optional.empty();
        }
        for (JCTree.JCAnnotation jcAnnotation : annotations) {
            if (!jcAnnotation.hasTag(JCTree.Tag.ANNOTATION)) {
                continue;
            }
            Type type = classTree.getFullType(jcAnnotation.annotationType.toString());
            //该注解和要查找的注解不匹配
            if (!annotationClass.getName().equals(type.toString())) {
                continue;
            }
            List<JCTree.JCExpression> args = jcAnnotation.args;
            //把参数全部参入一个map中
            Map<String, String> argsMap = argsToMap(args);

            SimpleAnnotationInvocationHandler invocationHandler =
                    new SimpleAnnotationInvocationHandler(annotationClass, argsMap);

            T proxyInstance = (T) Proxy.newProxyInstance(annotationClass.getClassLoader(),
                    new Class<?>[]{annotationClass}, invocationHandler);
            return Optional.of(proxyInstance);
        }
        return Optional.empty();
    }

    private static Map<String, String> argsToMap(List<JCTree.JCExpression> args) {
        Map<String, String> result = new HashMap<>();
        for (JCTree.JCExpression arg : args) {
            if (arg instanceof JCTree.JCAssign) {
                JCTree.JCAssign jcAssign = (JCTree.JCAssign) arg;
                result.put(jcAssign.lhs.toString(), ((JCTree.JCLiteral) jcAssign.rhs).getValue().toString());
            }
            if (arg instanceof JCTree.JCLiteral) {
                JCTree.JCLiteral jcLiteral = (JCTree.JCLiteral) arg;
                String value = (String) jcLiteral.getValue();
                result.put("value", value);
            }

        }
        return result;
    }

}
