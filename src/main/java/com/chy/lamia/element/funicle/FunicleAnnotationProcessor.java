package com.chy.lamia.element.funicle;

import com.chy.lamia.log.Logger;
import com.chy.lamia.processor.marked.MarkedContext;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.visitor.RandomMethodCreateVisitor;
import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.comp.Annotate;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes({"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class FunicleAnnotationProcessor extends AbstractProcessor {

    JavacElements elementUtils;

    TreeMaker treeMaker;

    JavacTrees trees;

    JCUtils jcUtils;
    private Set<String> funiclePersistence = new HashSet<>();


    private Set<String> funicleComplete = new HashSet<>();

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

        if (JCUtils.instance == null) {
            jcUtils = new JCUtils(treeMaker, elementUtils, annotate, attr, enter, names);
            JCUtils.instance = jcUtils;
        }

        this.funiclePersistence = FunicleFactory.readPersistence();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            doProcess(annotations, roundEnv);
        } catch (Throwable e) {
            Logger.throwableLog(e);
            throw e;
        } finally {
            Logger.push();
        }
        return false;
    }


    private void doProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> rootElements = roundEnv.getRootElements();
        for (Element rootElement : rootElements) {
            String classpath = rootElement.toString();
            if (funiclePersistence.contains(classpath) && !funicleComplete.contains(classpath)) {
                //需要去生成脐带方法
                JCUtils.instance.genStaticRandomMethod(classpath, "Funicle");
                funicleComplete.add(classpath);
            }
        }
    }


}
