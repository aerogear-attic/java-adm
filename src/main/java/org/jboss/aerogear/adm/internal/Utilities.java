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

package org.jboss.aerogear.adm.internal;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public final class Utilities {

    public static final String APPLICATION_JSON = "application/json";
    public static final String AMAZON_TYPE_VERSION = "com.amazon.device.messaging.ADMMessage@1.0";
    public static final String AMAZON_ACCEPT_TYPE = "com.amazon.device.messaging.ADMSendResult@1.0";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public static final String ADM_URL_TEMPLATE = "https://api.amazon.com/messaging/registrations/%1$s/messages";
    public static final String HTTPS_API_AMAZON_COM_AUTH_O2_TOKEN = "https://api.amazon.com/auth/O2/token";

    public static final String UTF_8 = "UTF-8";
    public static final Charset UTF_8_CHARSET= Charset.forName(UTF_8);

    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String MESSAGING_PUSH = "messaging:push";


    private Utilities() {}

    /**
     * Convenience method to open/establish the HttpsURLConnection against ADM
     */
    public static HttpsURLConnection getHttpsURLConnection(URL url) throws IOException {
        final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        return conn;
    }


    /**
     * Generic method to parse an http response
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static String parseResponse(final InputStream in) throws IOException {
        // Read from the input stream and convert into a String.
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, Utilities.UTF_8));
        final StringBuilder sb = new StringBuilder();

        try {
            String line = reader.readLine();
            while(line != null) {
                sb.append(line);
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }

        return sb.toString();
    }

    public static String getStringFromJson(String jsonString, String property) {
        final Gson gson = new Gson();
        final JsonObject parsedObject = gson.fromJson(jsonString,JsonObject.class);
        return parsedObject.get(property).getAsString();
    }
}
