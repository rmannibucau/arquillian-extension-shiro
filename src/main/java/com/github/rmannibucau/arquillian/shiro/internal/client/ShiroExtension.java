package com.github.rmannibucau.arquillian.shiro.internal.client;

import com.github.rmannibucau.arquillian.shiro.internal.container.ShiroRemoteExtension;
import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class ShiroExtension implements LoadableExtension {
    @Inject
    private Instance<Container> container;

    @Override
    public void register(final ExtensionBuilder builder) {
        builder.service(AuxiliaryArchiveAppender.class, ShiroAuxiliaryArchiveAppender.class)
                .service(ApplicationArchiveProcessor.class, ShiroArchiveProcessor.class)
                .service(RemoteLoadableExtension.class, ShiroRemoteExtension.class)
                .observer(ShiroConfigurationProducer.class)
                .observer(ShiroClientObserver.class);
    }
}
