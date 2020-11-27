package com.chy.lamia.processor;

import com.chy.lamia.annotation.SmartReturn;
import com.chy.lamia.element.AssembleFactory;
import com.chy.lamia.element.ClassElement;
import com.chy.lamia.entity.ChosenClass;
import com.chy.lamia.entity.Getter;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.visitor.MethodUpdateVisitor;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.code.Symbol;


import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.util.*;

@SupportedAnnotationTypes("com.chy.lamia.annotation.SmartReturn")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class SmartReturnAnnotationProcessor extends AbstractProcessor {

    JavacElements elementUtils;

    TreeMaker treeMaker;

    JavacTrees trees;

    JCUtils jcUtils;

    private ChosenClass chosenClass = new ChosenClass();

    private Map<String, ClassElement> classElementCache = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        elementUtils = (JavacElements) processingEnv.getElementUtils();
        trees = (JavacTrees) Trees.instance(processingEnv);
        jcUtils = new JCUtils(treeMaker, elementUtils);

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (roundEnv.processingOver()) {
            handleSignMethod();
        } else {
            prepare(annotations, roundEnv);
        }
        return true;
    }

    /**
     * 处理标注了@SmartReturn 的方法， 生成对应的实现代码
     */
    private void handleSignMethod() {
        chosenClass.forEach((className, simpleClass) -> {
            //解析这个类里面所有打了注解的方法
            simpleClass.getLists().forEach(methodSymbol->{
                //解析这个方法的返回值
                Type returnType = methodSymbol.getReturnType();
                //返回值不是一个对象就不进行处理了
                if (returnType.getTag() != TypeTag.CLASS) {
                    return;
                }
                //解析返回值 的类结构
                ClassElement returnClassElement = getClassElement(returnType.toString());
                AssembleFactory assembleFactory = returnClassElement.getAssembleFactory();
                assembleForParameters(methodSymbol.getParameters(), assembleFactory);
                List<JCTree.JCStatement> treeStatements = assembleFactory.generateTree();
                JCTree ownerTree = elementUtils.getTree(methodSymbol.owner);
                ownerTree.accept(new MethodUpdateVisitor(treeStatements, jcUtils));
            });
        });
    }

    private void assembleForParameters(List<Symbol.VarSymbol> params, AssembleFactory assembleFactory) {
        if (params == null || params.size() == 0) {
            return;
        }
        params.stream().forEach(varSymbol -> {
            ClassElement classElement = getClassElement(varSymbol.type.toString());
            Map<String, Getter> getters = classElement.getInstantGetters();
            getters.forEach((k, v) -> {
                JCTree.JCExpressionStatement getterExpression = jcUtils.execMethod(varSymbol.name.toString(),
                        v.getSimpleName(), new LinkedList<>());
                assembleFactory.match(k, v.getTypePath(), getterExpression.expr);
            });
        });
    }


    /**
     * 收集项目里所有类的 Element 对象
     * 同时把标注了 @SmartReturn 的方法 给存储下来
     *
     * @param annotations
     * @param roundEnv
     */
    private void prepare(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(SmartReturn.class)) {
            Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) element;
            String key = methodSymbol.owner.toString();
            chosenClass.put(key, methodSymbol);
        }
    }


    private ClassElement getClassElement(String classPath) {

        ClassElement result = classElementCache.get(classPath);
        if (result != null) {
            return result;
        }
        result = new ClassElement(jcUtils, classPath);
        classElementCache.put(classPath, result);
        return result;
    }


}
