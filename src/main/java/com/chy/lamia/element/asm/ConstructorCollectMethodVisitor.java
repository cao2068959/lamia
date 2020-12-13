package com.chy.lamia.element.asm;


import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.NameAndType;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ConstructorCollectMethodVisitor extends MethodVisitor {


    private final List<Constructor> constructors;
    private List<NameAndType> args = new LinkedList<>();


    public ConstructorCollectMethodVisitor(List<Constructor> constructors) {
        super(Opcodes.ASM5);
        this.constructors = constructors;

    }


    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if("this".equals(name)){
            return;
        }

        String className = Type.getType(desc).getClassName();
        NameAndType nameAndType = new NameAndType(name, className);
        args.add(nameAndType);
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
