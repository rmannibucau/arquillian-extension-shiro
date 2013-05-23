package com.github.rmannibucau.arquillian.shiro;

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
public class ShiroTest {
    @Deployment
    public static Archive<?> war() {
        return ShrinkWrap.create(WebArchive.class, "app.war");
    }

    @Test
    public void checkASecurityManagerIsInstalled() {
        assertFalse(SecurityUtils.getSubject().isAuthenticated());
    }

    @Test
    public void checkUserCanLogIn() {
        final Subject subject = SecurityUtils.getSubject();
        {
            subject.login(new UsernamePasswordToken("admin", "adminpwd"));
            assertTrue(subject.isAuthenticated());
            subject.logout();
        }
        assertFalse(subject.isAuthenticated());

        {
            subject.login(new UsernamePasswordToken("user", "userpwd"));
            assertTrue(subject.isAuthenticated());
            subject.logout();
        }
        assertFalse(subject.isAuthenticated());
    }

    @Test
    public void checkUsersHavePermissions() {
        final Subject subject = SecurityUtils.getSubject();

        {
            subject.login(new UsernamePasswordToken("admin", "adminpwd"));
            assertTrue(subject.isPermitted("foo"));
            subject.logout();
        }
        {
            subject.login(new UsernamePasswordToken("user", "userpwd"));
            assertTrue(subject.isPermitted("user:foo"));
            assertFalse(subject.isPermitted("root:foo"));
            subject.logout();
        }
    }
}
