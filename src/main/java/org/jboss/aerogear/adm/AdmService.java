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
import java.nio.charset.Charset;

/**
 * A service to submit your payload to the ADM Network.
 * The code is freely inspired from <a href="https://developer.amazon.com/appsandservices/apis/engage/device-messaging/tech-docs/06-sending-a-message">Amazon's Developers Documentation</a>
 *
 */
public class AdmService {

    private static final String ADM_URL_TEMPLATE = "https://api.amazon.com/messaging/registrations/%1$s/messages";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final TokenService tokenService;
    private String accessToken;

    public AdmService() {
        this.tokenService = new TokenService();
    }

    /**
     * Request that ADM deliver your message to a specific instance of your app.
     *
     * @param registrationId representing the unique identifier of the device
     * @param clientId unique ID supplied by ADM Services
     * @param clientSecret secret value supplied by ADM services
     * @param payload , a String representing the complete payload to be submitted
     * @throws Exception if sending the message fails
     */
    public void sendMessageToDevice(final String registrationId, final String clientId, final String clientSecret, final String payload) throws Exception {

        if (accessToken == null) {
            accessToken = tokenService.getAuthToken(clientId,clientSecret);
        }

        // Generate the HTTPS connection for the POST request.
        // You cannot make a connection over plain HTTP.
        HttpsURLConnection conn = post(registrationId, payload);

        // Obtain the response code from the connection.
        final int responseCode = conn.getResponseCode();

        // Check if we received a failure response, and if so, get the reason for the failure.
        if (responseCode != 200) {
            if ( responseCode == 401) {
                accessToken = tokenService.getAuthToken(clientId,clientSecret);
                sendMessageToDevice(registrationId, clientId, clientSecret, payload);
            }
            else {
                String errorContent = parseResponse(conn.getErrorStream());
                throw new RuntimeException(String.format("ERROR: The enqueue request failed with a " +
                        "%d response code, with the following message: %s",
                        responseCode, errorContent));
            }

        }
        else {
            // The request was successful. The response contains the canonical Registration ID for the specific instance of your
            // app, which may be different that the one used for the request.

            final String responseContent = parseResponse(conn.getInputStream());
            final JSONObject parsedObject = new JSONObject(responseContent);

            final String canonicalRegistrationId = parsedObject.getString("registrationID");

            // Check if the two Registration IDs are different.
            if(!canonicalRegistrationId.equals(registrationId)) {
                // At this point the data structure that stores the Registration ID values should be updated
                // with the correct Registration ID for this particular app instance.
            }
        }

    }

    /**
     * Returns HttpsURLConnection that 'posts' the given payload to ADM.
     */
    private HttpsURLConnection post(final String registrationId, final String payload) throws Exception {

        final HttpsURLConnection conn = getHttpsURLConnection(registrationId);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        // Set the content type and accept headers.
        conn.setRequestProperty("content-type", "application/json");
        conn.setRequestProperty("accept", "application/json");
        conn.setRequestProperty("X-Amzn-Type-Version ", "com.amazon.device.messaging.ADMMessage@1.0");
        conn.setRequestProperty("X-Amzn-Accept-Type", "com.amazon.device.messaging.ADMSendResult@1.0");

        conn.setRequestMethod("POST");

        // Add the authorization token as a header.
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        OutputStream out = null;
        final byte[] bytes = payload.getBytes(UTF_8);
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
    private HttpsURLConnection getHttpsURLConnection(final String registrationId) throws Exception {
        // Establish the base URL, including the section to be replaced by the registration
        // ID for the desired app instance. Because we are using String.format to create
        // the URL, the %1$s characters specify the section to be replaced.
        final URL admUrl = new URL(String.format(ADM_URL_TEMPLATE ,registrationId));

        final HttpsURLConnection conn = (HttpsURLConnection) admUrl.openConnection();
        return conn;
    }

    private String parseResponse(final InputStream in) throws Exception {
        // Read from the input stream and convert into a String.
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
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
