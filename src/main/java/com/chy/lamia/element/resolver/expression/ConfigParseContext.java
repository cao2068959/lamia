package com.chy.lamia.element.resolver.expression;

import java.util.Stack;

public class ConfigParseContext {

    Stack<String> scope = new Stack<>();
    String scopeText = "";

    public void intoScope(String name) {
        scope.push(name);
        genScopeText();
    }


    public void outScope(String name) {
        String popName = scope.pop();
        if (!popName.equals(name)) {
            throw new RuntimeException("[ConfigParseContext] 移除了不属于当前scope的name, 期望移除[" + name + "] 当前移除的是[" + popName + "]");
        }
        genScopeText();
    }

    public String getScope() {
        return scopeText;
    }

    public String getScope(String name) {
        if ("".equals(scopeText)) {
            return name;
        }
        return scopeText + "." + name;
    }


    private void genScopeText() {
        if (scope.isEmpty()) {
            scopeText = "";
            return;
        }
        StringBuilder result = new StringBuilder();
        for (String name : scope) {
            if (result.length() != 0) {
                result.append(".");
            }
            result.append(name);
        }
        scopeText = result.toString();
    }


}
