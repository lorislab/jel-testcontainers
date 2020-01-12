package org.lorislab.jel.testcontainers.docker.properties;

import org.lorislab.jel.testcontainers.docker.DockerTestEnvironment;

public abstract class TestProperty {

    public String name;

    public abstract String getValue(DockerTestEnvironment environment);

}
