package com.chy.lamia.element.resolver.expression;


import com.chy.lamia.components.entity.JcExpression;
import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.element.resolver.expression.builder.BuilderContext;
import com.chy.lamia.element.resolver.expression.builder.BuilderHandler;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;

/**
 * lamia 的表达式解析器, 通过这个解析器来解析用户输入的表达式
 *
 * @author bignosecat
 */
public class LamiaExpressionResolver {


    public LamiaExpression resolving(JCTree contextTree, JCTree.JCExpression jcExpression) {

        JCTree.JCMethodInvocation methodInvocation;
        JCTree.JCTypeCast typeCast = null;
        if (jcExpression instanceof JCTree.JCTypeCast) {
            typeCast = (JCTree.JCTypeCast) jcExpression;
            JCTree.JCExpression expr = typeCast.expr;
            // 强转的应该是一个方法
            if (!(expr instanceof JCTree.JCMethodInvocation)) {
                return null;
            }
            methodInvocation = (JCTree.JCMethodInvocation) expr;
        } else if (jcExpression instanceof JCTree.JCMethodInvocation) {
            methodInvocation = (JCTree.JCMethodInvocation) jcExpression;
        } else {
            return null;
        }


        // 整个表达式string
        String expressionStr = methodInvocation.toString();

        // 如果不是以 Lamia. 开头的那么 肯定不是对应的表达式
        if (!expressionStr.startsWith("Lamia.")) {
            return null;
        }

        LamiaExpression result = parseMethod(methodInvocation);
        // 表达式解析不出来说明有问题
        if (result == null) {
            return null;
        }
        if (typeCast != null) {
            result.setTargetType(JCUtils.instance.toTypeDefinition(contextTree, typeCast.clazz));
        }
        return result;
    }

    private void parseBuildConfig(LamiaExpression result, JCTree.JCMethodInvocation methodInvocation) {
        List<MethodWrapper> methodWrappers = disassembleMethod(methodInvocation);
        // 第一个是 endMethod,已经解析过了, 所以把他移除
        MethodWrapper buildMethod = methodWrappers.remove(0);
        JCTree.JCExpression target = buildMethod.getOnlyArgs();
        if (target != null){
            result.setTarget(new JcExpression(target));
        }
        // 没有任何的配置项, 直接返回了
        if (methodWrappers.isEmpty()) {
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
            BuilderHandler handler = BuilderContext.getHandler(key);
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


    private LamiaExpression parseMethod(JCTree.JCMethodInvocation data) {
        JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) data.meth;
        String name = fieldAccess.name.toString();

        // 结束方法一共有 3个  convert / setArgs / build
        if ("mapping".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            List<String> argsNames = fetchArgsName(data);
            result.addSpreadArgs(argsNames, null);
            return result;
        }
        if ("setField".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            List<String> argsNames = fetchArgsName(data);
            result.addArgs(argsNames);
            return result;
        }

        if ("build".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            parseBuildConfig(result, data);
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
