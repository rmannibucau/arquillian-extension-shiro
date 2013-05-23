package com.github.rmannibucau.arquillian.shiro.internal.client;

import com.github.rmannibucau.arquillian.shiro.internal.shared.ShiroObserver;
import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.test.impl.RunModeUtils;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;

public class ShiroClientObserver extends ShiroObserver {
    @Inject
    private Instance<Container> container;

    protected boolean shouldRun(final TestEvent event) {
        return super.shouldRun(event) && RunModeUtils.isLocalContainer(container.get());
    }
}
