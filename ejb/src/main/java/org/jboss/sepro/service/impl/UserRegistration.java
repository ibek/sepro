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
import java.util.List;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.sepro.dao.IUserDao;
import org.jboss.sepro.model.MUser;
import org.jboss.sepro.model.transform.UserDTOTransformer;
import org.jboss.sepro.dto.User;
import org.jboss.sepro.exception.DuplicateException;
import org.jboss.sepro.service.IUserRegistration;

@Stateless
@DeclareRoles({ "ADMIN" })
@PermitAll
public class UserRegistration implements IUserRegistration {

    @Inject
    IUserDao userDao;

    @Inject
    UserDTOTransformer transformer;

    @Inject
    private Event<User> userEventSrc;

    @Override
    public void registerUser(User user) throws DuplicateException {
        userDao.addUser(transformer.transformFrom(user));
        userEventSrc.fire(user);
    }

    @Override
    public User getUser(String username) {
        return transformer.transformTo(userDao.getUser(username));
    }

    @Override
    public List<User> getAllUsers() {
        List<User> all = new ArrayList<User>();
        for (MUser model : userDao.getAll()) {
            all.add(transformer.transformTo(model));
        }
        return all;
    }

    @Override
    @RolesAllowed("ADMIN")
    public void removeUser(User user) {
        userDao.removeUser(userDao.getUser(user.getUsername()));
    }

}
