package com.docomodigital.exerciseapi.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.docomodigital.exerciseapi.common.utils.ApiConstants;

/**
 * In cooperation with an aspect, this is a marker to log method inputs
 * 
 * @author Amanuel
 *
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
	String format() default ApiConstants.PARAMETER_2;
	String[] exclusions() default "";
}