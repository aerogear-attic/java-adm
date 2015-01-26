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


public class PayloadBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, Object> root;
    private final Map<String, Object> data;

    PayloadBuilder() {
        root = new HashMap<String, Object>();
        data = new HashMap<String, Object>();
    }

    public PayloadBuilder dataField(final String key, final Object value) {
        data.put(key, value);
        return this;
    }

    public PayloadBuilder dataFields(final Map<String, ?> values) {
        data.putAll(values);
        return this;
    }

    public PayloadBuilder consolidationKey(final Object value) {
        root.put("consolidationKey", value);
        return this;
    }

    public PayloadBuilder expiresAfter(final Object value) {
        root.put("expiresAfter", value);
        return this;
    }

    public PayloadBuilder md5(final Object value) {
        root.put("md5", value);
        return this;
    }

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
