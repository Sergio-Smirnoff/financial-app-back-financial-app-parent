package com.financialapp.commons.web.openapi;

import com.financialapp.commons.core.error.ErrorCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorCodes {
    Class<? extends ErrorCode> catalog();
    String[] value();
}
