package com.chy.lamia.element;


import com.chy.lamia.element.type.ExpressionFunction;
import com.chy.lamia.element.type.TypeProcessorFactory;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.TypeProcessorResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UnPackMutliParameterType {

    ParameterType parameterType;

    /**
     * 一个复合类型的解包都会放入 这里面 按照解包的顺序去存放
     * 如 : List<Optional<String>> ---> 那么 链表按照顺序是 List<Optional<String>> -->  Optional<String>
     */
    List<TypeProcessorResult> typeProcessorResultLink = new LinkedList<>();

    public UnPackMutliParameterType(ParameterType parameterType) {
        parseType(parameterType);
        this.parameterType = parameterType;
    }

    private void parseType(ParameterType parameterType) {
        TypeProcessorResult typeProcessorResult = TypeProcessorFactory.instance.unpack(parameterType);
        if (typeProcessorResult == null) {
            return;
        }
        typeProcessorResultLink.add(typeProcessorResult);
        parseType(typeProcessorResult.getNextParameterType());
    }


    public UnPackTypeMatchResult matchType(ParameterType targetType) {

        UnPackTypeMatchResult unPackTypeMatchResult = doMatchType(targetType, new LinkedList<>());
        if (unPackTypeMatchResult.isMatch()) {
            return unPackTypeMatchResult;
        }
        return UnPackTypeMatchResult.matchFail();
    }


    private UnPackTypeMatchResult doMatchType(ParameterType targetType, List<ExpressionFunction> unpackFunChain) {

        if (this.parameterType.matchType(targetType)) {
            return UnPackTypeMatchResult.matchSuccess(unpackFunChain);
        }

        List<ExpressionFunction> boxingFunChain = new LinkedList();
        for (TypeProcessorResult typeProcessorResult : typeProcessorResultLink) {
            boxingFunChain.add(typeProcessorResult.getAutoboxingFun());
            boolean typeMatch = typeProcessorResult.getNextParameterType().matchType(targetType);
            if (typeMatch) {
                return new UnPackTypeMatchResult(true, boxingFunChain, unpackFunChain);
            }
        }

        //检查一下 目标对象是否是包装类型, 把他解析后再比较一次
        TypeProcessorResult targetUnpack = TypeProcessorFactory.instance.unpack(targetType);
        if (targetUnpack != null) {
            unpackFunChain.add(targetUnpack.getUnboxingFun());
            return doMatchType(targetUnpack.getNextParameterType(), unpackFunChain);
        }
        return UnPackTypeMatchResult.matchFail();
    }


}
