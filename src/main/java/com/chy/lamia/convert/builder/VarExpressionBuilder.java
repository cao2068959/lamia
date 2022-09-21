package com.chy.lamia.convert.builder;


import com.chy.lamia.element.boxing.ExpressionFunction;
import com.chy.lamia.element.boxing.processor.TypeBoxingDefinition;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 变量生成器, 可以用 TypeDefinition 来生成一个变量 , 能生成对应表达式
 * <p>
 * 如果放入的是  TypeBoxingDefinition 类型的变量, 那么会根据参数自动 解包/装包 来生成变量
 *
 * @author bignosecat
 */
@Getter
public class VarExpressionBuilder {

    private final boolean isBoxing;
    String varName;
    TypeDefinition typeDefinition;

    BuilderResult resultCache;

    /**
     * 如果 isBoxing 为 true 则是 自动装包生成变量,  为 false 则是以解包的方式生成变量
     *
     * @param typeDefinition isBoxing:true的时候是 当前解包所在的类型 , isBoxing:false的时候是 解包的目标类型
     * @param varName        isBoxing:true的时候是 当前解包后的变量, isBoxing:false的时候是 父类型的变量名称
     * @param isBoxing
     */
    public VarExpressionBuilder(TypeDefinition typeDefinition, String varName, boolean isBoxing) {
        this.typeDefinition = typeDefinition;
        this.varName = varName;
        this.isBoxing = isBoxing;
    }

    /**
     * 获取生成的变量的表达式
     * 如果是第一次获取,还将获取到, 在生成这个变量之前还需要的表达式
     */
    public BuilderResult getVarExpression() {
        // 不是第一次生成了, 直接返回结果
        if (resultCache != null) {
            return new BuilderResult(resultCache.expression);
        }
        // 生成这个变量
        BuilderResult builderResult = createdVar();
        resultCache = builderResult;
        return builderResult;
    }

    private BuilderResult createdVar() {
        // 不是 TypeBoxingDefinition 没有解包/装包 操作直接返回了
        if (!(typeDefinition instanceof TypeBoxingDefinition)) {
            return new BuilderResult(JCUtils.instance.memberAccess(varName));
        }
        TypeBoxingDefinition typeBoxingDefinition = (TypeBoxingDefinition) typeDefinition;
        if (isBoxing) {
            return createdBoxingExpression(typeBoxingDefinition);
        } else {
            return createdUnboxingExpression(typeBoxingDefinition);
        }

    }

    /**
     * 变量装包,并生成对应的装包语句  以及最终这个变量的表达式
     */
    private BuilderResult createdBoxingExpression(TypeBoxingDefinition typeBoxingDefinition) {
        ExpressionFunction boxingExpression = typeBoxingDefinition.getBoxingExpression();



        return null;
    }

    /**
     * 变量解包,并生成对应的解包语句, 以及最终这个变量的表达式
     */
    private BuilderResult createdUnboxingExpression(TypeBoxingDefinition typeBoxingDefinition) {
        return null;
    }

    private String doCreatedVarExpression(ExpressionFunction function, ){


    }


    @Data
    public static class BuilderResult {
        /**
         * 前置表达式
         */
        List<JCTree.JCStatement> before = new ArrayList<>();

        /**
         * 生成的变量表达式
         */
        JCTree.JCExpression expression;

        public BuilderResult(JCTree.JCExpression expression) {
            this.expression = expression;
        }
    }

}
