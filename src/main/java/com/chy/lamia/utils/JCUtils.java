package com.chy.lamia.utils;

import com.chy.lamia.entity.ParameterType;
import com.chy.lamia.visitor.RandomMethodCreateVisitor;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.comp.*;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;


public class JCUtils {

    private final Attr attr;
    private final Enter enter;
    private final Annotate annotate;
    private final Names names;
    TreeMaker treeMaker;
    JavacElements elementUtils;
    public static JCUtils instance;

    public Annotate getAnnotate() {
        return annotate;
    }

    public JCUtils(TreeMaker treeMaker, JavacElements elementUtils, Annotate annotate, Attr attr, Enter enter, Names names) {
        this.treeMaker = treeMaker;
        this.elementUtils = elementUtils;
        this.attr = attr;
        this.enter = enter;
        this.annotate = annotate;
        this.names = names;
    }

    /**
     * String等类的简写还原成 全路径
     *
     * @param node
     * @param variable
     * @return
     */
    public Type attribType(JCTree node, JCTree.JCVariableDecl variable) {
        return attribType(node, variable.vartype);
    }

    /**
     * String等类的简写还原成 全路径
     *
     * @param node
     * @param ident
     * @return
     */
    public Type attribType(JCTree node, String ident) {
        if (ident == null) {
            return null;
        }
        return attribType(node, memberAccess(ident));
    }

    public Type attribType(JCTree node, JCTree.JCExpression expression) {
        if (!(node instanceof JCTree.JCClassDecl)) {
            return null;
        }
        JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl) node;
        Env<AttrContext> classEnv = enter.getClassEnv(jcClassDecl.sym);
        return attr.attribType(expression, classEnv);
    }

    public JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(elementUtils.getName(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, elementUtils.getName(componentArray[i]));
        }
        return expr;
    }

    public JCTree.JCExpression getNullExpression() {
        JCTree.JCLiteral literal = treeMaker.Literal(TypeTag.BOT, null);
        return literal;
    }

    public JCTree.JCExpression geStringExpression(String data) {
        return treeMaker.Literal(data);
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
        return execMethod(left, methodName, param);
    }

    public JCTree.JCExpressionStatement execMethod(JCTree.JCExpression left, String methodName,
                                                   java.util.List<JCTree.JCExpression> param) {
        return treeMaker.Exec(
                treeMaker.Apply(
                        List.nil(),
                        treeMaker.Select(left, elementUtils.getName(methodName)),
                        toSunList(param) // 方法的入参
                )
        );
    }


    public JCTree.JCBlock createBlock(java.util.List<JCTree.JCStatement> statements) {
        List<JCTree.JCStatement> jcStatements = toSunList(statements);
        return treeMaker.Block(0, jcStatements);
    }

    public JCTree.JCReturn createReturn(String returnName) {

        return treeMaker.Return(treeMaker.Ident(elementUtils.getName(returnName)));
    }

    public JCTree.JCReturn createReturnToStringType(String returnContext) {
        return treeMaker.Return(geStringExpression(returnContext));
    }

    public JCTree.JCExpressionStatement execMethod(String methodInstanceName, String methodName,
                                                   JCTree.JCExpression param) {
        java.util.List params = new LinkedList();
        params.add(param);
        return execMethod(methodInstanceName, methodName, params);
    }


    public JCTree getTree(String treePath) {
        Symbol.ClassSymbol typeElement = elementUtils.getTypeElement(treePath);
        if (typeElement == null) {
            return null;
        }
        return elementUtils.getTree(typeElement);
    }

    /**
     * 生成一个 new语句 比如 new User("1","2")
     *
     * @param className   要new的class 的全路径
     * @param argAndTypes 构造器的入参
     * @return
     */
    public JCTree.JCNewClass newClass(String className, java.util.List<JCTree.JCExpression> argAndTypes) {
        java.util.List<JCTree.JCExpression> args = new ArrayList();
        int pos = 0;
        for (JCTree.JCExpression argAndType : argAndTypes) {
            JCTree.JCExpression expression = argAndType;
            if (pos == 0) {
                pos = expression.pos;
            } else {
                pos = pos + expression.toString().length();
                expression.pos = pos;
            }
            args.add(expression);
        }
        List<JCTree.JCExpression> agslist = toSunList(args);
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


    /**
     * 生成 增强for循环
     *
     * @param forVarType
     * @param collectionVar
     * @param blockFun
     * @return
     */
    public JCTree.JCEnhancedForLoop createForeachLoop(ParameterType forVarType, String collectionVar,
                                                      Function<JCTree.JCVariableDecl, java.util.List<JCTree.JCStatement>> blockFun) {
        String forVarName = CommonUtils.generateVarName("forVar");
        JCTree.JCVariableDecl forVar = createVar(forVarName, forVarType.getTypePatch(), null);
        JCTree.JCBlock block = createBlock(blockFun.apply(forVar));
        return treeMaker.ForeachLoop(forVar, memberAccess(collectionVar), block);
    }

    /**
     * 生成 增强for循环
     *
     * @param collectionExpression 需要循环的变量的表达式
     * @param itemType             需要循环的变量的类型
     * @param itemName             每一个 item对应的名称
     * @param statements
     * @return
     */
    public JCTree.JCEnhancedForLoop createForeachLoop(JCTree.JCExpression collectionExpression, ParameterType itemType,
                                                      String itemName, java.util.List<JCTree.JCStatement> statements) {

        JCTree.JCVariableDecl forVar = createVar(itemName, itemType.getTypePatch(), null);
        JCTree.JCBlock block = createBlock(statements);
        return treeMaker.ForeachLoop(forVar, collectionExpression, block);
    }


    public Optional<String> genRandomMethod(String className) {
        JCTree tree = elementUtils.getTree(elementUtils.getTypeElement(className));
        if (tree == null) {
            return Optional.empty();
        }
        RandomMethodCreateVisitor visitor = new RandomMethodCreateVisitor("random", false);
        tree.accept(visitor);
        return Optional.ofNullable(visitor.getRandomMethodName());
    }

    /**
     * 生成一个静态的随机方法
     *
     * @param className
     * @return
     */
    public Optional<String> genStaticRandomMethod(String className, String methodType) {
        JCTree tree = elementUtils.getTree(elementUtils.getTypeElement(className));
        if (tree == null) {
            return Optional.empty();
        }
        RandomMethodCreateVisitor visitor = new RandomMethodCreateVisitor(methodType, true);
        tree.accept(visitor);
        return Optional.ofNullable(visitor.getRandomMethodName());
    }


    public JCTree.JCMethodDecl createMethod(String methodName, String returnType, boolean isStatic,
                                            java.util.List<JCTree.JCStatement> statements,
                                            java.util.List<JCTree.JCVariableDecl> methodParam) {

        JCTree.JCBlock block = treeMaker.Block(0, toSunList(statements));
        // 生成返回对象
        JCTree.JCExpression methodType;
        if (returnType == null) {
            methodType = treeMaker.Type(new Type.JCVoidType());
        } else {
            methodType = memberAccess(returnType);
        }
        int flags = Flags.PUBLIC;
        if (isStatic) {
            flags = flags | Flags.STATIC;
        }


        return treeMaker.MethodDef(treeMaker.Modifiers(flags), names.fromString(methodName), methodType, List.nil(),
                toSunList(methodParam), List.nil(), block, null);
    }


    /**
     * JCTypeApply 转 ParameterType, 会处理泛型
     *
     * @param data
     * @return
     */

    public ParameterType generateParameterType(JCTree node, JCTree data) {
        if (data instanceof JCTree.JCTypeApply) {
            JCTree.JCTypeApply jcTypeApply = (JCTree.JCTypeApply) data;

            JCTree.JCExpression clazz = jcTypeApply.clazz;
            Type completeType = attribType(node, clazz);
            ParameterType result = new ParameterType(completeType.toString());
            java.util.List<ParameterType> genericRsult = new LinkedList<>();
            result.setGeneric(genericRsult);

            //查找泛型
            List<JCTree.JCExpression> generics = jcTypeApply.getTypeArguments();

            for (JCTree.JCExpression generic : generics) {
                genericRsult.add(generateParameterType(node, generic));
            }
            return result;
        } else if (data instanceof JCTree.JCIdent) {
            JCTree.JCIdent jcIdent = (JCTree.JCIdent) data;
            Type completeType = attribType(node, jcIdent);
            return new ParameterType(completeType.toString());
        }
        throw new RuntimeException("无法解析泛型类型 [" + data.toString() + "] class:[" + data.getClass().toString() + "]");
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
