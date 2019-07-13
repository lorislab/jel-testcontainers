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

import org.mockserver.client.ForwardChainExpectation;
import org.mockserver.mock.action.ExpectationResponseCallback;

import java.util.ArrayList;
import java.util.List;

public class MockForwardChainExpectation {

    private ForwardChainExpectation expectation;

    private List<MockExpectationResponseCallback> callbacks = new ArrayList<>();

    private int index = 0;

    public MockForwardChainExpectation(ForwardChainExpectation expectation) {
        this.expectation = expectation;
        this.expectation.respond((request) -> {
            int tmp = index;
            if (tmp < (callbacks.size()-1)) {
                index++;
            }
            return callbacks.get(tmp).handle(request);
        });
    }

    public MockForwardChainExpectation respond(String name, ExpectationResponseCallback callback, int callCount) {
        callbacks.add(new MockExpectationResponseCallback(name, callback, callCount));
        return this;
    }

    public MockForwardChainExpectation respond(String name, ExpectationResponseCallback callback) {
        return respond(name, callback, 1);
    }

    public List<MockExpectationResponseCallback> getCallbacks() {
        return callbacks;
    }
}
