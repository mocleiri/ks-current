package com.sigmasys.kuali.ksa.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Url {

    public abstract String value() default "";

}