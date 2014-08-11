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
package org.jboss.sepro.service.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.jboss.sepro.service.IServiceLogger;
import org.jboss.sepro.stereotype.REST;

@Provider
@Path("/status")
public class HttpStatus {

    @Inject
    @REST
    IServiceLogger slogger;
    
    @GET
    @Path("/{code}")
    public Response getStatus(@PathParam("code") int code) {
        slogger.addServiceLog(getClass().getSimpleName(), "Get status code " + code);
        if (code < 100 || code > 599) {
            code = 400;
        }
        return Response.status(code).build();
    }

}
