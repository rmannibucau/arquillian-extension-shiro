package com.github.rmannibucau.arquillian.shiro.internal.client;

import com.github.rmannibucau.arquillian.shiro.api.LoggedWithSubject;
import com.github.rmannibucau.arquillian.shiro.internal.shared.Annotations;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.subject.Subject;
import org.apache.ziplock.JarLocation;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.filter.IncludeRegExpPaths;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class ShiroArchiveProcessor implements ApplicationArchiveProcessor {
    private static final Logger LOGGER = Logger.getLogger(ShiroArchiveProcessor.class.getName());

    @Override
    public void process(final Archive<?> applicationArchive, final TestClass testClass) {
        if (!LibraryContainer.class.isInstance(applicationArchive)) {
            return;
        }

        addShiroIfMissing(applicationArchive);
        addCustomIni(applicationArchive, testClass);
    }

    private void addCustomIni(final Archive<?> applicationArchive, final TestClass testClass) {
        final Archive<?> iniArchive = ShrinkWrap.create(JavaArchive.class, "arquillian-shiro-ini.jar");

        int found = 0;
        final Class<?> javaClass = testClass.getJavaClass();

        for (final Method method : javaClass.getMethods()) {
            final LoggedWithSubject annotation = Annotations.findAnnotation(LoggedWithSubject.class, javaClass, method);
            if (annotation != null && !annotation.ini().isEmpty()) {
                try {
                    final InputStream is = ResourceUtils.getInputStreamForPath(annotation.ini());
                    iniArchive.add(new StringAsset(IOs.slurp(is)), method.getName() + ".ini");
                    found++;
                } catch (final IOException e) {
                    LOGGER.severe("Can't find " + annotation.ini());
                }
            }
        }


        if (found > 0) {
            LibraryContainer.class.cast(applicationArchive).addAsLibraries(iniArchive);
        }
    }

    private void addShiroIfMissing(final Archive<?> applicationArchive) {
        if (applicationArchive.getContent(new IncludeRegExpPaths("/WEB-INF/lib/shiro-core.*.jar")).size() > 0) {
            return;
        }

        try {
            LibraryContainer.class.cast(applicationArchive).addAsLibraries(JarLocation.jarLocation(Subject.class));
        } catch (final Exception e) {
            LOGGER.warning("Can't find shiro and it seems missing in the archive, ensure you added it manually");
        }
    }
}
