package com.chy.lamia.reporter;

import com.chy.lamia.context.LamiaContext;
import com.chy.lamia.convert.core.log.Logger;
import com.chy.lamia.convert.core.utils.struct.Pair;
import com.chy.lamia.exception.IgnoreException;
import com.sun.tools.javac.tree.JCTree;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class Reporter {

    public static Messager messager;


    public static void reportException(RuntimeException exception, JCTree tree) {
        Logger.throwableLog(exception);
        try {
            sendMessager(exception, tree);
        } catch (Exception e) {
            if (e instanceof IgnoreException) {
                throw e;
            }
            Logger.throwableLog(e);
            throw exception;
        }
    }

    public static void sendMessager(Exception exception, JCTree expression) {
        if (exception instanceof IgnoreException) {
            return;
        }
        String filePath = LamiaContext.getCurrentJavaFileObject().getName();
        Pair<Long, Long> currentPosition = LamiaContext.getCurrentPosition(expression);
        StringBuilder msg = new StringBuilder(filePath);
        msg.append(":[").append(currentPosition.getLeft()).append(",")
                .append(currentPosition.getRight()).append("] ")
                .append("错误: ").append(exception.getMessage());
        messager.printMessage(Diagnostic.Kind.ERROR, msg.toString());
        throw new IgnoreException();
    }

}
