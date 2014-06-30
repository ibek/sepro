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
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.sepro.dto.User;
import org.jboss.sepro.service.IUserRegistration;
import org.jboss.sepro.service.producer.SecurityProducer;
import org.jboss.sepro.util.HashTool;

public class WSSecurityHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String AUTHN_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String AUTHN_LNAME = "Security";
    private static final String AUTHN_PREFIX = "wsse";
    private static final String PASSWORD_DIGEST_SEPARATOR = ">";

    @Inject
    Logger log;

    @Inject
    IUserRegistration userRegistration;

    @Inject
    SecurityProducer securityProducer;

    @Override
    public void close(MessageContext arg0) {

    }

    @Override
    public boolean handleFault(SOAPMessageContext arg0) {
        return true;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        try {
            boolean outMessageIndicator = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (!outMessageIndicator) {
                SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                SOAPHeader header = envelope.getHeader();

                if (header == null) {
                    log.warning("No headers found in the input SOAP request");
                    return true;
                }
                Entry<String, String> entry = processSOAPHeader(header);
                if (entry != null) {
                    // Verifying Username and password
                    User user = userRegistration.getUser(entry.getKey());
                    if (user == null) {
                        return breakHandlerChain(context);
                    }
                    String p = entry.getValue();
                    if (p.contains(PASSWORD_DIGEST_SEPARATOR)) {
                        String[] parts = p.split(PASSWORD_DIGEST_SEPARATOR);
                        String nonce = parts[0];
                        String created = parts[1];
                        String passwordDigest = parts[2];
                        String generatedPasswordDigest = HashTool.hashPasswordDigest(nonce, created,
                                user.getPlainPassword());
                        if (!passwordDigest.equals(generatedPasswordDigest)) {
                            return breakHandlerChain(context);
                        }
                    } else if (!user.getPassword().equals(entry.getValue())) {
                        return breakHandlerChain(context);
                    }
                    securityProducer.setLoggedUser(user);
                }
            }
        } catch (SOAPException|IOException|NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    protected Entry<String, String> processSOAPHeader(SOAPHeader header) {
        Entry<String, String> authenticated = null;

        Iterator<SOAPElement> childElems = header.getChildElements(new QName(AUTHN_URI, AUTHN_LNAME));

        while (childElems.hasNext()) {
            SOAPElement child = childElems.next();
            authenticated = processSOAPHeaderInfo(child);
        }
        if (authenticated != null && (authenticated.getKey() == null || authenticated.getValue() == null)) {
            authenticated = null;
        }
        return authenticated;
    }

    @SuppressWarnings("unchecked")
    protected Entry<String, String> processSOAPHeaderInfo(SOAPElement e) {
        String id = null;
        String password = null;

        Iterator<SOAPElement> childElements = e.getChildElements(new QName(AUTHN_URI, "UsernameToken"));
        while (childElements.hasNext()) {
            SOAPElement usernameToken = childElements.next();

            Iterator<SOAPElement> username = usernameToken.getChildElements(new QName(AUTHN_URI, "Username"));
            Iterator<SOAPElement> childElements2 = usernameToken.getChildElements(new QName(AUTHN_URI, "Password"));

            if (username.hasNext()) {
                SOAPElement next = username.next();
                id = next.getValue();
            }
            if (childElements2.hasNext()) {
                SOAPElement next = childElements2.next();
                String type = next.getAttribute("Type");
                try {
                    if (type.equals("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest")) {
                        Iterator<SOAPElement> nonce = usernameToken.getChildElements(new QName(AUTHN_URI, "Nonce"));
                        Iterator<SOAPElement> created = usernameToken.getChildElements(new QName(
                                "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
                                "Created"));
                        String n = "";
                        String c = "";
                        if (nonce.hasNext()) {
                            n = nonce.next().getValue();
                        } else {
                            return null;
                        }
                        if (created.hasNext()) {
                            c = created.next().getValue();
                        } else {
                            return null;
                        }
                        password = n + PASSWORD_DIGEST_SEPARATOR + c + PASSWORD_DIGEST_SEPARATOR + next.getValue();
                    } else {
                        password = HashTool.hashPassword(next.getValue());
                    }
                } catch (Exception ex) {
                }
            }
        }
        return new SimpleEntry<String, String>(id, password);
    }
    
    protected boolean breakHandlerChain(SOAPMessageContext context) {
        throw new WebServiceException("HTTP Error 401 Unauthorized");
    }

    @Override
    public Set<QName> getHeaders() {
        Set<QName> headers = new HashSet<>();
        QName securityHeader = new QName(AUTHN_URI, AUTHN_LNAME, AUTHN_PREFIX);
        headers.add(securityHeader);
        return headers;
    }

}
