# Amazon Device Messaging Java Connector [![Build Status](https://travis-ci.org/aerogear/java-adm.png)](https://travis-ci.org/aerogear/java-adm)

A Java Library to send Push Notification to the Amazon Device Messaging Network.
A complete documentation of the protocol can be found [here](https://developer.amazon.com/appsandservices/apis/engage/device-messaging/)

## Get started

Add the following to your ```pom.xml``` file:

```
<dependency>
  <groupId>org.jboss.aerogear</groupId>
  <artifactId>java-adm</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Then :

```
final AdmService messageService = ADM.newService();
final PayloadBuilder builder = ADM.newPayload();
builder.dataField("custom","custom");

messageService.sendMessageToDevice(<registrationId>, <clientId>, <clientSecret>, builder.build());

```

