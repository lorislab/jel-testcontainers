package org.lorislab.jel.testcontainers.docker.properties;

import org.lorislab.jel.testcontainers.docker.DockerComposeService;
import org.lorislab.jel.testcontainers.docker.DockerTestEnvironment;

public class RefPortProperty extends TestProperty {

    String service;

    String port;

    @Override
    public String getValue(DockerTestEnvironment environment) {
        DockerComposeService dcs = environment.getService(service);
        return "" + dcs.getPort(Integer.parseInt(port));
    }

    public static RefPortProperty createTestProperty(String name, String[] data) {
        RefPortProperty r = new RefPortProperty();
        r.name = name;
        r.service = data[1];
        r.port = data[2];
        return r;
    }
}
