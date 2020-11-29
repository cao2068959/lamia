package com.chy.lamia.entity;

import com.sun.tools.javac.util.List;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SunList<T> {

    List<T> list;

    public SunList(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

    public int size() {
        return list.size();
    }

    public void forEach(Consumer<T> action) {
        list.forEach((v) -> {
            action.accept(v);
        });
    }

}
