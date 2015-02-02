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


import org.jboss.aerogear.adm.internal.Utilities;
import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;


public class TokenService {



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
        String body = buildBody(clientId, clientSecret);

        // Generate the HTTPS connection. You cannot make a connection over HTTP.
        final HttpsURLConnection con = post(body);

        // Convert the response into a String object.
        final String responseContent = Utilities.parseResponse(con.getInputStream());

        final String accessToken = Utilities.getStringFromJson(responseContent, "access_token");
        return accessToken;
    }


    private String buildBody(String clientId, String clientSecret) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder
                .append("grant_type=")
                .append(URLEncoder.encode(Utilities.CLIENT_CREDENTIALS, Utilities.UTF_8) )
                .append("&")
                .append("scope=")
                .append( URLEncoder.encode(Utilities.MESSAGING_PUSH, Utilities.UTF_8) )
                .append("&")
                .append("client_id=")
                .append("URLEncoder.encode(clientId, Utilities.UTF_8)")
                .append("&")
                .append("client_secret=")
                .append(URLEncoder.encode(clientSecret, Utilities.UTF_8));
        return builder.toString();
    }

    /**
     * Returns HttpsURLConnection that 'posts' the given payload to ADM.
     */
    private HttpsURLConnection post(final String payload) throws Exception {

        // Create a new URL object with the base URL for the access token request.
        URL authUrl = new URL(Utilities.HTTPS_API_AMAZON_COM_AUTH_O2_TOKEN);

        final HttpsURLConnection conn = Utilities.getHttpsURLConnection(authUrl);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        // Set the content type .
        conn.setRequestProperty("content-type", Utilities.APPLICATION_X_WWW_FORM_URLENCODED);
        conn.setRequestProperty("charset", Utilities.UTF_8);

        conn.setRequestMethod("POST");

        OutputStream out = null;
        final byte[] bytes = payload.getBytes(Utilities.UTF_8_CHARSET);
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

}
