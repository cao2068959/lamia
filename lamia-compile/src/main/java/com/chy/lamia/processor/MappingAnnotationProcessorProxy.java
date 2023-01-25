package com.chy.lamia.processor;

import com.chy.lamia.utils.ReflectUtils;
import sun.misc.Unsafe;

import javax.annotation.processing.*;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

@SupportedAnnotationTypes({"com.chy.lamia.annotation.Mapping"})
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

    private Processor getMappingAnnotationProcessor(ProcessingEnvironment processingEnv){
        try {
            return  (Processor) Class.forName("com.chy.lamia.processor.MappingAnnotationProcessor", false, MappingAnnotationProcessorProxy.class.getClassLoader())
                    .getConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
                "com.sun.tools.javac.jvm"
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

}
