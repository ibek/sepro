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

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.jboss.sepro.dto.Acronym;
import org.jboss.sepro.dto.Dictionary;
import org.jboss.sepro.exception.DuplicateException;
import org.jboss.sepro.service.IAcronymsFinder;

@Provider
@Path("/acronym")
public class AcronymsDictionary {

    @Inject
    IAcronymsFinder acronymFinder;

    @POST
    @Path("/")
    @Consumes({ "application/xml", "application/json" })
    public Response addAcronym(Acronym acronym) {
        try {
            acronymFinder.addAcronym(acronym);
            return Response.status(Status.CREATED).build();
        } catch (DuplicateException ex) {
            return Response.status(Status.CONFLICT).build();
        }
    }

    @GET
    @Path("/")
    @Produces({ "application/xml", "application/json" })
    public Dictionary getDictionary() {
        return acronymFinder.getDictionary();
    }

    @GET
    @Path("/{abbreviation}")
    @Produces({ "application/xml", "application/json" })
    public List<Acronym> getAcronyms(@PathParam("abbreviation") String abbreviation) {
        return acronymFinder.getAcronymsFor(abbreviation);
    }

    @DELETE
    @Consumes({ "application/xml", "application/json" })
    public void removeAcronym(Acronym acronym) {
        acronymFinder.removeAcronym(acronym);
    }

}
