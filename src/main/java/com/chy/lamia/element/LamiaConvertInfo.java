package com.chy.lamia.element;


import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.utils.CommonUtils;
import com.sun.source.tree.TreeVisitor;
import com.sun.tools.javac.tree.JCTree;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 每一个 Lamia.convert 表达式能够使用到的作用域
 *
 * @author bignosecat
 */
public class LamiaConvertInfo {

    @Getter
    String id = CommonUtils.getRandomString(16);


    /**
     * 要转换成的类型
     */
    @Getter
    @Setter
    TypeDefinition targetType;

    /**
     * 参与转换的参数, 这里仅仅只包含了方法体中变量提供的参数, 方法入参提供的参数没在里面
     * key: 参与转换变量的名称, 如果使用 @MapMember 修改过名称,那这里是修改后的名称
     * value: 对应的变量
     */
    @Getter
    Map<String, VarDefinition> varArgs = new HashMap<>();

    @Getter
    @Setter
    List<String> allArgsNames;

    public void addVarArgs(VarDefinition varDefinition) {
        String varName = varDefinition.getVarName();
        varArgs.put(varName, varDefinition);
    }

    public JCTree.JCStatement getStatement() {
        return new Statement(id);
    }

    @AllArgsConstructor
    @Getter
    public static class Statement extends JCTree.JCStatement {
        String id;

        @Override
        public Tag getTag() {
            return null;
        }

        @Override
        public void accept(Visitor visitor) {

        }

        @Override
        public Kind getKind() {
            return null;
        }

        @Override
        public <R, D> R accept(TreeVisitor<R, D> treeVisitor, D d) {
            return null;
        }
    }

}
