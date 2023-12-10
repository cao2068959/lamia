package com.chy.lamia.convert.core.expression.parse.entity;

import com.chy.lamia.expose.rule.RuleType;
import lombok.Data;
import lombok.Getter;

@Data
public class RuleTypeArgWrapper extends  ArgWrapper{

    @Getter
    RuleType ruleType;

}
