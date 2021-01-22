package com.chy.lamia.element;

import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.enums.MatchReuslt;

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


    private Map<String, ParameterType> constructorParamMap = new HashMap<>();
    private Map<String, ParameterType> setterMap = new HashMap<>();
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
                    ParameterType result = new ParameterType(setter.getKey(), setter.getValue().getTypePath(),
                            setter.getValue().getSimpleName());
                    setterMap.put(setter.getKey(), result);
                });
    }

    /**
     * 传入字段进来，看看能不能和构造器和setter匹配上
     * 如果匹配到那么 将返回true
     *
     * @return
     */
    public MatchReuslt match(ParameterType target) {
        String fieldName = target.getName();
        //获取构造器的字段，然后匹配，匹配上了就放入 hit容器中
        ParameterType constructor = constructorParamMap.get(fieldName);
        MatchReuslt constructorMatchReuslt = matchType(fieldName, constructor, target, constructorHit);
        if (constructorMatchReuslt == MatchReuslt.HIT) {
            return MatchReuslt.HIT;
        }

        //获取setter的字段，然后匹配，匹配上了就放入 hit容器中
        ParameterType setter = setterMap.get(fieldName);
        MatchReuslt setterMatchReuslt = matchType(fieldName, setter, target, setterHit);
        if (setterMatchReuslt == MatchReuslt.HIT) {
            return MatchReuslt.HIT;
        }

        //只要构造器和setter 中有一个是可能，那么就直接返回
        if(setterMatchReuslt == MatchReuslt.MAY || constructorMatchReuslt == MatchReuslt.MAY){
            return MatchReuslt.MAY;
        }

        return MatchReuslt.MISS;
    }


    private MatchReuslt matchType(String fieldName, ParameterType componentType,
                                  ParameterType target, Set<String> his) {
        if (componentType == null) {
            return MatchReuslt.MISS;
        }
        if (componentType.matchType(target)) {
            his.add(fieldName);
            return MatchReuslt.HIT;
        }
        return MatchReuslt.MAY;
    }

    public int score() {
        //如果 构造器都没满足则不及格
        if (constructorHit.size() != constructorParamMap.size()) {
            return -1;
        }
        return setterHit.size();
    }

    public Map<String, ParameterType> getHitSetter() {
        Map<String, ParameterType> result = new HashMap<>();
        setterHit.forEach(hit -> {
            result.put(hit, setterMap.get(hit));
        });
        return result;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public void clear() {
        constructorHit = new HashSet<>();
        setterHit = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "constructorParamMap=" + constructorParamMap +
                ", setterMap=" + setterMap +
                ", constructor=" + constructor +
                ", constructorHit=" + constructorHit +
                ", setterHit=" + setterHit +
                '}';
    }
}
