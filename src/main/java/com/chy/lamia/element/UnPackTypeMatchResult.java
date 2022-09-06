package com.chy.lamia.element;

import com.chy.lamia.element.boxing.ExpressionFunction;

import java.util.List;

public class UnPackTypeMatchResult {

    boolean match;
    //装包需要从链尾开始
    List<ExpressionFunction> boxingFunChain;
    //解包从链头开始
    List<ExpressionFunction> unpackFunChain;

    public UnPackTypeMatchResult(boolean match) {
        this.match = match;
    }

    public UnPackTypeMatchResult(boolean match, List<ExpressionFunction> unpackFunChain) {
        this.match = match;
        this.unpackFunChain = unpackFunChain;
    }

    public UnPackTypeMatchResult(boolean match, List<ExpressionFunction> boxingFunChain, List<ExpressionFunction> unpackFunChain) {
        this.match = match;
        this.boxingFunChain = boxingFunChain;
        this.unpackFunChain = unpackFunChain;
    }


    public static UnPackTypeMatchResult matchSuccess(List<ExpressionFunction> unpackFunChain) {
        return new UnPackTypeMatchResult(true, unpackFunChain);
    }

    public static UnPackTypeMatchResult matchFail() {
        return new UnPackTypeMatchResult(false);
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public List<ExpressionFunction> getBoxingFunChain() {
        return boxingFunChain;
    }

    public void setBoxingFunChain(List<ExpressionFunction> boxingFunChain) {
        this.boxingFunChain = boxingFunChain;
    }

    public List<ExpressionFunction> getUnpackFunChain() {
        return unpackFunChain;
    }
}
