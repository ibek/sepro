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
package org.jboss.sepro.model.transform;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.sepro.model.MUser;
import org.jboss.sepro.util.HashTool;
import org.jboss.sepro.dto.User;

public class UserDTOTransformer implements IDTOTransformer<MUser, User> {

    @Inject
    Logger log;

    @Override
    public User transformTo(MUser model) {
        if (model == null) {
            return null;
        }
        User dto = new User();
        dto.setUsername(model.getUsername());
        dto.setPassword(model.getPassword());
        dto.setPlainPassword(model.getPlainPassword());
        return dto;
    }

    @Override
    public MUser transformFrom(User dto) {
        if (dto == null) {
            return null;
        }
        MUser model = new MUser();
        model.setUsername(dto.getUsername().trim());
        try {
            model.setPlainPassword(dto.getPassword().trim());
            model.setPassword(HashTool.hashPassword(dto.getPassword().trim()));
        } catch (Exception ex) {
            log.severe("Hash generation error for password");
            log.throwing(UserDTOTransformer.class.getName(), "transformFrom", ex);
            return null;
        }
        return model;
    }

}
