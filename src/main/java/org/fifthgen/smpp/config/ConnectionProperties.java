package org.fifthgen.smpp.config;

public class ConnectionProperties {

    public int get(final String key, final int defaultFinal) {
        String property = System.getProperty(key);

        return property == null ? defaultFinal : Integer.parseInt(property);
    }
}
