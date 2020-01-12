package org.lorislab.jel.testcontainers.docker.properties;

import org.lorislab.jel.testcontainers.docker.DockerComposeService;
import org.lorislab.jel.testcontainers.docker.DockerTestEnvironment;

public class RefHostProperty extends TestProperty {

    String service;

    @Override
    public String getValue(DockerTestEnvironment environment) {
        DockerComposeService dcs = environment.getService(service);
        return dcs.getHost();
    }

    public static RefHostProperty createTestProperty(String name, String[] data) {
        RefHostProperty r = new RefHostProperty();
        r.name = name;
        r.service = data[1];
        return r;
    }
}
