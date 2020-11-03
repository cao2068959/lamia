package com.chy.lamia.processor;

import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;


import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("com.chy.lamia.annotation.CopyBean")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class CopyBeanAnnotationProcessor extends AbstractProcessor {

    JavacElements elementUtils;

    TreeMaker treeMaker;

    JavacTrees trees;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        elementUtils = (JavacElements) processingEnv.getElementUtils();
        trees = (JavacTrees) Trees.instance(processingEnv);

    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        return true;
    }
}
