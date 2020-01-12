package org.lorislab.jel.testcontainers.docker.properties;

import org.lorislab.jel.testcontainers.docker.DockerTestEnvironment;

public class TestValueProperty extends TestProperty {

    String value;

    @Override
    public String getValue(DockerTestEnvironment environment) {
      return value;
    }

    public static TestValueProperty createTestProperty(String name, String data) {
        TestValueProperty r = new TestValueProperty();
        r.name = name;
        r.value = data;
        return r;
    }
}
