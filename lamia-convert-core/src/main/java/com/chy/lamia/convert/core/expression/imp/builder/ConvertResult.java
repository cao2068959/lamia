package com.chy.lamia.convert.core.expression.imp.builder;

import com.chy.lamia.convert.core.components.entity.NewlyStatementHolder;
import lombok.Data;

import java.util.List;

@Data
public class ConvertResult {

    /**
     * 转换的时候，中间可能有一些过程，这些过程存这里面
     */
    List<NewlyStatementHolder> convertStatement;


    public ConvertResult(List<NewlyStatementHolder> convertStatement) {
        this.convertStatement = convertStatement;
    }

    public ConvertResult() {
    }

}