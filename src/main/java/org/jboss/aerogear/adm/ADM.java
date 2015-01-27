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

/**
 * The main class to interact with the ADM Service.
 */
public class ADM {

    private ADM() {
        throw new AssertionError("Uninstantiable class");
    }

    /**
     * @return a new {@link org.jboss.aerogear.adm.PayloadBuilder} instance
     */
    public static PayloadBuilder newPayload() {
        return new PayloadBuilder();
    }

    /**
     * @return a new {@link org.jboss.aerogear.adm.MessageService} for sending ADM notifications
     */
    public static MessageService newService() {
        return new MessageService();
    }

}
