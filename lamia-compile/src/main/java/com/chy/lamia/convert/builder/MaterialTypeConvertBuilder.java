package com.chy.lamia.convert.builder;


import com.chy.lamia.convert.assemble.Material;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 类型转换生成器, 把 material 对象转成适合的类型(如果需要) 并生成对应的转换语句,以及返回转换后的 对象名
 * 如: material--> A a , target---> B, 会生成表达式 B b --> a.getB()
 *
 * @author bignosecat
 */
@Data
public class MaterialTypeConvertBuilder {

    Material material;
    TypeDefinition targetType;

    public MaterialTypeConvertBuilder(Material material, TypeDefinition targetType) {
        this.material = material;
        this.targetType = targetType;
    }

    /**
     * 将这个类型 转换成 指定的类型
     */
    public ConvertResult convert() {
        ConvertResult result = new ConvertResult();

        VarDefinition materialVarDefinition = material.getVarDefinition();
        JCTree.JCExpression materialExpression = JCUtils.instance.memberAccess(materialVarDefinition.getVarRealName());
        // 把 material 转成真正执行表达式, 也就是拿到 material.getA() 这样的表达式
        JCTree.JCExpression expression = material.getVarExpressionFunction().run(materialExpression);

        result.setVarExpression(expression);
        return result;
    }




    @Data
    public static class ConvertResult {
        List<JCTree.JCStatement> convertStatement = new ArrayList<>();
        JCTree.JCExpression varExpression;

        public void addConvertStatement(List<JCTree.JCStatement> statements) {
            convertStatement.addAll(statements);
        }
    }


}
