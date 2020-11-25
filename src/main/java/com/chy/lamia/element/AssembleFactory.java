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
    private boolean complete = false;


    public AssembleFactory(List<Constructor> constructors, Map<String, Setter> setterMap) {
        //根据构造器来分组
        // 如有 构造器 -> Z(A,B,C)  setter -> setA setB setC setD   那么 这个组合就成了  Z(A,B,C) And setD
        for (Constructor constructor : constructors) {
            Candidate candidate = new Candidate(constructor, setterMap);
            allCandidate.add(candidate);
        }
    }


    public void match(String fieldName, String fieldType) {
        if (complete) {
            return;
        }
        for (Candidate candidate : allCandidate) {
            boolean match = candidate.match(fieldName, fieldType);
            if (match) {
                complete = true;
            }
        }
    }

    public Candidate choose() {
        int score = -1;
        Candidate result = null;
        for (Candidate candidate : allCandidate){
            int cScore = candidate.score();
            if(score < cScore){
                cScore = cScore;
                result = candidate;
            }
        }
        return result;
    }


    /**
     * 候选人对象
     * 一个候选人对象里面持有了 一个构造器以及多个setter方法 ，最少需要满足构造器的全部要求， setter方法不做要求，作为加分项
     * 多个候选人 都满足 构造器的最低要求的情况下， setter 满足的越多越有可能选中
     */
    public static class Candidate {
        private Map<String, NameAndType> constructorParamMap = new HashMap<>();
        private Map<String, NameAndType> setterMap = new HashMap<>();
        private Set<String> constructorHit = new HashSet<>();
        private Set<String> setterHit = new HashSet<>();

        public Candidate(Constructor constructor, Map<String, Setter> allSetter) {
            constructor.getParams().stream().forEach(param -> {
                constructorParamMap.put(param.getName(), param);
            });

            allSetter.entrySet().stream()
                    .filter(setter -> !constructorParamMap.containsKey(setter.getKey()))
                    .forEach(setter -> {
                        NameAndType result = new NameAndType(setter.getKey(), setter.getValue().getTypePath());
                        setterMap.put(setter.getKey(), result);
                    });
        }

        /**
         * 传入字段进来，看看能不能和构造器和setter匹配上
         * 如果能提前匹配完所有的字段，那么将返回true
         *
         * @param fieldName
         * @param fieldType
         * @return
         */
        public boolean match(String fieldName, String fieldType) {
            NameAndType constructor = constructorParamMap.get(fieldName);

            if (constructor != null && constructor.getTypePath().equals(fieldType)) {
                constructorHit.add(fieldName);
                return end();
            }

            NameAndType setter = setterMap.get(fieldName);
            if (setter != null && setter.getTypePath().equals(fieldType)) {
                setterHit.add(fieldName);
                return end();
            }
            return false;
        }


        private boolean end() {
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


    }

}
