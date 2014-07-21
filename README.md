# Amazon Device Messaging Java Connector

A Java Library to send Push Notification to the Amazon Device Messaging Network

## Get started

Add the following to your ```pom.xml``` file:

```
<dependency>
  <groupId>org.sebi</groupId>
  <artifactId>java-adm</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Then :

```
MessageService messageService = new MessageService();

PayloadBuilder builder = new PayloadBuilder();
builder.dataField("custom","custom");

messageService.sendMessageToDevice(<registrationId>, <clientId>, <clientSecret>, builder.build());

```

