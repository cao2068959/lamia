package com.chy.lamia.context;

import com.chy.lamia.convert.core.utils.struct.Pair;
import com.chy.lamia.utils.JCUtils;
import com.sun.source.tree.LineMap;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree;

import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;

public class LamiaContext {

    public static Element currentElement;


    public static JavaFileObject getCurrentJavaFileObject() {
        TreePath path = JCUtils.instance.trees.getPath(currentElement);
        return path.getCompilationUnit().getSourceFile();
    }

    public static Pair<Long, Long> getCurrentPosition(JCTree.JCExpression expression) {
        TreePath path = JCUtils.instance.trees.getPath(currentElement);
        LineMap lineMap = path.getCompilationUnit().getLineMap();
        int startPosition = expression.getStartPosition();
        long lineNumber = lineMap.getLineNumber(startPosition);
        long columnNumber = lineMap.getColumnNumber(startPosition);
        return new Pair<>(lineNumber, columnNumber);
    }

}
