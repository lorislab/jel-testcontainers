package org.lorislab.jel.testcontainers.docker;

public class RefUrlProperty extends TestProperty {

    String service;

    String port;

    @Override
    String getValue(DockerTestEnvironment environment) {
        DockerComposeService dcs = environment.getService(service);
        return dcs.getUrl(Integer.parseInt(port));
    }

    static RefUrlProperty createTestProperty(String name, String[] data) {
        RefUrlProperty r = new RefUrlProperty();
        r.name = name;
        r.service = data[1];
        r.port = data[2];
        return r;
    }

}
