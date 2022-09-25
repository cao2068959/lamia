package com.chy.lamia.convert.builder;


import com.chy.lamia.element.boxing.ExpressionFunction;
import com.chy.lamia.element.boxing.processor.TypeBoxingDefinition;
import com.chy.lamia.entity.ExpressionWrapper;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
import com.chy.lamia.utils.CommonUtils;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Function;

/**
 * 如果放入的是  TypeBoxingDefinition 类型的变量, 那么会根据参数自动 解包/装包 来生成变量
 *
 * @author bignosecat
 */
@Getter
public class BoxingExpressionBuilder {

    private final boolean isBoxing;
    private TypeDefinition targetType;
    String varName;
    TypeDefinition currentType;

    ExpressionWrapper resultCache;

    /**
     * 如果 isBoxing 为 true 则是 自动装包生成变量,  为 false 则是以解包的方式生成变量
     *
     * @param currentType 当前要操作的类型
     * @param varName     当前最初始的变量名
     * @param targetType  要转换的目标类型
     */
    public BoxingExpressionBuilder(TypeDefinition currentType, String varName, TypeDefinition targetType) {
        this.varName = varName;
        this.isBoxing = decideBoxing(currentType, targetType);

    }

    /**
     * 判断 装包还是解包操作
     */
    private boolean decideBoxing(TypeDefinition currentType, TypeDefinition targetType) {
        // 解包
        if (currentType.isChildrenGeneric(targetType)) {
            TypeBoxingDefinition typeBoxingDefinition = TypeDefinitionFactory.unPackage(currentType, targetType);
            if (typeBoxingDefinition == null) {
                throw new RuntimeException("类型 [" + currentType + "] 和 类型 [" + targetType + "] 转换失败");
            }
            this.targetType = typeBoxingDefinition;
            this.currentType = typeBoxingDefinition.top();
            return false;
        }


        // 装包
        if (targetType.isChildrenGeneric(currentType)) {
            TypeBoxingDefinition typeBoxingDefinition = TypeDefinitionFactory.unPackage(targetType, currentType);
            if (typeBoxingDefinition == null) {
                throw new RuntimeException("类型 [" + currentType + "] 和 类型 [" + targetType + "] 转换失败");
            }
            this.targetType = typeBoxingDefinition.top();
            this.currentType = typeBoxingDefinition;
            return false;
        }
        throw new RuntimeException("类型 [" + currentType + "] 和 类型 [" + targetType + "] 无法相互转换");
    }


    /**
     * 获取生成的变量的表达式
     * 如果是第一次获取,还将获取到, 在生成这个变量之前还需要的表达式
     */
    public ExpressionWrapper getVarExpression() {
        // 不是第一次生成了, 直接返回结果
        if (resultCache != null) {
            return new ExpressionWrapper(resultCache.getExpression(), resultCache.getReturnType());
        }
        // 生成这个变量
        ExpressionWrapper builderResult = createdVar();
        resultCache = builderResult;
        return builderResult;
    }

    private ExpressionWrapper createdVar() {
        // 不是 TypeBoxingDefinition 没有解包/装包 操作直接返回了
        if (!(currentType instanceof TypeBoxingDefinition)) {
            return new ExpressionWrapper(JCUtils.instance.memberAccess(varName), currentType);
        }


        TypeBoxingDefinition typeBoxingDefinition = (TypeBoxingDefinition) currentType;
        if (isBoxing) {
            return createdBoxingExpression(typeBoxingDefinition);
        } else {
            return createdUnboxingExpression(typeBoxingDefinition);
        }

    }

    /**
     * 变量装包,并生成对应的装包语句  以及最终这个变量的表达式
     */
    private ExpressionWrapper createdBoxingExpression(TypeBoxingDefinition typeBoxingDefinition) {
        return doCreatedVarExpression(typeBoxingDefinition, TypeBoxingDefinition::getBoxingExpression, type -> {
            if (type == targetType) {
                return null;
            }
            return type.last();
        });
    }

    /**
     * 变量解包,并生成对应的解包语句, 以及最终这个变量的表达式
     */
    private ExpressionWrapper createdUnboxingExpression(TypeBoxingDefinition typeBoxingDefinition) {
        return doCreatedVarExpression(typeBoxingDefinition, TypeBoxingDefinition::getUnboxingExpression, type -> {
            if (type == targetType) {
                return null;
            }
            return type.next();
        });
    }

    private ExpressionWrapper doCreatedVarExpression(TypeBoxingDefinition typeBoxingDefinition,
                                                     Function<TypeBoxingDefinition, ExpressionFunction> fetchExpressionFunction,
                                                     Function<TypeBoxingDefinition, TypeBoxingDefinition> fetchNext) {
        // 参与装换的 表达式,最开始应该是 参数的变量名
        JCTree.JCExpression paramExpression = JCUtils.instance.memberAccess(varName);
        ExpressionWrapper result = new ExpressionWrapper();
        TypeBoxingDefinition current = typeBoxingDefinition;

        while (true) {
            ExpressionFunction function = fetchExpressionFunction.apply(current);
            ExpressionWrapper wrapper = function.getExpression(paramExpression);
            // 当前计算出来的表达式,应该是下一个函数的入参
            paramExpression = wrapper.getExpression();
            result.addBeforeStatement(wrapper.getBefore());
            // 获取下一个
            current = fetchNext.apply(current);

            // 已经是最后一个 直接返回了
            if (current == null) {
                result.setExpression(paramExpression);
                result.setReturnType(wrapper.getReturnType());
                return result;
            }

            Optional<TypeDefinition> returnType = wrapper.getReturnType();
            if (!returnType.isPresent()) {
                continue;
            }

            // 把这一行表达式添加上变量名, 让他变成一行完整的语句
            String newName = CommonUtils.generateVarName(returnType.get().simpleClassName());
            JCTree.JCVariableDecl variableDecl = JCUtils.instance.createVar(newName, returnType.get().toString(), paramExpression);
            result.addBeforeStatement(variableDecl);
            paramExpression = JCUtils.instance.memberAccess(newName);
        }
    }


}
