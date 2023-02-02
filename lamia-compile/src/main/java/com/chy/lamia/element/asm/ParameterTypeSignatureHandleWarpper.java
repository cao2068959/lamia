package com.chy.lamia.element.asm;

import com.chy.lamia.entity.TypeDefinition;
import org.objectweb.asm.signature.SignatureReader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<TypeDefinition> getParameter(int index) {
        ParameterTypeSignatureHandle handle = getHandle();
        if (handle == null) {
            return Optional.empty();
        }

        ArrayList<ParameterTypeSignatureHandle> parameters = handle.getParameters();
        if (parameters.size() < index + 1) {
            return Optional.empty();
        }
        ParameterTypeSignatureHandle parameterTypeSignatureHandle = parameters.get(index);
        return Optional.ofNullable(parameterTypeSignatureHandle.getParameterType());
    }


    public List<TypeDefinition> getParameters() {
        ParameterTypeSignatureHandle handle = getHandle();
        if (handle == null) {
            return new LinkedList<>();
        }
        return handle.getParameters().stream().map(ParameterTypeSignatureHandle::getParameterType).
                collect(Collectors.toList());
    }


    public Optional<TypeDefinition> getSuperClass() {
        ParameterTypeSignatureHandle handle = getHandle();
        if (handle == null) {
            return Optional.empty();
        }

        ParameterTypeSignatureHandle superClass = handle.getSuperClass();
        if (superClass == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(superClass.getParameterType());
    }

    public Optional<TypeDefinition> getReturnType() {
        ParameterTypeSignatureHandle handle = getHandle();
        if (handle == null) {
            return Optional.empty();
        }

        ParameterTypeSignatureHandle returnType = handle.getReturnType();
        if (returnType == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(returnType.getParameterType());
    }


}
