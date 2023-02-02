package com.chy.lamia.element.resolver.expression;


import com.chy.lamia.element.resolver.expression.configuration.ConfigurationContext;
import com.chy.lamia.element.resolver.expression.configuration.ConfigurationHandler;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;

/**
 * lamia 的表达式解析器, 通过这个解析器来解析用户输入的表达式
 *
 * @author bignosecat
 */
public class LamiaExpressionResolver {


    public LamiaExpression resolving(JCTree.JCExpression jcExpression) {
        // 首先这个表达式应该是一个 强转类型, 如果不是强转类型就直接 不处理了
        if (!(jcExpression instanceof JCTree.JCTypeCast)) {
            return null;
        }

        JCTree.JCTypeCast typeCast = (JCTree.JCTypeCast) jcExpression;
        JCTree.JCExpression expr = typeCast.expr;


        // 强转的应该是一个方法
        if (!(expr instanceof JCTree.JCMethodInvocation)) {
            return null;
        }

        JCTree.JCMethodInvocation methodInvocation = (JCTree.JCMethodInvocation) expr;
        // 整个表达式string
        String expressionStr = methodInvocation.toString();

        // 如果不是以 Lamia. 开头的那么 肯定不是对应的表达式
        if (!expressionStr.startsWith("Lamia.")) {
            return null;
        }

        LamiaExpression result = parseEndMethod(methodInvocation);
        // 整个表达式的结束方法不存在,那么整个表达式应该是无效的
        if (result == null) {
            return null;
        }

        result.setTypeCast(typeCast);
        // 前置可能会有一些配置表达式, 去解析前置的配置表达式
        parseConfig(result, methodInvocation);

        return result;
    }

    private void parseConfig(LamiaExpression result, JCTree.JCMethodInvocation methodInvocation) {
        List<MethodWrapper> methodWrappers = disassembleMethod(methodInvocation);
        // 第一个是 endMethod,已经解析过了, 所以把他移除
        methodWrappers.remove(0);

        // 没有任何的配置项, 直接返回了
        if (methodWrappers.size() == 0) {
            return;
        }
        ConfigParseContext context = new ConfigParseContext();

        int iSize = methodWrappers.size() - 1;
        // 倒序遍历
        for (int i = 0; i < methodWrappers.size(); i++) {
            int index = iSize - i;
            MethodWrapper methodWrapper = methodWrappers.get(index);
            String name = methodWrapper.getName();

            String key = context.getScope(name);
            ConfigurationHandler handler = ConfigurationContext.getHandler(key);
            if (handler == null) {
                throw new RuntimeException("[LamiaExpressionResolver] 无法找到 配置处理器 key: [" + key + "]");
            }

            handler.config(result, methodWrapper, context);
        }


    }

    private List<MethodWrapper> disassembleMethod(JCTree.JCMethodInvocation methodInvocation) {

        List<MethodWrapper> result = new ArrayList<>();

        JCTree.JCExpression next = methodInvocation;
        while (true) {
            if (!(next instanceof JCTree.JCMethodInvocation)) {
                return result;
            }
            JCTree.JCMethodInvocation nextMethod = (JCTree.JCMethodInvocation) next;
            JCTree.JCExpression expression = nextMethod.meth;

            if (!(expression instanceof JCTree.JCFieldAccess)) {
                return result;
            }

            JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) expression;
            MethodWrapper methodWrapper = new MethodWrapper(fieldAccess.name.toString());
            methodWrapper.setArgs(nextMethod.args);
            result.add(methodWrapper);
            next = fieldAccess.selected;
        }
    }


    private LamiaExpression parseEndMethod(JCTree.JCMethodInvocation data) {
        JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) data.meth;
        String name = fieldAccess.name.toString();

        // 结束方法一共有 2个  convert / mapping
        if ("convert".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            List<String> argsNames = fetchArgsName(data);
            result.addArgs(argsNames);
            return result;
        }
        if ("mapping".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            List<String> argsNames = fetchArgsName(data);
            result.addArgs(argsNames);
            result.setDefaultSpread(true);
            return result;
        }
        return null;
    }


    private List<String> fetchArgsName(JCTree.JCMethodInvocation data) {
        List<String> result = new ArrayList<>();
        for (JCTree.JCExpression arg : data.args) {
            result.add(arg.toString());
        }
        return result;
    }

}
