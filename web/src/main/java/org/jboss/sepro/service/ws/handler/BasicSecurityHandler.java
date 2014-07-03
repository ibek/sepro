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
package org.jboss.sepro.service.ws.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.resteasy.util.Base64;
import org.jboss.sepro.dto.User;
import org.jboss.sepro.service.IUserRegistration;
import org.jboss.sepro.service.producer.SecurityProducer;
import org.jboss.sepro.util.HashTool;

public class BasicSecurityHandler implements SOAPHandler<SOAPMessageContext> {

    @Inject
    IUserRegistration userRegistration;

    @Inject
    SecurityProducer securityProducer;

    @Override
    public final void close(MessageContext context) {

    }

    @Override
    public final boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    @Override
    public final boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
            return true; // only inbound messages are of interest
        }

        if (!isAuthHeaderIncluded(context)) {
            return true;
        }

        List<String> authHeaders = getHttpRequestHeaders(context).get("Authorization");
        for (String authHeader : authHeaders) {
            if (authHeader.startsWith("Basic ")) {
                try {
                    if (authenticateRequest(context, authHeader)) {
                        return true;
                    }
                    return breakHandlerChain(context);
                } catch (Exception e) {
                    return breakHandlerChainWithException(context, e);
                }
            }
        }
        return breakHandlerChain(context);
    }

    @Override
    public final Set<QName> getHeaders() {
        return null;
    }

    protected boolean authenticateRequest(SOAPMessageContext context, String authHeader) throws Exception {
        // Get encoded username and password
        final String encodedUserPassword = authHeader.substring("Basic ".length());

        // Decode username and password
        String usernameAndPassword;
        try {
            usernameAndPassword = new String(Base64.decode(encodedUserPassword));
        } catch (IOException e) {
            context.put(MessageContext.HTTP_RESPONSE_CODE, 500);
            return false;
        }

        // Split username and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        if (tokenizer.countTokens() != 2) {
            context.put(MessageContext.HTTP_RESPONSE_CODE, 401);
            return false;
        }
        final String username = tokenizer.nextToken();
        String password;
        try {
            password = HashTool.hashPassword(tokenizer.nextToken());
        } catch (Exception e) {
            context.put(MessageContext.HTTP_RESPONSE_CODE, 401);
            return false;
        }

        // Verifying Username and password
        User user = userRegistration.getUser(username);
        System.out.println(user);
        if (user == null || !user.getPassword().equals(password)) {
            context.put(MessageContext.HTTP_RESPONSE_CODE, 401);
            return false;
        }
        securityProducer.setLoggedUser(user);

        return true;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, List<String>> getHttpRequestHeaders(SOAPMessageContext context) {
        return (Map<String, List<String>>) context.get(MessageContext.HTTP_REQUEST_HEADERS);
    }

    protected boolean breakHandlerChain(SOAPMessageContext context) {
        throw new WebServiceException("HTTP Error 401 Unauthorized");
    }

    protected boolean breakHandlerChainWithException(SOAPMessageContext context, Exception exception) {
        context.put(MessageContext.HTTP_RESPONSE_CODE, 500);

        if (exception instanceof WebServiceException) {
            throw (WebServiceException) exception;
        }
        throw new WebServiceException("Internal server error");
    }

    protected boolean isAuthHeaderIncluded(SOAPMessageContext context) {
        Map<String, List<String>> httpRequestHeaders = getHttpRequestHeaders(context);
        return (httpRequestHeaders != null && httpRequestHeaders.get("Authorization") != null && httpRequestHeaders
                .get("Authorization").size() > 0);
    }
}
