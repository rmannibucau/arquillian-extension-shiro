package com.github.rmannibucau.arquillian.shiro.internal.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class IOs {
    public static String slurp(final String resource) {
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (is == null) {
            return "";
        }

        return slurp(is);
    }

    public static String slurp(final InputStream is) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buffer = new byte[512];
        try {
            while ((is.read(buffer)) != -1) {
                baos.write(buffer);
            }
        } catch (final IOException e) {
            // no-op
        }
        return new String(baos.toByteArray());
    }

    private IOs() {
        // no-op
    }
}
