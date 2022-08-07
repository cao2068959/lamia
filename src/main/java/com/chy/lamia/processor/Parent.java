package com.chy.lamia.processor;

import java.io.OutputStream;

public class Parent {
	boolean first;
	static final Object staticObj = OutputStream.class;
	volatile Object second;
	private static volatile boolean staticSecond;
	private static volatile boolean staticThird;
}