package com.chy.lamia.element;

import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 持有了 所有 lamia.convert 语句 的代码块, 这些代码块也就是后续需要去修改内容的
 *
 * @author bignosecat
 */
@Getter
public class LamiaConvertHolderBlock {

    /**
     * 在代码中的所有 lamia.convert 语句, 以及对应这个语句能够访问到所有的变量
     * <p>
     * key : lamia.convert 语句 在代码块中的id, 用于标识这个转换语句在代码块中的位置
     * value: 对应的 LamiaConvertScope 对象
     */
    private Map<String, LamiaConvertInfo> lamiaConvertScopes;

    private List<JCTree.JCStatement> contents;

    public LamiaConvertHolderBlock(List<JCTree.JCStatement> contents) {
        this.contents = contents;
    }

    public void replaceStatement(LamiaConvertInfo lamiaConvertInfo) {
        lamiaConvertScopes.put(lamiaConvertInfo.getId(), lamiaConvertInfo);
        contents.add(lamiaConvertInfo.getStatement());
    }


    public LamiaConvertInfo getLamiaConvertInfo(LamiaConvertInfo.Statement statement) {
        String id = statement.getId();
        LamiaConvertInfo lamiaConvertInfo = lamiaConvertScopes.get(id);
        if (lamiaConvertInfo == null) {
            throw new RuntimeException("id [" + id + "] 无法找到对应的 LamiaConvertInfo");
        }
        return lamiaConvertInfo;
    }
}
