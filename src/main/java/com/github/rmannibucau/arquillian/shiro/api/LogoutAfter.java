package com.github.rmannibucau.arquillian.shiro.api;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Force the subject to be logged out after the method.
 * It is only relevant when you use a custom SecurityManager.
 */
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
public @interface LogoutAfter {
}
