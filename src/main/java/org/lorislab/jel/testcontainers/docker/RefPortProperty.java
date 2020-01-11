package org.lorislab.jel.testcontainers.docker;

public class RefPortProperty extends TestProperty {

    String service;

    String port;

    @Override
    String getValue(DockerTestEnvironment environment) {
        DockerComposeService dcs = environment.getService(service);
        return "" + dcs.getPort(Integer.parseInt(port));
    }

    static RefPortProperty createTestProperty(String name, String[] data) {
        RefPortProperty r = new RefPortProperty();
        r.name = name;
        r.service = data[1];
        r.port = data[2];
        return r;
    }
}
