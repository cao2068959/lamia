package com.chy.lamia.element;

import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.entity.NameAndType;
import com.chy.lamia.entity.Setter;

import java.util.*;

/**
 * 组装工厂,使用 构造器或者 setter 去组装一个对象出来
 * 不同的构造器和不同的 setter方法决定了 拥有不同的组装方式, 这里可以根据传入进来的 组装 材料来决定使用什么样的组合才是最优解
 */
public class AssembleFactory {
    List<Candidate> allCandidate = new ArrayList<>();

    public AssembleFactory(List<Constructor> constructors, Map<String, Setter> setterMap) {
        for (Constructor constructor : constructors) {
            Candidate candidate = new Candidate(constructor,setterMap);
            allCandidate.add(candidate);
        }
    }

    static class Candidate {
        private Map<String, NameAndType> constructorParamSet = new HashMap<>();
        private Map<String, NameAndType> setterMap = new HashMap<>();

        public Candidate(Constructor constructor, Map<String, Setter> allSetter) {

            constructor.getParams().stream().forEach(param -> {
                constructorParamSet.put(param.getName(), param);
            });

            allSetter.entrySet().stream().filter(setter -> !constructorParamSet.containsKey(setter.getKey()))
                    .forEach(setter -> {
                        NameAndType result = new NameAndType(setter.getKey(), "");
                        setterMap.put(setter.getKey(), result);
                    });
        }
    }

}
