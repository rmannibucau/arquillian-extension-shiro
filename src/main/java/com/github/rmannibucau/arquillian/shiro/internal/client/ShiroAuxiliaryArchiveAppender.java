package com.github.rmannibucau.arquillian.shiro.internal.client;

import com.github.rmannibucau.arquillian.shiro.api.LoggedWithSubject;
import com.github.rmannibucau.arquillian.shiro.internal.configuration.ShiroConfiguration;
import com.github.rmannibucau.arquillian.shiro.internal.container.ShiroRemoteExtension;
import com.github.rmannibucau.arquillian.shiro.internal.shared.ShiroObserver;
import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.CachedAuxilliaryArchiveAppender;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class ShiroAuxiliaryArchiveAppender extends CachedAuxilliaryArchiveAppender {
    @Inject
    private Instance<ShiroConfiguration> configuration;

    @Override
    protected Archive<?> buildArchive() {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "arquillian-extension-shiro.jar")
                .addAsServiceProvider(RemoteLoadableExtension.class, ShiroRemoteExtension.class)
                .addPackage(ShiroRemoteExtension.class.getPackage())
                .addPackage(ShiroObserver.class.getPackage())
                .addPackage(ShiroConfiguration.class.getPackage())
                .addPackage(LoggedWithSubject.class.getPackage());


        final ShiroConfiguration config = configuration.get();
        if (config != null) {
            jar.addAsResource(new StringAsset(config.asString()), ShiroConfiguration.REMOTE_FILE);
            if (config.getIni() != null) {
                jar.addAsResource(new StringAsset(IOs.slurp(config.getIni())), ShiroConfiguration.DEFAULT_INI_FILE);
            }
        }

        return jar;
    }
}
