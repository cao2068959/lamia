package com.chy.lamia.element.asm;

import com.chy.lamia.entity.ParameterType;
import jdk.internal.org.objectweb.asm.signature.SignatureReader;

import java.util.ArrayList;
import java.util.Optional;

public class ParameterTypeSignatureHandleWarpper {

    private ParameterTypeSignatureHandle handle;
    private String signature;

    public ParameterTypeSignatureHandleWarpper(String signature) {
        this.signature = signature;
    }

    private ParameterTypeSignatureHandle getHandle() {
        if (signature == null) {
            return null;
        }
        if (handle != null) {
            return handle;
        }
        ParameterTypeSignatureHandle signatureVisitorImp = new ParameterTypeSignatureHandle();
        SignatureReader signatureReader = new SignatureReader(signature);
        signatureReader.accept(signatureVisitorImp);
        handle = signatureVisitorImp;
        return handle;
    }

    public Optional<ParameterType> getParameter(int index) {
        ParameterTypeSignatureHandle handle = getHandle();
        ArrayList<ParameterTypeSignatureHandle> parameters = handle.getParameters();
        if (parameters.size() < index) {
            return Optional.empty();
        }
        ParameterTypeSignatureHandle parameterTypeSignatureHandle = parameters.get(index);
        return Optional.ofNullable(parameterTypeSignatureHandle.getParameterType());
    }

}
