package com.chy.lamia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.LOCAL_VARIABLE,ElementType.TYPE_USE})
@Retention(RetentionPolicy.SOURCE)
public @interface MapMember {
}
