package com.chy.lamia.convert.core.expression.builder;

import com.chy.lamia.convert.core.components.entity.Statement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Material 的表达式生成器
 *
 * @author bignosecat
 */
public class MaterialStatementBuilder {

    /**
     * 构建函数
     */
    @Getter
    @Setter
    BuilderFunction function;


    /**
     * 生成对应的代码
     *
     * @return
     */
    public List<Statement> build() {
        return function.builder();
    }


    public interface BuilderFunction {

        /**
         * 构建函数, 用于实现真正的表达式
         *
         * @return
         */
        List<Statement> builder();

    }


}
