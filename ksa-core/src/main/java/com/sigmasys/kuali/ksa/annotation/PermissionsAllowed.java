package com.sigmasys.kuali.ksa.annotation;

import com.sigmasys.kuali.ksa.model.security.Permission;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionsAllowed {

    Permission[] value();

}
