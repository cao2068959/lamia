package com.chy.lamia.element.asm;


import com.chy.lamia.entity.TypeDefinition;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;

class ParameterTypeSignatureHandle extends SignatureVisitor {

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
     * {@link ParameterTypeSignatureHandle} generated when parsing the return type of <em>this</em>
     * signature. Initially null.
     */
    private ParameterTypeSignatureHandle mReturnType;

    /**
     * {@link ParameterTypeSignatureHandle} generated when parsing the super class of <em>this</em>
     * signature. Initially null.
     */
    private ParameterTypeSignatureHandle mSuperClass;

    /**
     * {@link ParameterTypeSignatureHandle}s for each parameters generated when parsing the method parameters
     * of <em>this</em> signature. Initially empty but not null.
     */
    private ArrayList<ParameterTypeSignatureHandle> mParameters = new ArrayList<ParameterTypeSignatureHandle>();

    /**
     * 把整个泛型以 typeDefinition 类型的形式去存储
     */
    TypeDefinition typeDefinition;
    /**
     * 栈顶元素就是当前正常处理的 泛型对象
     */
    Stack<TypeDefinition> currentOperateGeneric = new Stack<>();

    public ParameterTypeSignatureHandle() {
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


    public ParameterTypeSignatureHandle getReturnType() {
        return mReturnType;
    }


    public ArrayList<ParameterTypeSignatureHandle> getParameters() {
        return mParameters;
    }


    public boolean hasFormalsContent() {
        return mFormalsBuf.length() > 0;
    }

    public String formalsToString() {
        return mFormalsBuf.toString();
    }


    public ParameterTypeSignatureHandle getSuperClass() {
        return mSuperClass;
    }

    public TypeDefinition getParameterType() {
        return typeDefinition;
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
        ParameterTypeSignatureHandle sourcer = new ParameterTypeSignatureHandle();
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
        ParameterTypeSignatureHandle sourcer = new ParameterTypeSignatureHandle();
        mParameters.add(sourcer);
        return sourcer;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        endFormals();
        ParameterTypeSignatureHandle sourcer = new ParameterTypeSignatureHandle();
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
        String classPath = name.replace('/', '.');
        getBuf().append(classPath);
        TypeDefinition newParameterType = new TypeDefinition(classPath);
        if (this.typeDefinition == null) {
            this.typeDefinition = newParameterType;
        }
        getTopParameterType(false).ifPresent(vt -> vt.addGeneric(newParameterType));

        currentOperateGeneric.push(newParameterType);
        mArgumentStack *= 2;
    }

    private Optional<TypeDefinition> getTopParameterType(boolean isRemove) {
        if (currentOperateGeneric.empty()) {
            return Optional.empty();
        }
        if (isRemove) {
            return Optional.of(currentOperateGeneric.pop());
        } else {
            return Optional.of(currentOperateGeneric.peek());
        }
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
            getTopParameterType(true);
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
            getTopParameterType(true);
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
            getTopParameterType(true);
            getBuf().append('>');
        }
        mArgumentStack /= 2;
    }
}
