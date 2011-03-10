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
package com.funambol.email.items.manager;


import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.common.PropertyWithTimeZone;
import com.funambol.common.pim.converter.BaseConverter;
import com.funambol.common.pim.converter.ConverterException;
import com.funambol.common.pim.converter.VCalendarConverter;
import com.funambol.common.pim.icalendar.ICalendarParser;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.common.pim.utility.TimeUtils;
import com.funambol.email.exception.EntityException;
import com.funambol.email.model.InternalPart;
import com.funambol.email.pdi.mail.Ext;
import com.funambol.email.util.Def;
import com.funambol.email.util.Utility;
import com.funambol.email.util.UtilityDate;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.Base64;
import java.awt.datatransfer.DataFlavor;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.DataHandler;
import org.clapper.util.html.HTMLUtil;

/**
 *
 * This class is used in order to create an email using the list
 * of InternalPart items.
 * this class is an helper for the EntityManagerFilter class
 *
 * @version $Id: MessageCreator.java,v 1.13 2008-09-04 09:33:42 testa Exp $
 *
 */

public class MessageCreator {

    public static final String TOKEN_PARAM_NAME = "AUTH";
    public static final String USERID_PARAM_NAME = "USER";
    public static final String INDEX_PARAM_NAME = "INDEX";
    public static final String UNKNOWN_MIME_TYPE = "";
    public static final int    TOKEN_PARAM_NAME_LENGTH = 50;

    private static final String PRODID_PROPERTY_NAME = "PRODID:";

    private static final String PRODID_OUTLOOK      = "Outlook";
    private static final String PRODID_OUTLOOK_2007 = "Outlook 12";
    private static final String PRODID_MOZILLA      = "Mozilla";

    private static final String PROPERTY_VALUE_NOT_AVALIABLE = "N/A";

    //---------------------------------------------------------------- CONSTANTS

    /**     */
    private static FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);


    //----------------------------------------------------------- PUBLIC METHODS

    /**
     *
     * this method checks if message contains non-ASCII characters
     * and set the correct encoding type
     * It's a public method because there is an external unit test
     *
     * @param newMsg Message
     * @return encodingType String
     */
    public static String getEncodingType(MimeMessage newMsg)
    throws EntityException {

        // default
        String encodingType = Def.ENCODE_QUOTED_PRINTABLE;

        String transfertEnc = Utility.getHeaderTransfertEncoding(newMsg);
        String contentType  = Utility.getHeaderCharset(newMsg);

        if (transfertEnc != null){
            if (transfertEnc.indexOf("quoted-printable") != -1 ||
                    transfertEnc.indexOf("QUOTED-PRINTABLE") != -1 ){
                // don't apply a double encoding
                return null;
            }
        }

        if (contentType != null){
            if (contentType.indexOf("UTF-8") != -1 ||
                    contentType.indexOf("UTF8") != -1 ){
                // don't apply a double encoding
                return null;
            }
        }

        return encodingType;

    }


    /**
     * create an Ext object with the following info
     * <br>
     * - truncated
     * <br>
     * - body size
     * <br>
     * - all info about attachments
     *
     * @param ips part list ArrayList
     * @param headersDimension int
     * @return ext Ext
     */
    public static Ext getExtComplete(ArrayList ips, int headersDimension,
            ContentProviderInfo contentProviderInfo)
    throws EntityException {

        Ext ext       = new Ext();
        int totalSize = 0;
        int itemSize  = 0;
        boolean isTextBodyAlreadyGot = false;

        try {

            // truncated
            ext.setTruncated(true);

            // body & attachments
            ArrayList xvalList = new ArrayList();
            InternalPart item = null;
            String[] value = null;
            for (int i=0; i < ips.size(); i++){

                item = (InternalPart)ips.get(i);

                itemSize = item.getSize();

                if (item.isTextBody()){
                    if (!isTextBodyAlreadyGot){
                        ext.setXvalBody(""+item.getSize());
                        totalSize = totalSize + itemSize;

                        isTextBodyAlreadyGot = true;
                    } else{
                        // test 009
                        // it's a part in txt but is not the main body
                        // and is not empty
                        if (((String) item.getPartValue()) == null ||
                                ((String) item.getPartValue()).equalsIgnoreCase("")) {
                            // some email have an attachment empty issue:4245
                        } else {
                            item.setDisposition(MimeBodyPart.ATTACHMENT);
                            item.setFileName("Part00" + i + ".txt");
                        }
                    }

                }

                // get html only if there is no TXT body
                if (item.isHtmlBody()) {
                    if (!isTextBodyAlreadyGot) {
                        ext.setXvalBody(Integer.toString(itemSize));
                        totalSize = totalSize + itemSize;
                    }
                }

                boolean testForInline =
                        item.getDisposition() != null &&
                        item.getDisposition().equalsIgnoreCase(MimeBodyPart.INLINE);
                boolean testForAttachment =
                        item.getDisposition() != null &&
                        item.getDisposition().equalsIgnoreCase(MimeBodyPart.ATTACHMENT);
                boolean testForRFC822 =
                        item.getContentType().startsWith(Def.CONTENT_CONTENTTYPE_RFC822) &&
                        (!testForInline && !testForAttachment);
                // ext
                if (item.getContentType().startsWith(Def.CONTENT_CONTENTTYPE_RFC822) ||
                        testForAttachment){

                    // create the ext
                    value = getExtValue(item, contentProviderInfo);

                    xvalList.add(value);
                    totalSize = totalSize + itemSize;
                }

                // ext
                if (!item.isTextBody() && testForInline){

                    // create the ext
                    value = getExtValue(item, contentProviderInfo);

                    xvalList.add(value);
                    totalSize = totalSize + itemSize;
                }

            }
            ext.setXvalAttach(xvalList);

            if (ext.isTruncated()){
                ext.setXsize("" +  (totalSize + headersDimension));
            }

        } catch (Exception e){
            throw new EntityException("Error creating the Ext property", e);
        }

        return ext;

    }



    /**
     *
     * @param ips
     * @param numBytes
     * @param ext
     * @param headersDimension
     * @throws com.funambol.email.exception.EntityException
     * @return list of the part of the email
     */
    public static ArrayList getHBA_GetParts(ArrayList ips,
            int numBytes,
            Ext ext,
            int headersDimension,
            ContentProviderInfo contentProviderInfo)
            throws EntityException {

        InternalPart item                 = null;
        ArrayList    newMsgParts          = new ArrayList();
        boolean      isTextBodyAlreadyGot = false;
        boolean      isHtmlBodyAlreadyGot = false;
        int          htmlBodySize         = 0;
        String[]     value                = null;
        ArrayList    xvalList             = new ArrayList();
        int          itemSize             = 0;
        int          totalSize            = 0;

        for (int i=0; i<ips.size(); i++){

            item = (InternalPart)ips.get(i);

            itemSize = item.getSize();

            if (item.isTextBody()){
                if (!isTextBodyAlreadyGot){

                    // fix the issue about the <blocked:: tag
                    String body = String.valueOf(item.getPartValue().toString());
                    body = removeBlockedTag(body);
                    item.setPartValue(body);

                    handleTextCalendarItem(item);

                    //
                    // handleTextCalendarItem could have changed the size of
                    // the item
                    //
                    itemSize = item.getSize();

                    if ( numBytes < (totalSize + item.getSize()) ){
                        newMsgParts.add(truncBody(item, numBytes, ext, itemSize));
                    } else {
                        newMsgParts.add(item);
                    }

                    if (isHtmlBodyAlreadyGot){
                        totalSize = totalSize - htmlBodySize;
                        totalSize = totalSize + item.getSize();
                    }

                    totalSize = totalSize + itemSize;
                    isTextBodyAlreadyGot = true;
                } else{
                    // test 009
                    // it's a part in txt but is not the main body
                    if ( ((String)item.getPartValue()) == null ||
                            ((String)item.getPartValue()).equalsIgnoreCase("") ) {
                        // some email have an attachment empty issue:4245
                    } else {
                        item.setDisposition(MimeBodyPart.ATTACHMENT);
                        item.setFileName("Part00" + i + ".txt");
                    }
                }
            }

            if (item.isHtmlBody()){
                if (!isTextBodyAlreadyGot){

                    //
                    // - convert html
                    // - set size to the converted part
                    // - trucate part (if necessary)
                    //
                    String body = convertHtmlBody(item);

                    //
                    // fix the issue about the <blocked:: tag
                    //
                    body = removeBlockedTag(body);
                    item.setPartValue(body);

                    itemSize = body.getBytes().length;
                    item.setSize(itemSize);

                    if ( numBytes < (totalSize + item.getSize()) ){
                        newMsgParts.add(truncBody(item, numBytes, ext, itemSize));
                    } else {
                        newMsgParts.add(item);
                    }
                    totalSize = totalSize + itemSize;
                    isHtmlBodyAlreadyGot = true;
                    htmlBodySize = item.getSize();
                    //totalSize = totalSize + item.getSize();
                }
            }

            boolean testForInline =
                    item.getDisposition() != null &&
                    item.getDisposition().equalsIgnoreCase(MimeBodyPart.INLINE);
            boolean testForAttachment =
                    item.getDisposition() != null &&
                    item.getDisposition().equalsIgnoreCase(MimeBodyPart.ATTACHMENT);
            boolean testForRFC822 =
                    item.getContentType().startsWith(Def.CONTENT_CONTENTTYPE_RFC822) &&
                    (!testForInline && !testForAttachment);

            if (!item.isTextBody() && testForInline){

                if ( numBytes < (totalSize + item.getSize()) ){
                    // don't get the attachment, but set the ext
                    ext.setTruncated(true);

                    // create the ext
                    try {
                        value = getExtValue(item, contentProviderInfo);
                    } catch (Exception e) {
                        throw new EntityException("Error while creating ext information", e);
                    }

                    xvalList.add(value);
                } else {
                    // don't set the ext, but get the attachment
                    newMsgParts.add(item);
                    //totalSize = totalSize + item.getSize(); // @TODO test carefully
                }
                totalSize = totalSize + item.getSize();
            }

            if (testForRFC822 || testForAttachment){

                if ( numBytes < (totalSize + item.getSize()) ){
                    // don't get the attachment, but set the ext
                    ext.setTruncated(true);

                    // create the ext
                    try {
                        value = getExtValue(item, contentProviderInfo);
                    } catch (Exception e) {
                        throw new EntityException("Error while creating ext information", e);
                    }

                    xvalList.add(value);
                } else {
                    // don't set the ext, but get the attachment
                    newMsgParts.add(item);
                    //totalSize = totalSize + item.getSize();
                }
                totalSize = totalSize + item.getSize(); // @TODO test carefully
            }

        }

        ext.setXvalAttach(xvalList);

        if (ext.isTruncated()){
            ext.setXsize("" +  (totalSize + headersDimension));
        }

        return newMsgParts;

    }


    /**
     *
     * @param ips
     * @param ext
     * @param headersDimension
     * @throws com.funambol.email.exception.EntityException
     * @return list with all parts of the email
     */
    public static ArrayList getHBA_GetParts(ArrayList ips,
            Ext ext,
            int headersDimension)
            throws EntityException {

        InternalPart item                 = null;
        ArrayList    newMsgParts          = new ArrayList();
        boolean      isTextBodyAlreadyGot = false;
        int          itemSize             = 0;
        int          totalSize            = 0;

        for (int i=0; i<ips.size(); i++){

            item = (InternalPart)ips.get(i);

            itemSize = item.getSize();

            // get the body
            if (item.isTextBody()){
                if (!isTextBodyAlreadyGot){

                    // fix the issue about the <blocked:: tag
                    String body = String.valueOf(item.getPartValue().toString());
                    body = removeBlockedTag(body);
                    item.setPartValue(body);

                    handleTextCalendarItem(item);

                    //
                    // handleTextCalendarItem could have changed the size of
                    // the item
                    //
                    itemSize = item.getSize();

                    newMsgParts.add(item);
                    totalSize = totalSize + itemSize;
                    isTextBodyAlreadyGot = true;

                } else{
                    // test 009
                    // it's a part in txt but is not the main body
                    if ( ((String)item.getPartValue()) == null ||
                            ((String)item.getPartValue()).equalsIgnoreCase("") ) {
                        // some email have an attachment empty issue:4245
                    } else {
                        item.setDisposition(MimeBodyPart.ATTACHMENT);
                        item.setFileName("Part00"+i+".txt");
                    }
                }
            }

            // get html only if there is no TXT body
            if (item.isHtmlBody()){
                if (!isTextBodyAlreadyGot){

                    //
                    // - convert html
                    // - set size to the converted part
                    // - trucate part (if necessary)
                    //
                    String body = convertHtmlBody(item);
                    //
                    // fix the issue about the <blocked:: tag
                    body = removeBlockedTag(body);
                    item.setPartValue(body);

                    itemSize = body.getBytes().length;
                    item.setSize(itemSize);

                    newMsgParts.add(item);
                    totalSize = totalSize + itemSize;
                }
            }

            // @todo: verify those tests
            boolean testForInline =
                    item.getDisposition() != null &&
                    item.getDisposition().equalsIgnoreCase(MimeBodyPart.INLINE);
            boolean testForAttachment =
                    item.getDisposition() != null &&
                    item.getDisposition().equalsIgnoreCase(MimeBodyPart.ATTACHMENT);
            boolean testForRFC822 =
                    item.getContentType().startsWith(Def.CONTENT_CONTENTTYPE_RFC822) &&
                    (!testForInline && !testForAttachment);

            // original test was: !item.isTextBody() && testForInline
            if (testForRFC822 || (!item.isTextBody() && testForInline)){
                newMsgParts.add(item);
                totalSize = totalSize + itemSize;
            }

            // get all the attachments
            if (testForAttachment){
                newMsgParts.add(item);
                totalSize = totalSize + itemSize;
            }

        }

        if (ext.isTruncated()){
            ext.setXsize("" +  (totalSize+headersDimension));
        }

        return newMsgParts;

    }

    /**
     * get just the InternalPart that contains the body
     *
     * @param newMsg MimeMessage new Message
     * @param oldMsg Message old Message
     * @param ip InternalPart part of theemail
     */
    public static void getHB_Content(MimeMessage newMsg,
            Message oldMsg,
            InternalPart ip)
            throws EntityException {
        try {
            if (ip != null){

                String msgEnc = Utility.getHeaderTransfertEncoding(oldMsg);

                if ( ip.isHtmlBody() && !msgEnc.equalsIgnoreCase(Def.ENCODE_BASE64)){

                    try {

                        newMsg.setContent((String)ip.getPartValue(),
                                Def.CONTENT_CONTENTTYPE);
                        newMsg.setHeader(Def.HEADER_CONTENT_TRANSFERTENC, Def.CONTENT_TRANSFERTENC);

                    } catch (Exception e){
                        // if there is an error returns the html body
                        newMsg.setContent(ip.getPartValue(), ip.getContentType());
                    }

                } else {

                    newMsg.setContent(ip.getPartValue(), ip.getContentType());
                    newMsg.setHeader(Def.HEADER_CONTENT_TRANSFERTENC, ip.getTransfertEnc());

                }

            } else {

                newMsg.setContent(Def.CONTENT_BODY_ALERT, Def.CONTENT_CONTENTTYPE);
                newMsg.setHeader(Def.HEADER_CONTENT_TRANSFERTENC, Def.CONTENT_TRANSFERTENC);

            }
        } catch (MessagingException me){
            throw new EntityException("Error setting the new content for the getHB method");
        }
    }

    /**
     * get just the InternalPart that contains the body
     *
     * @param newMsg MimeMessage new Message
     * @param oldMsg Message old Message
     * @param ips InternalPart part of theemail
     * @throws EntityException
     */
    public static void getHBA_Content(MimeMessage newMsg,
            Message oldMsg,
            ArrayList ips)
            throws EntityException {

        try {

            if ( ips.size() == 1){

                InternalPart ip = (InternalPart)ips.get(0);

                String msgEnc = Utility.getHeaderTransfertEncoding(oldMsg);

                if ( ip.isTextBody()){

                    newMsg.setContent(ip.getPartValue(), ip.getContentType());
                    newMsg.setHeader(Def.HEADER_CONTENT_TRANSFERTENC, ip.getTransfertEnc());

                } else if ( ip.isHtmlBody() && !msgEnc.equalsIgnoreCase(Def.ENCODE_BASE64)){

                    try {
                        newMsg.setContent((String)ip.getPartValue(),
                                Def.CONTENT_CONTENTTYPE);
                        newMsg.setHeader(Def.HEADER_CONTENT_TRANSFERTENC, Def.CONTENT_TRANSFERTENC);
                    } catch (Exception e){
                        // if there is an error returns the html body
                        newMsg.setContent(ip.getPartValue(), ip.getContentType());
                    }

                } else {

                    newMsg.setContent(Def.CONTENT_BODY, Def.CONTENT_CONTENTTYPE);
                    newMsg.setHeader(Def.HEADER_CONTENT_TRANSFERTENC, Def.CONTENT_TRANSFERTENC);

                }

            } else if (ips.size() > 1){

                MimeMultipart multipart = new MimeMultipart();
                BodyPart      part      = null;
                InternalPart  ip        = null;
                String        msgEnc    = null;


                for (int i = 0; i < ips.size(); i++) {

                    part = new MimeBodyPart();

                    ip = (InternalPart)ips.get(i);

                    msgEnc = ip.getTransfertEnc();

                    if (ip.isTextBody()) {

                        part.setContent(ip.getPartValue(), ip.getContentType());
                        part.setHeader(Def.HEADER_CONTENT_TRANSFERTENC, Def.CONTENT_TRANSFERTENC);

                        if (ip.getFileName() != null){
                            part.setFileName(ip.getFileName());
                        }

                        part.setDisposition(ip.getDisposition());

                    } else if ( ip.isHtmlBody() && !msgEnc.equalsIgnoreCase(Def.ENCODE_BASE64)){

                        try {
                            part.setContent((String)ip.getPartValue(),
                                    Def.CONTENT_CONTENTTYPE);
                            part.setHeader(Def.HEADER_CONTENT_TRANSFERTENC, Def.CONTENT_TRANSFERTENC);
                        } catch (Exception e){
                            // if there is an error returns the html body
                            part.setContent(ip.getPartValue(), ip.getContentType());
                        }

                        if (ip.getFileName() != null){
                            part.setFileName(ip.getFileName());
                        }

                        part.setDisposition(ip.getDisposition());

                    } else if (
                            ip.getContentType().startsWith(Def.CONTENT_CONTENTTYPE_RFC822) ||
                            ip.getDisposition() != null &&
                            (ip.getDisposition().equalsIgnoreCase(MimeBodyPart.ATTACHMENT) ||
                            ip.getDisposition().equalsIgnoreCase(MimeBodyPart.INLINE))){

                        if (ip.getFileName() != null){
                            part.setFileName(ip.getFileName());
                        }

                        part.setDisposition(ip.getDisposition());

                        try {
                            DataHandler dh = ip.getDHandler();
                            checkDataHandlerContent(dh);
                            part.setDataHandler(dh);
                        } catch (Exception me){
                            // do nothing
                            log.error("Error setting the Content Part: " +
                                    me.getMessage() + " for the attachment " + ip.getFileName(), me);
                            String defContent = Def.CONTENT_BODY_ALERT;
                            if (ip.getFileName() != null){
                                defContent = "Error Handling Attachment " + ip.getFileName();
                            }
                            DataHandler dhNew = new DataHandler(defContent, Def.CONTENT_CONTENTTYPE);
                            part.setDataHandler(dhNew);
                            if (ip.getFileName() != null){
                                part.setFileName("DefaultAttachment00"+i+".txt");
                            }
                        }

                    } else {

                        // default
                        part.setContent(Def.CONTENT_BODY, Def.CONTENT_CONTENTTYPE);
                        part.setHeader(Def.HEADER_CONTENT_TRANSFERTENC, Def.CONTENT_TRANSFERTENC);

                    }

                    multipart.addBodyPart(part);

                }

                newMsg.setContent(multipart);

            } else {
                // just in case of error
                newMsg.setContent(Def.CONTENT_BODY, Def.CONTENT_CONTENTTYPE);
            }
        } catch (Exception me){
            throw new EntityException("Error setting new content for the getHBA method");
        }

    }

    /**
     *
     * get just the InternalPart that contains the body
     *
     * @param ips All parts of the emails
     * @param numBytes int
     * @param ext Ext
     * @param headersDimension int
     * @return InternalPart
     * @throws EntityException
     */
    public static InternalPart getHB_GetPart(ArrayList ips,
            int numBytes,
            Ext ext,
            int headersDimension,
            ContentProviderInfo contentProviderInfo)
            throws EntityException {

        // itemOut keeps just the body of the email
        InternalPart itemOut              = null;

        boolean      isTextBodyAlreadyGot = false;
        InternalPart item                 = null;
        String[]     value                = null;
        ArrayList    xvalList             = new ArrayList();
        int          totalSize            = 0;
        int          itemSize             = 0;

        for (int i=0; i<ips.size(); i++){

            item = (InternalPart)ips.get(i);

            itemSize = item.getSize();

            if (item.isTextBody()){
                if (!isTextBodyAlreadyGot){

                    // fix the issue about the <blocked:: tag
                    String body = String.valueOf(item.getPartValue().toString());
                    body = removeBlockedTag(body);
                    item.setPartValue(body);

                    // if the part is text/calendar, then it has to be transformed
                    // in a human readable format
                    handleTextCalendarItem(item);

                    //
                    // handleTextCalendarItem could have changed the size of
                    // the item
                    //
                    itemSize = item.getSize();

                    if (numBytes == -1){
                        itemOut = item;
                    } else {
                        itemOut = truncBody(item, numBytes, ext, itemSize);
                    }
                    totalSize = totalSize + itemSize;
                    isTextBodyAlreadyGot = true;
                } else {
                    // test 009
                    // it's a part in txt but is not the main body
                    if ( ((String)item.getPartValue()) == null ||
                            ((String)item.getPartValue()).equalsIgnoreCase("") ) {
                        // some email have an attachment empty issue:4245
                    } else {
                        item.setDisposition(MimeBodyPart.ATTACHMENT);
                        item.setFileName("Part00"+i+".txt");
                    }
                }
            }

            // get html only if there is no TXT body
            if (item.isHtmlBody()){
                if (!isTextBodyAlreadyGot){
                    // no body already got, so get the HTML body

                    //
                    // - convert html
                    // - set size to the converted part
                    // - trucate part (if necessary)
                    //
                    String body = convertHtmlBody(item);
                    //
                    // fix the issue about the <blocked:: tag
                    body = removeBlockedTag(body);
                    item.setPartValue(body);

                    itemSize = body.getBytes().length;
                    item.setSize(itemSize);

                    if (numBytes == -1){
                        itemOut = item;
                    } else {
                        itemOut = truncBody(item, numBytes, ext, itemSize);
                    }
                    totalSize = totalSize + itemSize;
                }
            }

            boolean testForInline =
                    item.getDisposition() != null &&
                    item.getDisposition().equalsIgnoreCase(MimeBodyPart.INLINE);
            boolean testForAttachment =
                    item.getDisposition() != null &&
                    item.getDisposition().equalsIgnoreCase(MimeBodyPart.ATTACHMENT);
            boolean testForRFC822 =
                    item.getContentType().startsWith(Def.CONTENT_CONTENTTYPE_RFC822) &&
                    (!testForInline && !testForAttachment);

            // ext
            if (testForRFC822 || testForAttachment){

                // create the ext
                try {
                    value = getExtValue(item, contentProviderInfo);
                } catch (Exception e) {
                    throw new EntityException("Error while creating ext information", e);
                }

                xvalList.add(value);
                totalSize = totalSize + itemSize;
            }

            // ext
            if (!item.isTextBody() && testForInline){

                // create the ext
                try {
                    value = getExtValue(item, contentProviderInfo);
                } catch (Exception e) {
                    throw new EntityException("Error while creating ext information", e);
                }

                xvalList.add(value);
                totalSize = totalSize + itemSize;
            }

        }

        ext.setXvalAttach(xvalList);

        if (ext.isTruncated()){
            ext.setXsize("" +  (totalSize + headersDimension));
        }

        return itemOut;

    }


    /**
     *
     * @param item
     * @param numBytes
     * @param ext
     * @param origSize
     * @throws com.funambol.email.exception.EntityException
     * @return internal part object
     */
    public static InternalPart truncBody(InternalPart item,
            int numBytes,
            Ext ext,
            int origSize)
            throws EntityException {

        try {
            String partContent = "";
            byte[] partBytes   = null;
            if (item.getPartValue() != null) {

                partContent = String.valueOf(item.getPartValue().toString());
                partBytes = partContent.getBytes();

                if (numBytes < partBytes.length) {
                    item.setPartValue(new String(partBytes, 0, numBytes));
                    item.setSize(numBytes);
                    ext.setTruncated(true);
                    ext.setXvalBody(""+(origSize-numBytes)); // dimension of the missing part
                }
            }
        }catch (Exception e){
            throw new EntityException("Error cutting the email body " + e.getMessage());
        }

        return item;
    }

    /**
     * convert an Html Part to Txt Part
     *
     * @param v Part
     * @return part in txt format String
     */
    private static String convertHtmlBody(InternalPart v)
    throws EntityException {
        String bodyhtml = "";
        String bodytext = "";
        if (v.getPartValue() instanceof String){
            bodyhtml = (String)v.getPartValue();
            bodytext = HTMLUtil.textFromHTML(bodyhtml);
        }
        return bodytext;
    }

    /**
     * removes the <blocked:: tag.
     * if there is an error the method returns the original body
     *
     * @param v Part
     * @return part in txt format String
     */
    private static String removeBlockedTag(String bodyOld) {
        String bodyNew = "";
        try {
            String regex = " <blocked::.*?>(\\r)?\\n";
            Pattern fmt  = Pattern.compile(regex);
            Matcher m    = fmt.matcher(bodyOld);
            bodyNew      = m.replaceAll("");
        } catch (Exception e){
            return bodyOld;
        }
        return bodyNew;
    }

    /**
     *
     * set a new header but keep the old Message-ID for
     * a message that must be downloaded
     *
     *
     * @param FID
     * @param FMID
     * @param isDevice
     * @param newMsg Message
     * @param oldMsg Message
     * @return header-id and header dimension
     * @throws EntityException
     */
    public static String[] setNewHeaderIn(Message newMsg,
            Message oldMsg,
            String FID,
            String FMID,
            boolean isDevice)
            throws EntityException {


        String   oldMessageID     = null;
        String   label            = "";
        String   date             = "";
        int      headersDimension = 0;
        int      headerlength     = 0;
        Header   h                = null;
        String   name             = null;
        String   value            = null;
        String   valueqp          = null;
        String[] out              = new String[2];
        boolean  isReceivedInTheHeader = false;

        try {

            Enumeration en = oldMsg.getAllHeaders();

            while (en.hasMoreElements()) {

                h = (Header) en.nextElement();

                name  = h.getName();
                value = h.getValue();
                headerlength = 0;

                // save the date in string format for the createDummyMessageID()
                if (name.equalsIgnoreCase(Def.HEADER_DATE)) {
                    date = value;
                    // check if the date is parsable
                    if (!UtilityDate.isHeaderDateParsable(oldMsg)){
                        value = UtilityDate.getNowPatch();
                    }
                }

                // save the original Message-ID. it will be set in the caller method
                if (name.equalsIgnoreCase(Def.HEADER_MESSAGE_ID)) {
                    oldMessageID = value;
                }

                // create the new header step by step
                if(isDevice){
                    if (isHeaderMinimum(name)){

                        //
                        // reaplces not parsable chars
                        //
                        value = encodeHeaderField(name, value);

                        headerlength = getFieldLength(name, value);
                        if (name.equals(Def.HEADER_RECEIVED)){
                            if (!isReceivedInTheHeader){
                                // save just the first received value
                                isReceivedInTheHeader = true;
                                newMsg.addHeader(name, value);
                                headersDimension = headersDimension + headerlength;
                            }
                        } else {

                            // create the new header value
                            Object newHeaderValue = null;
                            try {
                                newHeaderValue = createNewHeader(oldMsg, h);
                            } catch (Exception e) {
                                log.error("Error while setting header " + name, e);
                            }

                            if (newHeaderValue == null){
                                //
                                // The header is not one of the header handled
                                //
                                newMsg.addHeader(name, value);
                            } else if (newHeaderValue instanceof String){
                                newMsg.addHeader(name, (String)newHeaderValue);
                            } else if (newHeaderValue instanceof InternetAddress[]) {
                                newMsg.addHeader(name, InternetAddress.toString(
                                        (InternetAddress[]) newHeaderValue));
                            }

                            //
                            // Note: every header lenght is taken from the header
                            // content as it appears in the original email. It is
                            // not the length of the decoded string or even of the
                            // new header, so they could actually be different.
                            //
                            headersDimension = headersDimension + headerlength;
                        }

                    }
                } else {
                    // encode value (QP)
                    value = encodeHeaderField(name, value);
                    headerlength = getFieldLength(name, value);
                    if (name.equals(Def.HEADER_RECEIVED)){
                        if (!isReceivedInTheHeader){
                            // save just the first received value
                            isReceivedInTheHeader = true;
                            newMsg.addHeader(name, value);
                            headersDimension = headersDimension + headerlength;
                        }
                    } else {
                        newMsg.addHeader(name, value);
                        headersDimension = headersDimension + headerlength;
                    }
                }

            }

            // set a message-id if the old message-id is null
            try {
                if (oldMessageID == null){
                    String guid = Utility.createIMAPGUID(FID,FMID,"");
                    oldMessageID = Utility.createDummyMessageID(guid,
                            ""+headersDimension,
                            date);
                }
                // the message-id is set in the caller method
            } catch (Exception e) {
                // do nothing
            }


            // add the size of the
            // Content-Type: text/plain; charset=UTF-8
            // Content-Transfer-Encoding
            headersDimension = headersDimension + 100;

        } catch (MessagingException me) {
            throw new EntityException(me);
        }

        out[0] = oldMessageID;
        out[1] = String.valueOf(headersDimension);

        return out;

    }


    /**
     *
     * set a new header but keep the old Message-ID for
     * a message that must be sent
     *
     * @param newMsg Message
     * @param oldMsg Message
     * @param from String
     * @param firstname String
     * @param lastname String
     * @throws EntityException
     */
    public static void setNewHeaderOut(Message newMsg,
            Message oldMsg,
            String from,
            String firstname,
            String lastname)
            throws EntityException {


        Header   h            = null;
        String   name         = null;
        String   value        = null;
        String   oldMessageID = null;
        boolean  hasFromField = false;

        try {

            String[] recs  = oldMsg.getHeader(Def.HEADER_RECEIVED);

            Enumeration en = oldMsg.getAllHeaders();

            hasFromField = Utility.hasHeaderFrom(oldMsg);

            while (en.hasMoreElements()) {

                h = (Header) en.nextElement();

                name  = h.getName();
                value = h.getValue();

                if (name.equalsIgnoreCase(Def.HEADER_MESSAGE_ID)) {
                    oldMessageID = value;
                }

                if (!name.equals(Def.HEADER_RECEIVED)){
                    // doesn't save the received
                    newMsg.addHeader(name, value);
                }

            }

            // save the from field
            if (!hasFromField){
                InternetAddress fromAddress = new InternetAddress(from);
                if (firstname == null){
                    firstname = "";
                }
                if (lastname == null){
                    lastname = "";
                }
                fromAddress.setPersonal(firstname + " " + lastname);
                newMsg.setFrom(fromAddress);
            }

            // save all the received fileds
            if (recs != null){
                for (int i=recs.length-1; i>=0; i--){
                    newMsg.addHeader(Def.HEADER_RECEIVED, recs[i]);
                }
            }

        } catch (MessagingException me) {
            throw new EntityException(me);
        } catch (UnsupportedEncodingException me) {
            throw new EntityException(me);
        }

    }

    /**
     *
     * set a new header but keep the old Message-ID for
     * a message that must be sent
     *
     * @param newMsg Message
     * @param oldMsg Message
     * @param from String
     * @param firstname String
     * @param lastname String
     * @throws EntityException
     */
    public static void setNewHeaderSent(Message newMsg,
            Message oldMsg,
            String from,
            String firstname,
            String lastname)
            throws EntityException {


        Header   h            = null;
        String   name         = null;
        String   value        = null;
        String   oldMessageID = null;
        boolean  hasFromField = false;

        try {

            Enumeration en = oldMsg.getAllHeaders();

            hasFromField = Utility.hasHeaderFrom(oldMsg);

            while (en.hasMoreElements()) {

                h = (Header) en.nextElement();

                name  = h.getName();
                value = h.getValue();

                if (name.equalsIgnoreCase(Def.HEADER_MESSAGE_ID)) {
                    oldMessageID = value;
                }

                // save just minimum info
                if (name.equalsIgnoreCase(Def.HEADER_DATE) ||
                        name.equalsIgnoreCase(Def.HEADER_MESSAGE_ID) ||
                        name.equalsIgnoreCase(Def.HEADER_FROM) ||
                        name.equalsIgnoreCase(Def.HEADER_TO) ) {
                    newMsg.addHeader(name, value);
                }

            }

            //
            newMsg.addHeader(Def.HEADER_SUBJECT, "");

            // save the from field
            if (!hasFromField){
                InternetAddress fromAddress = new InternetAddress(from);
                fromAddress.setPersonal(firstname + " " + lastname);
                newMsg.setFrom(fromAddress);
            }

        } catch (MessagingException me) {
            throw new EntityException(me);
        } catch (UnsupportedEncodingException me) {
            throw new EntityException(me);
        }

    }

    /**
     *
     * create an exact copy of the content
     *
     * @param newMsg Message
     * @param oldMsg Message
     */
    public static void setNewContentToBeSent(Message newMsg, Message oldMsg)
      throws EntityException {
        try {

            // get all parts of the email that will be sent
            ArrayList newContents = MessageParser.getAllPartsOfMessage(oldMsg, false);
            int totalPartNumber = newContents.size();

            InternalPart  item = null;

            // create a new email
            if (totalPartNumber == 1){

                // just a TXT body
                item = (InternalPart)newContents.get(0);
                newMsg.setContent(item.getPartValue(), item.getContentType());

            } else {

                // multipart

                MimeMultipart multipart = new MimeMultipart();
                BodyPart      part;
                boolean       isTextBodyAlreadyGot = false;
                boolean       setDataHandler = true;
                String        newBody = null;

                for (int i=0; i<totalPartNumber; i++) {

                    setDataHandler = true;

                    part = new MimeBodyPart();

                    item = (InternalPart)newContents.get(i);

                    // get the body
                    if (item.isTextBody() && !isTextBodyAlreadyGot) {
                        newBody = item.getPartValue() + "";
                        item.setPartValue(newBody);
                        isTextBodyAlreadyGot = true;
                        setDataHandler = false;
                    }

                    if (item.isHtmlBody() && !isTextBodyAlreadyGot){
                        if(i == totalPartNumber-1){
                            item.setPartValue(newBody);
                            isTextBodyAlreadyGot = true;
                            setDataHandler = false;
                        }
                    }

                    part.setContent(item.getPartValue(), item.getContentType());

                    if (setDataHandler){
                        part.setDataHandler(item.getDHandler());
                    }

                    if (item.getDisposition() != null){
                        part.setDisposition(item.getDisposition());
                    }

                    if (item.getFileName() != null){
                        part.setFileName(item.getFileName());
                    }

                    multipart.addBodyPart(part);

                }

                newMsg.setContent(multipart);

            }

        } catch (Exception e){
            throw new EntityException("Error creating new Content");
        }
    }



    /**
     *
     * create a copy of the content with the signature Part
     * this method is used in the sending procedure
     *
     * @param newMsg Message
     * @param oldMsg Message
     * @param sign String
     */
    public static void setNewContent(Message newMsg, Message oldMsg, String trailer)
      throws Exception {

        // get all parts of the email that will be sent
        ArrayList newContents = MessageParser.getAllPartsOfMessage(oldMsg, false);
        int totalPartNumber = newContents.size();

        InternalPart  item = null;

        // create a new email
        if (totalPartNumber == 1){

            // just a TXT body

            item = (InternalPart)newContents.get(0);
            setNewTextBody(item, trailer);
            newMsg.setContent(item.getPartValue(), item.getContentType());

        } else {

            // multipart

            MimeMultipart multipart = new MimeMultipart();
            BodyPart      part;
            boolean       isTextBodyAlreadyGot = false;
            boolean       setDataHandler = true;
            String        newBody = null;

            for (int i=0; i<totalPartNumber; i++) {

                setDataHandler = true;

                part = new MimeBodyPart();

                item = (InternalPart)newContents.get(i);

                // add the signature to the body.
                //
                // check if is the part is the body.
                // if it's a txt-body the method adds the signature
                // if there is just the html-body method adds the html signature
                // the second option should never happen because the client doesn't
                // send the email in html format

                // get the body
                if (item.isTextBody() && !isTextBodyAlreadyGot) {
                    if (trailer != null && !trailer.equals("")) {
                        newBody = item.getPartValue() + setTrailerTXT(trailer);
                        item.setPartValue(newBody);
                        isTextBodyAlreadyGot = true;
                        setDataHandler = false;
                    }
                }

                if (item.isHtmlBody() && !isTextBodyAlreadyGot){
                    if(i == totalPartNumber-1){
                        if (trailer != null && !trailer.equals("")) {
                            newBody = createNewHtmlBody(item, trailer);
                            if (newBody != null){
                                item.setPartValue(newBody);
                                isTextBodyAlreadyGot = true;
                                setDataHandler = false;
                            }
                        }
                    }
                }

                part.setContent(item.getPartValue(), item.getContentType());

                if (setDataHandler){
                    part.setDataHandler(item.getDHandler());
                }

                if (item.getDisposition() != null){
                    part.setDisposition(item.getDisposition());
                }

                if (item.getFileName() != null){
                    part.setFileName(item.getFileName());
                }

                multipart.addBodyPart(part);

            }

            newMsg.setContent(multipart);

        }


    }

    //----------------------------------------------------------- Private Methods

    /**
     * add the signature to the body.
     *
     * check if is the part is the body.
     * if it's a txt-body the method adds the signature
     * if there is just the html-body method adds the html signature
     * the second option should never happen because the client doesn't
     * send the email in html format
     *
     */
    private static void setNewTextBody (InternalPart item, String sign){
        String newBody = null;
        if (sign != null && !sign.equals("")) {
            if (item.isTextBody()){
                newBody = item.getPartValue() + setTrailerTXT(sign);
            }
            if (item.isHtmlBody()){
                newBody = createNewHtmlBody(item, sign);
            }
            if (newBody != null){
                item.setPartValue(newBody);
            }
        }
    }

    /**
     *
     */
    private static String createNewHtmlBody (InternalPart item, String sign){

        String newBody  = null;

        try {

            String htmlBody = (String)item.getPartValue();

            int closeBodyIndex    = htmlBody.indexOf("</body>");
            int closeCapBodyIndex = htmlBody.indexOf("</BODY>");


            if (closeBodyIndex != -1){
                newBody = htmlBody.substring(0,closeBodyIndex) +
                          setTrailerHTML(sign) +
                          htmlBody.substring(closeBodyIndex,htmlBody.length());
            } else if (closeCapBodyIndex != -1){
                newBody = htmlBody.substring(0,closeCapBodyIndex) +
                           setTrailerHTML(sign) +
                          htmlBody.substring(closeCapBodyIndex,htmlBody.length());
            } else {
                // "</body> tag" not found
            }

        } catch (Exception e){
            // do nothing
        }

        return newBody;
    }

    /**
     * replace non-parsable character
     * @param name
     * @param value
     * @return encoded string
     */
    private static String encodeHeaderField(String name, String value){

        String out = value;
        char[] chars = null;

        if (!name.equalsIgnoreCase(Def.HEADER_MESSAGE_ID)) {
            chars = value.toCharArray();
            char chr;
            for (int i = 0; i < chars.length; i++) {
                chr = chars[i];
                if (chr < 0x20) {
                    if (chr != 0x9 && chr != 0xA && chr != 0xD) {
                        chars[i] = ' ';
                    }
                }
            }
            out = new String(chars);
        }

        return out;

    }

    /**
     *
     * @param name
     * @return true if the header field is in the header_minimum list
     */
    private static boolean isHeaderMinimum(String name){
        boolean check = false;
        for (int i=0; i < Def.HEADER_MINIMUM.length; i++){
            if (name.equalsIgnoreCase(Def.HEADER_MINIMUM[i])){
                check = true;
                break;
            }
        }
        return check;
    }


    /**
     *
     * check about the content of the datahandler.
     * the Datahandler could contains the following error:
     * <p>
     * - some unparsable content type (mail with image in the body sent by
     *   outlook 2002: "IMAGE/JPEG; name=image001.jpg; image/spj=")
     * <p>
     * - ...
     * <p>
     * In this way the system can insert a default Part in the email and the
     * email can be sent to the client
     *
     * @param dh
     * @throws java.lang.Exception
     */
    private static void checkDataHandlerContent(DataHandler dh)
    throws Exception {
        try {
            DataFlavor[] df = dh.getTransferDataFlavors();
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    /**
     * retreive the length of the header field string
     *
     * @param name
     * @param value
     * @return length of the string
     *
     */
    private static int getFieldLength(String name, String value){
        String label = name + ": " + value;
        int headerlength = label.getBytes().length;
        return headerlength;
    }


    /**
     * Creates the ext info. It includes (in the following order):
     * - name
     * - size
     * - mimetype
     * - url
     *
     * @param ip
     * @param contentProviderInfo
     * @return
     * @throws java.io.UnsupportedEncodingException
     * @throws javax.mail.internet.ParseException
     */
    private static String[] getExtValue(InternalPart ip, ContentProviderInfo contentProviderInfo) {

        String[] value = new String[4];
        value[0] = ip.getFileName();
        value[1] = String.valueOf(ip.getSize());

        try {
            if (ip.getDHandler() != null) {
                String attachmentUrl = getAttachmentUrl(contentProviderInfo, ip);
                if (attachmentUrl != null && "".equals(attachmentUrl) == false) {
                    String mimeType = getMimeType(ip.getContentType());
                    value[2] = mimeType;
                    value[3] = attachmentUrl;
                }
            }
        } catch (Exception ex) {
            log.error("Error while creating ext information for Content Provider", ex);
            // do nothing: the sync item will be sent to the client without the
            // content provider info so the attachment could not be opened but
            // the email is still sent
        }

        return value;
    }

    /**
     * Create the Content Provider Service URL where the attachment can be
     * retrieved
     * @param contentProviderInfo
     * @param internalPart
     * @return The url where the attachment can be retrieved or null if it is
     * unable to create a valid attachment url
     * @throws java.io.UnsupportedEncodingException
     */
    private static String getAttachmentUrl(ContentProviderInfo contentProviderInfo,
            InternalPart internalPart)
            throws UnsupportedEncodingException, EntityException{

        if (contentProviderInfo.getToken() == null || "".equals(contentProviderInfo.getToken())) {
            if (log.isTraceEnabled()) {
                log.trace("Token must be not empty to create a valid attachment url");
            }
            return null;
        }
        if (contentProviderInfo.getUserName() == null || "".equals(contentProviderInfo.getUserName())) {
            if (log.isTraceEnabled()) {
                log.trace("User Name must be not empty to create a valid attachment url");
            }
            return null;
        }

        StringBuilder attachmentUrl = new StringBuilder();
        attachmentUrl.append(contentProviderInfo.getAttachProviderUrl());
        attachmentUrl.append("/");

        String fileName = internalPart.getFileName();
        try {
            fileName = MimeUtility.decodeWord(fileName);
        } catch (ParseException ex) {
            // DO nothing
        }
        String attFileName = truncName(fileName);

        if (attFileName != null) {
            attachmentUrl.append(URLEncoder.encode(attFileName, "UTF-8"));
        }

        //
        // Attachment url will be something like:
        // http://www.myfunambol.com/content/filename.ext?AUTH=token&USER=userid&INDEX=index
        //
        attachmentUrl.append("?");
        attachmentUrl.append(TOKEN_PARAM_NAME);
        attachmentUrl.append("=");
        attachmentUrl.append(URLEncoder.encode(contentProviderInfo.getToken(), "utf-8"));

        attachmentUrl.append("&");
        attachmentUrl.append(USERID_PARAM_NAME);
        attachmentUrl.append("=");
        attachmentUrl.append(URLEncoder.encode(contentProviderInfo.getUserName(), "utf-8"));

        attachmentUrl.append("&");
        attachmentUrl.append(INDEX_PARAM_NAME);
        attachmentUrl.append("=");
        attachmentUrl.append(internalPart.getIndex());

        return attachmentUrl.toString();
    }

    /**
     * Extract the mimetype from the contetnt type string (which is formed by the
     * mimetype and some parameters).
     *
     * @param contentType
     * @return the mime type contained in the email content type or the
     * UNKNOWN_MIME_TYPE if content type is null, empty or process to extract
     * content type fails.
     * @throws ParseException if retrieving
     */
    private static String getMimeType(String contentType) throws ParseException{

        if (contentType == null || contentType.length() == 0){
            return UNKNOWN_MIME_TYPE;
        }

        //
        // extract the mimetype from contentType
        //
        ContentType javaMailContentType = new ContentType(contentType);

        return javaMailContentType.getBaseType();
    }

    /**
     * Truncate the name so that the result lenght doesn't exceed TOKEN_PARAM_NAME_LENGTH
     * If the name has an extention "filename.ext", the extention is maantained.
     *
     * @param name the file name
     * @return the truncated file name
     */
    private static String truncName(String name) {
        String result = name;
        if (name != null) {
            if (name.length() > TOKEN_PARAM_NAME_LENGTH) {
                String[] aux = name.split("\\.");
                if (aux != null && aux.length > 1) {
                    String attFileExt = "." + aux[aux.length - 1];
                    result = name.substring(0, TOKEN_PARAM_NAME_LENGTH - attFileExt.length()) +
                            attFileExt;
                } else {
                    result = name.substring(0, TOKEN_PARAM_NAME_LENGTH);
                }
            }
        }
        return result;
    }

    /**
     *
     * @return complete trailer
     */
    private static String setTrailerTXT(String sign){
        String complete = "\r\n" +
                          "__________\r\n" +
                          sign +
                          "\r\n";
        return complete;

    }

    /**
     *
     * @return complete trailer
     */
    private static String setTrailerHTML(String sign){
        String complete = "<br>" +
                          "<hr><br>" +
                          sign +
                          "<br>\r\n";
        return complete;

    }

    /**
     * Parse an invitation.
     *
     * @param vcalText the invitation
     * @param charset
     * @param deviceTimeZone
     * @return calendar object that represent the given vcalendar
     * @throws com.funambol.common.pim.converter.ConverterException
     * @throws com.funambol.common.pim.icalendar.ParseException
     */
    private static com.funambol.common.pim.calendar.Calendar parseVCalendar(
            String vcalText,
            String charset,
            TimeZone deviceTimeZone)
            throws ConverterException, com.funambol.common.pim.icalendar.ParseException {

        String vcalVersion = "2.0";

        if (charset == null) {
            charset = BaseConverter.CHARSET_UTF8; // (RFC 2445)
        }

        if (deviceTimeZone == null){
            deviceTimeZone = TimeZone.getTimeZone("GMT+00");
        }

        ByteArrayInputStream buffer =
                new ByteArrayInputStream(vcalText.getBytes());

        // iCalendar (2.0)
        ICalendarParser parser = new ICalendarParser(buffer);
        VCalendar vcalendar = (VCalendar) parser.ICalendar();

        VCalendarConverter vcf =
                new VCalendarConverter(deviceTimeZone, charset, false);
        com.funambol.common.pim.calendar.Calendar c =
                vcf.vcalendar2calendar(vcalendar);

        return c;
    }

    /**
     * Creates a human readable representation of an invitation.
     * @param vcalText invitation ccalendar
     * @return a human readable representation of an invitation
     */
    private static String vCal2HumanReadableCalendar (String vcalText)
            throws ConverterException, com.funambol.common.pim.icalendar.ParseException,
            java.text.ParseException, IOException {

        com.funambol.common.pim.calendar.Calendar c =
                parseVCalendar(vcalText, null, null);

        Property prodIdProperty = c.getProdId();
        String prodId = null;
        if (prodIdProperty != null){
            prodId = prodIdProperty.getPropertyValueAsString();
        }

        //
        // dtstart
        //
        PropertyWithTimeZone dtstart = c.getCalendarContent().getDtStart();
        StringBuilder dtstartSb = createFormattedDate (dtstart, "from ");

        //
        // dtend
        //
        PropertyWithTimeZone dtend = c.getCalendarContent().getDtEnd();
        StringBuilder dtendSb = createFormattedDate (dtend, "to ");

        //
        // location
        //
        Property location = c.getCalendarContent().getLocation();
        StringBuilder locationSb = getPropertyValue(location);

        //
        // description
        //
        Property description = c.getCalendarContent().getDescription();
        StringBuilder descriptionSb = getPropertyValue(description);

        //
        // summary
        //
        Property summary = c.getCalendarContent().getSummary();
        StringBuilder summarySb = getPropertyValue(summary);

        //
        // Creation of the output.
        //
        StringBuilder out = new StringBuilder();

        out.append("Summary: ").append(summarySb);
        out.append('\n');

        if (prodId != null) {
            if (prodId.contains(PRODID_OUTLOOK)) {

                //
                // example with Outlook:
                //
                //   Summary: Invitation subject
                //   When: mar 28 ottobre 2008 16.30-17.00 (GMT + 1.00 h)
                //   Amsterdam, Berlino, Berna, Roma, Stoccolma, Vienna.
                //   Where: location of the invitation
                //
                //
                //   *~*~*~*~*~*~*~*~*~*
                //
                //   Some free notes.
                //
                out.append(descriptionSb);
                out.append('\n');

            } else if (prodId.contains(PRODID_MOZILLA)) {

                //
                // example with Thunderbird:
                //
                //   Summary: Invitation subject
                //   When: from mar 28 ottobre 2008 16.30 CEST
                //   to mar 28 ottobre 2008 17.00 CEST
                //   Where: location of the invitation
                //   Description: Some free notes.
                //

                out.append("When: ").append(dtstartSb).append('\n').append(dtendSb);
                out.append('\n');

                out.append("Where: ").append(locationSb);
                out.append('\n');

                out.append("Description: " + descriptionSb);
                out.append('\n');

            } else {
                //
                // Any other client.
                //
                out.append("When: ").append(dtstartSb).append('\n').append(dtendSb);
                out.append('\n');

                out.append("Where: ").append(locationSb);
                out.append('\n');

                out.append("Description: " + descriptionSb);
                out.append('\n');
            }
        }

        return out.toString();
    }

    /**
     * Returns formatted date property value.
     * <p/>
     * Format is: "[head]E dd MMM yyyy, HH:mm [(timezone)]"
     * @param property
     * @param head
     * @return
     */
    private static StringBuilder createFormattedDate (
            PropertyWithTimeZone property,
            String head)
            throws java.text.ParseException{

        StringBuilder sb = new StringBuilder();

        if (property != null) {
            sb.append(head);

            sb.append(convertDateFromTo((String) property.getPropertyValue(),
                    "EEEE dd MMM yyyy, HH:mm", Locale.US));

            String timezoneId = property.getTimeZone();
            if (timezoneId != null) {
                sb.append(" (");
                sb.append(TimeZone.getTimeZone(timezoneId).
                        getDisplayName(true, TimeZone.SHORT));
                sb.append(')');
            }
        }
        return sb;
    }

    /**
     * Returns the value the property if it is not null or empty, or a proper
     * string otherwise.
     * @param property
     * @return
     * @throws java.text.ParseException
     */
    private static StringBuilder getPropertyValue (
            Property property)
            throws java.text.ParseException{

        StringBuilder sb = new StringBuilder();

        if (property != null) {
            String locationValue = (String)property.getPropertyValue();
            if (locationValue.length() != 0) {
                sb.append(property.getPropertyValue());
            } else {
                sb.append(PROPERTY_VALUE_NOT_AVALIABLE);
            }
        } else {
            sb.append(PROPERTY_VALUE_NOT_AVALIABLE);
        }

        return sb;
    }

    /**
     * If the part is a calendar part, then transform its content (a Vcalendar
     * format string) in a human readable format.
     * <p/>
     * Note that the part content could also contain some text, not only a
     * Vcalendar string.
     * <p/>
     * If transformation fails, then the part will not be affected.
     * @param item the part to be possibly transformed
     */
    private static void handleTextCalendarItem(InternalPart item){
        if (item.isTextCalendar()) {

            String partContent = (String) item.getPartValue();

            //
            // Extract the vcaledar section from the content.
            //
            int i = partContent.indexOf("BEGIN:VCALENDAR");
            String vcalText = partContent.substring(i).trim();

            try {
                String humanReadableCalendar =
                        vCal2HumanReadableCalendar(vcalText);

                // replace vcalendar string with the human readable format
                StringBuilder tmp = new StringBuilder(
                        partContent.substring(0, i));
                tmp.append(humanReadableCalendar);
                item.setPartValue(tmp.toString());

                // the part is no more a text/calendar
                item.setIsTextCalendar(false);

                // content of the part is changed, so its size changes
                item.setSize(
                        ((String)item.getPartValue()).length());

            } catch (Throwable t) {
                log.error("Error while converting text/calendar part. " +
                        " Content of the message part will not be transformed.", t);
            }
        }
    }

    private static String convertDateFromTo(String stringDate, String patternToUse, Locale locale)
            throws java.text.ParseException {

        if (stringDate == null || stringDate.length() == 0) {
            return "";
        }

        String pattern = TimeUtils.getDateFormat(stringDate);
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, locale);
        formatter.setLenient(false);
        Date date = formatter.parse(stringDate);
        formatter.applyPattern(patternToUse);
        return formatter.format(date);
    }

    /**
     * Create the value of an header of the new message giving original message
     * and the actual header.
     *
     * @param message original message
     * @param header the header to put in the new message
     * @return the new value for the header, null if the header is not handled by
     * this method (does not need decoding/encoding)
     * @throws javax.mail.MessagingException
     * @throws java.io.UnsupportedEncodingException
     */
    private static Object createNewHeader(Message message, Header header)
            throws MessagingException, UnsupportedEncodingException {

        String headerName = header.getName();
        Object newHeaderValue = null;

        //
        // Decoded value of original header has to be taken using proper method.
        //
        // Example:
        //   message.getSubject() to get the decoded subject
        //
        if (Def.HEADER_SUBJECT.equals(headerName)) {

            String headerValue = header.getValue();
            int i = headerValue.indexOf("=?");

            if (i != -1) {

                if (headerValue.toLowerCase().indexOf("utf-8", i) != -1) {
                    //
                    // If the value is already utf-8 encoded, then it is returned
                    //
                    return headerValue;
                }

                //
                // Whatever the original encoding was, the header is UTF-8/Base64
                // encoded.
                //
                StringBuilder newValue = new StringBuilder();
                newValue.append("=?UTF-8?B?");
                newValue.append(
                        new String(Base64.encode(
                        message.getSubject().getBytes("utf-8"))));
                newValue.append("?=");

                return newValue.toString();
            } else {
                //
                // If the original value is not encoded, then is returned, since it
                // is an ascii string
                //
                return headerValue;
            }

        } else if (Def.HEADER_FROM.equals(headerName)) {

            InternetAddress[] addresses =
                    (InternetAddress[]) message.getFrom();
            newHeaderValue = encodeNewHeaderAddresses(addresses);

        } else if (Def.HEADER_TO.equals(headerName)) {

            InternetAddress[] addresses =
                    (InternetAddress[]) message.getRecipients(Message.RecipientType.TO);
            newHeaderValue = encodeNewHeaderAddresses(addresses);

        } else if (Def.HEADER_REPLY_TO.equals(headerName)) {

            InternetAddress[] addresses =
                    (InternetAddress[]) message.getReplyTo();
            newHeaderValue = encodeNewHeaderAddresses(addresses);

        } else if (Def.HEADER_CC.equals(headerName.toUpperCase())) {

            InternetAddress[] addresses =
                    (InternetAddress[]) message.getRecipients(Message.RecipientType.CC);
            newHeaderValue = encodeNewHeaderAddresses(addresses);

        } else if (Def.HEADER_BCC.equals(headerName.toUpperCase())) {

            InternetAddress[] addresses =
                    (InternetAddress[]) message.getRecipients(Message.RecipientType.BCC);
            newHeaderValue = encodeNewHeaderAddresses(addresses);

        }

        return newHeaderValue;
    }

    private static Address[] encodeNewHeaderAddresses(InternetAddress[] addresses)
            throws UnsupportedEncodingException{

        Address[] newAddresses = new InternetAddress[addresses.length];

        for (int i = 0; i < addresses.length; i++) {

            String decodedPersonal = addresses[i].getPersonal();

            // Address cannot be encoded. Only ascii chars are permitted.
            String address = addresses[i].getAddress();

            InternetAddress newAddress = new InternetAddress();
            newAddress.setAddress(address);
            if (decodedPersonal != null) {
                newAddress.setPersonal(decodedPersonal, "utf-8");
            }

            newAddresses[i] = newAddress;
        }

        return newAddresses;
    }
}









