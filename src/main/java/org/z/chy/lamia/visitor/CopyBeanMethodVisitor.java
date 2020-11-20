package org.z.chy.lamia.visitor;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;


public class CopyBeanMethodVisitor extends TreeTranslator {

    TreeMaker treeMaker;
    JavacElements elementUtils;

    public CopyBeanMethodVisitor(TreeMaker treeMaker, JavacElements elementUtils) {
        this.treeMaker = treeMaker;
        this.elementUtils = elementUtils;
    }


    @Override
    public void visitMethodDef(JCTree.JCMethodDecl tree) {
        List<JCTree.JCVariableDecl> parameters = tree.getParameters();
        for (JCTree.JCVariableDecl parameter : parameters) {
            parameter.accept(new ParameterVisitor());
        }

        super.visitMethodDef(tree);
    }
}
