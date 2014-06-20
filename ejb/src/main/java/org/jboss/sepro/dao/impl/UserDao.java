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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.sepro.dao.IUserDao;
import org.jboss.sepro.model.MUser;
import org.jboss.sepro.exception.DuplicateException;

public class UserDao implements IUserDao {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Override
    public void addUser(MUser user) throws DuplicateException {
        log.info("Registering " + user.getUsername());
        MUser u = getUser(user.getUsername());
        if (u == null) {
            try {
                em.persist(user);
            } catch (Exception ex) {
                log.log(Level.SEVERE, "Cannot create a new user due to: {0}", ex.getMessage());
                throw ex;
            }
        } else {
            throw new DuplicateException();
        }
    }

    @Override
    public List<MUser> getAll() {
        return em.createQuery("SELECT u FROM SerProUser u ORDER BY u.username", MUser.class).getResultList();
    }

    @Override
    public MUser getUser(String username) {
        MUser g = null;
        try {
            g = em.createQuery("SELECT u FROM SerProUser u WHERE u.username = :username", MUser.class)
                    .setParameter("username", username).getSingleResult();
        } catch (NoResultException e) {

        }
        return g;
    }

    @Override
    public void removeUser(MUser user) {
        if (user != null && !em.contains(user)) {
            em.merge(user);
        }
        em.remove(user);
    }

}
