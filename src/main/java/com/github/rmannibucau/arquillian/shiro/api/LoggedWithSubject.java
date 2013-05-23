package com.github.rmannibucau.arquillian.shiro.api;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use a mock shiro.ini describe by the attributes and log the defined user automatically.
 */
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
public @interface LoggedWithSubject {
    /**
     * @return name of the subject (login).
     */
    String value();

    /**
     * @return the login password, only relevant if using a custom config.
     */
    String password() default "";

    /**
     * @return ini file configuration.
     */
    String ini() default "";

    /**
     * @return roles of the subject.
     */
    String[] roles() default {};
}
