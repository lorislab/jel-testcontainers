package org.lorislab.jel.testcontainers.docker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ContainerConfig {

    static final Pattern REF_PATTERN = Pattern.compile("\\$\\{(.*?)}");

    public static final Integer DEFAULT_PRIORITY = 100;

    static final Map<String, TestProperty.TestPropertyBuilder> BUILDER = new HashMap<>();

    static {
        BUILDER.put("port", RefPortProperty::createTestProperty);
        BUILDER.put("host", RefHostProperty::createTestProperty);
        BUILDER.put("url", RefUrlProperty::createTestProperty);
    }

    public String name;

    public boolean integrationTest;

    public boolean unitTest;

    public boolean imagePull;

    public Integer priority = DEFAULT_PRIORITY;

    public String image;

    public String waitLogRegex;

    public int waitLogTimes;

    public boolean log;

    public Map<String, String> environments;

    public Map<String, String> volumes;

    public Map<String, String> ports;

    public List<TestProperty> properties = new ArrayList<>();

    public List<TestProperty> refEnvironments = new ArrayList<>();

    private ContainerConfig(String name, Map<String, Object> data) {
        this.name = name;
        load(data);
    }

    public static ContainerConfig createContainerProperties(String name, Map<String, Object> data) {
        return new ContainerConfig(name, data);
    }

    private void load(Map<String, Object> data) {

        // docker image
        image = (String) data.get("image");

        // labels
        Map<String, String> labels = getMapFromList(data, "labels", "=");
        if (!labels.isEmpty()) {
            // check if the service is only for the integration test
            integrationTest = getLabelBoolean(labels, "test.integration", true);
            unitTest = getLabelBoolean(labels, "test.unit", true);

            // image pull policy
            imagePull = getLabelBoolean(labels, "test.image.pull", true);

            // wait log rule
            waitLogRegex = labels.getOrDefault("test.Wait.forLogMessage.regex", null);
            waitLogTimes = getLabelInteger(labels, "test.Wait.forLogMessage.times", 1);

            // update log flag
            log = getLabelBoolean(labels, "test.Log", true);

            // update priority
            priority = getLabelInteger(labels, "test.priority", DEFAULT_PRIORITY);
        }

        environments = getMap(data, "environment");

        volumes = getMapFromList(data, "volumes", ":");

        ports = getMapFromList(data, "ports", ":");

        labels.forEach((k, v) -> {
            if (k.startsWith("test.property")) {
                String key = k.substring("test.property.".length());
                properties.add(createRefProperty(key, v));
            } else if (k.startsWith("test.env.")) {
                String key = k.substring("test.env.".length());
                refEnvironments.add(createRefProperty(key, v));
            }
        });
    }

    private TestProperty createRefProperty(String key, String value) {
        Matcher m = REF_PATTERN.matcher(value);
        if (m.find()) {
            String v = m.group(1);
            String[] data = v.split(":", 3);
            TestProperty.TestPropertyBuilder builder = BUILDER.get(data[0]);
            if (builder != null) {
                return builder.createTestProperty(key, data);
            }
            throw new IllegalStateException("Not supported type " + data[0]);
        }
        return TestValueProperty.createTestProperty(key, value);
    }

    private static Map<String, String> getMap(Map<String, Object> properties, String key) {
        Object map = properties.get(key);
        if (map instanceof Map) {
            return (Map<String, String>) map;
        }
        return Collections.emptyMap();
    }

    private static List<String> getList(Map<String, Object> properties, String key) {
        Object list = properties.get(key);
        if (list instanceof List) {
            return (List<String>) list;
        }
        return Collections.emptyList();
    }

    private static Map<String, String> getMapFromList(Map<String, Object> properties, String key, String regex) {
        List<String> list = getList(properties, key);
        if (!list.isEmpty()) {
            return list.stream().map(s -> s.split(regex, 2))
                    .collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : ""));
        }
        return Collections.emptyMap();
    }

    private static boolean getLabelBoolean(Map<String, String> labels, String name, boolean defaultValue) {
        return Boolean.parseBoolean(
                labels.getOrDefault(name, Boolean.toString(defaultValue))
        );
    }

    private static int getLabelInteger(Map<String, String> labels, String name, int defaultValue) {
        return Integer.parseInt(
                labels.getOrDefault(name, Integer.toString(defaultValue))
        );
    }
}
