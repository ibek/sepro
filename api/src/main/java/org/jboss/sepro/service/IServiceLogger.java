package org.jboss.sepro.service;

public interface IServiceLogger {
    
    public void addServiceLog(String service, String msg);
    
    public String getServiceLogs(String service);

    public String getServiceLogs();
    
}
