package org.jboss.sepro.view;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.sepro.service.IServiceLogger;
import org.jboss.sepro.stereotype.JMS;
import org.jboss.sepro.stereotype.REST;
import org.jboss.sepro.stereotype.WS;

@Named
public class Manager {

    @Inject
    @REST
    IServiceLogger restLogger;

    @Inject
    @WS
    IServiceLogger wsLogger;

    @Inject
    @JMS
    IServiceLogger jmsLogger;

    public String getIPAddress() {
        String ip = System.getenv("OPENSHIFT_JBOSSEAP_IP");
        if (ip == null) {
            ip = System.getenv("jboss.host.name");
        }
        return ip;
    }
    
    public String getPort() {
        return System.getenv("OPENSHIFT_JBOSSEAP_REMOTING_PROXY_PORT");
    }
    
    public String getRestLogs() {
        return restLogger.getServiceLogs();
    }
    
    public String getWsLogs() {
        return wsLogger.getServiceLogs();
    }
    
    public String getJmsLogs() {
        return jmsLogger.getServiceLogs();
    }
    
}
