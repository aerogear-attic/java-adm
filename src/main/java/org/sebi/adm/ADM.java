package org.sebi.adm;

/**
 * Created by sebastienblanc on 1/26/15.
 */
public class ADM {

    private ADM() {
        throw new AssertionError("Uninstantiable class");
    }

    public static PayloadBuilder newPayload() {
        return new PayloadBuilder();
    }

    public static MessageService newService() {
        return new MessageService();
    }

}
