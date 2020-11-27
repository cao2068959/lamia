package com.chy.lamia.utils;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;

import java.util.LinkedList;


public class JCUtils {

    TreeMaker treeMaker;
    JavacElements elementUtils;

    public JCUtils(TreeMaker treeMaker, JavacElements elementUtils) {
        this.treeMaker = treeMaker;
        this.elementUtils = elementUtils;
    }

    public JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(elementUtils.getName(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, elementUtils.getName(componentArray[i]));
        }
        return expr;
    }


    /**
     * @param methodInstanceName 方法所在对象的名称 / 全路径的类名称
     * @param methodName         方法名称
     * @param param              参数
     * @return
     */
    public JCTree.JCExpressionStatement execMethod(String methodInstanceName, String methodName,
                                                   java.util.List<JCTree.JCExpression> param) {
        JCTree.JCExpression left;
        if (methodInstanceName.contains(".")) {
            left = memberAccess(methodInstanceName);
        } else {
            left = treeMaker.Ident(elementUtils.getName(methodInstanceName));
        }

        return treeMaker.Exec(
                treeMaker.Apply(
                        List.nil(),
                        treeMaker.Select(left, // . 左边的内容
                                elementUtils.getName(methodName) // . 右边的内容
                        ),
                        toSunList(param) // 方法中的内容
                )
        );
    }


    public JCTree.JCBlock createBlock(java.util.List<JCTree.JCStatement> statements) {
        List<JCTree.JCStatement> jcStatements = toSunList(statements);
        return treeMaker.Block(0, jcStatements);
    }

    public JCTree.JCExpressionStatement execMethod(String methodInstanceName, String methodName,
                                                   JCTree.JCExpression param) {
        java.util.List params = new LinkedList();
        params.add(param);
        return execMethod(methodInstanceName, methodName, params);
    }


    public JCTree getTree(String treePath) {
        return elementUtils.getTree(elementUtils.getTypeElement(treePath));
    }

    /**
     * 生成一个 new语句 比如 new User("1","2")
     *
     * @param className 要new的class 的全路径
     * @param arg       构造器的入参
     * @return
     */
    public JCTree.JCNewClass newClass(String className, java.util.List<JCTree.JCExpression> arg) {
        List<JCTree.JCExpression> agslist = toSunList(arg);
        return treeMaker.NewClass(null, List.nil(), memberAccess(className), agslist, null);
    }


    /**
     * 创建一个变量/常量  比如  String aa = "" / User u = xxxx
     *
     * @param varName   变量的名称
     * @param varClass  变量的类型
     * @param varValue  等好右变的值
     * @param modifiers public private final 等修饰符
     * @return
     */
    public JCTree.JCVariableDecl createVar(String varName, String varClass, JCTree.JCExpression varValue, Long modifiers) {
        return treeMaker.VarDef(
                treeMaker.Modifiers(modifiers),
                elementUtils.getName(varName),
                memberAccess(varClass),
                varValue
        );
    }

    /**
     * 创建一个变量/常量  比如  String aa = "" / User u = xxxx
     *
     * @param varName  变量的名称
     * @param varClass 变量的类型
     * @param varValue 等好右变的值
     * @return
     */
    public JCTree.JCVariableDecl createVar(String varName, String varClass, JCTree.JCExpression varValue) {
        return createVar(varName, varClass, varValue, Flags.PARAMETER);
    }


    private <T> List<T> toSunList(java.util.List<T> list) {
        if (list == null || list.size() == 0) {
            return List.nil();
        }
        return List.from(list);
    }


    public TreeMaker getTreeMaker() {
        return treeMaker;
    }

    public JavacElements getElementUtils() {
        return elementUtils;
    }
}
