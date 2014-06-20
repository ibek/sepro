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

import org.jboss.sepro.model.MAcronym;
import org.jboss.sepro.dto.Acronym;

public class AcronymDTOTransformer implements IDTOTransformer<MAcronym, Acronym> {

    @Override
    public Acronym transformTo(MAcronym model) {
        if (model == null) {
            return null;
        }
        Acronym dto = new Acronym();
        dto.setAbbreviation(model.getAbbreviation());
        dto.setMeaning(model.getMeaning());
        return dto;
    }

    @Override
    public MAcronym transformFrom(Acronym dto) {
        if (dto == null) {
            return null;
        }
        MAcronym model = new MAcronym();
        model.setAbbreviation(dto.getAbbreviation().trim());
        model.setMeaning(dto.getMeaning().trim());
        return model;
    }

}
