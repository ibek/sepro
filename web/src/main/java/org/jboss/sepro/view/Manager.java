package org.jboss.sepro.view;

import javax.inject.Named;

@Named
public class Manager {

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
    
}
