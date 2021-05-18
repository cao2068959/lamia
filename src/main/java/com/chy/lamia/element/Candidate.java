package com.chy.lamia.element;

import com.chy.lamia.element.type.ExpressionFunction;
import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.enums.MatchReuslt;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Pair;

import java.util.*;

/**
 * 候选人对象
 * 一个候选人对象里面持有了 一个构造器以及多个setter方法 ，最少需要满足构造器的全部要求， setter方法不做要求，作为加分项
 * 多个候选人 都满足 构造器的最低要求的情况下， setter 满足的越多越有可能选中
 */
public class Candidate {

    /**
     * 构造器和setter中可以注入的参数都放入这个map, 如果有同名的, 构造器的优先于setter的参数
     * UnPackMutliParameterType : 是一个 解包后的符合类型,
     * 如 ： List<Optional<String>> 他就包含了 List<Optional<String>> ，Optional<String> ，String 三种类型
     */
    private Map<String, UnPackMutliParameterType> allParamMap = new HashMap<>();

    private Map<String, ParameterType> constructorParamMap = new HashMap<>();
    private Map<String, ParameterType> setterMap = new HashMap<>();
    private Constructor constructor;
    private Set<String> constructorHit = new HashSet<>();
    private Set<String> setterHit = new HashSet<>();
    private Map<String, Pair<Integer, UnPackTypeMatchResult>> hitUnPackTypeMatchResult = new HashMap<>();


    public Candidate(Constructor constructor, Map<String, Setter> allSetter) {
        this.constructor = constructor;

        constructor.getParams().stream().forEach(param -> {
            constructorParamMap.put(param.getName(), param);
            UnPackMutliParameterType unPackMutliParameterType = new UnPackMutliParameterType(param);
            allParamMap.put(param.getName(), unPackMutliParameterType);

        });


        allSetter.entrySet().stream()
                .filter(setter -> !constructorParamMap.containsKey(setter.getKey()))
                .forEach(setter -> {
                    ParameterType result = new ParameterType(setter.getKey(), setter.getValue().getParameterType(),
                            setter.getValue().getSimpleName());
                    setterMap.put(setter.getKey(), result);
                    UnPackMutliParameterType unPackMutliParameterType = new UnPackMutliParameterType(result);
                    allParamMap.put(setter.getKey(), unPackMutliParameterType);
                });
    }

    /**
     * 传入字段进来，看看能不能和构造器和setter匹配上
     * 如果匹配到那么 将返回true
     *
     * @return
     */
    public MatchReuslt match(ParameterType target, Integer priority) {
        String fieldName = target.getName();
        //使用字段的名称去匹配有没对应的setter或者构造器
        UnPackMutliParameterType unPackMutliParameterType = allParamMap.get(fieldName);
        //没有匹配上
        if (unPackMutliParameterType == null) {
            return MatchReuslt.MISS;
        }
        //去对比类型是否相同
        UnPackTypeMatchResult unPackTypeMatchResult = unPackMutliParameterType.matchType(target);
        //类型不对, 没有匹配上
        if (!unPackTypeMatchResult.isMatch()) {
            return MatchReuslt.MISS;
        }

        //记录一下是构造器命中的,还是setter方法命中的
        if (constructorParamMap.containsKey(fieldName)) {
            constructorHit.add(fieldName);
        } else {
            setterHit.add(fieldName);
        }
        //把匹配的结构记录一下
        updateHitUnPackTypeMatchResult(fieldName, unPackTypeMatchResult, priority);
        return MatchReuslt.HIT;
    }

    /**
     * 根据优先级来判断是否去更新 HitUnPackTypeMatchResult 容器
     *
     * @param fieldName
     * @param unPackTypeMatchResult
     * @param priority
     */
    private void updateHitUnPackTypeMatchResult(String fieldName, UnPackTypeMatchResult unPackTypeMatchResult,
                                                Integer priority) {
        Pair<Integer, UnPackTypeMatchResult> integerUnPackTypeMatchResultPair = hitUnPackTypeMatchResult.get(fieldName);
        if (integerUnPackTypeMatchResultPair != null) {
            Integer oldPriority = integerUnPackTypeMatchResultPair.fst;
            if (priority < oldPriority) {
                return;
            }
        }
        Pair<Integer, UnPackTypeMatchResult> result = Pair.of(priority, unPackTypeMatchResult);
        hitUnPackTypeMatchResult.put(fieldName, result);
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
        hitUnPackTypeMatchResult = new HashMap<>();
    }

    public JCTree.JCExpression createdWapperExpression(String name, JCTree.JCExpression expression) {
        Pair<Integer, UnPackTypeMatchResult> integerUnPackTypeMatchResultPair = hitUnPackTypeMatchResult.get(name);
        if (integerUnPackTypeMatchResultPair == null) {
            return expression;
        }
        UnPackTypeMatchResult unPackTypeMatchResult = integerUnPackTypeMatchResultPair.snd;
        List<ExpressionFunction> unpackFunChain = unPackTypeMatchResult.getUnpackFunChain();
        if (unpackFunChain != null) {
            for (ExpressionFunction unpackFun : unpackFunChain) {
                expression = unpackFun.getExpression(expression);
            }
        }

        List<ExpressionFunction> boxingFunChain = unPackTypeMatchResult.getBoxingFunChain();
        if (boxingFunChain != null) {
            for (int i = boxingFunChain.size() - 1; i >= 0; i--) {
                expression = boxingFunChain.get(i).getExpression(expression);
            }
        }
        return expression;
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
