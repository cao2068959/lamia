package com.chy.lamia.element.funicle;

import com.chy.lamia.log.Logger;
import com.chy.lamia.utils.JCUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes({"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FunicleAnnotationProcessor extends AbstractProcessor {

    private Set<String> funiclePersistence = new HashSet<>();
    private Set<String> funicleComplete = new HashSet<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.funiclePersistence = FunicleFactory.readPersistence();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            ProcessingEnvironment processingEnv = ReflectUtils.getFile(roundEnv, "processingEnv", ProcessingEnvironment.class);
            JCUtils.refreshJCUtils(processingEnv);
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
