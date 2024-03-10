package com.chy.lamia.element;

import com.chy.lamia.convert.core.entity.LamiaConvertInfo;
import com.chy.lamia.entity.StatementWrapper;
import com.chy.lamia.utils.Lists;
import com.sun.source.tree.BlockTree;
import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 持有了 所有 lamia.convert 语句 的代码块, 这些代码块也就是后续需要去修改内容的
 *
 * @author bignosecat
 */
@Getter
public class LamiaConvertHolderBlock {

    private final BlockTree parent;
    /**
     * 在代码中的所有 lamia.convert 语句, 以及对应这个语句能够访问到所有的变量
     * <p>
     * key : lamia.convert 语句 在代码块中的id, 用于标识这个转换语句在代码块中的位置
     * value: 对应的 LamiaConvertScope 对象
     */
    private Map<String, LamiaConvertInfo> lamiaConvertScopes = new HashMap<>();

    private List<JCTree.JCStatement> contents;

    public LamiaConvertHolderBlock(List<JCTree.JCStatement> contents, BlockTree block) {
        this.contents = contents;
        this.parent = block;
    }

    public void replaceStatement(LamiaConvertInfo lamiaConvertInfo) {
        lamiaConvertScopes.put(lamiaConvertInfo.getId(), lamiaConvertInfo);
        String id = lamiaConvertInfo.getId();
        contents.add(new StatementWrapper(id));
    }


    public LamiaConvertInfo getLamiaConvertInfo(StatementWrapper statement) {
        String id = statement.getId();
        LamiaConvertInfo lamiaConvertInfo = lamiaConvertScopes.get(id);
        if (lamiaConvertInfo == null) {
            throw new RuntimeException("id [" + id + "] 无法找到对应的 LamiaConvertInfo");
        }
        return lamiaConvertInfo;
    }

    public void modifyMethodBody(List<JCTree.JCStatement> newStatement) {
        if (parent == null) {
            return;
        }

        if (parent instanceof JCTree.JCBlock) {
            ((JCTree.JCBlock) parent).stats = Lists.toSunList(newStatement);
        }

    }
}
