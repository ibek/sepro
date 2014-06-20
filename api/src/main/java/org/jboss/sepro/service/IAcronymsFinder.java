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
package org.jboss.sepro.service;

import java.util.List;

import org.jboss.sepro.dto.Acronym;
import org.jboss.sepro.dto.Dictionary;
import org.jboss.sepro.exception.DuplicateException;

public interface IAcronymsFinder {

    public void addAcronym(Acronym acronym) throws DuplicateException;

    public Acronym getFirstAcronym(String abbreviation);

    public List<Acronym> getAcronymsFor(String abbreviation);

    public List<Acronym> getAll();

    public Dictionary getDictionary();

    public void removeAcronym(Acronym acronym);

}
