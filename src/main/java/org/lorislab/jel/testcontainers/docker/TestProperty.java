package org.lorislab.jel.testcontainers.docker;

public abstract class TestProperty{

    String name;

    abstract String getValue(DockerTestEnvironment environment);

    @FunctionalInterface
    interface TestPropertyBuilder {
        TestProperty createTestProperty(String name, String[] data);
    }
}
