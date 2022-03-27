
package com.zestic.system.util;

import com.zestic.system.annotation.concurrent.NotThreadSafe;

import java.util.Properties;

/*
 * The global configuration utility. See
 * {@code src/main/resources/com.zestic.system.properties} for default values.
 * <p>
 * This class is not thread safe if methods manipulating the configuration are
 * used. These methods are intended for use by a single thread at startup,
 * before instantiation of any other OSHI classes. OSHI does not guarantee re-
 * reading of any configuration changes.
 */
@NotThreadSafe public final class GlobalConfig {

    private static final String OSHI_PROPERTIES = "com.zestic.system.properties";

    private static final Properties CONFIG = FileUtil.readPropertiesFromFilename(OSHI_PROPERTIES);

    private GlobalConfig() {
    }

    /*
     * Get the {@code String} property associated with the given key.
     *
     * @param key The property key
     * @param def The default value
     * @return The property value or the given default if not found
     */
    public static String get(String key, String def) {
        return CONFIG.getProperty(key, def);
    }

    /*
     * Get the {@code int} property associated with the given key.
     *
     * @param key The property key
     * @param def The default value
     * @return The property value or the given default if not found
     */
    public static int get(String key, int def) {
        String value = CONFIG.getProperty(key);
        return value == null ? def : ParseUtil.parseIntOrDefault(value, def);
    }

    /*
     * Get the {@code double} property associated with the given key.
     *
     * @param key The property key
     * @param def The default value
     * @return The property value or the given default if not found
     */
    public static double get(String key, double def) {
        String value = CONFIG.getProperty(key);
        return value == null ? def : ParseUtil.parseDoubleOrDefault(value, def);
    }

    /*
     * Get the {@code boolean} property associated with the given key.
     *
     * @param key The property key
     * @param def The default value
     * @return The property value or the given default if not found
     */
    public static boolean get(String key, boolean def) {
        String value = CONFIG.getProperty(key);
        return value == null ? def : Boolean.parseBoolean(value);
    }

    /*
     * Set the given property, overwriting any existing value. If the given value is
     * {@code null}, the property is removed.
     *
     * @param key The property key
     * @param val The new value
     */
    public static void set(String key, Object val) {
        if (val == null) {
            CONFIG.remove(key);
        } else {
            CONFIG.setProperty(key, val.toString());
        }
    }

    /*
     * Reset the given property to its default value.
     *
     * @param key The property key
     */
    public static void remove(String key) {
        CONFIG.remove(key);
    }

    /*
     * Clear the configuration.
     */
    public static void clear() {
        CONFIG.clear();
    }

    /*
     * Load the given {@link Properties} into the global configuration.
     *
     * @param properties The new properties
     */
    public static void load(Properties properties) {
        CONFIG.putAll(properties);
    }

    /*
     * Indicates that a configuration value is invalid.
     */
    public static class PropertyException extends RuntimeException {

        private static final long serialVersionUID = -7482581936621748005L;

        /*
         * @param property The property name
         */
        public PropertyException(String property) {
            super("Invalid property: \"" + property + "\" = " + GlobalConfig.get(property, null));
        }

        /*
         * @param property The property name
         * @param message  An exception message
         */
        public PropertyException(String property, String message) {
            super("Invalid property \"" + property + "\": " + message);
        }
    }
}
