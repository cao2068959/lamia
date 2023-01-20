package com.chy.lamia.element.resolver.expression;

import lombok.Data;

import java.util.List;

/**
 * @author bignosecat
 */
@Data
public class LamiaExpression {
    List<String> convertArgsNames;

    boolean defaultSpread = false;

}
