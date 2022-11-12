package com.chy.lamia.element.asm;


import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.LinkedList;
import java.util.List;

public class ConstructorCollectMethodVisitor extends MethodVisitor {


    private final List<Constructor> constructors;
    private List<VarDefinition> args = new LinkedList<>();


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
        TypeDefinition parameterType = signatureWarpper.getSuperClass()
                .orElseGet(() -> new TypeDefinition(className));
        VarDefinition varDefinition = new VarDefinition(name, parameterType);
        args.add(varDefinition);
    }

    @Override
    public void visitEnd() {
        Constructor constructor = new Constructor();
        args.forEach(constructor::add);
        constructors.add(constructor);
    }
}
