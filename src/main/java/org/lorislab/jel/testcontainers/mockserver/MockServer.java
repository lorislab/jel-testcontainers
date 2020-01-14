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

package org.lorislab.jel.testcontainers.mockserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MockServer {

    private static final Logger log = LoggerFactory.getLogger(MockServer.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writer();

    private List<MockForwardChainExpectation> expectations = new ArrayList<>();

    private MockServerClient mockServerClient;

    private long timeout;

    public MockServer(MockServerClient mockServerClient) {
        this(mockServerClient, 5);
    }

    private MockServer(MockServerClient mockServerClient, long timeout) {
        this.mockServerClient = mockServerClient;
        this.timeout = timeout;
    }

    public MockForwardChainExpectation when(HttpRequest request) {
        MockForwardChainExpectation e = new MockForwardChainExpectation(mockServerClient.when(request));
        expectations.add(e);
        return e;
    }

    public MockExpectationResponseCallback isFinished() {
        for (int i = 0; i < expectations.size(); i++) {
            MockForwardChainExpectation expectation = expectations.get(i);
            for (MockExpectationResponseCallback c : expectation.getCallbacks()) {
                CountDownLatch cd = c.getCountDownLatch();
                log.info("Check exception at index: {} name: {} executed: {} expected: {}", i, c.getName(), cd.getCount(), c.getCount());
                try {
                    log.info("Wait for mock callback '{}' timeout: {}s", c.getName(), timeout);
                    if (!cd.await(timeout, TimeUnit.SECONDS)) {
                        log.error("Waiting time elapsed before the count at index: {} name: {} executed: {} expected: {}", i, c.getName(), cd.getCount(), c.getCount());
                        return c;
                    }
                } catch (InterruptedException ex) {
                    log.error("Interrupted exception at index: {} name: {} executed: {} expected: {}", i, c.getName(), cd.getCount(), c.getCount());
                    return c;
                }
            }
        }
        return null;
    }

    public static HttpResponse withResponse() {
        return HttpResponse.response().withStatusCode(HttpStatusCode.OK_200.code());
    }

    public static HttpResponse withResponse(String resource) {
        return withResponse(resource, HttpStatusCode.OK_200);
    }

    public static HttpResponse withResponse(String resource, HttpStatusCode status) {
        return HttpResponse.response().withBody(loadResource(resource), JSON_UTF_8).withStatusCode(status.code());
    }

    public static HttpResponse withResponse(Object data) {
        return withResponse(data, HttpStatusCode.OK_200);
    }

    public static HttpResponse withResponse(Object data, HttpStatusCode status) {
        try {
            String body = OBJECT_WRITER.writeValueAsString(data);
            return HttpResponse.response().withBody(body, JSON_UTF_8).withStatusCode(status.code());
        } catch (Exception ex) {
            throw new RuntimeException("Error create a mock response body!", ex);
        }
    }

    private static String loadResource(String name) {
        try {
            URL url = MockServer.class.getResource(name);
            if (url != null) {
                Path path = Paths.get(url.toURI());
                return new String(Files.readAllBytes(path));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        throw new RuntimeException("Missing resource " + name);
    }
}
