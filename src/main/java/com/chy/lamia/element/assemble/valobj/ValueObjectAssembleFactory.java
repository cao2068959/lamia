package com.chy.lamia.element.assemble.valobj;

import com.chy.lamia.annotation.MapMember;
import com.chy.lamia.element.assemble.AssembleFactoryChain;
import com.chy.lamia.element.assemble.AssembleMaterial;
import com.chy.lamia.element.assemble.AssembleResult;
import com.chy.lamia.element.assemble.IAssembleFactory;
import com.chy.lamia.element.assemble.map.MapAssembleFactory;
import com.chy.lamia.element.funicle.FunicleFactory;
import com.chy.lamia.entity.*;
import com.chy.lamia.enums.MatchReuslt;
import com.chy.lamia.utils.CommonUtils;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.Lists;
import com.sun.tools.javac.tree.JCTree;

import java.util.*;

/**
 * 值对象组装工厂
 * 使用 构造器或者 setter 去组装一个对象出来
 * 不同的构造器和不同的 setter方法决定了 拥有不同的组装方式, 这里可以根据传入进来的 组装 材料来决定使用什么样的组合才是最优解
 */
public class ValueObjectAssembleFactory implements IAssembleFactory {
    List<Candidate> allCandidate = new ArrayList<>();
    Map<String, AssembleMaterial> expressionMap = new HashMap<>();
    JCUtils jcUtils;
    private boolean complete = false;
    private String originalClassPath;

    private AssembleMaterial omnipotentVar;

    public ValueObjectAssembleFactory(JCUtils jcUtils, String originalClassPath,
                                      List<Constructor> constructors,
                                      Map<String, Setter> setterMap) {
        this.jcUtils = jcUtils;
        this.originalClassPath = originalClassPath;

        //根据构造器来分组
        // 如有 构造器 -> Z(A,B,C)  setter -> setA setB setC setD   那么 这个组合就成了  Z(A,B,C) And setD
        for (Constructor constructor : constructors) {
            Candidate candidate = new Candidate(constructor, setterMap);
            allCandidate.add(candidate);
        }
    }

    @Override
    public void addMaterial(AssembleMaterial assembleMaterial, AssembleFactoryChain chian) {
        ParameterType parameterType = assembleMaterial.getParameterType();
        Integer priority = assembleMaterial.getPriority();
        //判断一下是不是万能变量
        if (isOmnipotent(assembleMaterial)) {
            omnipotentVar = assembleMaterial;
        }

        String name = assembleMaterial.getMapMember().map(MapMember::value).orElse(parameterType.getName());
        for (Candidate candidate : allCandidate) {
            MatchReuslt matchReuslt = candidate.match(name, parameterType, priority);
            //类型和名称都相同了 说明 这个表达式将是构成的一部分，把他存起来
            if (matchReuslt == MatchReuslt.HIT) {
                updateExpressionMap(name, assembleMaterial);
            }
        }
        chian.addMaterial(assembleMaterial, chian);
    }

    private boolean isOmnipotent(AssembleMaterial assembleMaterial) {
        //不扩散那肯定不是
        if (!assembleMaterial.isSpread()) {
            return false;
        }
        //不是Map 类型的,没有机会了
        if (!MapAssembleFactory.isNeedDeal(assembleMaterial.getParameterType())) {
            return false;
        }
        return true;
    }


    private void updateExpressionMap(String name, AssembleMaterial assembleMaterial) {
        AssembleMaterial existAssembleMaterial = expressionMap.get(name);
        if (existAssembleMaterial == null) {
            expressionMap.put(name, assembleMaterial);
            return;
        }
        if (assembleMaterial.getPriority() > existAssembleMaterial.getPriority()) {
            expressionMap.put(name, assembleMaterial);
        }
        return;
    }

    @Override
    public AssembleResult generate(AssembleFactoryChain chain) {
        //chain.generate(chain);
        //选择一个合适的Candidate
        Candidate candidate = choose();
        //没有万能变量,并且不满足构造器,那么就直接报错了
        if (omnipotentVar == null && candidate == null) {
            throw new RuntimeException("类 ： [" + originalClassPath + "] 构造器参数不够");
        }
        if (candidate == null) {
            //没有找到, 但是发现有万能变量, 去选一个差的最少的
            candidate = asChooseAs();
        }
        return doGenerateTree(candidate);
    }


    private AssembleResult doGenerateTree(Candidate candidate) {
        List<JCTree.JCStatement> statements = new ArrayList<>();
        Set<String> dependentClassPath = new HashSet<>();

        //先使用构造器生成要返回的 空 对象
        String newInstant = createNewInstant(candidate, statements, dependentClassPath);
        //生成对应的 set 方法
        createSetter(candidate, newInstant, statements, dependentClassPath);
        AssembleResult result = new AssembleResult(statements, newInstant, dependentClassPath);
        return result;
    }


    private void createSetter(Candidate candidate, String instantName,
                              List<JCTree.JCStatement> result, Set<String> dependentVar) {

        Map<String, ParameterType> hitSetter = candidate.getHitSetter();
        if (omnipotentVar != null) {
            hitSetter = candidate.getSetter();
        }

        hitSetter.forEach((k, v) -> {
            AssembleMaterial assembleMaterial = omnipotentVarReplace(expressionMap.get(k), k);
            gatherDependent(assembleMaterial, dependentVar);
            JCTree.JCExpression wapperExpression = candidate.createdWapperExpression(k,
                    assembleMaterial.getExpression().getExpressionByHandle(v));
            JCTree.JCExpressionStatement setterExpression = jcUtils.execMethod(instantName, v.getMethodName(), wapperExpression);
            result.add(setterExpression);
        });
    }

    /**
     * 收集依赖到了什么 对象
     *
     * @param assembleMaterial
     */
    private void gatherDependent(AssembleMaterial assembleMaterial, Set<String> result) {
        assembleMaterial.getTopParent().ifPresent(am -> {
            result.add(am.getParameterType().getTypePatch());
        });
    }

    private String createNewInstant(Candidate candidate, List<JCTree.JCStatement> result,
                                    Set<String> dependentVar) {
        Constructor constructor = candidate.getConstructor();
        List<JCTree.JCExpression> paramsExpression = new ArrayList<>();
        constructor.getParams().forEach(param -> {
            String name = param.getName();
            AssembleMaterial assembleMaterial = omnipotentVarReplace(expressionMap.get(name), name);
            gatherDependent(assembleMaterial, dependentVar);
            paramsExpression.add(candidate.createdWapperExpression(name, assembleMaterial.getExpression().getExpressionByHandle(param)));
        });
        String varName = CommonUtils.generateVarName("result");

        JCTree.JCNewClass jcNewClass = jcUtils.newClass(originalClassPath, paramsExpression);
        JCTree.JCVariableDecl newVar = jcUtils.createVar(varName, originalClassPath, jcNewClass);
        result.add(newVar);
        return varName;
    }

    /**
     * 判断是否应该由 万能变量去替换
     *
     * @param assembleMaterial
     * @return
     */
    private AssembleMaterial omnipotentVarReplace(AssembleMaterial assembleMaterial, String name) {
        if (omnipotentVar == null) {
            return assembleMaterial;
        }

        if (assembleMaterial != null && assembleMaterial.getPriority() > omnipotentVar.getPriority()) {
            return assembleMaterial;
        }


        //生成 map.get(name) 这个语句
        JCTree.JCExpressionStatement mapGetStatement = jcUtils.execMethod(omnipotentVar.getExpression().getExpression(), "get",
                Lists.of(jcUtils.geStringExpression(name)));

        Expression expression = new Expression(mapGetStatement.expr, (oldExpression, parameterType) -> {
            if (parameterType == null) {
                return oldExpression;
            }
            return jcUtils.typeCast(parameterType.getTypePatch(), oldExpression);
        });
        //去生成新的 assembleMaterial;
        return new AssembleMaterial(omnipotentVar.getParameterType(), expression);
    }


    private Candidate choose() {
        int score = -1;
        Candidate result = null;
        for (Candidate candidate : allCandidate) {
            int cScore = candidate.score();
            if (score < cScore) {
                score = cScore;
                result = candidate;
            }
        }
        return result;
    }

    /**
     * 尽可能的去选择一个最接近的
     *
     * @return
     */
    public Candidate asChooseAs() {
        Candidate result = null;
        for (Candidate candidate : allCandidate) {
            if (result == null) {
                result = candidate;
                continue;
            }
            if (result.constructorDifference() > candidate.constructorDifference()) {
                result = candidate;
            }
        }
        return result;
    }

    @Override
    public void clear(AssembleFactoryChain chain) {
        complete = false;
        expressionMap = new HashMap<>();
        allCandidate.forEach(Candidate::clear);
        chain.clear(chain);
    }

    @Override
    public String toString() {
        return "AssembleFactory{" +
                "allCandidate=" + allCandidate +
                ", expressionMap=" + expressionMap +
                '}';
    }
}
