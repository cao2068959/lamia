package com.chy.lamia.convert.core.components.entity;

import com.chy.lamia.convert.core.entity.AbnormalVar;
import lombok.Data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Data
public class NewlyStatementHolder {

    /**
     * 生成的语句
     */
    Statement statement;

    /**
     * 这一行语句的类型是否匹配
     */
    boolean typeMatch = true;

    /**
     * 存在异常的字段
     */
    Set<AbnormalVar> abnormalVars;


    public NewlyStatementHolder(Statement statement) {
        this.statement = statement;
    }

    public Set<AbnormalVar> getAbnormalVars() {
        if (abnormalVars == null) {
            return Collections.emptySet();
        }
        return abnormalVars;
    }

    public void addAbnormalVar(AbnormalVar abnormalVar) {
        if (abnormalVars == null) {
            abnormalVars = new HashSet<>();
        }
        abnormalVars.add(abnormalVar);
    }
}
