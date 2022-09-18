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

import java.util.*;

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

    /**
     * 根据优先级 来获取 对应的 参数
     * 优先级 数字越大 则越优先
     * 低优先级的放队头，高优先级放队尾
     * <p>
     * <p>
     * 1.根据 varDefinition.getPriority() 来获取优先级，不设置 则是 -1
     * 2.如果获取的优先级是一样的，那么 根据所在 allArgsNames 中的位置判定
     *
     * @return 对应的 var列表
     */
    public List<VarDefinition> getArgsByPriority() {
        Collections.unmodifiableSortedMap()


    }


    public void checkArgs() {
        if (allArgsNames.size() != args.size()) {
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
