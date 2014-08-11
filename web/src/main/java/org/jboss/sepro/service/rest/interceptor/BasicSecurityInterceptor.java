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
package org.jboss.sepro.service.rest.interceptor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.SecurityPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.util.Base64;
import org.jboss.sepro.dto.User;
import org.jboss.sepro.service.IServiceLogger;
import org.jboss.sepro.service.IUserRegistration;
import org.jboss.sepro.service.producer.SecurityProducer;
import org.jboss.sepro.stereotype.REST;
import org.jboss.sepro.util.HashTool;

/**
 * BasicSecurityInterceptor is used when "security" parameter is set to "basic".
 * 
 * e.g. http://localhost:8080/service-provider/rest/echo?security=basic
 * 
 * To logout, use
 * http://foo@localhost:8080/service-provider/rest/echo/ping?security=basic
 */
@Singleton
@Provider
@ServerInterceptor
@SecurityPrecedence
public class BasicSecurityInterceptor implements PreProcessInterceptor {
    
    @Inject
    @REST
    IServiceLogger slogger;

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    @SuppressWarnings("serial")
    private static final ServerResponse AUTH_REQUIRED = new ServerResponse("Authentication required for this resource",
            401, new Headers<Object>() {
                {
                    add("WWW-Authenticate", "Basic realm=\"Service Provider\"");
                }
            });
    private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401,
            new Headers<Object>());
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", 403,
            new Headers<Object>());
    private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", 500,
            new Headers<Object>());

    @Inject
    IUserRegistration userRegistration;

    @Inject
    SecurityProducer securityProducer;

    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod methodInvoked) throws Failure,
            WebApplicationException {
        Method method = methodInvoked.getMethod();
        String security = request.getUri().getQueryParameters().getFirst("security");

        if (security == null || !security.equals("basic")) {
            return null;
        }
        // Access allowed for all
        if (method.isAnnotationPresent(PermitAll.class)) {
            return null;
        }
        // Access denied for all
        if (method.isAnnotationPresent(DenyAll.class)) {
            return ACCESS_FORBIDDEN;
        }

        // Get request headers
        final HttpHeaders headers = request.getHttpHeaders();

        // Fetch authorization header
        final List<String> authorization = headers.getRequestHeader(AUTHORIZATION_PROPERTY);

        // If no authorization information present; block access
        if (authorization == null || authorization.isEmpty()) {
            slogger.addServiceLog("BasicSecurity", "No authorization information present, hence blocking access.");
            return AUTH_REQUIRED;
        }

        // Get encoded username and password
        final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

        // Decode username and password
        String usernameAndPassword;
        try {
            usernameAndPassword = new String(Base64.decode(encodedUserPassword));
        } catch (IOException e) {
            return SERVER_ERROR;
        }

        // Split username and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        if (tokenizer.countTokens() != 2) {
            return AUTH_REQUIRED;
        }
        final String username = tokenizer.nextToken();
        String password;
        try {
            password = HashTool.hashPassword(tokenizer.nextToken());
        } catch (Exception e) {
            return ACCESS_DENIED;
        }

        // Verifying Username and password
        User user = userRegistration.getUser(username);
        System.out.println(user);
        if (user == null || !user.getPassword().equals(password)) {
            slogger.addServiceLog("BasicSecurity", "Access denied for " + username);
            return ACCESS_DENIED;
        }
        securityProducer.setLoggedUser(user);

        // Return null to continue request processing
        return null;
    }
}
