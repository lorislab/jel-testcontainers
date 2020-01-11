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

import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockserver.model.HttpRequest.request;

public class MockExpectationResponseCallback implements ExpectationResponseCallback {

    private static final Logger log = LoggerFactory.getLogger(MockExpectationResponseCallback.class);

    private ExpectationResponseCallback callback;

    private int executed = 0;

    private int expected;

    private String name;

    public MockExpectationResponseCallback(String name, ExpectationResponseCallback callback, int expected) {
        this.callback = callback;
        this.expected = expected;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getExecuted() {
        return executed;
    }

    public int getExpected() {
        return expected;
    }

    public boolean isFinished() {
        boolean result = executed == expected;
        if (!result) {
            log.info("The exception: {} does not finished yet. Executed: {} expected: {}", name, executed, expected);
        }
        return result;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        executed = executed+1;
        log.info("Execute response {}  {}", executed, expected);
        return callback.handle(request());
    }
}
