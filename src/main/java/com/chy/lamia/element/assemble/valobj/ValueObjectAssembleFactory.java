package com.chy.lamia.element.assemble.valobj;

import com.chy.lamia.element.assemble.AssembleFactoryChain;
import com.chy.lamia.element.assemble.AssembleResult;
import com.chy.lamia.element.assemble.IAssembleFactory;
import com.chy.lamia.entity.*;
import com.chy.lamia.enums.MatchReuslt;
import com.chy.lamia.utils.CommonUtils;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.*;

/**
 * 值对象组装工厂
 * 使用 构造器或者 setter 去组装一个对象出来
 * 不同的构造器和不同的 setter方法决定了 拥有不同的组装方式, 这里可以根据传入进来的 组装 材料来决定使用什么样的组合才是最优解
 */
public class ValueObjectAssembleFactory implements IAssembleFactory {
    List<Candidate> allCandidate = new ArrayList<>();
    Map<String, PriorityExpression> expressionMap = new HashMap<>();
    JCUtils jcUtils;
    private boolean complete = false;
    private String originalClassPath;

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
    public void addMaterial(ParameterType parameterType, JCTree.JCExpression expression,
                            Integer priority, AssembleFactoryChain chian) {
        for (Candidate candidate : allCandidate) {
            MatchReuslt matchReuslt = candidate.match(parameterType, priority);
            //类型和名称都相同了 说明 这个表达式将是构成的一部分，把他存起来
            if (matchReuslt == MatchReuslt.HIT) {
                updateExpressionMap(parameterType.getName(), expression, priority);
            }
        }
        chian.addMaterial(parameterType, expression, priority, chian);
    }

    private void updateExpressionMap(String name, JCTree.JCExpression expression, Integer priority) {
        PriorityExpression priorityExpression = expressionMap.get(name);
        if (priorityExpression == null) {
            expressionMap.put(name, new PriorityExpression(expression, priority));
            return;
        }
        if (priority > priorityExpression.getPriority()) {
            expressionMap.put(name, new PriorityExpression(expression, priority));
        }
        return;
    }

    @Override
    public AssembleResult generate(AssembleFactoryChain chain) {
        chain.generate(chain);
        Candidate candidate = choose();
        if (candidate == null) {
            throw new RuntimeException("类 ： [" + originalClassPath + "] 构造器参数不够");
        }
        return doGenerateTree(candidate);
    }


    private AssembleResult doGenerateTree(Candidate candidate) {
        List<JCTree.JCStatement> statements = new ArrayList<>();
        //先使用构造器生成要返回的 空 对象
        String newInstant = createNewInstant(candidate, statements);
        //生成对应的 set 方法
        createSetter(candidate, newInstant, statements);
        AssembleResult result = new AssembleResult(statements, newInstant);
        return result;
    }

    private void createSetter(Candidate candidate, String instantName, List<JCTree.JCStatement> result) {
        candidate.getHitSetter().forEach((k, v) -> {
            JCTree.JCExpression wapperExpression = candidate.createdWapperExpression(k, expressionMap.get(k).getExpression());
            JCTree.JCExpressionStatement setterExpression = jcUtils.execMethod(instantName, v.getMethodName(), wapperExpression);
            result.add(setterExpression);
        });
    }

    private String createNewInstant(Candidate candidate, List<JCTree.JCStatement> result) {
        Constructor constructor = candidate.getConstructor();
        List<JCTree.JCExpression> paramsExpression = new ArrayList<>();
        constructor.getParams().forEach(param -> {
            String name = param.getName();
            paramsExpression.add(candidate.createdWapperExpression(name, expressionMap.get(name).getExpression()));
        });
        String varName = CommonUtils.generateVarName("result");

        JCTree.JCNewClass jcNewClass = jcUtils.newClass(originalClassPath, paramsExpression);
        JCTree.JCVariableDecl newVar = jcUtils.createVar(varName, originalClassPath, jcNewClass);
        result.add(newVar);
        return varName;
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
