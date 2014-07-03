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
package org.jboss.sepro.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class HashTool {

    public static String hashPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] hash = MessageDigest.getInstance("sha-256").digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String hashPasswordDigest(String nonce, String timestamp, String password)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ByteBuffer buf = ByteBuffer.allocate(1000);
        buf.put(Base64.decodeBase64(nonce));
        buf.put(timestamp.getBytes("UTF-8"));
        buf.put(password.getBytes("UTF-8"));
        byte[] toHash = new byte[buf.position()];
        buf.rewind();
        buf.get(toHash);
        return Base64.encodeBase64String(MessageDigest.getInstance("sha-1").digest(toHash)).trim();
    }

}
