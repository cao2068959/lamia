package com.chy.lamia.processor;

import com.chy.lamia.annotation.Mapping;
import com.chy.lamia.element.funicle.FunicleFactory;
import com.chy.lamia.log.Logger;
import com.chy.lamia.processor.marked.MarkedContext;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.visitor.MethodUpdateVisitor;
import com.sun.tools.javac.comp.Annotate;
import com.sun.tools.javac.comp.Attr;
import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.util.*;

@SupportedAnnotationTypes({"com.chy.lamia.annotation.Mapping"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MappingAnnotationProcessor extends AbstractProcessor {

    JavacElements elementUtils;

    TreeMaker treeMaker;

    JavacTrees trees;

    JCUtils jcUtils;

    private MarkedContext markedContext = new MarkedContext();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        elementUtils = (JavacElements) processingEnv.getElementUtils();
        trees = (JavacTrees) Trees.instance(processingEnv);
        Attr attr = Attr.instance(context);
        Enter enter = Enter.instance(context);
        Annotate annotate = Annotate.instance(context);
        Names names = Names.instance(context);
        jcUtils = new JCUtils(treeMaker, elementUtils, annotate, attr, enter, names);
        JCUtils.instance = jcUtils;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            Logger.log("执行--------->");

            if (roundEnv.processingOver()) {
                handleSignMethod();
            } else {
                prepare(annotations, roundEnv);
            }
            return true;
        } catch (Throwable e) {
            Logger.throwableLog(e);
            throw e;
        } finally {
            Logger.push();
        }
    }

    /**
     * 处理标注了@Mapping 的方法， 生成对应的实现代码
     */
    private void handleSignMethod() {
        markedContext.forEach((className, markedMethods) -> {
            JCTree tree = elementUtils.getTree(elementUtils.getTypeElement(className));
            //去修改原本方法中的逻辑
            tree.accept(new MethodUpdateVisitor(markedMethods, jcUtils, tree, className));
            //给这个类加上对应的脐带方法
            FunicleFactory.createFunicleMethod(tree,className);
        });
        FunicleFactory.persistence();
    }

    /**
     * 收集项目里所有类的 Element 对象
     * 同时把标注了 @Mapping 的方法 给存储下来
     *
     * @param annotations
     * @param roundEnv
     */
    private void prepare(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Mapping.class)) {
            Mapping annotation = element.getAnnotation(Mapping.class);
            Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) element;
            String key = methodSymbol.owner.toString();
            markedContext.put(key, methodSymbol);
        }
    }

}
