package com.chy.lamia.convert.builder;

import com.sun.tools.javac.tree.JCTree;
import lombok.Data;

import java.util.List;

@Data
public class ConvertResult {

    /**
     * 转换的时候，中间可能有一些过程，这些过程存这里面
     */
    List<JCTree.JCStatement> convertStatement;


    public ConvertResult(List<JCTree.JCStatement> convertStatement) {
        this.convertStatement = convertStatement;
    }

    public ConvertResult() {
    }

}