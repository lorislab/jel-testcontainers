package org.lorislab.jel.testcontainers.docker.properties;

import org.lorislab.jel.testcontainers.docker.DockerTestEnvironment;

public class TestEnvProperty extends TestProperty {

    String key;

    @Override
    public String getValue(DockerTestEnvironment environment) {
      return System.getenv(key);
    }

    public static TestEnvProperty createTestProperty(String name, String[] data) {
        TestEnvProperty r = new TestEnvProperty();
        r.name = name;
        r.key = data[1];
        return r;
    }
}
