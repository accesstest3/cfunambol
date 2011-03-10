/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */
package com.funambol.server.engine.transformer;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.funambol.framework.engine.transformer.DataTransformer;
import com.funambol.framework.engine.transformer.TransformerException;
import com.funambol.framework.engine.transformer.TransformationInfo;

/**
 * This class is a DataTransformer that encodes the given content with the
 * cryptographic algorithm DES.
 *
 * @version $Id: DESEncoder.java,v 1.1.1.1 2008-02-21 23:35:50 stefano_fornari Exp $
 */
public class DESEncoder extends DataTransformer {

    // ------------------------------------------------------------ Constructors
    public DESEncoder() {
        super("des");
    }
    // ---------------------------------------------------------- Public methods

    public byte[] transform(byte[] data, TransformationInfo info)
    throws TransformerException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(info.getUserPassword().getBytes("UTF8"));
            byte[] keyBytes = md.digest();
            // create key
            DESKeySpec key = new DESKeySpec (keyBytes);
            SecretKeySpec DESKey = new SecretKeySpec (key.getKey(), "DES");
            // create cipher
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, DESKey);
            // encrypt info
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new TransformerException("Transformation error: ", e);
        }
    }

}
