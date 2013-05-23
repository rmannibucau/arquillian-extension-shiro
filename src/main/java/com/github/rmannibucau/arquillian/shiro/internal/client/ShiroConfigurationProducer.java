package com.github.rmannibucau.arquillian.shiro.internal.client;

import com.github.rmannibucau.arquillian.shiro.internal.configuration.ShiroConfiguration;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;

import java.util.Map;

public class ShiroConfigurationProducer {
    private static final String EXTENSION_NAME = "shiro";

    @Inject
    private Instance<ArquillianDescriptor> descriptor;

    @Inject
    @ApplicationScoped
    private InstanceProducer<ShiroConfiguration> configuration;

    public void loadConfiguration(final @Observes BeforeSuite event) {
        configuration.set(createConfiguration(descriptor.get()));
    }

    private ShiroConfiguration createConfiguration(final ArquillianDescriptor arquillianDescriptor) {
        final ShiroConfiguration configuration = new ShiroConfiguration();

        for (final ExtensionDef def : arquillianDescriptor.getExtensions()) {
            if (EXTENSION_NAME.equals(def.getExtensionName())) {
                final Map<String,String> extensionProperties = def.getExtensionProperties();

                { // config file
                    final String ini = extensionProperties.get("ini");
                    if (ini != null) {
                        configuration.ini(ini);
                    }
                }
                { // use provided security manager
                    if ("true".equalsIgnoreCase(extensionProperties.get("auto-configured"))) {
                        configuration.autoConfigured();
                    }
                }
            }
        }

        return configuration;
    }
}
