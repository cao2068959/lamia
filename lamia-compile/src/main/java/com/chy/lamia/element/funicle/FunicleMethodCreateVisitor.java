package com.chy.lamia.element.funicle;

import com.chy.lamia.convert.core.utils.CommonUtils;
import com.chy.lamia.entity.SimpleMethod;
import com.chy.lamia.utils.JCUtils;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FunicleMethodCreateVisitor extends TreeTranslator {

    JCUtils jcUtils = JCUtils.instance;
    String methodType = "FConnect";
    boolean complete = false;
    Map<String, SimpleMethod> simpleMethodMap;

    public FunicleMethodCreateVisitor(Map<String, SimpleMethod> simpleMethodMap) {
        this.simpleMethodMap = simpleMethodMap;
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl tree) {
        if (complete) {
            super.visitClassDef(tree);
            return;
        }
        String funicleMethodName = CommonUtils.generateVarName(methodType);
        List<JCTree.JCStatement> context = createConnectStatement();
        JCTree.JCMethodDecl method = jcUtils.createMethod(funicleMethodName, null, true,
                context, null);
        //添加方法
        tree.defs = tree.defs.prepend(method);
        this.complete = true;

        super.visitClassDef(tree);
    }


    /**
     * 生成对应的连接代码
     *
     * @return
     */
    private List<JCTree.JCStatement> createConnectStatement() {
        List<JCTree.JCStatement> result = new ArrayList<>();
        simpleMethodMap.forEach((classpath, simpleMethod) -> {
            JCTree.JCExpressionStatement jcExpressionStatement =
                    jcUtils.execMethod(classpath, simpleMethod.getName(), new LinkedList<>());
            result.add(jcExpressionStatement);
        });
        return result;
    }


}
