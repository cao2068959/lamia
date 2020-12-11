package com.chy.lamia.element.asm;


import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.entity.Var;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdk.internal.org.objectweb.asm.Type;

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

        //get 开头当做 getter方法处理
        if (name.startsWith("get")) {
            getterHandle(name, argumentTypes, returnType);
            return null;
        }

        //set 开头当做 setter方法处理
        if (name.startsWith("set")) {
            setterHandle(name, argumentTypes, returnType);
            return null;
        }

        return null;
    }

    private void setterHandle(String name, Type[] argumentTypes, Type returnType) {

        if (!"VOID".equals(returnType.getClassName())) {
            return;
        }

        if (argumentTypes == null || argumentTypes.length != 1) {
            return;
        }
        String parameterTypeName = argumentTypes[0].getClassName();
        String varName = varNameHandle(name.substring(3));
        Setter setter = new Setter();
        setter.setTypePath(parameterTypeName);
        setter.setSimpleName(varName);
        instantSetters.put(varName, setter);
    }

    private void getterHandle(String name, Type[] argumentTypes, Type returnType) {
        if (argumentTypes != null && argumentTypes.length > 0) {
            return;
        }
        String returnTypeName = returnType.getClassName();
        if ("VOID".equals(returnTypeName)) {
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

}
