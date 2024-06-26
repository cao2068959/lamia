package com.chy.lamia.element.resolver.expression;


import com.chy.lamia.components.entity.JcExpression;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.convert.core.entity.MethodParameterWrapper;
import com.chy.lamia.convert.core.entity.ProtoMaterialInfo;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.chy.lamia.convert.core.expression.parse.ConfigParseContext;
import com.chy.lamia.convert.core.expression.parse.builder.BuilderContext;
import com.chy.lamia.convert.core.expression.parse.builder.BuilderHandler;
import com.chy.lamia.convert.core.expression.parse.entity.ArgWrapper;
import com.chy.lamia.convert.core.expression.parse.entity.MethodWrapper;
import com.chy.lamia.convert.core.expression.parse.entity.RuleTypeArgWrapper;
import com.chy.lamia.entity.ClassTreeWrapper;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;

/**
 * lamia 的表达式解析器, 通过这个解析器来解析用户输入的表达式
 *
 * @author bignosecat
 */
public class LamiaExpressionResolver {


    public LamiaExpression resolving(ClassTreeWrapper contextTree, JCTree.JCExpression jcExpression) {

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

        LamiaExpression result = parseMethod(methodInvocation, contextTree);
        // 表达式解析不出来说明有问题
        if (result == null) {
            return null;
        }
        if (typeCast != null) {
            result.setTargetType(JCUtils.instance.toTypeDefinition(contextTree, typeCast.clazz));
        }
        return result;
    }

    private void parseBuildConfig(LamiaExpression result, JCTree.JCMethodInvocation methodInvocation,
                                  ClassTreeWrapper contextTree) {
        List<MethodWrapper> methodWrappers = disassembleMethod(methodInvocation, contextTree);
        // 第一个是 endMethod,已经解析过了, 所以把他移除
        MethodWrapper buildMethod = methodWrappers.remove(0);
        Expression target = buildMethod.useOnlyArgs();
        if (target != null) {
            result.setTarget(target);
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


    private List<MethodWrapper> disassembleMethod(JCTree.JCMethodInvocation methodInvocation, ClassTreeWrapper contextTree) {

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
            methodWrapper.setArgs(toArgWrapper(nextMethod.args, contextTree));
            result.add(methodWrapper);
            next = fieldAccess.selected;
        }
    }

    private List<ArgWrapper> toArgWrapper(List<JCTree.JCExpression> list, ClassTreeWrapper contextTree) {
        List<ArgWrapper> result = new ArrayList<>();
        for (JCTree.JCExpression data : list) {
            JcExpression jcExpression = new JcExpression(data);
            ArgWrapper argWrapper = new RuleTypeArgWrapper(jcExpression, data.toString());
            jcExpression.setContextTree(contextTree);
            argWrapper.setExpression(jcExpression);
            argWrapper.setName(data.toString());
            result.add(argWrapper);
        }
        return result;
    }


    private LamiaExpression parseMethod(JCTree.JCMethodInvocation data, ClassTreeWrapper contextTree) {
        JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess) data.meth;
        String name = fieldAccess.name.toString();

        // 结束方法一共有 3个  convert / setArgs / build
        if ("mapping".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            result.updateBuild();
            fetchArgs(data, contextTree).forEach(arg -> {
                arg.setSpread(true);
                result.addArgs(arg);
            });
            return result;
        }
        if ("setField".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            List<ProtoMaterialInfo> args = fetchArgs(data, contextTree);
            result.updateBuild();
            result.addArgs(args);
            return result;
        }

        if ("build".equals(name)) {
            LamiaExpression result = new LamiaExpression();
            parseBuildConfig(result, data, contextTree);
            return result;
        }

        return null;
    }


    private List<ProtoMaterialInfo> fetchArgs(JCTree.JCMethodInvocation data, ClassTreeWrapper contextTree) {
        List<ProtoMaterialInfo> result = new ArrayList<>();
        for (JCTree.JCExpression arg : data.args) {

            if (arg instanceof JCTree.JCMethodInvocation) {
                JCTree.JCMethodInvocation methodInvocation = (JCTree.JCMethodInvocation) arg;
                String methodInvocationText = methodInvocation.toString();
                ProtoMaterialInfo protoMaterialInfo = new ProtoMaterialInfo("methodInvocation-" + methodInvocationText);
                Type type = contextTree.getByAfterTypeInference(() -> methodInvocation.type);
                MethodParameterWrapper parameterWrapper = new MethodParameterWrapper(new TypeDefinition(type.toString()), new JcExpression(methodInvocation));
                protoMaterialInfo.setMaterial(parameterWrapper);
                parameterWrapper.setText(methodInvocationText);
                result.add(protoMaterialInfo);
                continue;
            }
            result.add(ProtoMaterialInfo.simpleMaterialInfo(arg.toString()));

        }
        return result;
    }

}
