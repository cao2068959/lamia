package com.chy.lamia.element.annotation;


import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AnnotationProxyFactory {


    public static <T extends Annotation> Optional<T> createdAnnotation(JCTree classTree, List<JCTree.JCAnnotation> annotations,
                                                                       Class<T> annotationClass) {
        if (annotations == null) {
            return Optional.empty();
        }
        for (JCTree.JCAnnotation jcAnnotation : annotations) {
            if (!jcAnnotation.hasTag(JCTree.Tag.ANNOTATION)) {
                continue;
            }
            Type type = JCUtils.instance.attribType(classTree, jcAnnotation.annotationType.toString());
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
