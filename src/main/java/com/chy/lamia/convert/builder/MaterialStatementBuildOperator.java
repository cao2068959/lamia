package com.chy.lamia.convert.builder;

import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;


/**
 * MaterialStatementBuilder 执行的中间操作者, 用于 BuilderFunction 和 MaterialStatementBuilder 的中间人交换数据和适配
 *
 * @author bignosecat
 */
public class MaterialStatementBuildOperator {

    MaterialStatementBuilder materialStatementBuilder;

    /**
     * 额外的 表达式, 如 要使用 Optional<A> a 需要先执行 A a1 = a.get(), 需要预先执行的这行语句就存在 exStatement 中
     */
    @Getter
    List<JCTree.JCStatement> exStatement;

    public MaterialStatementBuildOperator(MaterialStatementBuilder materialStatementBuilder) {
        this.materialStatementBuilder = materialStatementBuilder;
    }

    /**
     * 一个 Material 可能经历过多次解包之后,他表达式的变量发生了变化, 这里其实就是获取这个 material的最终形态
     * 如: Optional<A> var1 ---> A a1 = var1.get(); String name = a1.getName;
     * 这里最终获取到的就是 name 这个表达式,最终也是使用这个name去操作如 : target.setName(name)
     * <p>
     * 同时在解包的过程中可能会产生一些表达式,这些表达式都将放入 exStatement 中
     *
     * @param id
     * @return
     */
    public JCTree.JCExpression getExpression(String id) {

        MaterialTypeConvertBuilder convertBuilder = materialStatementBuilder.getMaterialTypeConvertBuilder(id);
        if (convertBuilder == null) {
            throw new RuntimeException("无效的 MaterialTypeConvertBuilder id : " + id);
        }
        // 转换成外接可以接受的类型, 返回这个变量的表达式 ,可能会带有这个表达式的转换语句
        MaterialTypeConvertBuilder.ConvertResult convertResult = convertBuilder.convert();
        exStatement.addAll(convertResult.getConvertStatement());
        return convertResult.getVarExpression();
    }

    public List<JCTree.JCExpression> getExpression(List<String> ids) {
        return ids.stream().map(this::getExpression).collect(Collectors.toList());
    }
}