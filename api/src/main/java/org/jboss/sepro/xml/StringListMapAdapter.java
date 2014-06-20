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
package org.jboss.sepro.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.jboss.sepro.xml.jaxb.JaxbStringListMap;
import org.jboss.sepro.xml.jaxb.JaxbStringListMapEntry;

public class StringListMapAdapter<T> extends XmlAdapter<JaxbStringListMap<T>, Map<String, List<T>>> {

    @Override
    public JaxbStringListMap<T> marshal(Map<String, List<T>> map) throws Exception {
        if (map == null) {
            return null;
        }
        JaxbStringListMap<T> xmlMap = new JaxbStringListMap<>();
        for (Entry<String, List<T>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<T> value = entry.getValue();

            JaxbStringListMapEntry<T> xmlEntry = new JaxbStringListMapEntry<>();
            xmlEntry.setKey(key);
            xmlEntry.setValue(value);
            xmlMap.addEntry(xmlEntry);
        }
        return xmlMap;
    }

    @Override
    public Map<String, List<T>> unmarshal(JaxbStringListMap<T> xmlMap) throws Exception {
        if (xmlMap == null) {
            return null;
        }
        Map<String, List<T>> map = new HashMap<>();
        for (JaxbStringListMapEntry<T> xmlEntry : xmlMap.getEntries()) {
            String key = xmlEntry.getKey();
            map.put(key, xmlEntry.getValue());
        }
        return map;
    }

}
