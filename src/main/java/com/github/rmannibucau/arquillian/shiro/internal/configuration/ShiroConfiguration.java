package com.github.rmannibucau.arquillian.shiro.internal.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ShiroConfiguration {
    public static final String REMOTE_FILE = "arquillian-shiro.properties";
    public static final String DEFAULT_INI_FILE = "arquillian-shiro.ini";

    private String ini = null;
    private boolean configured = false;

    public void ini(String ini) {
        this.ini = ini;
    }

    public void autoConfigured() {
        configured = true;
    }

    public String getIni() {
        return ini;
    }

    public boolean isConfigured() {
        return configured;
    }

    public String asString() {
        final StringBuilder builder = new StringBuilder();

        if (ini != null) {
            builder.append("ini = " + true);
        }
        builder.append("auto-configured = ").append(configured);

        return builder.toString();
    }

    public void fromInputStream(final InputStream is) {
        final Properties props = new Properties();
        try {
            props.load(is);
        } catch (final IOException e) {
            // no-op
        }

        if ("true".equals(props.getProperty("ini", "false"))) {
            ini = "classpath:" + DEFAULT_INI_FILE;
        }
        configured = "true".equals(props.getProperty("auto-configured", "false"));
    }

    @Override
    public String toString() {
        return "ShiroConfiguration{" +
                    "ini='" + ini + '\'' +
                    "auto-configured='" + configured + '\'' +
                '}';
    }
}
