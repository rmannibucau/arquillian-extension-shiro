package com.github.rmannibucau.arquillian.shiro.internal.shared;

import com.github.rmannibucau.arquillian.shiro.internal.configuration.ShiroConfiguration;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.util.ThreadContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

// only used to ensure lazy loading of classes
public final class Shiros {
    private static final ThreadLocal<org.apache.shiro.mgt.SecurityManager> oldSecurityManager = new ThreadLocal<SecurityManager>();

    private static final ThreadLocal<Subject> oldSubject = new ThreadLocal<Subject>();

    private static final String DEFAULT_PWD = "Arquillian20!3";
    private static final String ARQUILLIAN_ROLE = "_arquillian_";

    public static void initSecurityContext(final ShiroConfiguration configuration, final String iniPath, final String name, final String pwd, final String[] roles) {
        if (configuration.isConfigured()) {
            return;
        }

        oldSubject.set(ThreadContext.unbindSubject());
        oldSecurityManager.set(ThreadContext.getSecurityManager());

        String pwdToLogin = pwd;

        final Ini iniInstance;
        if (!iniPath.isEmpty()) {
            iniInstance = new Ini();
            iniInstance.loadFromPath(iniPath);
        } else if (configuration.getIni() != null) {
            iniInstance = new Ini();
            iniInstance.loadFromPath(configuration.getIni());
        } else {
            iniInstance = defaultIni();
        }

        if (name != null) {
            if (pwd.isEmpty()) {
                pwdToLogin = DEFAULT_PWD;
            } else {
                pwdToLogin = pwd;
            }

            updateIni(iniInstance, name, pwdToLogin, roles);
        }

        ThreadContext.bind(new NoImplicitIniSecurityManagerFactory(iniInstance).getInstance());

        if (pwdToLogin != null) {
            SecurityUtils.getSubject().login(new UsernamePasswordToken(name, pwdToLogin));
        }
    }

    private static Ini updateIni(final Ini ini, final String name, final String pwd, final String[] roles) {
        final boolean hasRoles = roles.length > 0;

        { // [users]
            Ini.Section users = ini.getSection("users");
            if (users == null) {
                users = ini.addSection("users");
            }

            final String existingRoles;
            if (users.containsKey(name)) {
                final String old = users.get(name);
                if (old.contains(",")) {
                    existingRoles = old.substring(old.indexOf(",") + 1);
                } else {
                    existingRoles = null;
                }
            } else {
                existingRoles = null;
            }

            if (hasRoles) {
                users.put(name, pwd + "," + ARQUILLIAN_ROLE);
            } else if (existingRoles != null) {
                users.put(name, pwd + "," + existingRoles);
            } else {
                users.put(name, pwd);
            }
        }
        { // [roles]
            if (hasRoles) {
                Ini.Section rolesSection = ini.getSection("roles");
                if (rolesSection == null) {
                    rolesSection = ini.addSection("roles");
                }

                final StringBuilder roleBuilder = new StringBuilder();
                for (int i = 0; i < roles.length; i++) {
                    roleBuilder.append(roles[i]);
                    if (i < roles.length - 1) {
                        roleBuilder.append(",");
                    }
                }
                rolesSection.put(ARQUILLIAN_ROLE, roleBuilder.toString());
            }

        }
        return ini;
    }

    public static void resetSecurityContext(final ShiroConfiguration configuration) {
        if (configuration.isConfigured()) {
            return;
        }

        LifecycleUtils.destroy(ThreadContext.getSecurityManager());
        ThreadContext.unbindSecurityManager();
        ThreadContext.unbindSubject();
        ThreadContext.bind(oldSubject.get());
        ThreadContext.bind(oldSecurityManager.get());
        oldSecurityManager.remove();
        oldSubject.remove();
    }

    public static void logout() {
        try {
            final Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated()) {
                subject.logout();
            }
        } catch (final Exception e) {
            // no-op: we swallow exceptions since that's just a cleanup step
        }
    }

    private static Ini defaultIni() {
        final Ini ini = new Ini();
        { // [users]
            final Ini.Section users = ini.addSection("users");
            users.put("admin", "adminpwd,admin");
            users.put("user", "userpwd,user");
        }
        { // [roles]
            final Ini.Section roles = ini.addSection("roles");
            roles.put("admin", "*");
            roles.put("user", "user:*");
        }
        return ini;
    }

    private Shiros() {
        // no-op
    }

    private static class NoImplicitIniSecurityManagerFactory extends IniSecurityManagerFactory {
        public NoImplicitIniSecurityManagerFactory(final Ini iniInstance) {
            super(iniInstance);
        }

        @Override
        protected void applyRealmsToSecurityManager(final Collection<Realm> realms, final SecurityManager securityManager) {
            if (realms.size() > 1) { // remove default one
                final Iterator<Realm> r = realms.iterator();
                while (r.hasNext()) {
                    if (IniRealm.class.isInstance(r.next())) {
                        r.remove();
                    }
                }
            }
            super.applyRealmsToSecurityManager(realms, securityManager);
        }
    }
}
