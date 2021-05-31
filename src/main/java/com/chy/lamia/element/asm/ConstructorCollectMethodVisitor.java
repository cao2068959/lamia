package com.chy.lamia.element.asm;


import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.utils.SymbolUtils;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
