/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.email.model;

import javax.mail.Message;
import javax.activation.DataHandler;

import com.funambol.email.util.Def;

/**
 *
 * @version $Id: InternalPart.java,v 1.3 2008-06-24 11:15:35 testa Exp $
 */
public class InternalPart {

    /**     */
    private Object  partvalue    = Def.CONTENT_BODY;

    /**   part content type */
    private String  contenttype  = Def.CONTENT_CONTENTTYPE;

    /**   transfert encoding */
    private String  transfertenc = Def.CONTENT_TRANSFERTENC;

    /**   part disposition */
    private String  disposition = null ;

    /**   file name .. if part is an attachment */
    private String  filename = null ;

    /**   part disposition */
    private DataHandler  dhandler = null ;

    /**    */
    private boolean  isTextBody      = false ;

    /**    */
    private boolean  isHtmlBody      = false ;

    /** if the email has a text/calendar content type  */
    private boolean  isTextCalendar  = false ;

    /** int size of the part */
    private int     size        = 0 ;

    /** 
     * Index of this part. Is used by the content Provider.
     * Values start from 0
     * A value of -1 indicates that the index was not set
     */
    private int index = -1;
    
    /**
     *
     */
    public InternalPart(){
    }

    /**
     *
     */
    public InternalPart(Object _partvalue,
                        String _contenttype,
                        String _transfertenc,
                        String _disposition,
                        String _filename,
                        DataHandler _dhandler,
                        boolean _isTextBody,
                        boolean _isHtmlBody,
                        boolean _isTextCalendar,
                        int _size) {
        this.partvalue      = _partvalue;
        this.contenttype    = _contenttype;
        this.transfertenc   = _transfertenc;
        this.disposition    = _disposition;
        this.filename       = _filename;
        this.dhandler       = _dhandler;
        this.isTextBody     = _isTextBody;
        this.isHtmlBody     = _isHtmlBody;
        this.isTextCalendar = _isTextCalendar;
        this.size           = _size;
    }

    /**
     * Returns the partvalue of the Message
     * @return the partvalue of the Message
     */
    public Object getPartValue() {
        return this.partvalue;
    }

    /**
     * Returns the contentType of the Message
     * @return the contentType of the Message
     */
    public String getContentType() {
        return this.contenttype;
    }

    /**
     * Returns the transfertenc of the Message
     * @return the transfertenc of the Message
     */
    public String getTransfertEnc() {
        return this.transfertenc;
    }

    /**
     * Returns the disposition of the Part
     * @return the disposition of the Part
     */
    public String getDisposition() {
        return this.disposition;
    }

    /**
     * Returns the filename of the Attachment
     * @return the filename of the Attachment
     */
    public String getFileName() {
        return this.filename;
    }

    /**
     * Returns the dhandler of the Part
     * @return the dhandler of the Part
     */
    public DataHandler getDHandler() {
        return this.dhandler;
    }


    /**
     * @return the flag
     */
    public boolean isTextBody() {
        return this.isTextBody;
    }

    /**
     * @return the flag
     */
    public boolean isHtmlBody() {
        return this.isHtmlBody;
    }

    /**
     * @return the flag
     */
    public boolean isTextCalendar() {
        return this.isTextCalendar;
    }

    /**
     * Returns the size of this Body
     * @return the size of this Body
     */
    public int getSize() {
        return this.size;
    }

    public int getIndex() {
        return index;
    }
        
    /**
     *
     */
    public void setPartValue(Object _partvalue) {
        this.partvalue = _partvalue;
    }

    /**
     *
     */
    public void setContentType(String _contenttype) {
        this.contenttype = _contenttype;
    }

    /**
     *
     */
    public void setTransfertEnc(String _transfertenc) {
        this.transfertenc = _transfertenc;
    }

    /**
     *
     */
    public void setDisposition(String _disposition) {
        this.disposition = _disposition;
    }

    /**
     *
     */
    public void setFileName(String _filename) {
        this.filename = _filename;
    }

    /**
     *
     */
    public void setDHandler(DataHandler _dhandler) {
        this.dhandler = _dhandler;
    }

    /**
     *
     */
    public void setIsTextBody(boolean _isTextBody) {
        this.isTextBody = _isTextBody;
    }

    /**
     *
     */
    public void setIsHtmlBody(boolean _isHtmlBody) {
        this.isHtmlBody = _isHtmlBody;
    }

    /**
     *
     */
    public void setIsTextCalendar(boolean _isTextCalendar) {
        this.isTextCalendar = _isTextCalendar;
    }

    /**
     *
     */
    public void setSize(int _size) {
        this.size = _size;
    }

    public void setIndex(int index) {
        this.index = index;
    }
        
    /**
     *
     */
    public void cloneIP(InternalPart _ip) {
        this.partvalue      = _ip.getPartValue();
        this.contenttype    = _ip.getContentType();
        this.disposition    = _ip.getDisposition();
        this.filename       = _ip.getFileName();
        this.dhandler       = _ip.getDHandler();
        this.isTextBody     = _ip.isTextBody();
        this.isHtmlBody     = _ip.isHtmlBody();
        this.isTextCalendar = _ip.isTextCalendar();
        this.size           = _ip.getSize();
        this.index          = _ip.getIndex();
    }

}
