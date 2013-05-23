package com.github.rmannibucau.arquillian.shiro.internal.shared;

import com.github.rmannibucau.arquillian.shiro.api.LoggedWithSubject;
import com.github.rmannibucau.arquillian.shiro.api.LogoutAfter;
import com.github.rmannibucau.arquillian.shiro.internal.configuration.ShiroConfiguration;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

import java.util.logging.Logger;

public class ShiroObserver {
    private static final Logger LOGGER = Logger.getLogger(ShiroObserver.class.getName());

    @Inject
    private Instance<ShiroConfiguration> configuration;

    public void beforeTest(final @Observes Before event) {
        if (!shouldRun(event)) {
            return;
        }

        String user = null;
        String ini = "";
        String password = null;
        String[] roles = null;
        {
            final LoggedWithSubject loggedWithSubject = Annotations.findAnnotation(LoggedWithSubject.class, event);
            if (loggedWithSubject != null) {
                user = loggedWithSubject.value();
                password = loggedWithSubject.password();
                ini = loggedWithSubject.ini();
                roles = loggedWithSubject.roles();
            }
        }

        // client side add the ini in the archive with the method name
        if (!ini.isEmpty()) {
            ini = "classpath:" + event.getTestMethod().getName() + ".ini";
        }

        Shiros.initSecurityContext(configuration.get(), ini, user, password, roles);
    }

    public void afterTest(final @Observes After event) {
        if (!shouldRun(event)) {
            return;
        }

        {
            final LogoutAfter logoutAfter = Annotations.findAnnotation(LogoutAfter.class, event);
            if (logoutAfter != null) {
                Shiros.logout();
            }
        }

        Shiros.resetSecurityContext(configuration.get());
    }

    protected boolean shouldRun(final TestEvent event) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("org.apache.shiro.SecurityUtils");
            return true;
        } catch (final ClassNotFoundException e) {
            LOGGER.info("Shiro not available");
            return false;
        }
    }
}
