package com.chy.lamia.element;


import com.chy.lamia.element.type.TypeProcessorFactory;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.TypeProcessorResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UnPackMutliParameterType {

    ParameterType parameterType;

    /**
     *  一个复合类型的解包都会放入 这里面 按照解包的顺序去存放
     *  如 : List<Optional<String>> ---> 那么 链表按照顺序是 List<Optional<String>> -->  Optional<String> --> String
     */
    List<TypeProcessorResult> typeProcessorResultLink = new LinkedList<>();

    public UnPackMutliParameterType(ParameterType parameterType) {
        parseType(parameterType);
        this.parameterType = parameterType;
    }

    private void parseType(ParameterType parameterType){
        TypeProcessorResult typeProcessorResult = TypeProcessorFactory.instance.unpack(parameterType);
        if (typeProcessorResult == null){
            return;
        }
        typeProcessorResultLink.add(typeProcessorResult);
        parseType(typeProcessorResult.getNextParameterType());
    }

}
