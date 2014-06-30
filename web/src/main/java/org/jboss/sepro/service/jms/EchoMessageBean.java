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

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/Echo"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class EchoMessageBean implements MessageListener {

    @Override
    public void onMessage(Message message) {
        Connection connection = null;
        Session session = null;
        try {
            Context ctx = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) ctx
                    .lookup("java:/JmsXA");
            Topic respTopic = (Topic) ctx
                    .lookup("java:/topic/EchoResponse");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            String text = "pong";
            if (message instanceof TextMessage) {
                text = ((TextMessage) message).getText();
            }
            MessageProducer producer = session.createProducer(respTopic);
            Message response = session.createTextMessage(text);
            response.setJMSCorrelationID(message.getJMSCorrelationID());
            producer.send(response);
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
