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

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;

public class TokenService {

    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String ENCODING = "UTF-8";
    public static final String MESSAGING_PUSH = "messaging:push";
    public static final String HTTPS_API_AMAZON_COM_AUTH_O2_TOKEN = "https://api.amazon.com/auth/O2/token";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    /**
     * To obtain an access token, make an HTTPS request to Amazon
     * and include your client_id and client_secret values.
     */
    public String getAuthToken(String clientId, String clientSecret) throws Exception
    {
        // Encode the body of your request, including your clientID and clientSecret values.
        String body = "grant_type="    + URLEncoder.encode(CLIENT_CREDENTIALS, ENCODING) + "&" +
                "scope="         + URLEncoder.encode(MESSAGING_PUSH, ENCODING)     + "&" +
                "client_id="     + URLEncoder.encode(clientId, ENCODING)             + "&" +
                "client_secret=" + URLEncoder.encode(clientSecret, ENCODING);

        // Create a new URL object with the base URL for the access token request.
        URL authUrl = new URL(HTTPS_API_AMAZON_COM_AUTH_O2_TOKEN);

        // Generate the HTTPS connection. You cannot make a connection over HTTP.
        HttpsURLConnection con = (HttpsURLConnection) authUrl.openConnection();
        con.setDoOutput( true );
        con.setRequestMethod( "POST" );

        // Set the Content-Type header.
        con.setRequestProperty( "Content-Type" , APPLICATION_X_WWW_FORM_URLENCODED);
        con.setRequestProperty( "Charset" , ENCODING );
        // Send the encoded parameters on the connection.
        OutputStream os = con.getOutputStream();
        os.write(body.getBytes( "UTF-8" ));
        os.flush();
        con.connect();

        // Convert the response into a String object.
        String responseContent = parseResponse(con.getInputStream());

        // Create a new JSONObject to hold the access token and extract
        // the token from the response.
        JSONObject parsedObject = new org.json.JSONObject(responseContent);
        String accessToken = parsedObject.getString("access_token");
        return accessToken;
    }

    private String parseResponse(InputStream in) throws Exception
    {
        InputStreamReader inputStream = new InputStreamReader(in, ENCODING );
        BufferedReader buff = new BufferedReader(inputStream);

        StringBuilder sb = new StringBuilder();
        String line = buff.readLine();
        while (line != null )
        {
            sb.append(line);
            line = buff.readLine();
        }

        return sb.toString();
    }
}
