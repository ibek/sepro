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
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.jboss.sepro.dto.Acronym;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AcronymsDictionaryJMSTest extends AbstractJMSTest {

    private static Queue queue;
    private static Topic topicResponse;

    @BeforeClass
    public static void lookup() throws Exception {
        queue = (javax.jms.Queue) ctx.lookup("queue/AcronymsDictionary");
        topicResponse = (javax.jms.Topic) ctx.lookup("topic/AcronymsDictionaryResponse");
    }

    @Test
    public void testAddAcronym() throws Exception {
        Acronym acronym = new Acronym();
        acronym.setAbbreviation("JMS");
        acronym.setMeaning("Java Message Service");

        addAcronym(acronym);

        // check that acronym has been added

        Object[] acronyms = getAcronym(acronym.getAbbreviation());
        Assert.assertNotNull(acronyms);
        Assert.assertEquals(1, acronyms.length);
        Acronym receivedAcronym = (Acronym) acronyms[0];
        Assert.assertEquals(acronym, receivedAcronym);
    }

    @Test
    public void testRemoveAcronym() throws Exception {
        Acronym acronym = new Acronym();
        acronym.setAbbreviation("JMS");
        acronym.setMeaning("Java Message Service");

        addAcronym(acronym);

        Thread.sleep(500);

        removeAcronym(acronym);

        Thread.sleep(500);

        Object[] acronyms = getAcronym(acronym.getAbbreviation());
        Assert.assertNotNull(acronyms);
        Assert.assertEquals(0, acronyms.length);
    }

    private void addAcronym(Acronym acronym) throws Exception {
        MessageProducer messageProducer = session.createProducer(queue);

        Message message = session.createObjectMessage(acronym);
        message.setStringProperty("operation", "addAcronym");
        String correlationId = UUID.randomUUID().toString();
        String messageSelector = "JMSCorrelationID = '" + correlationId + "'";
        message.setJMSCorrelationID(correlationId);

        System.out.println("Sending Acronym: " + acronym);
        messageProducer.send(message);

        MessageConsumer messageConsumer = session.createConsumer(topicResponse, messageSelector);

        Message msg = messageConsumer.receive(1000);
        String receivedMessage = null;
        if (msg instanceof TextMessage) {
            TextMessage txtMsg = (TextMessage) msg;
            receivedMessage = txtMsg.getText();
            System.out.println("Received Error Message: " + receivedMessage);
        }
    }

    private Object[] getAcronym(String abbreviation) throws Exception {
        Message message = session.createTextMessage(abbreviation);
        message.setStringProperty("operation", "getAcronyms");
        String correlationId = UUID.randomUUID().toString();
        String messageSelector = "JMSCorrelationID = '" + correlationId + "'";
        message.setJMSCorrelationID(correlationId);

        System.out.println("Sending a request for meanings of: JMS");
        MessageProducer messageProducer = session.createProducer(queue);
        messageProducer.send(message);

        MessageConsumer messageConsumer = session.createConsumer(topicResponse, messageSelector);
        Message msg = messageConsumer.receive(1000);
        Assert.assertTrue(msg instanceof ObjectMessage);
        Object[] acronyms = ((Object[]) ((ObjectMessage) msg).getObject());
        return acronyms;
    }

    private void removeAcronym(Acronym acronym) throws Exception {
        MessageProducer messageProducer = session.createProducer(queue);

        Message message = session.createObjectMessage(acronym);
        message.setStringProperty("operation", "removeAcronym");
        String correlationId = UUID.randomUUID().toString();
        message.setJMSCorrelationID(correlationId);

        System.out.println("Removing Acronym: " + acronym);
        messageProducer.send(message);
    }

}
