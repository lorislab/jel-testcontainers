# jel-testcontainers

JEL testcontainers library for the testcontainers framework

[![License](https://img.shields.io/github/license/lorislab/jel-testcontainers?style=for-the-badge&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)
[![CircleCI](https://img.shields.io/circleci/build/github/lorislab/jel-testcontainers?logo=circleci&style=for-the-badge)](https://circleci.com/gh/lorislab/jel-testcontainers)
[![Maven Central](https://img.shields.io/maven-central/v/org.lorislab.jel/jel-testcontainers?logo=java&style=for-the-badge)](https://maven-badges.herokuapp.com/maven-central/org.lorislab.jel/jel-testcontainers)
[![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/lorislab/jel-testcontainers?logo=github&style=for-the-badge)](https://github.com/lorislab/jel-testcontainers/releases/latest)

## Pipeline and tests

1. Build project, run the unit test and build native image: 
    * mvn clean package -Pnative (1) 
2. Build the docker image
    * docker build
3. Run the integration test
    * mvn failsafe:integration-test
4. Push the docker image 
    * docker push
    
(1) build native image with a docker image: 
    * mvn clean package -Pnative -Dquarkus.native.container-build=true        
    
For the pipeline build you can also use the [samo] (https://github.com/lorislab/samo/) cli utility which has these helpful build shortcuts:
* samo maven set-hash
* samo maven docker-build
* samo maven docker-push
 

## How to write the tests

Create abstract test class which will set up the docker test environment. The default location of the docker compose file
is `src/test/resources/docker-compose.yaml`

```java
public abstract class AbstractTest {
    // load the docker compose file from src/test/resources/docker-compose.yaml
    public static DockerTestEnvironment ENVIRONMENT = new DockerTestEnvironment();
     // Starts the containers before the tests
    static {        
        // star the docker test environment
        ENVIRONMENT.start();
        // update the rest assured port for the integration test
        DockerComposeService service = ENVIRONMENT.getService("p6-executor");
        if (service != null) {
            RestAssured.port = service.getPort(8080);
        }
    }
}
```
Create a common test for unit and integration test
```java
public class DeploymentRestControllerT extends AbstractTest {

    @Test
    public void deploymentTest() {
        // ...        
    }
}
```
Unit test
```java
@QuarkusTest
public class DeploymentRestControllerTest extends DeploymentRestControllerT {

}
```
Integration test
```java
public class DeploymentRestControllerTestIT extends DeploymentRestControllerT {

}
```

## Maven settings
Unit test maven plugin
```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>${surefire-plugin.version}</version>
    <configuration>
        <systemProperties>
            <com.arjuna.ats.arjuna.objectstore.objectStoreDir>${project.build.directory}/jta</com.arjuna.ats.arjuna.objectstore.objectStoreDir>
            <ObjectStoreEnvironmentBean.objectStoreDir>${project.build.directory}/jta</ObjectStoreEnvironmentBean.objectStoreDir>
            <java.util.logging.manager>org.jboss.logmanager.LogManager</java.util.logging.manager>
        </systemProperties>
    </configuration>
</plugin>
```
Integration test maven plugin
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>${surefire-plugin.version}</version>
    <executions>
        <execution>
            <id>native</id>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
            <phase>integration-test</phase>
        </execution>
    </executions>
    <configuration>
        <systemPropertyVariables>
            <test.integration>true</test.integration>
        </systemPropertyVariables>
    </configuration>                
</plugin>
```
The system property `<test.integration>true</test.integration>` activate the integration test.

## Docker labels

| label   | values | default | description |
|---|---|---|---|
| test.integration=true | `boolean` | `true` | enable the docker for the integration test |
| test.unit=true | `boolean` | `true` | enable the docker for the unit test |
| test.image.pull=true | `boolean` | `true` | pull docker image before test |
| test.Wait.forLogMessage.regex= | `string` | `null` | regex of the WaitStrategy for log messages |
| test.Wait.forLogMessage.times=1 | `int` | `1` | the number of times the pattern is expected in the WaitStrategy |
| test.Log=true | `boolean` | `true` | enabled log of the docker container |
| test.priority=100 | `int` | `100` | start priority |
| test.property.<name>=<value> | `string` | `null` | set the system property with <name> and <value> in the tests |
| test.env.<name>=<value> | `string` | `null` | set the environment variable with <name> and <value> in the docker container |

The value of the test.property.* or test.env.* supported this syntax:
* simple value: `123` result: 123
* host of the service: `${host:<service>}` the host of the service `<service>`
* port of the service: `${port:<service>:<port>}` the port number of the `<port>` of the `<service>` service
* url of the service: `${url:<service>:<port>}` the url of the service `http://<service>:<port>`
 
 Example:
 ```bash
test.property.quarkus.datasource.url=jdbc:postgresql://${host:postgres}:${port:postgres:5432}/p6?sslmode=disable
```
The system property `quarkus.datasource.url` will be set to 
`jdbc:postgresql://localhost:125432/p6?sslmode=disable` if the docker image host of the 
postgres is `localhost` and tet containers dynamic port ot the container port `5432` is set to
`125432` value.

## Docker compose example

```yaml
version: "2"
services:
  p6-executor-postgres:
    container_name: p6-executor-postgres
    image: postgres:10.5
    environment:
      POSTGRES_DB: "p6"
      POSTGRES_USER: "p6"
      POSTGRES_PASSWORD: "p6"
    labels:
      - "test.Wait.forLogMessage.regex=.*database system is ready to accept connections.*\\s"
      - "test.Wait.forLogMessage.times=2"
      - "test.log=true"
      - "test.property.quarkus.datasource.url=jdbc:postgresql://${host:p6-executor-postgres}:${port:p6-executor-postgres:5432}/p6?sslmode=disable"
      - "test.property.quarkus.infinispan-client.server-list=${host:p6-executor-infinispan}:${port:p6-executor-infinispan:11222}"
    ports:
      - "5433:5433"
    networks:
      - test
  p6-executor-infinispan:
    container_name: p6-executor-infinispan
    image: quay.io/lorislab/infinispan-quarkus-server:27102019
    ports:
      - "11222:11222"
    labels:
      - "test.Wait.forLogMessage.regex=.*Infinispan Server.*started in.*"
      - "test.Wait.forLogMessage.times=1"
      - "test.log=true"
    networks:
      - test
  p6-executor:
    container_name: p6-executor
    image: quay.io/p6-process/p6-executor:latest
    ports:
      - "8080:8080"
    labels:
      - "test.unit=false"
      - "test.priority=101"
      - "test.image.pull=false"
      - "test.env.QUARKUS_DATASOURCE_URL=jdbc:postgresql://p6-executor-postgres:5432/p6?sslmode=disable"
      - "test.env.QUARKUS_INFINISPAN_CLIENT_SERVER_LIST=p6-executor-infinispan:11222"
    networks:
      - test
networks:
  test:
```

## Release process

Create new release run
```bash
mvn semver-release:release-create
```

Create new patch branch run
```bash
mvn semver-release:patch-create -DpatchVersion=X.X.0
```
