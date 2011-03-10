/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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
package com.funambol.common.media.file.parser;

import com.funambol.framework.tools.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import junit.framework.TestCase;

/**
 * Base64Decoder's test cases
 * @version $Id: Base64DecoderTest.java 33362 2010-01-21 10:37:12Z testa $
 */
public class Base64DecoderTest extends TestCase {
    
    public Base64DecoderTest(String testName) {
        super(testName);
    }

    /**
     * Test of write method, of class Base64FilterOutputStreamDecoder.
     */
    public void testWrite() throws IOException {

        String s = "This is a pretty long test used to test ferfef return" +
                   "x x x x x x x x x x x xwemferpr ogkrpgkorpgok rgkrpog" +
                   "erpok eogepgokepgokrpgokwpowep wpog krpok gpogk pk gp" +
                   " fegpgokprgr ogrp pek pwekf pck epf epok porgk pp rt " +
                   " ewrwkì wrf okpfok pwokdeokfr kg ergi eoifk ef ke fko" +
                   "dwp dlw relrp oekgtpoe r kggok wpokrpo krpeo gkkp k c" +
                   "234 pokf03irkfopfk 059gkfpok 3dk34r0l4lfflv, v  pvepf" +
                   "erpok eogepgokepgokrpgokwpowep wpog krpok gpogk pk gp" +
                   " fegpgokprgr ogrp pek pwekf pck epf epok porgk pp rt " +
                   " ewrwkì wrf okpfok pwokdeokfr kg ergi eoifk ef ke fko" +
                   "dwp dlw relrp oekgtpoe r kggok wpokrpo krpeo gkkp k c" +
                   "234 pokf03irkfopfk 059gkfpok 3dk34r0l4lfflv, v  pvepf" +
                   "erpok eogepgokepgokrpgokwpowep wpog krpok gpogk pk gp" +
                   " fegpgokprgr ogrp pek pwekf pck epf epok porgk pp rt " +
                   " ewrwkì wrf okpfok pwokdeokfr kg ergi eoifk ef ke fko" +
                   "dwp dlw relrp oekgtpoe r kggok wpokrpo krpeo gkkp k c" +
                   "234 pokf03irkfopfk 059gkfpok 3dk34r0l4lfflv, v  pvepf" +
                   "erpok eogepgokepgokrpgokwpowep wpog krpok gpogk pk gp";

        String b64 = new String(Base64.encode(s.getBytes()));
        byte[] b = b64.getBytes();

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Base64Decoder str = new Base64Decoder(bout);
        for (byte b1 : b) {
            str.write(b1);
        }

        byte[] result = bout.toByteArray();
        String sResult = new String(result);
        assertEquals("Wrong result", s, sResult);
    }


}
