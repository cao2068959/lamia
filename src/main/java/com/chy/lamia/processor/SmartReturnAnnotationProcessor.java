package com.chy.lamia.processor;

import com.chy.lamia.annotation.SmartReturn;
import com.chy.lamia.element.ClassElement;
import com.chy.lamia.entity.Var;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.chy.lamia.visitor.ParameterVisitor;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("com.chy.lamia.annotation.SmartReturn")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class SmartReturnAnnotationProcessor extends AbstractProcessor {

    JavacElements elementUtils;

    TreeMaker treeMaker;

    JavacTrees trees;

    private Map<String, Symbol.MethodSymbol> pendMethod = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        elementUtils = (JavacElements) processingEnv.getElementUtils();
        trees = (JavacTrees) Trees.instance(processingEnv);

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
        pendMethod.values().stream().forEach(methodSymbol -> {
            //解析这个方法的返回值
            Type returnType = methodSymbol.getReturnType();
            //返回值不是一个对象就不进行处理了
            if (returnType.getTag() != TypeTag.CLASS) {
                return;
            }
            ClassElement returnElement = new ClassElement(elementUtils, trees, returnType.toString());
            //先获取返回值       ========
            Map<String, Var> instantVarName = returnElement.getInstantVarName();






            //String  = returnType.toString();

            //elementUtils.getTree(methodSymbol).accept(new CopyBeanMethodVisitor());

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
        Set<? extends Element> rootElements = roundEnv.getRootElements();
        for (Element rootElement : rootElements) {
            JCTree tree = elementUtils.getTree(rootElement);
            tree.accept(new ParameterVisitor());
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(SmartReturn.class)) {
            Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) element;
            pendMethod.put(methodSymbol.toString(), methodSymbol);
        }
    }


}
