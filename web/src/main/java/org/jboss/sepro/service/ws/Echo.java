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
package org.jboss.sepro.service.ws;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.jboss.sepro.service.IServiceLogger;
import org.jboss.sepro.stereotype.WS;

@WebService(name = Echo.WS_NAME, serviceName = Echo.WS_SERVICE_NAME, targetNamespace = Echo.WS_NAMESPACE)
@HandlerChain(file = "/handler-chain.xml")
public class Echo {

    @Inject
    @WS
    IServiceLogger slogger;
    
    public final static String WS_NAME = "Echo";
    public final static String WS_SERVICE_NAME = "EchoService";
    public final static String WS_NAMESPACE = "http://sepro.jboss.org";

    @Resource
    private WebServiceContext context;

    @WebMethod
    public String ping() {
        slogger.addServiceLog(getClass().getSimpleName(), "Ping");
        return "pong";
    }

    @WebMethod
    public String complete(@WebParam(name = "option") String option) {
        slogger.addServiceLog(getClass().getSimpleName(), "Complete " + option);
        switch (option) {
        case "ping": {
            return "pong";
        }
        case "hello": {
            return "world";
        }
        default: {
            return "default";
        }
        }
    }

    @WebMethod
    public String echo(@WebParam(name = "message") String message) {
        slogger.addServiceLog(getClass().getSimpleName(), "Echo " + message);
        return message;
    }

}
