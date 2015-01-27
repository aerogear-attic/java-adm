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

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class TokenService {

    private static final String UTF_8 = "UTF-8";
    private static final Charset UTF_8_CHARSET= Charset.forName(UTF_8);

    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String MESSAGING_PUSH = "messaging:push";
    private static final String HTTPS_API_AMAZON_COM_AUTH_O2_TOKEN = "https://api.amazon.com/auth/O2/token";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    /**
     * To obtain an access token, make an HTTPS request to Amazon
     * and include your client_id and client_secret values.
     * 
     * @param clientId unique ID supplied by ADM Services
     * @param clientSecret secret value supplied by ADM services
     * @return a String containing your auth token
     * @throws Exception if retrieving the Auth token fails
     */
    public String getAuthToken(String clientId, String clientSecret) throws Exception
    {
        // Encode the body of your request, including your clientID and clientSecret values.
        String body = "grant_type="    + URLEncoder.encode(CLIENT_CREDENTIALS, UTF_8) + "&" +
                "scope="         + URLEncoder.encode(MESSAGING_PUSH, UTF_8)     + "&" +
                "client_id="     + URLEncoder.encode(clientId, UTF_8)             + "&" +
                "client_secret=" + URLEncoder.encode(clientSecret, UTF_8);

        // Generate the HTTPS connection. You cannot make a connection over HTTP.
        final HttpsURLConnection con = post(body);

        // Convert the response into a String object.
        final String responseContent = parseResponse(con.getInputStream());

        // Create a new JSONObject to hold the access token and extract
        // the token from the response.
        final JSONObject parsedObject = new org.json.JSONObject(responseContent);
        final String accessToken = parsedObject.getString("access_token");
        return accessToken;
    }

    /**
     * Returns HttpsURLConnection that 'posts' the given payload to ADM.
     */
    private HttpsURLConnection post(final String payload) throws Exception {

        final HttpsURLConnection conn = getHttpsURLConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        // Set the content type .
        conn.setRequestProperty("content-type", APPLICATION_X_WWW_FORM_URLENCODED);
        conn.setRequestProperty("charset", UTF_8);

        conn.setRequestMethod("POST");

        OutputStream out = null;
        final byte[] bytes = payload.getBytes(UTF_8_CHARSET);
        try {
            out = conn.getOutputStream();
            out.write(bytes);
            out.flush();
        } finally {
            // in case something blows up, while writing
            // the payload, we wanna close the stream:
            if (out != null) {
                out.close();
            }
        }

        return conn;
    }

    /**
     * Convenience method to open/establish the HttpsURLConnection agains ADM
     */
    private HttpsURLConnection getHttpsURLConnection() throws Exception {
        // Create a new URL object with the base URL for the access token request.
        URL authUrl = new URL(HTTPS_API_AMAZON_COM_AUTH_O2_TOKEN);

        final HttpsURLConnection conn = (HttpsURLConnection) authUrl.openConnection();
        return conn;
    }

    private String parseResponse(InputStream in) throws Exception  {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, UTF_8_CHARSET ));
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
}
