package com.chy.lamia.processor;

import com.chy.lamia.annotation.Mapping;
import com.chy.lamia.element.funicle.FunicleFactory;
import com.chy.lamia.log.Logger;
import com.chy.lamia.processor.marked.MarkedContext;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.ReflectUtils;
import com.chy.lamia.visitor.MethodUpdateVisitor;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import sun.misc.Unsafe;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public class MappingAnnotationProcessor extends AbstractProcessor {

    public MappingAnnotationProcessor(MarkedContext markedContext) {
        this.markedContext = markedContext;
    }

    public MappingAnnotationProcessor() {
    }

    private MarkedContext markedContext = new MarkedContext();

    static {
        addOpensForLombok();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            ProcessingEnvironment processingEnv = ReflectUtils.getFile(roundEnv, "processingEnv", ProcessingEnvironment.class);
            JCUtils.refreshJCUtils(processingEnv);

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
        JavacElements elementUtils = JCUtils.instance.getElementUtils();
        markedContext.forEach((className, markedMethods) -> {
            JCTree tree = elementUtils.getTree(elementUtils.getTypeElement(className));
            //去修改原本方法中的逻辑
            tree.accept(new MethodUpdateVisitor(markedMethods, JCUtils.instance, tree, className));
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

    private static Object getOwnModule() {
        try {
            Method m = ReflectUtils.getMethod(Class.class, "getModule");
            return m.invoke(MappingAnnotationProcessor.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getJdkCompilerModule() {
        try {
            Class<?> cModuleLayer = Class.forName("java.lang.ModuleLayer");
            Method mBoot = cModuleLayer.getDeclaredMethod("boot");
            Object bootLayer = mBoot.invoke(null);
            Class<?> cOptional = Class.forName("java.util.Optional");
            Method mFindModule = cModuleLayer.getDeclaredMethod("findModule", String.class);
            Object oCompilerO = mFindModule.invoke(bootLayer, "jdk.compiler");
            return cOptional.getDeclaredMethod("get").invoke(oCompilerO);
        } catch (Exception e) {
            return null;
        }
    }

    private static void addOpensForLombok() {
        Class<?> cModule;
        try {
            cModule = Class.forName("java.lang.Module");
        } catch (ClassNotFoundException e) {
            return; //jdk8-; this is not needed.
        }

        Unsafe unsafe = getUnsafe();
        Object jdkCompilerModule = getJdkCompilerModule();
        Object ownModule = getOwnModule();
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
                "com.sun.tools.javac.jvm",
        };

        try {
            Method m = cModule.getDeclaredMethod("implAddOpens", String.class, cModule);
            long firstFieldOffset = getFirstFieldOffset(unsafe);
            unsafe.putBooleanVolatile(m, firstFieldOffset, true);
            for (String p : allPkgs) {
                m.invoke(jdkCompilerModule, p, ownModule);
            }
        } catch (Exception ignore) {}
    }

    private static long getFirstFieldOffset(Unsafe unsafe) {
        try {
            return unsafe.objectFieldOffset(Parent.class.getDeclaredField("first"));
        } catch (NoSuchFieldException e) {
            // can't happen.
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            // can't happen
            throw new RuntimeException(e);
        }
    }

    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            return null;
        }
    }

}
