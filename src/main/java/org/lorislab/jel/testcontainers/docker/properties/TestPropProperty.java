package org.lorislab.jel.testcontainers.docker.properties;

import org.lorislab.jel.testcontainers.docker.DockerTestEnvironment;

public class TestPropProperty extends TestProperty {

    String key;

    String defaultValue;

    @Override
    public String getValue(DockerTestEnvironment environment) {
      return System.getProperty(key, defaultValue);
    }

    public static TestPropProperty createTestProperty(String name, String[] data) {
        TestPropProperty r = new TestPropProperty();
        r.name = name;
        r.key = data[1];
        if (data.length > 2) {
            r.defaultValue = data[2];
        }
        return r;
    }
}
