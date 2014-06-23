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

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.jboss.sepro.dto.Acronym;
import org.jboss.sepro.dto.Dictionary;
import org.jboss.sepro.exception.DuplicateException;
import org.jboss.sepro.service.IAcronymsFinder;

@WebService(name = AcronymsDictionary.WS_NAME, serviceName = AcronymsDictionary.WS_SERVICE_NAME, targetNamespace = AcronymsDictionary.WS_NAMESPACE)
@HandlerChain(file="/handler-chain.xml")
public class AcronymsDictionary {

    public final static String WS_NAME = "AcronymsDictionary";
    public final static String WS_SERVICE_NAME = "AcronymsDictionaryService";
    public final static String WS_NAMESPACE = "http://sepro.jboss.org";

    @Resource
    private WebServiceContext context;

    @Inject
    IAcronymsFinder acronymFinder;

    @WebMethod
    public void addAcronym(@WebParam(name = "acronym", targetNamespace = "http://sepro.jboss.org") Acronym acronym) {
        MessageContext ctx = context.getMessageContext();
        HttpServletResponse response = (HttpServletResponse) ctx.get(MessageContext.SERVLET_RESPONSE);
        try {
            try {
                acronymFinder.addAcronym(acronym);
                response.sendError(201); // CREATED
            } catch (DuplicateException ex) {
                response.sendError(409);
            }
        } catch (Exception ex) {

        }
    }

    @WebMethod
    @WebResult(name = "dictionary")
    public Dictionary getDictionary() {
        return acronymFinder.getDictionary();
    }
    
    @WebMethod
    @WebResult(name = "acronym")
    public List<Acronym> getAcronyms(@WebParam(name = "abbreviation") String abbreviation) {
        return acronymFinder.getAcronymsFor(abbreviation);
    }
    
    @WebMethod
    public void removeAcronym(@WebParam(name = "acronym", targetNamespace = "http://sepro.jboss.org") Acronym acronym) {
        acronymFinder.removeAcronym(acronym);
    }

}
