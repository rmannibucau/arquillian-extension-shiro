package com.github.rmannibucau.arquillian.shiro.internal.container;

import com.github.rmannibucau.arquillian.shiro.internal.configuration.ShiroConfiguration;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;

import java.io.InputStream;

public class ShiroRemoteConfigurationProducer {
    @Inject
    @ApplicationScoped
    private InstanceProducer<ShiroConfiguration> configuration;

    public void readConfiguration(final @Observes BeforeSuite event) {
        final ShiroConfiguration config = new ShiroConfiguration();
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(ShiroConfiguration.REMOTE_FILE);
        if (is != null) {
            config.fromInputStream(is);
        }
        configuration.set(config);
    }
}
