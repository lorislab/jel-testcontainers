package org.lorislab.jel.testcontainers.docker;

public class TestValueProperty extends TestProperty {

    String value;

    @Override
    String getValue(DockerTestEnvironment environment) {
      return value;
    }

    public static TestValueProperty createTestProperty(String name, String data) {
        TestValueProperty r = new TestValueProperty();
        r.name = name;
        r.value = data;
        return r;
    }
}
