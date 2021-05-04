package com.chy.lamia.element.asm;


import com.chy.lamia.entity.*;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;


import java.util.*;

import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.signature.SignatureReader;

public class ClassMetadataReadingVisitor extends ClassVisitor {

    /**
     * 实例中的所有属性
     */
    private Map<String, Var> instantVars = new HashMap<>();

    /**
     * 实例中所有的 getter
     * key getter方法对应的 字段的名称
     */
    private Map<String, Getter> instantGetters = new HashMap<>();
    private Map<String, Setter> instantSetters = new HashMap<>();

    /**
     * 实例中所有的构造器
     */
    private List<Constructor> constructors = new ArrayList<>();

    public ClassMetadataReadingVisitor() {
        super(Opcodes.ASM5);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        //构造器, 构造器需要去获取 参数的名字 所以使用 MethodVisitor去解析
        if ("<init>".equals(name)) {
            return new ConstructorCollectMethodVisitor(constructors);
        }

        Type returnType = Type.getReturnType(desc);
        Type[] argumentTypes = Type.getArgumentTypes(desc);

        ParameterTypeSignatureHandleWarpper signatureHandleWarpper = new ParameterTypeSignatureHandleWarpper(signature);
        //get 开头当做 getter方法处理
        if (name.startsWith("get")) {
            getterHandle(name, argumentTypes, returnType, signatureHandleWarpper);
            return null;
        }

        //set 开头当做 setter方法处理
        if (name.startsWith("set")) {
            setterHandle(name, argumentTypes, returnType, signatureHandleWarpper);
            return null;
        }

        return null;
    }

    private void setterHandle(String name, Type[] argumentTypes, Type returnType, ParameterTypeSignatureHandleWarpper signatureHandleWarpper) {

        if (!"void".equals(returnType.getClassName().toLowerCase())) {
            return;
        }

        if (argumentTypes == null || argumentTypes.length != 1) {
            return;
        }
        Type argumentType = argumentTypes[0];


        Setter setter = new Setter();
        String parameterTypeName = argumentTypes[0].getClassName();
        setter.setSimpleName(name);


        ParameterType parameterType = signatureHandleWarpper.getParameter(0)
                .orElseGet(() -> new ParameterType(parameterTypeName));
        setter.setParameterType(parameterType);
        String varName = varNameHandle(name.substring(3));
        instantSetters.put(varName, setter);
    }

    private void getterHandle(String name, Type[] argumentTypes, Type returnType, ParameterTypeSignatureHandleWarpper signatureHandleWarpper) {
        if (argumentTypes != null && argumentTypes.length > 0) {
            return;
        }
        String returnTypeName = returnType.getClassName();
        String lowReturnTypeName = returnTypeName.toLowerCase();
        if ("void".equals(lowReturnTypeName.toLowerCase())) {
            return;
        }
        String varName = varNameHandle(name.substring(3));
        if (varName == null) {
            return;
        }


        Getter getter = new Getter();
        getter.setSimpleName(name);
        getter.setTypePath(returnTypeName);
        instantGetters.put(varName, getter);

    }

    /**
     * 处理一下 var的名字
     * 把开头字母给小写
     *
     * @param data
     * @return
     */
    private String varNameHandle(String data) {
        if (data == null || data.length() < 1) {
            return null;
        }

        char[] chars = data.toCharArray();
        chars[0] = toLow(chars[0]);
        return new String(chars);
    }

    private char toLow(char c) {
        if (c >= 'A' && c <= 'Z') {
            c += 32;
        }
        return c;
    }

    public Map<String, Var> getInstantVars() {
        return instantVars;
    }

    public Map<String, Getter> getInstantGetters() {
        return instantGetters;
    }

    public Map<String, Setter> getInstantSetters() {
        return instantSetters;
    }

    public List<Constructor> getConstructors() {
        return constructors;
    }
}
