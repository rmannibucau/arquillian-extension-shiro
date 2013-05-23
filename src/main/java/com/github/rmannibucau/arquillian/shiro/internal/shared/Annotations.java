package com.github.rmannibucau.arquillian.shiro.internal.shared;

import org.jboss.arquillian.test.spi.event.suite.TestEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class Annotations {
    public static <T extends Annotation> T findAnnotation(final Class<T> annotation, final Class<?> clazz, final Method method) {
        if (method != null) {
            final T found = method.getAnnotation(annotation);
            if (found != null) {
                return found;
            }
        }
        return clazz.getAnnotation(annotation);
    }

    public static <T extends Annotation> T findAnnotation(final Class<T> annotation, final TestEvent event) {
        return findAnnotation(annotation, event.getTestClass().getJavaClass(), event.getTestMethod());
    }

    private Annotations() {
        // no-op
    }
}
