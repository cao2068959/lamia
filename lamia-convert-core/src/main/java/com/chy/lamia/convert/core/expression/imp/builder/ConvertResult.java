package com.chy.lamia.convert.core.expression.imp.builder;

import com.chy.lamia.convert.core.components.entity.Statement;
import lombok.Data;

import java.util.List;

@Data
public class ConvertResult {

    /**
     * 转换的时候，中间可能有一些过程，这些过程存这里面
     */
    List<Statement> convertStatement;


    public ConvertResult(List<Statement> convertStatement) {
        this.convertStatement = convertStatement;
    }

    public ConvertResult() {
    }

}