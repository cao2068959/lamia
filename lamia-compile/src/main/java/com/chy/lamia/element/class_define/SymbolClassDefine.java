package com.chy.lamia.element.class_define;

import com.chy.lamia.convert.core.entity.*;
import com.chy.lamia.element.resolver.type.JcTypeResolver;
import com.chy.lamia.entity.SimpleMethod;
import com.chy.lamia.entity.Var;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
import com.chy.lamia.utils.StringUtils;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;

import javax.lang.model.element.Name;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolClassDefine implements IClassDefine {

    Symbol.ClassSymbol classSymbol;

    Map<String, Var> instantVars = new HashMap<>();
    Map<String, Getter> getters = new HashMap<>();
    Map<String, Setter> setters = new HashMap<>();
    List<Constructor> constructors = new ArrayList<>();
    List<SimpleMethod> allMethod = new ArrayList<>();

    public SymbolClassDefine(Symbol.ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
        doParseClass();
    }

    private void doParseClass() {
        List<Symbol> enclosedElements = classSymbol.getEnclosedElements();
        for (Symbol enclosedElement : enclosedElements) {
            if (enclosedElement instanceof Symbol.VarSymbol) {
                handlerVar((Symbol.VarSymbol) enclosedElement);
                continue;
            }

            if (enclosedElement instanceof Symbol.MethodSymbol) {
                handlerMethod((Symbol.MethodSymbol) enclosedElement);
                continue;
            }
        }

        Type superclass = classSymbol.getSuperclass();
        if (superclass != null) {
            String superclassString = superclass.toString();
            if (invalidSuperclass(superclassString)) {
                return;
            }
            JcTypeResolver parentClassElement = JcTypeResolver.getTypeResolver(TypeDefinitionFactory.create(superclass));
            getters.putAll(parentClassElement.getInstantGetters());
            setters.putAll(parentClassElement.getInstantSetters());
        }

    }

    private boolean invalidSuperclass(String superclassString) {
        if (superclassString == null) {
            return true;
        }
        return superclassString.startsWith("java.");
    }

    private void handlerMethod(Symbol.MethodSymbol methodSymbol) {
        if (methodSymbol.isConstructor()) {
            handlerConstructor(methodSymbol);
            return;
        }
        String name = getName(methodSymbol.name);
        if (name.startsWith("get")) {
            getterHandle(name, methodSymbol);
        } else if (name.startsWith("set")) {
            setterHandle(name, methodSymbol);
        }
    }

    private void setterHandle(String name, Symbol.MethodSymbol methodSymbol) {
        com.sun.tools.javac.util.List<Symbol.VarSymbol> parameters = methodSymbol.getParameters();
        // set的参数只能有一个
        if (parameters == null || parameters.size() != 1) {
            return;
        }
        Symbol.VarSymbol varSymbol = parameters.get(0);
        String type = getType(varSymbol.type);

        Setter setter = new Setter();
        String varName = StringUtils.toCamelCase(name.substring(3));
        setter.setMethodName(name);
        setter.setType(new TypeDefinition(type));
        setter.setVarName(varName);
        setters.put(varName, setter);


    }

    private void getterHandle(String name, Symbol.MethodSymbol methodSymbol) {
        String type = getType(methodSymbol.getReturnType());
        // 返回值不对
        if ("void".equals(type)) {
            return;
        }
        com.sun.tools.javac.util.List<Symbol.VarSymbol> parameters = methodSymbol.getParameters();
        // 对于getter来说 不能存在入参
        if (parameters != null && !parameters.isEmpty()) {
            return;
        }
        Getter getter = new Getter();
        String varName = StringUtils.toCamelCase(name.substring(3));
        getter.setMethodName(name);
        getter.setType(new TypeDefinition(type));
        getter.setVarName(varName);
        getters.put(varName, getter);
    }


    private void handlerConstructor(Symbol.MethodSymbol methodSymbol) {
        Constructor constructor = new Constructor();
        methodSymbol.getParameters().forEach(varSymbol -> {
            VarDefinition varDefinition = new VarDefinition(getName(varSymbol.name), new TypeDefinition(getType(varSymbol.type)));
            constructor.add(varDefinition);
        });
        constructors.add(constructor);
    }

    private void handlerVar(Symbol.VarSymbol varSymbol) {
        String name = getName(varSymbol.name);
        String type = getType(varSymbol.type);
        Var var = new Var(name, type);
        instantVars.put(name, var);
    }

    private String getType(Type type) {
        if (type == null) {
            return "void";
        }
        if (type instanceof Type.JCVoidType) {
            return "void";
        }
        return type.toString();
    }

    private String getName(Name name) {
        if (name == null) {
            return "null";
        }
        return name.toString();
    }

    @Override
    public Map<String, Var> getInstantVars() {
        return instantVars;
    }

    @Override
    public Map<String, Getter> getInstantGetters() {
        return getters;
    }

    @Override
    public Map<String, Setter> getInstantSetters() {
        return setters;
    }

    @Override
    public List<Constructor> getConstructors() {
        return constructors;
    }

    public List<SimpleMethod> getAllMethod() {
        return allMethod;
    }
}
