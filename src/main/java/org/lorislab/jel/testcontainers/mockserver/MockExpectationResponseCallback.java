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

import java.util.concurrent.CountDownLatch;

import static org.mockserver.model.HttpRequest.request;

public class MockExpectationResponseCallback implements ExpectationResponseCallback {

    private static final Logger log = LoggerFactory.getLogger(MockExpectationResponseCallback.class);

    private ExpectationResponseCallback callback;

    private int count;

    private String name;

    private CountDownLatch countDownLatch;

    public MockExpectationResponseCallback(String name, ExpectationResponseCallback callback, int count) {
        this.callback = callback;
        this.count = count;
        this.countDownLatch = new CountDownLatch(count);
        this.name = name;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        countDownLatch.countDown();
        log.info("Wait for execution '{}' executed {}  expected {}", name, count - countDownLatch.getCount(), count);
        return callback.handle(request());
    }
}
