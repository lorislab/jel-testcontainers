package org.lorislab.jel.testcontainers.docker;

public class RefHostProperty extends TestProperty {

    String service;

    @Override
    String getValue(DockerTestEnvironment environment) {
        DockerComposeService dcs = environment.getService(service);
        return dcs.getHost();
    }

    static RefHostProperty createTestProperty(String name, String[] data) {
        RefHostProperty r = new RefHostProperty();
        r.name = name;
        r.service = data[1];
        return r;
    }
}
