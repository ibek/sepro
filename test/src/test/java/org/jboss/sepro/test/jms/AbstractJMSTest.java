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
