package org.lorislab.jel.testcontainers.docker.properties;

import org.lorislab.jel.testcontainers.docker.DockerComposeService;
import org.lorislab.jel.testcontainers.docker.DockerTestEnvironment;

public class RefUrlProperty extends TestProperty {

    String service;

    String port;

    @Override
    public String getValue(DockerTestEnvironment environment) {
        DockerComposeService dcs = environment.getService(service);
        return dcs.getUrl(Integer.parseInt(port));
    }

    public static RefUrlProperty createTestProperty(String name, String[] data) {
        RefUrlProperty r = new RefUrlProperty();
        r.name = name;
        r.service = data[1];
        r.port = data[2];
        return r;
    }

}
