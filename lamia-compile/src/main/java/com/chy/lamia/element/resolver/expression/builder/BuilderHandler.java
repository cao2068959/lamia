package com.chy.lamia.element.resolver.expression.builder;

import com.chy.lamia.convert.core.entity.LamiaExpression;
import com.chy.lamia.element.resolver.expression.ConfigParseContext;
import com.chy.lamia.element.resolver.expression.MethodWrapper;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置项处理器的基础接口
 *
 * @author bignosecat
 */
public interface BuilderHandler {

    public void config(LamiaExpression lamiaExpression, MethodWrapper methodWrapper, ConfigParseContext context);

    default public  <T> T getBaseTypeArgs(MethodWrapper methodWrapper, int index, Class<T> type) {

        List<JCTree.JCExpression> args = methodWrapper.getArgs();
        if (args.size() < index + 1) {
            throw new RuntimeException("[" + this.getClass().getName() + "] 获取参数错误, 期望获取第[" + (index + 1) + "]个参数, 而最大参数长度为[" + args.size() + "]");
        }
        JCTree.JCExpression jcExpression = args.get(index);

        if (!(jcExpression instanceof JCTree.JCLiteral)) {
            throw new RuntimeException("[" + this.getClass().getName() + "] 参数类型错误");
        }

        JCTree.JCLiteral jcLiteral = (JCTree.JCLiteral) jcExpression;
        TypeTag typetag = jcLiteral.typetag;

        if (type == Boolean.class) {
            if (typetag != TypeTag.BOOLEAN) {
                throw new RuntimeException("[" + this.getClass().getName() + "] 获取参数类型错误 期望类型:[" + type.getName() + "] 实际类型 [" + typetag.toString() + "]");
            }
            Object value = jcLiteral.value;
            if (value == null) {
                return null;
            }

            return (T) ("1".equals(value.toString()) ? Boolean.TRUE : Boolean.FALSE);
        }
        return null;
    }

    default List<String> fetchArgsName(List<JCTree.JCExpression> args) {
        List<String> result = new ArrayList<>();
        for (JCTree.JCExpression arg : args) {
            result.add(arg.toString());
        }
        return result;
    }
}
