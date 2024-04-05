package com.chy.lamia.convert.core.expression.imp.builder.rule.handler;


import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.NewlyStatementHolder;
import com.chy.lamia.convert.core.components.entity.Statement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bignosecat
 */
public class IgnoreNullRuleHandler implements IRuleHandler {

    @Override
    public void run(Expression varExpression, RuleChain chain) {

        TreeFactory treeFactory = ComponentFactory.getComponent(TreeFactory.class);

        Expression varNotEqNull = treeFactory.createVarNotEqNull(varExpression);

        // 规则链继续执行，并且把之后生成的数据都返回出去, 后续执行的数据，不会直接放入到 chain 对象中，而是从这里返回出来
        List<NewlyStatementHolder> jcStatements = chain.continueCallAndReturn(varExpression);
        List<Statement> statements = jcStatements.stream().map(NewlyStatementHolder::getStatement).collect(Collectors.toList());
        // 生成if语句
        Statement anIf = treeFactory.createIf(varNotEqNull, statements, null);
        chain.addStatement(new NewlyStatementHolder(anIf));
    }

    @Override
    public boolean isReRefVar() {
        return true;
    }
}
