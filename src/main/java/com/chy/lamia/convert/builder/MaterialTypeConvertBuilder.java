package com.chy.lamia.convert.builder;


import com.chy.lamia.convert.assemble.Material;
import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.entity.factory.TypeDefinitionFactory;
import com.chy.lamia.utils.struct.Pair;
import com.sun.tools.javac.tree.JCTree;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 类型转换生成器, 把 material 对象转成适合的类型(如果需要) 并生成对应的转换语句,以及返回转换后的 对象名
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
        VarDefinition varDefinition = material.getVarDefinition();

        // 变量真正提供的类型
        TypeDefinition realType = varDefinition.getType();
        // 合成材料 能够提供的类型  get方法提供的类型
        TypeDefinition execType = material.getExecType();
        // set表达式需要的类型
        TypeDefinition targetType = this.targetType;

        // 需要的类型和转换材料提供的材料是否相同, 如果不相同则需要 转相同
        if (!targetType.matchType(execType, true)) {
            // 解包匹配到合适的 类型
            Pair<TypeDefinition, TypeDefinition> unPackagePair = TypeDefinitionFactory.unPackageMatch(execType, targetType);
            if (unPackagePair == null) {
                throw new RuntimeException(" 变量[" + material.getVarDefinition() + "] 参与转换失败 类型 [" + execType + "] 无法转换成 [" + targetType + "]");
            }
            execType = unPackagePair.getLeft();
            targetType = unPackagePair.getRight();
        }




    }

    @Data
    public static class ConvertResult {
        List<JCTree.JCStatement> convertStatement = new ArrayList<>();
        JCTree.JCExpression varExpression;

        public ConvertResult(JCTree.JCExpression varExpression) {
            this.varExpression = varExpression;
        }
    }


}
