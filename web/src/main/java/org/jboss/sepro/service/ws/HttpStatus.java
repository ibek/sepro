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

import java.io.IOException;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

@WebService(name = HttpStatus.WS_NAME, serviceName = HttpStatus.WS_SERVICE_NAME, targetNamespace = HttpStatus.WS_NAMESPACE)
@HandlerChain(file = "/handler-chain.xml")
public class HttpStatus {

    public final static String WS_NAME = "HttpStatus";
    public final static String WS_SERVICE_NAME = "HttpStatusService";
    public final static String WS_NAMESPACE = "http://sepro.jboss.org";

    @Inject
    Logger log;

    @Resource
    private WebServiceContext context;

    @WebMethod
    public void getStatus(@WebParam(name = "code") int code) {
        if (code < 100 || code > 599) {
            code = 400;
        }
        try {
            MessageContext ctx = context.getMessageContext();
            HttpServletResponse response = (HttpServletResponse) ctx.get(MessageContext.SERVLET_RESPONSE);
            response.sendError(code);
        } catch (IOException e) {

        }
    }

}
