package com.chy.lamia.element;

import com.chy.lamia.entity.Constructor;
import com.chy.lamia.entity.Expression;
import com.chy.lamia.entity.Setter;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;

import java.util.*;

/**
 * 组装工厂,使用 构造器或者 setter 去组装一个对象出来
 * 不同的构造器和不同的 setter方法决定了 拥有不同的组装方式, 这里可以根据传入进来的 组装 材料来决定使用什么样的组合才是最优解
 */
public class AssembleFactory {
    List<Candidate> allCandidate = new ArrayList<>();
    Map<String, Expression> expressionMap = new HashMap<>();
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


    public void match(String fieldName, String fieldType, JCTree.JCExpression expression) {
        if (complete) {
            return;
        }
        for (Candidate candidate : allCandidate) {
            boolean match = candidate.match(fieldName, fieldType);
            //返回true 说明 这个表达式将是构成的一部分，把他存起来
            if (match) {
                Expression expressionWrapper = new Expression(expression, fieldType);
                expressionMap.put(fieldName, expressionWrapper);
            }
            if (candidate.end()) {
                complete = true;
            }
        }
    }

    public List<JCTree.JCStatement> generateTree() {
        Candidate candidate = choose();
        if (candidate == null) {
            throw new RuntimeException("类 ： [" + originalClassPath + "] 构造器参数不够");
        }
        return doGenerateTree(candidate);

    }

    private List<JCTree.JCStatement> doGenerateTree(Candidate candidate) {
        List<JCTree.JCStatement> result = new ArrayList<>();
        //先使用构造器生成要返回的 空 对象
        String newInstant = createNewInstant(candidate, result);
        //生成对应的 set 方法
        createSetter(candidate, newInstant, result);
        return result;
    }

    private void createSetter(Candidate candidate, String instantName, List<JCTree.JCStatement> result) {
        candidate.getHitSetter().forEach((k, v) -> {
            Expression expression = expressionMap.get(k);
            JCTree.JCExpressionStatement setterExpression = jcUtils.execMethod(instantName, v.getMethodName(), expression.getExpression());
            result.add(setterExpression);
        });
    }


    private String createNewInstant(Candidate candidate, List<JCTree.JCStatement> result) {

        Constructor constructor = candidate.getConstructor();
        List<Expression> paramsExpression = new ArrayList<>();
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


}
