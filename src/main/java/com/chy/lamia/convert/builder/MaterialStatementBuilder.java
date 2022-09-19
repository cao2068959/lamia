package com.chy.lamia.convert.builder;

import com.chy.lamia.utils.CommonUtils;
import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * 所有参与表达式构建的 Material
     */
    Map<String, MaterialTypeConvertBuilder> materialMap = new HashMap<>();

    public List<String> addMaterial(List<MaterialTypeConvertBuilder> constructorParam) {
        return constructorParam.stream().map(this::addMaterial).collect(Collectors.toList());
    }

    public String addMaterial(MaterialTypeConvertBuilder material) {
        String id = CommonUtils.getRandomString(12);
        materialMap.put(id, material);
        return id;
    }

    public MaterialTypeConvertBuilder getMaterialTypeConvertBuilder(String id) {
        return materialMap.get(id);
    }


    /**
     * 生成对应的代码
     *
     * @return
     */
    public List<JCTree.JCStatement> build() {
        MaterialStatementBuildOperator operator = new MaterialStatementBuildOperator(this);
        List<JCTree.JCStatement> statements = function.builder(operator);
        // 在生成的转换语句之前 可能存在一些前置转换语句, 获取前置转换语句
        List<JCTree.JCStatement> result = operator.getExStatement();
        // 把生成的代码放前置转换语句之后
        result.addAll(statements);
        return result;
    }


    public interface BuilderFunction {

        /**
         * 构建函数, 用于实现真正的表达式
         *
         * @param builder
         * @return
         */
        List<JCTree.JCStatement> builder(MaterialStatementBuildOperator builder);

    }


}
