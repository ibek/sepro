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
package org.jboss.sepro.util;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.sepro.dto.User;
import org.jboss.sepro.service.IServiceLogger;
import org.jboss.sepro.service.impl.ServiceLogger;
import org.jboss.sepro.stereotype.JMS;
import org.jboss.sepro.stereotype.LoggedIn;
import org.jboss.sepro.stereotype.REST;
import org.jboss.sepro.stereotype.WS;

/**
 * This class uses CDI to alias Java EE resources, such as the persistence
 * context, to CDI beans.
 */
public class Resources {

    // use @SuppressWarnings to tell IDE to ignore warnings about field not
    // being referenced directly
    @SuppressWarnings("unused")
    @Produces
    @PersistenceContext
    private EntityManager em;
    
    private User loggedUser = new User();

    @Produces
    public Logger produceLog(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
    
    @Produces
    @REST
    @ApplicationScoped
    public IServiceLogger getRESTLogger() {
        return new ServiceLogger();
    }
    
    @Produces
    @WS
    @ApplicationScoped
    public IServiceLogger getWSLogger() {
        return new ServiceLogger();
    }
    
    @Produces
    @JMS
    @ApplicationScoped
    public IServiceLogger getJMSLogger() {
        return new ServiceLogger();
    }

    @Produces
    @LoggedIn
    @RequestScoped
    public User getLoggedUser() {
        return loggedUser;
    }

}
