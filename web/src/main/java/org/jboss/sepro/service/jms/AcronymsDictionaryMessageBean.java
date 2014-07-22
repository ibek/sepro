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
package org.jboss.sepro.service.jms;

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.sepro.dto.Acronym;
import org.jboss.sepro.exception.DuplicateException;
import org.jboss.sepro.service.IAcronymsFinder;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/AcronymsDictionary"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class AcronymsDictionaryMessageBean implements MessageListener {

    @Inject
    IAcronymsFinder acronymFinder;

    @Override
    public void onMessage(Message message) {
        Connection connection = null;
        Session session = null;
        try {
            String operation = message.getStringProperty("operation");

            Context ctx = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("java:/JmsXA");
            Topic respTopic = (Topic) ctx.lookup("topic/AcronymsDictionaryResponse");

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();

            switch (operation) {
            case "addAcronym": {
                if (message instanceof ObjectMessage) {
                    Acronym acronym = ((Acronym) ((ObjectMessage) message).getObject());
                    try {
                        acronymFinder.addAcronym(acronym);
                    } catch (DuplicateException dex) {
                        MessageProducer producer = session.createProducer(respTopic);
                        Message response = session.createTextMessage("409 Conflict - this " + acronym
                                + " already exists");
                        response.setJMSCorrelationID(message.getJMSCorrelationID());
                        producer.send(response);
                    }
                }
                break;
            }
            case "getDictionary": {
                MessageProducer producer = session.createProducer(respTopic);
                Message response = session.createObjectMessage(acronymFinder.getDictionary());
                response.setJMSCorrelationID(message.getJMSCorrelationID());
                producer.send(response);
                break;
            }
            case "getAcronyms": {
                if (message instanceof TextMessage) {
                    String abbreviation = ((TextMessage) message).getText();
                    List<Acronym> acronyms = acronymFinder.getAcronymsFor(abbreviation);

                    MessageProducer producer = session.createProducer(respTopic);
                    Message response = session.createObjectMessage(acronyms.toArray());
                    response.setJMSCorrelationID(message.getJMSCorrelationID());
                    producer.send(response);
                }
                break;
            }
            case "removeAcronym": {
                if (message instanceof ObjectMessage) {
                    Acronym acronym = ((Acronym) ((ObjectMessage) message).getObject());
                    acronymFinder.removeAcronym(acronym);
                }
                break;
            }
            default: {
                MessageProducer producer = session.createProducer(respTopic);
                Message response = session
                        .createTextMessage("No operation specified - please set 'operation' string message parameter.");
                response.setJMSCorrelationID(message.getJMSCorrelationID());
                producer.send(response);
            }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != session) {
                    session.close();
                }
            } catch (JMSException exc) {

            }
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (JMSException exc) {

            }
        }
    }

}
