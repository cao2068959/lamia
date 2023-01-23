package com.chy.lamia.convert.builder;

import com.sun.tools.javac.tree.JCTree;
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
    public List<JCTree.JCStatement> build() {
        return function.builder();
    }


    public interface BuilderFunction {

        /**
         * 构建函数, 用于实现真正的表达式
         *
         * @return
         */
        List<JCTree.JCStatement> builder();

    }


}
