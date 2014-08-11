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

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.HandlerChain;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.Addressing;

import org.apache.cxf.headers.Header;
import org.apache.cxf.ws.addressing.impl.AddressingPropertiesImpl;
import org.jboss.sepro.service.IServiceLogger;
import org.jboss.sepro.service.ws.skeleton.AsyncEchoService;
import org.jboss.sepro.stereotype.WS;

@WebService(name = AsyncEcho.WS_NAME, serviceName = AsyncEcho.WS_SERVICE_NAME, targetNamespace = AsyncEcho.WS_NAMESPACE)
@HandlerChain(file = "/handler-chain.xml")
@Addressing
public class AsyncEcho {
    
    @Inject
    @WS
    IServiceLogger slogger;

    public final static String WS_NAME = "AsyncEcho";
    public final static String WS_SERVICE_NAME = "AsyncEchoService";
    public final static String WS_NAMESPACE = "http://sepro.jboss.org";

    @Resource
    private WebServiceContext context;

    @WebMethod
    @Oneway
    public void ping() {
        slogger.addServiceLog(getClass().getSimpleName(), "Ping");
        AddressingPropertiesImpl aprop = (AddressingPropertiesImpl) context.getMessageContext().get(
                "javax.xml.ws.addressing.context.inbound");
        if (aprop == null) {
            System.out.println("null");
            return;
        }
        String address = aprop.getReplyTo().getAddress().getValue();
        AsyncEchoService aes = new AsyncEchoService();
        org.jboss.sepro.service.ws.skeleton.AsyncEcho portType = aes.getAsyncEchoPort();

        BindingProvider provider = (BindingProvider) portType;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);
        ArrayList<Header> headers = new ArrayList<Header>(1);
        headers.add(new Header(new QName("http://schemas.xmlsoap.org/ws/2004/08/addressing", "RelatesTo"), aprop
                .getMessageID().getValue()));
        provider.getResponseContext().put(Header.HEADER_LIST, headers);
        portType.callbackAsyncPing("asynchronous pong");
    }

    @WebMethod
    @Oneway
    public void callbackAsyncPing(String message) {

    }

}
