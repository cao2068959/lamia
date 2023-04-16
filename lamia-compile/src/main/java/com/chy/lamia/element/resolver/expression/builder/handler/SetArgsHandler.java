package com.chy.lamia.element.resolver.expression.builder.handler;

import com.chy.lamia.element.resolver.expression.ConfigParseContext;
import com.chy.lamia.element.resolver.expression.LamiaExpression;
import com.chy.lamia.element.resolver.expression.MethodWrapper;
import com.chy.lamia.element.resolver.expression.builder.BuilderHandler;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;

/**
 * 扩散参数自定义的配置处理器
 *
 * @author bignosecat
 */
public class SetArgsHandler implements BuilderHandler {
    @Override
    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context) {
        List<String> argsName = fetchArgsName(methodWrapper.getArgs());
        lamiaExpression.addArgs(argsName);
    }

    private List<String> fetchArgsName(List<JCTree.JCExpression> args) {
        List<String> result = new ArrayList<>();
        for (JCTree.JCExpression arg : args) {
            result.add(arg.toString());
        }
        return result;
    }
}
