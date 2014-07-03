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

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractJMSTest {

    /**
     * Add a new user via ./eap6/bin/add_user.sh script and put the user into
     * the group "guest" for the default settings.
     */
    private static final String USERNAME = "ibek";
    private static final String PASSWORD = "ibek1234;";

    protected static Context ctx;
    protected static Session session;

    private static Connection connection;

    @BeforeClass
    public static void setUp() throws Exception {
        Properties initialProps = new Properties();
        initialProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        initialProps.setProperty(Context.PROVIDER_URL, "remote://localhost:4447");
        initialProps.setProperty(Context.SECURITY_PRINCIPAL, USERNAME);
        initialProps.setProperty(Context.SECURITY_CREDENTIALS, PASSWORD);

        ctx = new InitialContext(initialProps);

        ConnectionFactory connectionFactory = (javax.jms.ConnectionFactory) ctx
                .lookup("java:jms/RemoteConnectionFactory");
        connection = connectionFactory.createConnection(USERNAME, PASSWORD);
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        session.close();
        connection.close();
    }

}
