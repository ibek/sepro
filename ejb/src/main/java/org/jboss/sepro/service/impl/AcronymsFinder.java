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
package org.jboss.sepro.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.sepro.dao.IAcronymDao;
import org.jboss.sepro.model.MAcronym;
import org.jboss.sepro.model.transform.AcronymDTOTransformer;
import org.jboss.sepro.dto.Acronym;
import org.jboss.sepro.dto.Dictionary;
import org.jboss.sepro.exception.DuplicateException;
import org.jboss.sepro.service.IAcronymsFinder;

@Stateless
public class AcronymsFinder implements IAcronymsFinder {

    @Inject
    IAcronymDao acronymDao;

    @Inject
    AcronymDTOTransformer transformer;

    @Inject
    private Event<Acronym> acronymEventSrc;

    @Override
    public void addAcronym(Acronym acronym) throws DuplicateException {
        acronymDao.addAcronym(transformer.transformFrom(acronym));
        acronymEventSrc.fire(acronym);
    }

    @Override
    public Acronym getFirstAcronym(String abbreviation) {
        return transformer.transformTo(acronymDao.getFirstAcronym(abbreviation));
    }

    @Override
    public List<Acronym> getAcronymsFor(String abbreviation) {
        List<Acronym> acronyms = new ArrayList<Acronym>();
        for (MAcronym model : acronymDao.getAcronymsFor(abbreviation)) {
            acronyms.add(transformer.transformTo(model));
        }
        return acronyms;
    }

    @Override
    public List<Acronym> getAll() {
        List<Acronym> all = new ArrayList<Acronym>();
        for (MAcronym model : acronymDao.getAll()) {
            all.add(transformer.transformTo(model));
        }
        return all;
    }

    @Override
    public Dictionary getDictionary() {
        Map<String, List<Acronym>> dict = new HashMap<>();
        for (Acronym acronym : getAll()) {
            String key = acronym.getAbbreviation();
            if (!dict.containsKey(key)) {
                dict.put(key, new ArrayList<Acronym>());
            }
            List<Acronym> list = dict.get(key);
            list.add(acronym);
        }
        Dictionary dictionary = new Dictionary();
        dictionary.setMap(dict);
        return dictionary;
    }

    @Override
    public void removeAcronym(Acronym acronym) {
        MAcronym model = acronymDao.getAcronym(acronym.getAbbreviation(), acronym.getMeaning());
        if (model != null) {
            acronymDao.removeAcronym(model);
        }
    }

}
