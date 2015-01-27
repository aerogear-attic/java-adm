/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.aerogear.adm;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * A builder class to construct a Payload message to be send to ADM Network
 *
 */
public class PayloadBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, Object> root;
    private final Map<String, Object> data;

    PayloadBuilder() {
        root = new HashMap<String, Object>();
        data = new HashMap<String, Object>();
    }

    /**
     * Adds an custom payload value for the given key.
     *
     * @param key of an user custom field
     * @param value of an user custom field
     * @return the current {@link org.jboss.aerogear.adm.PayloadBuilder} instance
     */
    public PayloadBuilder dataField(final String key, final Object value) {
        data.put(key, value);
        return this;
    }

    /**
     *
     * @param dataFields, a map containing custom key/value entries
     * @return the current {@link org.jboss.aerogear.adm.PayloadBuilder} instance
     */
    public PayloadBuilder dataFields(final Map<String, ?> dataFields) {
        data.putAll(dataFields);
        return this;
    }

    /**
     * This is an arbitrary string used to indicate that multiple messages are logically
     * the same and that ADM is allowed to drop previously enqueued messages in favor of
     * this new one
     *
     * @param value of the consolidation key
     * @return the current {@link org.jboss.aerogear.adm.PayloadBuilder} instance
     */
    public PayloadBuilder consolidationKey(final Object value) {
        root.put("consolidationKey", value);
        return this;
    }

    /**
     *The number of seconds that ADM should retain the message if the device is offline.
     * After this time, the message may be discarded
     *
     * @param value of the timeout (in seconds)
     * @return the current {@link org.jboss.aerogear.adm.PayloadBuilder} instance
     */
    public PayloadBuilder expiresAfter(final Object value) {
        root.put("expiresAfter", value);
        return this;
    }

    /**
     * This is a base-64-encoded MD5 checksum of the data parameter. If you provide a value
     * for the md5 parameter, ADM verifies its accuracy. If you do not provide a value,
     * the server calculates the value on your behalf
     *
     * @param value of the MD5
     * @return the current {@link org.jboss.aerogear.adm.PayloadBuilder} instance
     */
    public PayloadBuilder md5(final Object value) {
        root.put("md5", value);
        return this;
    }

    /**
     * Builds your complete payload
     *
     * @return a String representing your complete payload
     */
    public String build() {
        root.put("data", data);
        try {
            return mapper.writeValueAsString(root);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return build();
    }



}
