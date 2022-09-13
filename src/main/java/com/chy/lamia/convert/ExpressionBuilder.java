package com.chy.lamia.convert;

import com.chy.lamia.convert.assemble.Material;
import com.chy.lamia.utils.CommonUtils;
import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 表达式生成器
 *
 * @author bignosecat
 */
public class ExpressionBuilder {

    /**
     * 构建函数
     */
    @Getter
    @Setter
    BuilderFunction function;

    /**
     * 所有参与表达式构建的 Material
     */
    Map<String, Material> materialMap = new HashMap<>();

    public List<String> addMaterial(List<Material> constructorParam) {
        return constructorParam.stream().map(this::addMaterial).collect(Collectors.toList());
    }

    public String addMaterial(Material material) {
        String id = CommonUtils.getRandomString(12);
        materialMap.put(id, material);
        return id;
    }


    /**
     * 一个 Material 可能经历过多次解包之后,他表达式的变量发生了变化, 这里其实就是获取这个 material的最终形态
     * 如: Optional<A> var1 ---> A a1 = var1.get(); String name = a1.getName;
     * 这里最终获取到的就是 name 这个表达式,最终也是使用这个name去操作如 : target.setName(name)
     *
     * @param id
     * @return
     */
    public JCTree.JCExpression getExpression(String id) {
        return null;
    }

    public List<JCTree.JCExpression> getExpression(List<String> ids) {
        return ids.stream().map(this::getExpression).collect(Collectors.toList());
    }


    public static interface BuilderFunction {

        /**
         * 构建函数, 用于实现真正的表达式
         *
         * @param builder
         * @return
         */
        public List<JCTree.JCStatement> builder(ExpressionBuilder builder);

    }

}
