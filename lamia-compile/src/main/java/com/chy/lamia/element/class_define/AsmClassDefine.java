package com.chy.lamia.element.class_define;


import com.chy.lamia.convert.core.entity.Constructor;
import com.chy.lamia.convert.core.entity.Getter;
import com.chy.lamia.convert.core.entity.Setter;
import com.chy.lamia.convert.core.log.Logger;
import com.chy.lamia.convert.core.utils.ClassPath;
import com.chy.lamia.element.asm.ClassMetadataReadingVisitor;
import com.chy.lamia.entity.SimpleMethod;
import com.chy.lamia.entity.Var;
import com.chy.lamia.utils.JCUtils;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class AsmClassDefine implements IClassDefine {

    private final JCUtils jcUtils;
    private final String classPath;
    private final Class<?> leadClass;
    ClassMetadataReadingVisitor classMetadataReadingVisitor;


    public AsmClassDefine(JCUtils jcUtils, Class<?> leadClass) {
        this.jcUtils = jcUtils;
        this.classPath = leadClass.getName();
        this.leadClass = leadClass;
        classMetadataReadingVisitor = new ClassMetadataReadingVisitor();
        doParseClass(classMetadataReadingVisitor);

    }


    private void doParseClass(ClassMetadataReadingVisitor classMetadataReadingVisitor) {
        try {
            URL url = ClassPath.getPathFromClass(leadClass);
            ClassReader classReader = new ClassReader(url.openStream());
            classReader.accept(classMetadataReadingVisitor, 0);
        } catch (IOException e) {
            Logger.throwableLog(e);
            throw new RuntimeException("类 : [" + classPath + "] 无法解析");
        }
    }

    public List<SimpleMethod> getAllMethod() {
        return classMetadataReadingVisitor.getAllMethod();
    }


    @Override
    public Map<String, Var> getInstantVars() {
        return classMetadataReadingVisitor.getInstantVars();
    }

    @Override
    public Map<String, Getter> getInstantGetters() {
        return classMetadataReadingVisitor.getInstantGetters();
    }

    @Override
    public Map<String, Setter> getInstantSetters() {
        return classMetadataReadingVisitor.getInstantSetters();
    }

    @Override
    public List<Constructor> getConstructors() {
        return classMetadataReadingVisitor.getConstructors();
    }


}
