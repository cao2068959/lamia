package com.chy.lamia.element.asm;

import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;

public class MethodSignatureVisitor extends SignatureVisitor {

    public MethodSignatureVisitor() {
        super(ASM5);
    }


    @Override
    public SignatureVisitor visitParameterType() {
        return super.visitParameterType();
    }


    @Override
    public void visitFormalTypeParameter(String name) {
        super.visitFormalTypeParameter(name);
    }

    @Override
    public void visitBaseType(char descriptor) {
        super.visitBaseType(descriptor);
    }

    @Override
    public void visitTypeVariable(String name) {
        super.visitTypeVariable(name);
    }

    @Override
    public void visitClassType(String name) {
        super.visitClassType(name);
    }

    @Override
    public void visitInnerClassType(String name) {
        super.visitInnerClassType(name);
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        return super.visitTypeArgument(wildcard);
    }

    @Override
    public SignatureVisitor visitClassBound() {
        return super.visitClassBound();
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        return super.visitInterfaceBound();
    }



    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
