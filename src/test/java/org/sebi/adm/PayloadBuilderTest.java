/**
 * Copyright Sébastien Blanc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sebi.adm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;


public class PayloadBuilderTest {

    @Test
    public void testEmpty() {
        final PayloadBuilder builder = new PayloadBuilder();

        final String expected = "{\"data\":{}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void testConsolidationKey() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.consolidationKey("SyncNow");
        final String expected = "{\"consolidationKey\":\"SyncNow\",\"data\":{}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void testExpirationAfter() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.dataField("custom","custom");
        final String expected = "{\"data\":{\"custom\":\"custom\"}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }

    @Test
    public void testDataField() {
        final PayloadBuilder builder = new PayloadBuilder();
        builder.expiresAfter(86400);
        final String expected = "{\"expiresAfter\":86400,\"data\":{}}";
        final String actual = builder.toString();
        assertEqualsJson(expected, actual);
    }


    private void assertEqualsJson(final String expected, final String actual) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            @SuppressWarnings("unchecked")
            final
            Map<String, Object> exNode = mapper.readValue(expected, Map.class),
                    acNode = mapper.readValue(actual, Map.class);
            assertEquals(exNode, acNode);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
