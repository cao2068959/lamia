package com.chy.lamia.utils.struct;

import lombok.Data;

@Data
public class Pair<T, V> {

    T left;
    V right;

    public Pair(T left, V right) {
        this.left = left;
        this.right = right;
    }

    public static <T, V> Pair<T, V> of(T left, V right) {
        return new Pair<>(left, right);
    }

}
