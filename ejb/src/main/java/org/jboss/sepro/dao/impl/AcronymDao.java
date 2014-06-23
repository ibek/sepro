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
package org.jboss.sepro.dao.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.sepro.dao.IAcronymDao;
import org.jboss.sepro.dto.User;
import org.jboss.sepro.exception.DuplicateException;
import org.jboss.sepro.model.MAcronym;
import org.jboss.sepro.model.MAcronym_;
import org.jboss.sepro.stereotype.LoggedIn;

@RequestScoped
public class AcronymDao implements IAcronymDao {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    @LoggedIn
    @Any
    User user;

    @Override
    public void addAcronym(MAcronym acronym) throws DuplicateException {
        if (user != null) {
            acronym.setOwner(user.getUsername());
        }
        log.info("Adding " + acronym);
        for (MAcronym model : getAcronymsFor(acronym.getAbbreviation())) {
            if (model.getMeaning().equalsIgnoreCase(acronym.getMeaning())) {
                throw new DuplicateException();
            }
        }
        try {
            em.persist(acronym);
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Cannot create a new user due to: {0}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public MAcronym getAcronym(String abbreviation, String meaning) {
        MAcronym acronym = null;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MAcronym> cq = cb.createQuery(MAcronym.class);
        Root<MAcronym> acronyms = cq.from(MAcronym.class);

        Predicate ap = cb.equal(cb.lower(acronyms.get(MAcronym_.abbreviation)), abbreviation.trim().toLowerCase());
        Predicate mp = cb.equal(cb.lower(acronyms.get(MAcronym_.meaning)), meaning);
        Predicate op;
        if (user != null) {
            op = cb.equal(acronyms.get(MAcronym_.owner), user.getUsername());
        } else {
            op = cb.isNull(acronyms.get(MAcronym_.owner));
        }
        cq.where(cb.and(ap, mp, op));
        try {
            acronym = em.createQuery(cq).getSingleResult();
        } catch (NoResultException e) {

        }
        return acronym;
    }

    @Override
    public MAcronym getFirstAcronym(String abbreviation) {
        MAcronym acronym = null;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MAcronym> cq = cb.createQuery(MAcronym.class);
        Root<MAcronym> acronyms = cq.from(MAcronym.class);

        Predicate ap = cb.equal(cb.lower(acronyms.get(MAcronym_.abbreviation)), abbreviation.trim().toLowerCase());
        Predicate op;
        if (user != null) {
            op = cb.equal(acronyms.get(MAcronym_.owner), user.getUsername());
        } else {
            op = cb.isNull(acronyms.get(MAcronym_.owner));
        }
        cq.where(cb.and(ap, op));
        try {
            acronym = em.createQuery(cq).setMaxResults(1).getResultList().get(0);
        } catch (NoResultException e) {

        }
        return acronym;
    }

    @Override
    public List<MAcronym> getAcronymsFor(String abbreviation) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MAcronym> cq = cb.createQuery(MAcronym.class);
        Root<MAcronym> acronyms = cq.from(MAcronym.class);

        Predicate ap = cb.equal(cb.lower(acronyms.get(MAcronym_.abbreviation)), abbreviation.trim().toLowerCase());
        Predicate op;
        if (user != null) {
            op = cb.equal(acronyms.get(MAcronym_.owner), user.getUsername());
        } else {
            op = cb.isNull(acronyms.get(MAcronym_.owner));
        }
        cq.where(cb.and(ap, op));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<MAcronym> getAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MAcronym> cq = cb.createQuery(MAcronym.class);
        Root<MAcronym> acronyms = cq.from(MAcronym.class);
        if (user != null) {
            cq.where(cb.equal(acronyms.get(MAcronym_.owner), user.getUsername()));
        } else {
            cq.where(cb.isNull(acronyms.get(MAcronym_.owner)));
        }
        return em.createQuery(cq).getResultList();
    }

    @Override
    public void removeAcronym(MAcronym acronym) {
        if (acronym.getOwner() != null && !acronym.getOwner().equals(user.getUsername())) {
            return;
        }
        if (acronym != null && !em.contains(acronym)) {
            em.merge(acronym);
        }
        em.remove(acronym);
    }

}
