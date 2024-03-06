package com.chy.lamia.element.class_define;

import com.chy.lamia.convert.core.entity.Constructor;
import com.chy.lamia.convert.core.entity.Getter;
import com.chy.lamia.convert.core.entity.Setter;
import com.chy.lamia.entity.SimpleMethod;
import com.chy.lamia.entity.Var;
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

    private void getterHandle(String name, Symbol.MethodSymbol methodSymbol) {
        String type = getType(methodSymbol.getReturnType());
        // 返回值不对
        if ("void".equals(type)) {
            return;
        }

        Getter getter = new Getter();
        getters.put(name, getter);
    }


    private void handlerConstructor(Symbol.MethodSymbol methodSymbol) {
        Constructor constructor = new Constructor();
        constructors.add(constructor);
    }

    private void handlerVar(Symbol.VarSymbol varSymbol) {

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

    @Override
    public List<SimpleMethod> getAllMethod() {
        return allMethod;
    }
}
