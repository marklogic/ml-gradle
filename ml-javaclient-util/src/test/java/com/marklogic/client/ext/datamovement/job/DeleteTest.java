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
package com.marklogic.client.ext.datamovement.job;

import com.marklogic.client.ext.datamovement.AbstractDataMovementTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DeleteTest extends AbstractDataMovementTest {

    @Test
    public void deleteBothDocs() {
        DeleteJob job = new DeleteJob();
        job.setWhereUris(FIRST_URI, SECOND_URI);

        assertNotNull(client.newDocumentManager().exists(FIRST_URI));
        assertNotNull(client.newDocumentManager().exists(SECOND_URI));

        job.run(client);

        assertNull(client.newDocumentManager().exists(FIRST_URI));
        assertNull(client.newDocumentManager().exists(SECOND_URI));
    }

    @Test
    public void deleteOneDoc() {
        DeleteJob job = new DeleteJob();
        job.setWhereUris(FIRST_URI);

        assertNotNull(client.newDocumentManager().exists(FIRST_URI));
        assertNotNull(client.newDocumentManager().exists(SECOND_URI));

        job.run(client);

        assertNull(client.newDocumentManager().exists(FIRST_URI));
        assertNotNull(client.newDocumentManager().exists(SECOND_URI));
    }
}
