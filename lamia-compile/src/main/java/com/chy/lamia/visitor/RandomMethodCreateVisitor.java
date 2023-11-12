package com.chy.lamia.visitor;

import com.chy.lamia.convert.core.utils.CommonUtils;
import com.chy.lamia.utils.JCUtils;
import com.chy.lamia.utils.Lists;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

import java.util.List;

public class RandomMethodCreateVisitor extends TreeTranslator {

    String randomMethodName;
    JCUtils jcUtils = JCUtils.instance;
    String methodType;
    boolean isStatic = false;

    public RandomMethodCreateVisitor(String methodType, boolean isStatic) {
        this.methodType = methodType;
        this.isStatic = isStatic;
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl tree) {
        if (randomMethodName != null) {
            super.visitClassDef(tree);
            return;
        }
        String funicleMethodName = CommonUtils.generateVarName(methodType);
        List<JCTree.JCStatement> context = Lists.of(jcUtils.createReturnToStringType(funicleMethodName));
        JCTree.JCMethodDecl method = jcUtils.createMethod(funicleMethodName, "java.lang.String", isStatic,
                context, null);
        //添加方法
        tree.defs = tree.defs.prepend(method);
        this.randomMethodName = funicleMethodName;
        super.visitClassDef(tree);
    }

    public String getRandomMethodName() {
        return randomMethodName;
    }
}
