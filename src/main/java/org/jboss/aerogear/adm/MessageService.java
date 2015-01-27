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

/**
 * A service to submit your payload to the ADM Network.
 * The code is freely inspired from <a href="https://developer.amazon.com/appsandservices/apis/engage/device-messaging/tech-docs/06-sending-a-message">Amazon's Developers Documentation</a>
 *
 */
public class MessageService {

    private TokenService tokenService;

    private String accessToken;

    public MessageService() {
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
    public void sendMessageToDevice(String registrationId, String clientId, String clientSecret, String payload) throws Exception
    {


        if(accessToken == null){
            accessToken = tokenService.getAuthToken(clientId,clientSecret);
        }
        // Establish the base URL, including the section to be replaced by the registration
        // ID for the desired app instance. Because we are using String.format to create
        // the URL, the %1$s characters specify the section to be replaced.
        String admUrlTemplate = "https://api.amazon.com/messaging/registrations/%1$s/messages";

        URL admUrl = new URL(String.format(admUrlTemplate,registrationId));

        // Generate the HTTPS connection for the POST request. You cannot make a connection
        // over HTTP.
        HttpsURLConnection conn = (HttpsURLConnection) admUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // Set the content type and accept headers.
        conn.setRequestProperty("content-type", "application/json");
        conn.setRequestProperty("accept", "application/json");
        conn.setRequestProperty("X-Amzn-Type-Version ", "com.amazon.device.messaging.ADMMessage@1.0");
        conn.setRequestProperty("X-Amzn-Accept-Type", "com.amazon.device.messaging.ADMSendResult@1.0");

        // Add the authorization token as a header.
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        // Obtain the output stream for the connection and write the message payload to it.
        OutputStream os = conn.getOutputStream();
        os.write(payload.getBytes(), 0, payload.getBytes().length);
        os.flush();
        conn.connect();

        // Obtain the response code from the connection.
        int responseCode = conn.getResponseCode();

        // Check if we received a failure response, and if so, get the reason for the failure.
        if( responseCode != 200)
        {
            if( responseCode == 401 )
            {
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
        else
        {
            // The request was successful. The response contains the canonical Registration ID for the specific instance of your
            // app, which may be different that the one used for the request.

            String responseContent = parseResponse(conn.getInputStream());
            JSONObject parsedObject = new JSONObject(responseContent);

            String canonicalRegistrationId = parsedObject.getString("registrationID");

            // Check if the two Registration IDs are different.
            if(!canonicalRegistrationId.equals(registrationId))
            {
                // At this point the data structure that stores the Registration ID values should be updated
                // with the correct Registration ID for this particular app instance.
            }
        }

    }

    private String parseResponse(InputStream in) throws Exception
    {
        // Read from the input stream and convert into a String.
        InputStreamReader inputStream = new InputStreamReader(in);
        BufferedReader buff = new BufferedReader(inputStream);

        StringBuilder sb = new StringBuilder();
        String line = buff.readLine();
        while(line != null)
        {
            sb.append(line);
            line = buff.readLine();
        }

        return sb.toString();
    }
}
