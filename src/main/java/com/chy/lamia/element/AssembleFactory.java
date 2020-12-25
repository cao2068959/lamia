package com.chy.lamia.element;

import com.chy.lamia.element.type.TypeProcessorFactory;
import com.chy.lamia.entity.*;
import com.chy.lamia.enums.MatchReuslt;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.*;

/**
 * 组装工厂,使用 构造器或者 setter 去组装一个对象出来
 * 不同的构造器和不同的 setter方法决定了 拥有不同的组装方式, 这里可以根据传入进来的 组装 材料来决定使用什么样的组合才是最优解
 */
public class AssembleFactory {
    List<Candidate> allCandidate = new ArrayList<>();
    Map<String, PriorityExpression> expressionMap = new HashMap<>();
    JCUtils jcUtils;
    private boolean complete = false;
    private String originalClassPath;

    public AssembleFactory(JCUtils jcUtils, String originalClassPath, List<Constructor> constructors, Map<String, Setter> setterMap) {
        this.jcUtils = jcUtils;
        this.originalClassPath = originalClassPath;

        //根据构造器来分组
        // 如有 构造器 -> Z(A,B,C)  setter -> setA setB setC setD   那么 这个组合就成了  Z(A,B,C) And setD
        for (Constructor constructor : constructors) {
            Candidate candidate = new Candidate(constructor, setterMap);
            allCandidate.add(candidate);
        }


    }


    public void match(ParameterType parameterType, JCTree.JCExpression expression, Integer priority) {

        boolean additional = false;
        for (Candidate candidate : allCandidate) {
            MatchReuslt matchReuslt = candidate.match(parameterType);
            //类型和名称都相同了 说明 这个表达式将是构成的一部分，把他存起来
            if (matchReuslt == MatchReuslt.HIT) {
                updateExpressionMap(parameterType.getName(), expression, priority);
            }
            if (matchReuslt == MatchReuslt.MAY) {
                additional = true;
            }
        }

        // 如果additional=true 说明名称匹配上了，但是类型不同，类型可能是optional这样的包装类型 ，解析包装后再递归给他一次机会
        if (additional) {
            UnpackResult unpack = TypeProcessorFactory.instance.unpack(parameterType, expression);
            if(unpack != null){
                //match(nameAndType,)
            }
        }
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

    public AssembleResult generateTree() {
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
            PriorityExpression expression = expressionMap.get(k);
            JCTree.JCExpressionStatement setterExpression = jcUtils.execMethod(instantName, v.getMethodName(), expression.getExpression());
            result.add(setterExpression);
        });
    }


    private String createNewInstant(Candidate candidate, List<JCTree.JCStatement> result) {

        Constructor constructor = candidate.getConstructor();
        List<PriorityExpression> paramsExpression = new ArrayList<>();
        constructor.getParams().forEach(param -> {
            String name = param.getName();
            paramsExpression.add(expressionMap.get(name));
        });
        String varName = "result";
        JCTree.JCNewClass jcNewClass = jcUtils.newClass(originalClassPath, paramsExpression);
        JCTree.JCVariableDecl newVar = jcUtils.createVar("result", originalClassPath, jcNewClass);
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


    public void clear() {
        complete = false;
        expressionMap = new HashMap<>();
        allCandidate.forEach(Candidate::clear);
    }
}
