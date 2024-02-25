package com.chy.lamia.processor;

import com.chy.lamia.components.CommonNameHandler;
import com.chy.lamia.components.JcTreeFactory;
import com.chy.lamia.components.JcTypeResolverFactory;
import com.chy.lamia.components.entity.JcExpression;
import com.chy.lamia.components.entity.JcStatement;
import com.chy.lamia.convert.core.ConvertFactory;
import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.NameHandler;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.TypeResolverFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.chy.lamia.convert.core.log.Logger;
import com.chy.lamia.convert.core.utils.ReflectUtils;
import com.chy.lamia.element.funicle.FunicleFactory;
import com.chy.lamia.processor.marked.MarkedContext;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.visitor.MethodUpdateVisitor;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class MappingAnnotationProcessor extends AbstractProcessor {

    private final MarkedContext markedContext = new MarkedContext();


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
                prepare(roundEnv);
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
        if (!markedContext.isEmpty()) {
            // 注册lamia的通用组件
            registerComponents();
        }

        markedContext.forEach((className, markedMethods) -> {
            JCTree tree = JCUtils.instance.getJCTree(className);
            //去修改原本方法中的逻辑
            tree.accept(new MethodUpdateVisitor(markedMethods, tree, className));
            //给这个类加上对应的脐带方法
            FunicleFactory.createFunicleMethod(tree, className);
        });

        FunicleFactory.persistence();
    }


    /**
     * 收集项目里所有类的 Element 对象
     * 同时把标注了 @Mapping 的方法 给存储下来
     *
     * @param roundEnv
     */
    private void prepare(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(LamiaMapping.class)) {
            Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) element;
            String key = methodSymbol.owner.toString();
            markedContext.put(key, methodSymbol);
        }
    }

    private void registerComponents() {
        ComponentFactory.registerComponents(TreeFactory.class, new JcTreeFactory(JCUtils.instance));
        ComponentFactory.registerComponents(NameHandler.class, new CommonNameHandler());

        ComponentFactory.registerInstanceComponents(ConvertFactory.INSTANCE, TypeResolverFactory.class, new JcTypeResolverFactory());

        ComponentFactory.registerEntityStructure(Expression.class, JcExpression::new);
        ComponentFactory.registerEntityStructure(Statement.class, JcStatement::new);

    }


}
