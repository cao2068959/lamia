package org.z.chy.lamia.utils;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;


public class JCUtils {

    public static JCTree.JCExpression memberAccess(TreeMaker treeMaker, JavacElements elementUtils, String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(elementUtils.getName(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, elementUtils.getName(componentArray[i]));
        }
        return expr;
    }


}
