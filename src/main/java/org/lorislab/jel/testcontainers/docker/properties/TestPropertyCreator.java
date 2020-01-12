package org.lorislab.jel.testcontainers.docker.properties;

@FunctionalInterface
public interface TestPropertyCreator {
    TestProperty createTestProperty(String name, String[] data);
}
