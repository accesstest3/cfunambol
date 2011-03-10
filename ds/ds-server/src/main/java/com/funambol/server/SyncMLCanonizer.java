/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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

package com.funambol.server;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.core.SyncML;
import com.funambol.framework.core.Sync4jException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;


/**
 * This class is used to transform the input message into the canonical form.
 *
 * @version $Id: SyncMLCanonizer.java,v 1.1.1.1 2008-02-21 23:35:41 stefano_fornari Exp $
 */
public class SyncMLCanonizer implements Serializable {

    // ------------------------------------------------------------ Private data
    private static final FunambolLogger log = FunambolLoggerFactory.getLogger("server");

    // ------------------------------------------------------------ Constructors
    public SyncMLCanonizer() {
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Canonizes outgoing messages.
     *
     * @param message the input XML message
     *
     * @return message the input XML message canonized
     **/
    public String canonizeOutput(String message) {
        if (log.isTraceEnabled()) {
            log.trace("Starting output canonization");
        }

        message = devInfHandler(message);

        return metInfNamespaceHandler(message);
    }

    /**
     * The input message must be canonized every time before calling the XML-Java
     * mapping tool (JiBX) that parses the input and generates the SyncML object.
     *
     * @param message the XML input message
     *
     * @return the canonized XML input message
     **/
    public String canonizeInput(String message) {
        if (log.isTraceEnabled()) {
            log.trace("Starting input canonization");
        }

        message = fixXMLVersion(message);

        return message;
    }

    /**
     * Replace the '&' with "&amp;" within the message.
     *
     * @param message the original message xml
     *
     * @return the updated message
     */
    private String replaceEntity(String message) {
        return StringUtils.replace(message, "&", "&amp;");
    }

    /**
     * This is a temporary solution in order to obviate to a JiBX bug: it does
     * not allow to declare the namespace to level of structure.
     *
     * @param msg the server response
     *
     * @return the response with namespace correctly added into MetInf element
     */
    private String metInfNamespaceHandler(String msg) {
        int s = 0;
        int e = 0;

        StringBuffer response = new StringBuffer();
        while (( e = msg.indexOf("<Meta", s)) >= 0) {

            response = response.append(msg.substring(s, e));

            int a = msg.indexOf("</Meta>", e);
            String meta = msg.substring(e, a);

            meta = meta.replaceAll("<MetInf>" , "<MetInf xmlns='syncml:metinf'>");
            meta = meta.replaceAll("<Type>"   , "<Type xmlns='syncml:metinf'>");
            meta = meta.replaceAll("<Format>" , "<Format xmlns='syncml:metinf'>");
            meta = meta.replaceAll("<Mark>"   , "<Mark xmlns='syncml:metinf'>");
            meta = meta.replaceAll("<Size>"   , "<Size xmlns='syncml:metinf'>");
            meta = meta.replaceAll("<Anchor>" , "<Anchor xmlns='syncml:metinf'>");
            meta = meta.replaceAll("<Version>", "<Version xmlns='syncml:metinf'>");
            meta = meta.replaceAll("<NextNonce>" , "<NextNonce xmlns='syncml:metinf'>");
            meta = meta.replaceAll("<MaxMsgSize>", "<MaxMsgSize xmlns='syncml:metinf'>");
            meta = meta.replaceAll("<MaxObjSize>", "<MaxObjSize xmlns='syncml:metinf'>");
            meta = meta.replaceAll("<EMI>"    , "<EMI xmlns='syncml:metinf'>");
            meta = meta.replaceAll("<Mem>"    , "<Mem xmlns='syncml:metinf'>");

            s = a + 7;
            response.append(meta).append("</Meta>");
        }
        return response.append(msg.substring(s, msg.length())).toString();
    }

    private String devInfHandler(String msg) {

        msg = StringUtils.replaceOnce(msg, "<UTC></UTC>", "<UTC/>");
        msg = StringUtils.replaceOnce(msg, "<SupportLargeObjs></SupportLargeObjs>", "<SupportLargeObjs/>");
        msg = StringUtils.replaceOnce(msg, "<SupportNumberOfChanges></SupportNumberOfChanges>", "<SupportNumberOfChanges/>");


        msg = StringUtils.replaceOnce(msg, "<MoreData></MoreData>", "<MoreData/>");

        return msg;
    }

    /**
     * Replaces the XML version from any value to "1.0", which is the version
     * supported by our parser. This should not cause any problem since SyncML
     * does not use any XML 1.1 specific feature.
     *
     * @param msg the message to process
     *
     * @return the processed message
     */
    private String fixXMLVersion(String msg) {

        if (!msg.startsWith("<?xml")) {
            return msg;
        }

        int e = msg.indexOf("?>");
        if (e < 0) {
            return msg;
        }

        String prolog = msg.substring(0, e+2);

        if (log.isTraceEnabled()) {
            log.trace("prolog: " + prolog);
        }
        prolog = prolog.replaceFirst("version(\\s)*=[\"'][^\"']+[\"']", "version=\"1.0\"");

        return prolog + msg.substring(e+2);

    }
}


