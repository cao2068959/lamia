package com.chy.lamia.element;


import com.chy.lamia.entity.TypeDefinition;
import com.chy.lamia.entity.VarDefinition;
import com.chy.lamia.utils.CommonUtils;
import com.chy.lamia.utils.Lists;
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
     * 参与转换的参数
     * key: 参与转换变量的名称, 如果使用 @MapMember 修改过名称,那这里是修改后的名称
     * value: 对应的变量
     */
    @Getter
    Map<String, VarDefinition> args = new HashMap<>();

    @Getter
    @Setter
    List<String> allArgsNames;

    public void addVarArgs(VarDefinition varDefinition) {
        String varName = varDefinition.getVarName();
        VarDefinition existVd = args.get(varName);
        if (existVd == null) {
            args.put(varName, varDefinition);
            return;
        }
        // 如果有重复的, 那么使用优先级判断
        if (varDefinition.getPriority() > existVd.getPriority()) {
            args.put(varName, varDefinition);
        }
    }

    public void checkArgs(){
        if (allArgsNames.size() != args.size()){
            throw new RuntimeException("[LamiaConvertInfo] 中参数缺失, 需要参数" + Lists.toString(allArgsNames));
        }

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
