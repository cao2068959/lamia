package com.chy.lamia.element;

import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.NameAndType;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 候选人对象
 * 一个候选人对象里面持有了 一个构造器以及多个setter方法 ，最少需要满足构造器的全部要求， setter方法不做要求，作为加分项
 * 多个候选人 都满足 构造器的最低要求的情况下， setter 满足的越多越有可能选中
 */
public class Candidate {


    private Map<String, NameAndType> constructorParamMap = new HashMap<>();
    private Map<String, NameAndType> setterMap = new HashMap<>();
    private Constructor constructor;
    private Set<String> constructorHit = new HashSet<>();
    private Set<String> setterHit = new HashSet<>();


    public Candidate(Constructor constructor, Map<String, Setter> allSetter) {
        this.constructor = constructor;

        constructor.getParams().stream().forEach(param -> {
            constructorParamMap.put(param.getName(), param);
        });

        allSetter.entrySet().stream()
                .filter(setter -> !constructorParamMap.containsKey(setter.getKey()))
                .forEach(setter -> {
                    NameAndType result = new NameAndType(setter.getKey(), setter.getValue().getTypePath(),
                            setter.getValue().getSimpleName());
                    setterMap.put(setter.getKey(), result);
                });
    }

    /**
     * 传入字段进来，看看能不能和构造器和setter匹配上
     * 如果匹配到那么 将返回true
     *
     * @param fieldName
     * @param fieldType
     * @return
     */
    public boolean match(String fieldName, String fieldType) {
        NameAndType constructor = constructorParamMap.get(fieldName);

        if (constructor != null && constructor.getTypePath().equals(fieldType)) {
            constructorHit.add(fieldName);
            return true;
        }

        NameAndType setter = setterMap.get(fieldName);
        if (setter != null && setter.getTypePath().equals(fieldType)) {
            setterHit.add(fieldName);
            return true;
        }
        return false;
    }


    public boolean end() {
        if (constructorHit.size() != constructorParamMap.size()) {
            return false;
        }

        if (setterHit.size() != setterMap.size()) {
            return false;
        }
        return true;
    }

    public int score() {
        //如果 构造器都没满足则不及格
        if (constructorHit.size() != constructorParamMap.size()) {
            return -1;
        }
        return setterHit.size();
    }

    public Map<String, NameAndType> getHitSetter() {
        Map<String, NameAndType> result = new HashMap<>();
        setterHit.forEach(hit -> {
            result.put(hit, setterMap.get(hit));
        });
        return result;
    }

    public Constructor getConstructor() {
        return constructor;
    }
}
