package com.chy.lamia.element.asm;


import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.ParameterType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.LinkedList;
import java.util.List;

public class ConstructorCollectMethodVisitor extends MethodVisitor {


    private final List<Constructor> constructors;
    private List<ParameterType> args = new LinkedList<>();


    public ConstructorCollectMethodVisitor(List<Constructor> constructors) {
        super(Opcodes.ASM5);
        this.constructors = constructors;

    }


    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if ("this".equals(name)) {
            return;
        }

        Type type = Type.getType(desc);
        String className = type.getClassName();
        ParameterTypeSignatureHandleWarpper signatureWarpper = new ParameterTypeSignatureHandleWarpper(signature);
        ParameterType parameterType = signatureWarpper.getSuperClass()
                .orElseGet(() -> new ParameterType(name, className));
        args.add(parameterType);
    }

    @Override
    public void visitEnd() {
        Constructor constructor = new Constructor();
        args.forEach(arg -> {
            constructor.add(arg);
        });
        constructors.add(constructor);
    }
}
