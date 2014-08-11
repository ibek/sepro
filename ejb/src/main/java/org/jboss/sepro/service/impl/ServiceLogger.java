package org.jboss.sepro.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jboss.sepro.service.IServiceLogger;
import org.jboss.sepro.util.LimitedQueue;

public class ServiceLogger implements IServiceLogger {
    
    private LinkedHashMap<String, LimitedQueue<String>> logs;
    
    public ServiceLogger() {
        logs = new LinkedHashMap<>();
    }

    @Override
    public void addServiceLog(String service, String msg) {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String log = sdf.format(cal.getTime()) + " " + msg;
        LimitedQueue<String> lq = logs.get(service);
        if (lq == null) {
            lq = new LimitedQueue<>(10);
        }
        lq.add(log);
        logs.put(service, lq);
    }

    @Override
    public String getServiceLogs(String service) {
        String res = "<b>" + service + "</b><br/>";
        for (String log : logs.get(service)) {
            res += log + "<br/>";
        }
        return res;
    }

    @Override
    public String getServiceLogs() {
        String res = "";
        List<String> list = new ArrayList<>(logs.keySet());
        Collections.sort(list);
        for (String key : list) {
            res += getServiceLogs(key);
        }
        return res;
    }

}
