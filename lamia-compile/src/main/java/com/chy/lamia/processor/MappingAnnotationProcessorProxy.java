package com.chy.lamia.processor;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;

import javax.annotation.processing.*;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes({"com.chy.lamia.convert.core.annotation.LamiaMapping"})
public class MappingAnnotationProcessorProxy extends AbstractProcessor {

    static Processor processor;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        addOpensForLombok();
        processor = getMappingAnnotationProcessor(processingEnv);
        processor.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return processor.process(annotations, roundEnv);
    }

    private Processor getMappingAnnotationProcessor(ProcessingEnvironment processingEnv) {
        try {
            return LinkerFactory.createStaticLinker(ProcessCreator.class, Class.forName("com.chy.lamia.processor.MappingAnnotationProcessor"))
                    .newInstance();
        } catch (LinkerException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private static void addOpensForLombok() {
        try {
            LModule module = LinkerFactory.createStaticLinker(LModule.class, Class.forName("java.lang.ModuleLayer"));
            JdkCompilerModule jdkCompilerModule = module.getJdkCompilerModule();
            String[] allPkgs = {
                    "com.sun.tools.javac.code",
                    "com.sun.tools.javac.comp",
                    "com.sun.tools.javac.file",
                    "com.sun.tools.javac.main",
                    "com.sun.tools.javac.model",
                    "com.sun.tools.javac.parser",
                    "com.sun.tools.javac.processing",
                    "com.sun.tools.javac.tree",
                    "com.sun.tools.javac.util",
                    "com.sun.tools.javac.jvm"
            };
            for (String pkg : allPkgs) {
                jdkCompilerModule.implAddOpens(pkg);
            }
        } catch (LinkerException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            return;
        }
    }

    interface JdkCompilerModule {
        @Method.Expr("implAddOpens($0, class('com.chy.lamia.processor.MappingAnnotationProcessor').getModule())")
        void implAddOpens(String pn);
    }

    @Autolink
    interface LModule {
        @Method.Expr("boot().findModule('jdk.compiler').get()")
        JdkCompilerModule getJdkCompilerModule();
    }

    interface ProcessCreator extends Processor {
        @Method.Constructor
        ProcessCreator newInstance();
    }
}
