package com.chy.lamia.element.asm;


import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;

class SignatureSourcer extends SignatureVisitor {

    /**
     * Buffer used to construct the signature.
     */
    private final StringBuilder mBuf = new StringBuilder();

    /**
     * Buffer used to construct the formals signature.
     */
    private final StringBuilder mFormalsBuf = new StringBuilder();

    /**
     * Indicates if the signature is currently processing formal type parameters.
     */
    private boolean mWritingFormals;

    /**
     * Stack used to keep track of class types that have arguments. Each element
     * of this stack is a boolean encoded in one bit. The top of the stack is
     * the lowest order bit. Pushing false = *2, pushing true = *2+1, popping =
     * /2.
     */
    private int mArgumentStack;

    /**
     * {@link SignatureSourcer} generated when parsing the return type of <em>this</em>
     * signature. Initially null.
     */
    private SignatureSourcer mReturnType;

    /**
     * {@link SignatureSourcer} generated when parsing the super class of <em>this</em>
     * signature. Initially null.
     */
    private SignatureSourcer mSuperClass;

    /**
     * {@link SignatureSourcer}s for each parameters generated when parsing the method parameters
     * of <em>this</em> signature. Initially empty but not null.
     */
    private ArrayList<SignatureSourcer> mParameters = new ArrayList<SignatureSourcer>();



    /**
     * Constructs a new {@link SignatureWriter} object.
     */
    public SignatureSourcer() {
        super(Opcodes.ASM5);
    }

    private StringBuilder getBuf() {
        if (mWritingFormals) {
            return mFormalsBuf;
        } else {
            return mBuf;
        }
    }


    @Override
    public String toString() {
        return mBuf.toString();
    }


    public SignatureSourcer getReturnType() {
        return mReturnType;
    }


    public ArrayList<SignatureSourcer> getParameters() {
        return mParameters;
    }


    public boolean hasFormalsContent() {
        return mFormalsBuf.length() > 0;
    }

    public String formalsToString() {
        return mFormalsBuf.toString();
    }


    public SignatureSourcer getSuperClass() {
        return mSuperClass;
    }

    // ------------------------------------------------------------------------
    // Implementation of the SignatureVisitor interface
    // ------------------------------------------------------------------------

    @Override
    public void visitFormalTypeParameter(final String name) {
        if (!mWritingFormals) {
            mWritingFormals = true;
            getBuf().append('<');
        } else {
            getBuf().append(", ");
        }
        getBuf().append(name);
        getBuf().append(" extends ");
    }

    @Override
    public SignatureVisitor visitClassBound() {
        // we don't differentiate between visiting a sub class or interface type
        return this;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        // we don't differentiate between visiting a sub class or interface type
        return this;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        endFormals();
        SignatureSourcer sourcer = new SignatureSourcer();
        assert mSuperClass == null;
        mSuperClass = sourcer;
        return sourcer;
    }

    @Override
    public SignatureVisitor visitInterface() {
        return this;
    }

    @Override
    public SignatureVisitor visitParameterType() {
        endFormals();
        SignatureSourcer sourcer = new SignatureSourcer();
        mParameters.add(sourcer);
        return sourcer;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        endFormals();
        SignatureSourcer sourcer = new SignatureSourcer();
        assert mReturnType == null;
        mReturnType = sourcer;
        return sourcer;
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        getBuf().append('^');
        return this;
    }

    @Override
    public void visitBaseType(final char descriptor) {
        getBuf().append(Type.getType(Character.toString(descriptor)).getClassName());
    }

    @Override
    public void visitTypeVariable(final String name) {
        getBuf().append(name.replace('/', '.'));
    }

    @Override
    public SignatureVisitor visitArrayType() {
        getBuf().append('[');
        return this;
    }

    @Override
    public void visitClassType(final String name) {
        getBuf().append(name.replace('/', '.'));
        mArgumentStack *= 2;
    }

    @Override
    public void visitInnerClassType(final String name) {
        endArguments();
        getBuf().append('.');
        getBuf().append(name.replace('/', '.'));
        mArgumentStack *= 2;
    }

    @Override
    public void visitTypeArgument() {
        if (mArgumentStack % 2 == 0) {
            ++mArgumentStack;
            getBuf().append('<');
        } else {
            getBuf().append(", ");
        }
        getBuf().append('*');
    }

    @Override
    public SignatureVisitor visitTypeArgument(final char wildcard) {
        if (mArgumentStack % 2 == 0) {
            ++mArgumentStack;
            getBuf().append('<');
        } else {
            getBuf().append(", ");
        }
        if (wildcard != '=') {
            if (wildcard == '+') {
                getBuf().append("? extends ");
            } else if (wildcard == '-') {
                getBuf().append("? super ");
            } else {
                // can this happen?
                getBuf().append(wildcard);
            }
        }
        return this;
    }

    @Override
    public void visitEnd() {
        endArguments();
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    /**
     * Ends the formal type parameters section of the signature.
     */
    private void endFormals() {
        if (mWritingFormals) {
            getBuf().append('>');
            mWritingFormals = false;
        }
    }

    /**
     * Ends the type arguments of a class or inner class type.
     */
    private void endArguments() {
        if (mArgumentStack % 2 != 0) {
            getBuf().append('>');
        }
        mArgumentStack /= 2;
    }
}
