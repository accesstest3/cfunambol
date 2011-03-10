/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.email.pdi.mail;


import com.funambol.email.util.Def;
import java.util.ArrayList;
import java.util.List;

/**
 * An object representing a extension field.
 *
 * @version $Id: Ext.java,v 1.1 2008-03-25 11:28:19 gbmiglia Exp $
 */
public class Ext {

    /**
     * if the email is truncated
     */
    private boolean   truncated = false;

    /**
     * entire email size
     * the header size is approximated to 200b
     */
    private String    xsize = "" + (200 + Def.CONTENT_BODY.length());

    /**
     * (body size) if body is not truncated
     * (body size - filter size) if body is truncated
     */
    private String    xvalBody = "" + Def.CONTENT_BODY.length();

    /**
     *
     */
    private List      xvalAttach;

    /**
     *
     */
    private String    xnam;


    /**
     * Creates an empty mail
     */
    public Ext() {
        truncated  = false;
        xsize      = new String();
        xnam       = new String();
        xvalBody   = new String();
        xvalAttach = new ArrayList();
    }


    /**
     *
     */
    public boolean isTruncated() {
        return this.truncated;
    }

    /**
     *
     */
    public void setTruncated(boolean _truncated) {
        this.truncated = _truncated;
    }


    /**
     *
     */
    public String getXnam() {
        return this.xnam;
    }

    /**
     *
     */
    public void setXnam(String _xnam) {
        this.xnam = _xnam;
    }

    /**
     *
     */
    public String getXsize() {
        return this.xsize;
    }

    /**
     *
     */
    public void setXsize(String _xsize) {
        this.xsize = _xsize;
    }

    /**
     *
     */
    public String getXvalBody() {
        return this.xvalBody;
    }

    /**
     *
     */
    public void setXvalBody(String _xvalBody) {
        this.xvalBody = _xvalBody;
    }

    /**
     *
     */
    public List getXvalAttach() {
        return this.xvalAttach;
    }

    /**
     *
     */
    public void setXvalAttach(List _xvalAttach) {
        this.xvalAttach = _xvalAttach;
    }

    /**
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\nXNam: ").append(this.getXnam());
        sb.append("\nXVal: ").append(this.getXvalBody());
        for (int i=0; i<this.getXvalAttach().size(); i++){
           sb.append("\nXVal: ").append(this.getXvalAttach().get(i));
        }
        return sb.toString();
    }

}
