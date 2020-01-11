package org.lorislab.jel.testcontainers.docker;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.shaded.org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DockerTestEnvironment {

    private static final Logger log = LoggerFactory.getLogger(DockerTestEnvironment.class);

    private Map<String, DockerComposeService> containers = new HashMap<>();

    private Map<Integer, List<DockerComposeService>> containerProperties = new HashMap<>();

    private Network network;

    public DockerTestEnvironment() {
        this(System.getProperty("test.docker.compose.file", "./src/test/resources/docker-compose.yml"));
    }

    public DockerTestEnvironment(String dockerComposeFile) {
        load(new File(dockerComposeFile));
    }

    public DockerComposeService getService(String name) {
        return containers.get(name);
    }

    public Network getNetwork() {
        return network;
    }

    public void load(File dockerComposeFile) {
        network = Network.newNetwork();

        boolean integrationTest = Boolean.getBoolean("test.integration");

        Yaml yaml = new Yaml();
        try (FileInputStream fileInputStream = FileUtils.openInputStream(dockerComposeFile)) {
            Map<String, Object> map = yaml.load(fileInputStream);
            Object services = map.get("services");
            if (services instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) services;
                data.forEach((k, v) -> {
                    ContainerConfig config = ContainerConfig.createContainerProperties(k, (Map<String, Object>) v);
                    if ((integrationTest && config.integrationTest) || (!integrationTest && config.unitTest)) {
                        DockerComposeService service = DockerComposeService.createDockerComposeService(network, config);
                        containerProperties.computeIfAbsent(service.getConfig().priority, x -> new ArrayList<>()).add(service);
                        containers.put(k, service);
                    }
                });
            }
        } catch (IOException e) {
            log.warn("Failed to read YAML from {}", dockerComposeFile.getAbsolutePath(), e);
        }
    }

    public void start() {
        List<Integer> priorities = new ArrayList<>(containerProperties.keySet());
        Collections.sort(priorities);

        priorities.forEach(p -> {
            List<DockerComposeService> services = containerProperties.get(p);
            List<String> names = services.stream().map(DockerComposeService::getName).collect(Collectors.toList());
            log.info("\n------------------------------\nStart test containers\npriority: {}\nServices: {}\n------------------------------", p, names);
            services.parallelStream().forEach(s -> s.start(this));
        });
    }

    public void stop() {
        containers.values().parallelStream().forEach(DockerComposeService::stop);
    }
}