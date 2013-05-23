package com.github.rmannibucau.arquillian.shiro;

import com.github.rmannibucau.arquillian.shiro.api.LoggedWithSubject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class ShiroArquillianAPITest {
    @Deployment
    public static Archive<?> war() {
        return ShrinkWrap.create(WebArchive.class, "app.war");
    }

    @Test
    @LoggedWithSubject("arquillian")
    public void checkUserIsLogged() {
        final Subject subject = SecurityUtils.getSubject();
        assertTrue(subject.isAuthenticated());
        assertFalse(subject.isPermitted("foo"));
    }

    @Test
    @LoggedWithSubject(value = "arquillian", roles = "*")
    public void checkUserHasRoles() {
        final Subject subject = SecurityUtils.getSubject();
        assertTrue(subject.isAuthenticated());
        assertTrue(subject.isPermitted("foo"));
    }

    @Test
    @LoggedWithSubject(value = "foo", password = "bar", ini = "classpath:custom-shiro.ini")
    public void checkOverridedCustomIni() {
        final Subject subject = SecurityUtils.getSubject();
        assertTrue(subject.isAuthenticated());
        assertFalse(subject.isPermitted("test:ini"));

        // we are log but check it is with the right user
        subject.logout();
        subject.login(new UsernamePasswordToken("foo", "bar"));
    }

    @Test
    @LoggedWithSubject(value = "jboss", password = "arquillian", ini = "classpath:custom-shiro.ini")
    public void checkCustomIni() {
        final Subject subject = SecurityUtils.getSubject();
        assertTrue(subject.isAuthenticated());
        assertTrue(subject.isPermitted("test:ini"));

        subject.logout();
        subject.login(new UsernamePasswordToken("jboss", "arquillian"));
    }
}
