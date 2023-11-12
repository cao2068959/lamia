package com.chy.lamia.convert.core.expression.builder.rule.handler;


import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;

import java.util.List;

/**
 * @author bignosecat
 */
public class IgnoreNullRuleHandler implements IRuleHandler {

    @Override
    public void run(Expression varExpression, RuleChain chain) {

        TreeFactory treeFactory = ComponentFactory.getComponent(TreeFactory.class);

        Expression varNotEqNull =  treeFactory.createVarNotEqNull(varExpression);

        // 规则链继续执行，并且把之后生成的数据都返回出去, 后续执行的数据，不会直接放入到 chain 对象中，而是从这里返回出来
        List<Statement> jcStatements = chain.continueCallAndReturn(varExpression);

        // 生成if语句
        Statement anIf = treeFactory.createIf(varNotEqNull, jcStatements, null);
        chain.addStatement(anIf);
    }


}
