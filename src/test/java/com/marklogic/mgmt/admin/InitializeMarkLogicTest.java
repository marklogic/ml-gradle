/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.mgmt.admin;

import org.junit.jupiter.api.Test;

import com.marklogic.mgmt.AbstractMgmtTest;

public class InitializeMarkLogicTest extends AbstractMgmtTest {

    /**
     * The only way to really test this is to run it against a freshly installed MarkLogic, but in a test suite, we
     * always assume that we have a MarkLogic instance that has been initialized already. So this is just a smoke test
     * to ensure no errors are thrown from bad JSON.
     */
    @Test
    public void initAgainstAnAlreadyInitializedMarkLogic() {
        adminManager.init();
    }
}
