package com.github.rmannibucau.arquillian.shiro.internal.container;

import com.github.rmannibucau.arquillian.shiro.internal.shared.ShiroObserver;
import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;

public class ShiroRemoteExtension implements RemoteLoadableExtension {
    @Override
    public void register(final ExtensionBuilder builder) {
        builder.observer(ShiroObserver.class)
                .observer(ShiroRemoteConfigurationProducer.class);
    }
}
