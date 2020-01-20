/*
 * Copyright 2019 lorislab.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
