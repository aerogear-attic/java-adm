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
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;


/**
 * A service to submit your payload to the ADM Network.
 * The code is freely inspired from <a href="https://developer.amazon.com/appsandservices/apis/engage/device-messaging/tech-docs/06-sending-a-message">Amazon's Developers Documentation</a>
 *
 */
public class AdmService {

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
     * @param payload a String representing the complete payload to be submitted
     * @return a String representing the registrationId sent back from ADM services.
     * @throws IOException if sending the message fails
     */
    public String sendMessageToDevice(String registrationId, final String clientId, final String clientSecret, final String payload) throws IOException {

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
                String errorContent = Utilities.parseResponse(conn.getErrorStream());
                throw new RuntimeException(String.format("ERROR: The enqueue request failed with a " +
                        "%d response code, with the following message: %s",
                        responseCode, errorContent));
            }

        }
        else {
            // The request was successful. The response contains the canonical Registration ID for the specific instance of your
            // app, which may be different that the one used for the request.

            final String responseContent = Utilities.parseResponse(conn.getInputStream());
            final String canonicalRegistrationId = Utilities.getStringFromJson(responseContent, "registrationID");

            // Check if the two Registration IDs are different.
            if(!canonicalRegistrationId.equals(registrationId)) {
                registrationId = canonicalRegistrationId;
            }
        }

        return registrationId;
    }

    /**
     * Returns HttpsURLConnection that 'posts' the given payload to ADM.
     */
    private HttpsURLConnection post(final String registrationId, final String payload) throws IOException {

        // Establish the base URL, including the section to be replaced by the registration
        // ID for the desired app instance. Because we are using String.format to create
        // the URL, the %1$s characters specify the section to be replaced.
        final URL admUrl = new URL(String.format(Utilities.ADM_URL_TEMPLATE ,registrationId));

        final HttpsURLConnection conn = Utilities.getHttpsURLConnection(admUrl);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        // Set the content type and accept headers.
        conn.setRequestProperty("content-type", Utilities.APPLICATION_JSON);
        conn.setRequestProperty("accept", Utilities.APPLICATION_JSON);
        conn.setRequestProperty("X-Amzn-Type-Version ", Utilities.AMAZON_TYPE_VERSION);
        conn.setRequestProperty("X-Amzn-Accept-Type", Utilities.AMAZON_ACCEPT_TYPE);
        conn.setRequestMethod("POST");

        // Add the authorization token as a header.
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

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
