/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

package com.funambol.common.sms.core;

import com.funambol.framework.tools.DbgTools;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a generic sms message composed by:
 * <ui>
 * <li>wdp: wireless datagram protocol header</li>
 * <li>wsp: wireless session protocol header</li>
 * <li>content: sms content</li>
 * </ui>
 *
 * @version $Id: BinarySMSMessage.java,v 1.1 2008-06-30 14:22:16 nichele Exp $
 */
public class BinarySMSMessage extends SMSMessage {

    // ------------------------------------------------------------ Private Data

    private byte[] wdp;
    private byte[] wsp;
    private byte[] content;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a new BinarySMSMessage
     */
    public BinarySMSMessage(byte[] wdp, byte[] wsp, byte[] content) {
        this.wsp = wsp;
        this.wdp = wdp;
        this.content = content;
    }

    //   -------------------------------------------------------- Public Methods

    /**
     * Set the wdp
     * @param wdp the wdp to set.
     */
    public void setWdp(byte[] wdp) {
        this.wdp = wdp;
    }

    /**
     * Set the wsp
     * @param wsp the wsp to set.
     */
    public void setWsp(byte[] wsp) {
        this.wsp = wsp;
    }

    /**
     * Set the content
     * @param content the content to set.
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * Returns the wdp
     * @return the wdp.
     */
    public byte[] getWdp() {
        return wdp;
    }

    /**
     * Returns the wsp
     * @return the wsp.
     */
    public byte[] getWsp() {
        return wsp;
    }

    /**
     * Returns the content
     * @return the content.
     */
    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("wdp", wdp == null ? null : DbgTools.bytesToHex(wdp));
        sb.append("wsp", wsp == null ? null : DbgTools.bytesToHex(wsp));
        sb.append("content", content == null ? null : DbgTools.bytesToHex(content));
        return sb.toString();
    }
}