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

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(name = Timeout.WS_NAME, serviceName = Timeout.WS_SERVICE_NAME, targetNamespace = Timeout.WS_NAMESPACE)
@HandlerChain(file="/handler-chain.xml")
public class Timeout {

    public final static String WS_NAME = "Timeout";
    public final static String WS_SERVICE_NAME = "TimeoutService";
    public final static String WS_NAMESPACE = "http://sepro.jboss.org";
    
    @WebMethod
    public void wait(@WebParam(name = "millis") int millis) {
        if (millis < 1) {
            return;
        } else if (millis > 60000) {
            millis = 60000;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {

        }
    }

}
