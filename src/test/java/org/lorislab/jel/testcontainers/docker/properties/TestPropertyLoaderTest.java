package org.lorislab.jel.testcontainers.docker.properties;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.lorislab.jel.testcontainers.docker.DockerTestEnvironment;

public class TestPropertyLoaderTest {

    @Test
    public void testValueProperty() {
        DockerTestEnvironment env = new DockerTestEnvironment();
        String value = "123";
        String name = "name";
        TestProperty property = TestPropertyLoader.createTestProperty(name, value);
        Assertions.assertNotNull(property);
        Assertions.assertTrue(property instanceof TestValueProperty);
        TestValueProperty tv = (TestValueProperty) property;
        Assertions.assertEquals(name, tv.name);
        Assertions.assertEquals(value, tv.value);
        Assertions.assertEquals(value, tv.getValue(env));
    }

    @Test
    public void testGroupProperty() {
        String value1 = "XXX";
        System.setProperty("value1", value1);

        DockerTestEnvironment env = new DockerTestEnvironment();
        String value = "test ${prop:value1} should be ${prop:value1}";
        String name = "name";
        TestProperty property = TestPropertyLoader.createTestProperty(name, value);
        Assertions.assertNotNull(property);
        Assertions.assertTrue(property instanceof TestGroupProperty);
        TestGroupProperty tg = (TestGroupProperty) property;
        Assertions.assertEquals(name, tg.name);
        Assertions.assertEquals(2, tg.testProperties.size());
        String output = tg.getValue(env);
        Assertions.assertEquals("test " + value1 + " should be " + value1, output);
    }
}
