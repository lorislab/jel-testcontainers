package org.lorislab.jel.testcontainers.docker.properties;

import org.lorislab.jel.testcontainers.docker.DockerTestEnvironment;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestGroupProperty extends TestProperty {

    MessageFormat message;

    List<TestProperty> testProperties = new ArrayList<>();

    @Override
    public String getValue(DockerTestEnvironment environment) {
        List<String> parameters = testProperties.stream().map(c -> c.getValue(environment)).collect(Collectors.toList());
        return message.format(parameters.toArray(new Object[]{}), new StringBuffer(), null).toString();
    }

    public static TestGroupProperty createTestProperty(String name, String data, List<TestProperty> testProperties) {
        TestGroupProperty r = new TestGroupProperty();
        r.name = name;
        r.message = new MessageFormat(data);
        r.testProperties = testProperties;
        return r;
    }
}
