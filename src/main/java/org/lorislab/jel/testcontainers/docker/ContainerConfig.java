package org.lorislab.jel.testcontainers.docker;

import org.lorislab.jel.testcontainers.docker.properties.TestProperty;
import org.lorislab.jel.testcontainers.docker.properties.TestPropertyLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContainerConfig {

    public static final Integer DEFAULT_PRIORITY = 100;

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
        // docker compose environments
        environments = getMap(data, "environment");
        // docker compose volumes
        volumes = getMapFromList(data, "volumes", ":");
        // docker compose ports
        ports = getMapFromList(data, "ports", ":");

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

        // test properties store in the labels
        labels.forEach((k, v) -> {
            if (k.startsWith("test.property")) {
                String key = k.substring("test.property.".length());
                properties.add(TestPropertyLoader.createTestProperty(key, v));
            } else if (k.startsWith("test.env.")) {
                String key = k.substring("test.env.".length());
                refEnvironments.add(TestPropertyLoader.createTestProperty(key, v));
            }
        });
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
