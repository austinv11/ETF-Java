package com.austinv11.etf.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for a field's setter method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SetterMethod {
	
	/**
	 * This field's name.
	 *
	 * @return The field's name.
	 */
	String value();
}
