/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.sepro.test.jms;

import java.util.UUID;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.junit.Assert;
import org.junit.Test;

public class EchoJMSTest extends AbstractJMSTest {

    private static Queue queue;
    private static Topic topicResponse;
    
    public static void lookup() throws Exception {
        queue = (javax.jms.Queue) ctx.lookup("queue/Echo");
        topicResponse = (javax.jms.Topic) ctx.lookup("topic/EchoResponse");
    }

    @Test
    public void testHelloWorldMessage() throws Exception {
        MessageProducer messageProducer = session.createProducer(queue);

        TextMessage textMessage = session.createTextMessage();
        String correlationId = UUID.randomUUID().toString();
        String messageSelector = "JMSCorrelationID = '" + correlationId + "'";
        textMessage.setJMSCorrelationID(correlationId);
        String expectedMessage = "Hello World";
        textMessage.setText(expectedMessage);
        System.out.println("Sending Message: " + textMessage.getText());
        messageProducer.send(textMessage);

        MessageConsumer messageConsumer = session.createConsumer(topicResponse, messageSelector);

        Message msg = messageConsumer.receive();
        String receivedMessage = null;
        if (msg instanceof TextMessage) {
            TextMessage txtMsg = (TextMessage) msg;
            receivedMessage = txtMsg.getText();
            System.out.println("Received Message: " + receivedMessage);
        }

        Assert.assertEquals(expectedMessage, receivedMessage);
    }

}
