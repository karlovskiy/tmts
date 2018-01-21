package info.karlovskiy.tmts.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ServerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ServerConfiguration.class);

    private static final String CONFIGURATION_FILE_NAME = "tmts.properties";

    public static final String PORT = "port";
    public static final String MAX_THREADS = "max_threads";
    public static final String MIN_THREADS = "min_threads";
    public static final String IDLE_TIMEOUT_SECONDS = "idle_timeout_seconds";
    public static final String GZIP_ENABLED = "gzip_enabled";
    public static final String VERSION = "version";

    private static Properties config;

    public static String getProperty(String name, boolean optional) {
        String property = config.getProperty(name);
        if (!optional && property == null) {
            throw new IllegalArgumentException("Property " + name + " is mandatory and not found");
        }
        return property;
    }

    public static String getProperty(String name) {
        return getProperty(name, true);
    }

    public static int getIntProperty(String name) {
        String value = getProperty(name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Error parsing property " + name + " value " + value + " to int");
        }
    }

    public static boolean getOptionalBooleanProperty(String name) {
        String value = getProperty(name, true);
        return Boolean.parseBoolean(value);
    }

    public static void load() throws IOException {
        log.info("Start loading config");
        Properties properties = new Properties();
        String tmtsHome = System.getProperty("tmts.home");
        if (StringUtils.isNotEmpty(tmtsHome)) {
            File configFile = new File(tmtsHome, "conf" + File.separator + CONFIGURATION_FILE_NAME);
            if (configFile.exists()) {
                try (Reader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
                    properties.load(reader);
                    log.debug("Config loaded from {} :\n{}", configFile, properties);
                }
                config = properties;
            } else {
                throw new IllegalArgumentException("Config not found at " + configFile);
            }
        } else {
            InputStream in = ServerConfiguration.class.getResourceAsStream("/" + CONFIGURATION_FILE_NAME);
            if (in != null) {
                try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                    log.debug("Config loaded from classpath :\n{}", properties);
                }
            } else {
                throw new IllegalArgumentException("Config not found: use {tmts.home}/conf/tmts.properties file or tmts.properties resource from classpath");
            }
        }
        config = properties;
        log.info("Config loaded");
    }

    private ServerConfiguration() {
    }
}
