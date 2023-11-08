package com.chy.lamia.convert.core.expression.builder.rule.handler;


import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.List;

/**
 * @author bignosecat
 */
public class IgnoreNullRuleHandler implements IRuleHandler {

    @Override
    public void run(JCTree.JCExpression varExpression, RuleChain chain) {

        JCUtils jcUtils = JCUtils.instance;

        JCTree.JCBinary varNotEqNull = jcUtils.createVarNotEqNull(varExpression);

        // 规则链继续执行，并且把之后生成的数据都返回出去, 后续执行的数据，不会直接放入到 chain 对象中，而是从这里返回出来
        List<JCTree.JCStatement> jcStatements = chain.continueCallAndReturn(varExpression);

        // 生成if语句
        JCTree.JCIf anIf = jcUtils.createIf(varNotEqNull, jcStatements, null);
        chain.addStatement(anIf);
    }


}
