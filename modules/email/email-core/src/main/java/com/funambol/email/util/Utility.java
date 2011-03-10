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
package com.funambol.email.util;


import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.CRC32;
import java.util.LinkedHashMap;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.MessagingException;
import javax.mail.search.ComparisonTerm;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.MimeMessage.RecipientType;
import com.funambol.email.exception.EntityException;
import com.funambol.email.model.DefaultFolder;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.FlagProperties;
import com.funambol.email.model.ItemFolder;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.email.pdi.mail.Email;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.tools.Base64;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import java.util.Map;

/**
 * <p>Utility class</p>
 *
 * @version $Id: Utility.java,v 1.6 2008-07-17 09:55:50 testa Exp $
 */

public class Utility {

    // ------------------------------------------------------------ Public Methods

    /**     */
    protected static FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    // ------------------------------------------------------------ Public Methods

    /**
     * get the Messages from the Mail Server
     *
     * @param folder The mail folder name.
     * @param maxNum Maximum number of messages to retrieve.
     * @return Array of messages.
     */
    public static Message[] getMessages(javax.mail.Folder folder, int maxNum)
     throws MessagingException{

        if (log.isTraceEnabled()) {
            log.trace("Get Messages Utility");
        }

        int max = 100;
        Message[] messages = null;
        int mNumber  = folder.getMessageCount();
        if (mNumber > max){
            if (log.isTraceEnabled()) {
                log.trace("Get "+max+" messages");
            }
            messages = folder.getMessages(mNumber-max,mNumber);
        } else {
            if (log.isTraceEnabled()) {
               log.trace("Get all messages");
            }
            messages = folder.getMessages();
        }
        return messages;
    }

    /**
     * convert the db value 'y' in the boolean "true"
     */
    public static boolean booleanFromString(String value){
        boolean bol = false;
        if (value != null){
            if (!value.equals("")){
                if (value.equalsIgnoreCase("Y")){
                    bol = true;
                }
            }
        }
        return bol;
    }

    /**
     * convert the db value 'y' in the boolean "true"
     */
    public static String booleanToString(boolean value){
        String bol = "n";
        if (value){
            bol = "y";
        }
        return bol;
    }

    /**
     * convert a String db value in the integer value
     */
    public static int integerFromString(String value){
        int val = 0;
        if (value != null){
            if (!value.equals("")){
                try {
                    val = Integer.parseInt(value);
                } catch (Exception e){
                    // do nothing
                }
            }
        }
        return val;
    }

    /**
     * credantials format:  "username:passowrd"
     *
     * @param username    String
     * @param credentials String
     * @return password String
     */
    public static String getPassword(String username, String credentials) {
        String up = new String(Base64.decode(credentials));
        int l = username.length();
        return up.substring(l + 1, up.length());
    }



    /**
     * Returns true if the syncitem is a Mail,
     * false if the sycItem is a Folder
     *
     * @param si SyncItem
     * @return boolean
     */
    protected boolean isEmail(SyncItem si) {

        boolean isEmail = true;

        String itemType = si.getType();
        if (itemType != null){
            isEmail = itemType.equals(Def.TYPE_EMAIL);
        }

        return isEmail;
    }


    /**
     * create a dummy message-id if the email has message-id = null
     * 
     * @param guid
     * @param crc
     * @param date
     * @return
     */
    public static String createDummyMessageID(String guid, String crc, String date) {

        String dummyMsgId = null;

        try {

            // crc of date
            CRC32 checksumEngine = new CRC32();
            long checksum    = 0;
            byte[] bytesDate = date.getBytes();
            checksumEngine.update(bytesDate, 0, bytesDate.length);
            checksum = checksumEngine.getValue();
            checksumEngine.reset();

            dummyMsgId = "<" + crc + checksum + "." + guid + ">";

        } catch (Exception me) {
            // do nothing; we just want to return 0
        }

        return dummyMsgId;

    }


    /**
     * converts the Nokia folder name in the defaul folder name
     *
     * @param oldfolder folder name in the Server String
     * @param df        DefaultFolder
     * @return newfolder folder name in the Nokia device String
     */
    public static String convertNokiaToDefault(String oldfolder, DefaultFolder df) {
        String newfolder = null;
        if (oldfolder.equalsIgnoreCase(Def.NOKIA_INBOX_ENG)) {
            newfolder = df.getInboxName();
        } else if (oldfolder.equalsIgnoreCase(Def.NOKIA_OUTBOX_ENG)) {
            newfolder = df.getOutboxName();
        } else if (oldfolder.equalsIgnoreCase(Def.NOKIA_SENT_ENG)) {
            newfolder = df.getSentName();
        } else if (oldfolder.equalsIgnoreCase(Def.NOKIA_DRAFTS_ENG)) {
            newfolder = df.getDraftsName();
        }
        return newfolder;
    }


    /**
     *
     * converts the id of an email in the sent folder with an ID
     * for and email in the outbox folder
     *
     * @param oldID id that starts with S
     * @return newID id that starts with O
     */
    public static String convertSentIDToOutboxID(String oldID) {
        String newID = null;
        if (log.isTraceEnabled()) {
            log.trace("convertSentIDToOutboxID old id " + oldID);
        }
        int i = oldID.indexOf(Def.SEPARATOR_FIRST);
        newID = Def.FOLDER_OUTBOX_ID + oldID.substring(i, oldID.length());
        if (log.isTraceEnabled()) {
            log.trace("convertSentIDToOutboxID new id " + newID);
        }
        return newID;
    }

    /**
     *
     * converts the id of an email in the outbox folder with an ID
     * for and email in the sent folder
     *
     * @param oldID id that starts with O
     * @return newID id that starts with S
     */
    public static String convertOutboxIDToSentID(String oldID) {
        String newID = null;
        if (log.isTraceEnabled()) {
            log.trace("convertOutboxIDToSentID old id " + oldID);
        }
        int i = oldID.indexOf(Def.SEPARATOR_FIRST);
        newID = Def.FOLDER_SENT_ID + oldID.substring(i, oldID.length());
        if (log.isTraceEnabled()) {
            log.trace("convertOutboxIDToSentID new id " + newID);
        }
        return newID;
    }

    /**
     * get content from a SyncItem
     *
     * @param syncItem SyncItem
     * @return content of the SyncItem String
     * @throws EntityException
     */
    public static String getContentFromSyncItem(SyncItem syncItem)
            throws EntityException {
        String content;
        try {
            byte[] itemContent = syncItem.getContent();
            if (itemContent == null) {
                itemContent = new byte[0];
            }
            content = new String(itemContent);
        }
        catch (Exception e) {
            throw new EntityException("Error getting the item " + syncItem, e);
        }

        return content;
    }


    /**
     * 
     * @param message Message
     * @return String
     */
    public static String getHeaderMessageID(Message message) {
        try {
            String[] values = message.getHeader(Def.HEADER_MESSAGE_ID);
            if ((values != null) && (values.length > 0)) {
                return values[0];
            }
        } catch (MessagingException exception) {
            // do nothing; we just want to return null
        }
        return null;
    }

    /**
     * 
     * @param message Message
     * @return String
     */
    public static String getHeaderSubject(Message message) {
        try {
            String value = message.getSubject();
            // limit the subject
            if (value != null){
                if (value.length() >= 700){
                   value = value.substring(0,700);
                }
            }
            return value;
        } catch (MessagingException exception) {
            // do nothing; we just want to return null
        }
        return null;
    }

    /**
     * 
     * check if there is the Form field in the message
     *
     * @param message Message
     * @return String
     */
    public static boolean hasHeaderFrom(Message message) {
        try {
            Address[] values = message.getFrom();
            if (values == null){
                return false;
            } else {
                if (values.length == 0){
                    return false;
                } else {
                    return true;
                }
            }
        } catch (MessagingException exception) {
            // do nothing; we just want to return null
        }
        return false;
    }

    /**
     * 
     * @param message Message
     * @return String
     */
    public static String getHeaderSender(Message message) {
        try {
            Address address = null;

            Address[] values = message.getFrom();
            if (values != null){
                for (int i=0; i<values.length; i++){
                    address = values[i];
                    if (address != null){
                       return address.toString();
                    }
                }
            }
        } catch (MessagingException exception) {
            // do nothing; we just want to return null
        }
        return null;
    }

    /**
     * 
     * @param message Message
     * @return String
     */
    public static String getHeaderReceiver(Message message) {
        try {
            Address address = null;

            Address[] values = message.getRecipients(RecipientType.TO);
            if (values != null){
                for (int i=0; i<values.length; i++){
                    address = values[i];
                    if (address != null){
                       return address.toString();
                    }
                }
            }
        } catch (MessagingException exception) {
            // do nothing; we just want to return null
        }
        return null;
    }

    /**
     * 
     * @param message Message
     * @return String
     */
    public static String getHeaderTransfertEncoding(Message message) {
        try {
            String[] values = message.getHeader(Def.HEADER_CONTENT_TRANSFERTENC);
            if ((values != null) && (values.length > 0)) {
                return values[0];
            }
        } catch (MessagingException exception) {
            // do nothing; we just want to return null
        }
        return "";
    }

    /**
     * 
     * @param part Part
     * @return String
     */
    public static String getHeaderTransfertEncoding(Part part) {
        try {
            String[] values = part.getHeader(Def.HEADER_CONTENT_TRANSFERTENC);
            if ((values != null) && (values.length > 0)) {
                return values[0];
            }
        } catch (MessagingException exception) {
            // do nothing; we just want to return null
        }
        return "";
    }

    /**
     *
     * get the message id from the email that the client try to send
     * format: Message-ID: O/AAAAAK8wAAACAAAAggAAAA==@192.168.55.101
     *
     * @param msg String
     * @return String
     */
    public static String getHeaderMessageID(String msg) {
        String messageid = null;

        int s = Def.HEADER_MESSAGE_ID.length() + 2;

        int i = msg.indexOf(Def.HEADER_MESSAGE_ID+": ");

        if (i == -1){
            i = msg.indexOf("Message-Id: ");
            if (i != -1){
                int start = i + s;
                messageid = msg.substring(start, msg.length());
                int end = messageid.indexOf("\n");
                messageid = messageid.substring(0, end);
            }
        } else {
            int start = i + s;
            messageid = msg.substring(start, msg.length());
            int end = messageid.indexOf("\n");
            messageid = messageid.substring(0, end);
        }
        return messageid;
    }

    /**
     * 
     * @param msg
     * @return
     */
    public static String getHeaderFrom(String msg) {
        String messageid = null;

        int s = Def.HEADER_MESSAGE_ID.length() + 2;

        int i = msg.indexOf(Def.HEADER_FROM + ": ");

        if (i == -1){
            i = msg.indexOf("Message-Id: ");
            if (i != -1){
                int start = i + s;
                messageid = msg.substring(start, msg.length());
                int end = messageid.indexOf("\n");
                messageid = messageid.substring(0, end);
            }
        } else {
            int start = i + s;
            messageid = msg.substring(start, msg.length());
            int end = messageid.indexOf("\n");
            messageid = messageid.substring(0, end);
        }
        return messageid;
    }

    /**
     * 
     * @param message Message
     * @return String
     */
    public static String getHeaderSyncLabel(Message message) {

        String syncLabel = null;

        try {
            String[] values = message.getHeader(Def.HEADER_SYNCMESSAGE);
            if ((values != null) && (values.length > 0)) {
                syncLabel = values[0];
            }
        } catch (MessagingException exception) {
            // do nothing; we just want to return null
        }
        return syncLabel;
    }

    /**
     * Returns the charset in the ContentType
     * It can be used when the standard method
     * <br>
     *  ContentType ct = new ContentType(part.getContentType());
     * <br>
     *  String specifiedCharset = ct.getParameter("charset");
     * <br>
     * doesn't work and return null
     *
     *
     * @param message Message
     * @return charset String
     */
    public static String getHeaderCharset(Message message)
      throws EntityException {


        String   charset = null;
        String[] values  = null;
        String   subj    = "";

        try {
            subj   = message.getSubject();
            values = message.getHeader(Def.HEADER_CONTENT_TYPE);
            if ((values != null) && (values.length == 1)) {
               String s = values[0];
               int chstart = s.indexOf(Def.HEADER_CHARSET);
               if (chstart != -1){
                  charset = s.substring(chstart+Def.HEADER_CHARSET.length()+1,
                                        s.length());
               }
            }
        } catch (MessagingException me) {
            throw new EntityException(
                    "Error getting Header Charset for email: [" + subj + "]", me);
        }
        return charset;
    }


    /**
     * Returns the charset in the ContentType
     * It can be used when the standard method
     * <br>
     *  ContentType ct = new ContentType(part.getContentType());
     * <br>
     *  String specifiedCharset = ct.getParameter("charset");
     * <br>
     * doesn't work and return null
     *
     *
     * @param headerContentType String
     * @return charset String
     */
    public static String getHeaderCharset(String headerContentType)
      throws EntityException {

        String charset = null;

        try {
             int chstart = headerContentType.indexOf(Def.HEADER_CHARSET);
             if (chstart != -1){
                 charset = headerContentType.substring(
                               chstart+Def.HEADER_CHARSET.length()+1,
                               headerContentType.length());
             }
        } catch (Exception me) {
            throw new EntityException("Error getting Charset for email", me);
        }
        return charset;
    }

    /**
     * Date : Thu Oct 06 09:46:47 CEST 2005
     * Returns a received date in UTC format
     *
     * @param message Message
     * @param loc     Locale
     * @return String
     */
    public static String getHeaderReceived(Message message, Locale loc) {

        String out = null;

        try {
            Date d = message.getReceivedDate();

            // convert in UTC format
            DateFormat utcf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", loc);
            utcf.setTimeZone(TimeZone.getTimeZone("UTC"));

            out = utcf.format(d);

        }
        catch (Exception e) {
            // do nothing; we just want to return null
        }
        return out;
    }


    /**
     * Returns the Header Date in String Format
     *
     * @param message Message
     * @return date in the header Date
     */
    public static String getHeaderDateForCRC(Message message)
      throws EntityException {

        String date = "";
        try {
            String[] values = message.getHeader(Def.HEADER_DATE);
            if ((values != null) && (values.length > 0)) {
                date = values[0];
            }
        } catch (MessagingException me) {
            throw new EntityException("Error getting Header Date for CRC", me);
        }

        return date;
    }

    /**
     * the method checks if the folder is a default folder
     * On Exchange server the Default folder are:
     * - /Inbox
     * - /Outbox
     * - /Sent
     * - /Draft
     * - /Trash
     * It's depend of the language (see configuration panel)
     *
     * @param df       DefaultFolder
     * @param fullPath String
     * @return boolean
     * @throws EntityException
     */
    public static boolean defaultFolderChecker(DefaultFolder df, String fullPath)
            throws EntityException {
        return fullPath.equals(Def.SEPARATOR_FIRST + df.getInboxName()) ||
                fullPath.equals(Def.SEPARATOR_FIRST + df.getOutboxName()) ||
                fullPath.equals(Def.SEPARATOR_FIRST + df.getSentName()) ||
                fullPath.equals(Def.SEPARATOR_FIRST + df.getDraftsName()) ||
                fullPath.equals(Def.SEPARATOR_FIRST + df.getTrashName());
    }

    /**
     * the method checks if the folder is a default folder
     * On Exchange server the Default folder are:
     * - /Inbox
     * - /Outbox
     * - /Sent
     * - /Draft
     * - /Trash
     * It's depend of the language (see configuration panel)
     *
     * @param df       DefaultFolder
     * @param fullPath String
     * @return folder id String
     * @throws EntityException
     */
    public static String getDefaultFolderId(DefaultFolder df, String fullPath)
            throws EntityException {

        if (fullPath.equals(Def.SEPARATOR_FIRST + df.getInboxName())) {
            return Def.FOLDER_INBOX_GUID;
        }
        if (fullPath.equals(Def.SEPARATOR_FIRST + df.getOutboxName())) {
            return Def.FOLDER_OUTBOX_GUID;
        }
        if (fullPath.equals(Def.SEPARATOR_FIRST + df.getSentName())) {
            return Def.FOLDER_SENT_GUID;
        }
        if (fullPath.equals(Def.SEPARATOR_FIRST + df.getDraftsName())) {
            return Def.FOLDER_DRAFTS_GUID;
        }
        if (fullPath.equals(Def.SEPARATOR_FIRST + df.getTrashName())) {
            return Def.FOLDER_TRASH_GUID;
        }

        throw new EntityException(fullPath + " is not a default folder.");
    }


    /**
     * @param fullpath String
     * @return String
     */
    public static String getFolderNameFromFullpath(String fullpath) {
        String folderName;
        int lastslash = fullpath.lastIndexOf(Def.SEPARATOR_FIRST);
        folderName = fullpath.substring(lastslash + 1, fullpath.length());
        return folderName;
    }


    /**
     * set folder role using the name
     *
     * @param folderName String
     * @return folder role String
     */
    public static String getFolderRole(String folderName, DefaultFolder df) {
        String folderRole = null;
        if (folderName.equalsIgnoreCase(df.getInboxName())) {
            folderRole = Def.FOLDER_INBOX_ROLE;
        } else if (folderName.equalsIgnoreCase(df.getOutboxName())) {
            folderRole = Def.FOLDER_OUTBOX_ROLE;
        } else if (folderName.equalsIgnoreCase(df.getSentName())) {
            folderRole = Def.FOLDER_SENT_ROLE;
        } else if (folderName.equalsIgnoreCase(df.getDraftsName())) {
            folderRole = Def.FOLDER_DRAFTS_ROLE;
        } else if (folderName.equalsIgnoreCase(df.getTrashName())) {
            folderRole = Def.FOLDER_TRASH_ROLE;
        }
        return folderRole;
    }


    /**
     * set creation date of a folder
     *
     * @return UTC creation date String
     */
    public static String getFolderCreationDate() {
        String creationDate;
        creationDate = "20050101T000000Z";
        return creationDate;
    }

    /**
     * gets the parent path of a folder using the full folder path
     *
     * @param fullpath String
     * @return String
     */
    public static String getParentFullpathFromFullpath(String fullpath) {
        String parentFullpath;

        int lastslash = fullpath.lastIndexOf(Def.SEPARATOR_FIRST);

        // gell all before last slash
        parentFullpath = fullpath.substring(0, lastslash);

        // add initial separator
        parentFullpath = Def.SEPARATOR_FIRST + parentFullpath;

        if (parentFullpath.equals(Def.SEPARATOR_FIRST + "INBOX")) {
            parentFullpath = Def.SEPARATOR_FIRST + "Inbox";
        }

        return parentFullpath;
    }


    /**
     *
     * get the ID in the specified partIndex
     * 1 it returns the FID
     * 2 it returns the FMID
     * 3 it returns the UIDV
     *
     * @param key String
     * @return id (it depends on the separator type)
     */
    public static String getKeyPart(String key, int partIndex) {

        String id = null;

        if (partIndex == 1) { // get FID
            int endFolderId = key.indexOf(Def.SEPARATOR_FIRST);
            if (endFolderId > 0) {
                id = key.substring(0, endFolderId);
            } else {
                // it should return "key" but for backward compatibility it 
                // returns null
                id = null;
            }
        } else if (partIndex == 2) { // get FMID
            int endFolderId = key.indexOf(Def.SEPARATOR_FIRST);
            int startValidityId = key.indexOf(Def.SEPARATOR_SECOND);
            if (startValidityId > 0) {
                // there is the Validity ID
                if (endFolderId >= 0) {
                    id = key.substring((endFolderId + Def.SEPARATOR_FIRST.length()), startValidityId);
                } else {
                    id = key.substring(0, startValidityId);
                }
            } else {
                // there is not the Validity ID
                if (endFolderId >= 0) {
                    id = key.substring((endFolderId + Def.SEPARATOR_FIRST.length()), key.length());
                } else {
                    id = key;
                }
            }
        } else if (partIndex == 3) { // get UIDV
            int startValidityId = key.indexOf(Def.SEPARATOR_SECOND);
            if (startValidityId > 0) {
                id = key.substring((startValidityId + Def.SEPARATOR_SECOND.length()), key.length());
            }
        }

        return id;

    }


    /**
     * Convert the UTC string in a Timestamp
     *
     * @param utc the string containing the utc time.
     * @return Timestamp the Timestamp object, null if any error occurs during
     *  conversion.
     */
    public static Timestamp UtcToTimestamp(String utc) {
        Timestamp t = null;
        Date date   = UtcToDate(utc);
        if(date!=null) {
           t = new Timestamp(date.getTime());
        }
        return t;
    }


    /**
     * Convert an UTC string in a Date.
     *
     * @param utc the string containing the utc time.
     * @return Date the date object, null if any error occurs during conversion.
     */
    public static Date UtcToDate(String utc) {
        Date d = null;
        if (utc != null) {
            try {
                DateFormat utcFormatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
                utcFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                d = utcFormatter.parse(utc);
            } catch (Exception e) {
                if (log.isTraceEnabled()) {
                    log.trace("Unparsable date: " + e.toString());
                }
            }
        }
        return d;
    }

    /**
     * Converts the UTC string in a long value
     *
     * @param utc the string containing the utc time.
     *
     * @return long is the result value for the conversion of the string, 0 if any
     * error occurs.
     */
    public static long UtcToLong(String utc) {
        long longDate = 0;
        Date date = UtcToDate(utc);
        if(date!=null) {
            longDate = date.getTime();
        }
        return longDate;
    }

    /**
     * merge arrays
     *
     * @param i CrcSyncItemInfo[]
     * @param o CrcSyncItemInfo[]
     * @param d CrcSyncItemInfo[]
     * @param s CrcSyncItemInfo[]
     * @param t CrcSyncItemInfo[]
     * @return CrcSyncItemInfo[]
     */
    public static String[] mergeArray(String[] i,
                                      String[] o,
                                      String[] d,
                                      String[] s,
                                      String[] t) {

        String[] ids;

        int length = 0;
        int ilength = 0;
        int olength = 0;
        int dlength = 0;
        int slength = 0;
        int tlength = 0;

        if (i != null) {
            ilength = i.length;
            length = ilength;
        }
        if (o != null) {
            olength = o.length;
            length = length + olength;
        }
        if (d != null) {
            dlength = d.length;
            length = length + dlength;
        }
        if (s != null) {
            slength = s.length;
            length = length + slength;
        }
        if (t != null) {
            tlength = t.length;
            length = length + tlength;
        }

        ids = new String[length];

        int yi, yo, yd, ys, yt;

        for (yi = 0; yi < ilength; yi++) {
            ids[yi] = i[yi];
        }
        for (yo = 0; yo < olength; yo++) {
            ids[yi + yo] = o[yo];
        }
        for (yd = 0; yd < dlength; yd++) {
            ids[yi + yo + yd] = d[yd];
        }
        for (ys = 0; ys < slength; ys++) {
            ids[yi + yo + yd + ys] = s[ys];
        }
        for (yt = 0; yt < tlength; yt++) {
            ids[yi + yo + yd + ys + yt] = t[yt];
        }

        return ids;
    }

    /**
     * 
     * @param hmi
     * @param hmo
     * @param hms
     * @param hmd
     * @param hmt
     * @return
     */
    public static Map mergeMap(Map hmi,
                                         Map hmo,
                                         Map hms,
                                         Map hmd,
                                         Map hmt){

        Map all = new LinkedHashMap();

        if (hmi != null){
            if (hmi.size()>0){
               all.putAll(hmi);
            }
        }
        if (hmo != null){
            if (hmo.size()>0){
               all.putAll(hmo);
            }
        }
        if (hms != null){
            if (hms.size()>0){
               all.putAll(hms);
            }
        }
        if (hmd != null){
            if (hmd.size()>0){
               all.putAll(hmd);
            }
        }
        if (hmt != null){
            if (hmt.size()>0){
               all.putAll(hmt);
            }
        }

        return all;

    }


    /**
     * merge arrays
     *
     * @param i CrcSyncItemInfo[]
     * @param o CrcSyncItemInfo[]
     * @param d CrcSyncItemInfo[]
     * @param s CrcSyncItemInfo[]
     * @param t CrcSyncItemInfo[]
     * @return CrcSyncItemInfo[]
     */
    public static SyncItemInfo[] mergeArray(SyncItemInfo[] i,
                                            SyncItemInfo[] o,
                                            SyncItemInfo[] d,
                                            SyncItemInfo[] s,
                                            SyncItemInfo[] t) {
        SyncItemInfo[] syncItemInfo;

        int length = 0;
        int ilength = 0;
        int olength = 0;
        int dlength = 0;
        int slength = 0;
        int tlength = 0;

        if (i != null) {
            ilength = i.length;
            length = ilength;
        }
        if (o != null) {
            olength = o.length;
            length = length + olength;
        }
        if (d != null) {
            dlength = d.length;
            length = length + dlength;
        }
        if (s != null) {
            slength = s.length;
            length = length + slength;
        }
        if (t != null) {
            tlength = t.length;
            length = length + tlength;
        }

        syncItemInfo = new SyncItemInfo[length];

        int yi;
        int yo;
        int yd;
        int ys;
        int yt;

        for (yi = 0; yi < ilength; yi++) {
            syncItemInfo[yi] = i[yi];
        }
        for (yo = 0; yo < olength; yo++) {
            syncItemInfo[yi + yo] = o[yo];
        }
        for (yd = 0; yd < dlength; yd++) {
            syncItemInfo[yi + yo + yd] = d[yd];
        }
        for (ys = 0; ys < slength; ys++) {
            syncItemInfo[yi + yo + yd + ys] = s[ys];
        }
        for (yt = 0; yt < tlength; yt++) {
            syncItemInfo[yi + yo + yd + ys + yt] = t[yt];
        }

        return syncItemInfo;
    }

    /**
     * merge arrays
     *
     * @param i CrcSyncItemInfo[]
     * @param o CrcSyncItemInfo[]
     * @param d CrcSyncItemInfo[]
     * @param s CrcSyncItemInfo[]
     * @param t CrcSyncItemInfo[]
     * @return CrcSyncItemInfo[]
     */
    public static ItemFolder[] mergeArray(ItemFolder[] i,
                                          ItemFolder[] o,
                                          ItemFolder[] d,
                                          ItemFolder[] s,
                                          ItemFolder[] t) {
        ItemFolder[] itemFolder;

        int length = 0;
        int ilength = 0;
        int olength = 0;
        int dlength = 0;
        int slength = 0;
        int tlength = 0;

        if (i != null) {
            ilength = i.length;
            length = ilength;
        }
        if (o != null) {
            olength = o.length;
            length = length + olength;
        }
        if (d != null) {
            dlength = d.length;
            length = length + dlength;
        }
        if (s != null) {
            slength = s.length;
            length = length + slength;
        }
        if (t != null) {
            tlength = t.length;
            length = length + tlength;
        }

        itemFolder = new ItemFolder[length];

        int yi, yo, yd, ys, yt;

        for (yi = 0; yi < ilength; yi++) {
            itemFolder[yi] = i[yi];
        }
        for (yo = 0; yo < olength; yo++) {
            itemFolder[yi + yo] = o[yo];
        }
        for (yd = 0; yd < dlength; yd++) {
            itemFolder[yi + yo + yd] = d[yd];
        }
        for (ys = 0; ys < slength; ys++) {
            itemFolder[yi + yo + yd + ys] = s[ys];
        }
        for (yt = 0; yt < tlength; yt++) {
            itemFolder[yi + yo + yd + ys + yt] = t[yt];
        }

        return itemFolder;
    }


    /**
     *
     * create a String with the email flags
     *
     * @param msg Message
     * @return long
     */
    public static String createFlagsList(Message msg) {
        String fgS = "";
        try {
            FlagProperties fg = createFlagProperties(msg);
            fgS = fg.toString();
        } catch (Exception me) {
            // do nothing; return ""
        }
        return fgS;
    }

    /**
     *
     * Compute CRC-32 checksum value for the specified Message
     *
     * @param flagList String
     * @return long
     */
    public static long createCRC(String flagList, String protocol) {

        if (Def.PROTOCOL_POP3.equals(protocol)){
            return 0;
        }
        
        CRC32 checksumEngine = new CRC32();
        long checksumFlag    = 0;

        try {

            byte[] bytesFlags = flagList.getBytes();
            checksumEngine.update(bytesFlags, 0, bytesFlags.length);
            checksumFlag = checksumEngine.getValue();
            checksumEngine.reset();

        } catch (Exception me) {
            // do nothing; we just want to return 0
        }

        return checksumFlag;

    }

    /**
     * the Funambol client send id in format: I/1234
     * the mobile phone can send the LUID not in the format : 1234
     *
     * @param syncItem SyncItem
     * @return FID, FMID and UIDV in an array String[]
     */
    public static String[] getKeyParts(SyncItem syncItem) {

        String[] keys = new String[3];

        String key = syncItem.getKey().getKeyAsString();

        String FID;
        if (syncItem.getParentKey() != null) {
            FID = getKeyPart(syncItem.getParentKey().getKeyAsString(), 2);
        } else {
            FID = getKeyPart(key,1);
        }
        keys[0] = FID;

        String FMID = getKeyPart(key,2);
        keys[1] = FMID;

        // get uid validity IMAP GUID : I/1234-FUN-646464
        String UIDV = getKeyPart(key,3);
        keys[2] = UIDV;

        return keys;
    }


    /**
     *
     * 
     * @param protocol
     * @param uid
     * @param UIDV
     * @return
     */
    public static String createGUID (String protocol, String uid, String UIDV){
        String GUID = null;
        if (protocol.equals(Def.PROTOCOL_IMAP)){
           GUID = Utility.createIMAPGUID(Def.FOLDER_INBOX_ID, uid, UIDV);
        } else {
           GUID = Utility.createPOPGUID(Def.FOLDER_INBOX_ID, uid);
        }
        return GUID;
    }

    /**
     *
     * 
     * @param protocol
     * @param GUID
     * @return
     */
    public static String createUID (String protocol, String GUID){
        String FMID = null;
        if (protocol.equals(Def.PROTOCOL_IMAP)){
            FMID = Utility.getKeyPart(GUID,2);
        } else {
            FMID = Utility.getKeyPart(GUID,2);
        }
        return FMID;
    }


    /**
     * sets the EMAIL GUID =  FID + Def.SEPARATOR + FMID + Def.SEPARATOR + UIDV
     *
     * @param FID  folder id String
     * @param FMID mail id in the folder String
     * @param UIDV folder validity String
     * @return String
     */
    public static String createIMAPGUID(String FID, String FMID, String UIDV) {
        return FID + Def.SEPARATOR_FIRST + FMID + Def.SEPARATOR_SECOND + UIDV;
    }

    /**
     * sets the EMAIL GUID =  FID + Def.SEPARATOR + FMID
     *
     * @param FID  folder id String
     * @param FMID mail id in the folder String
     * @return String
     */
    public static String createPOPGUID(String FID, String FMID) {
        return FID + Def.SEPARATOR_FIRST + FMID ;
    }

    /**
     * sets the FOLDER GUID =  FID + Def.SEPARATOR + FMID
     *
     * @param FID  folder id String
     * @param FMID mail id in the folder String
     * @return String
     */
    public static String createFolderGUID(String FID, String FMID) {
        return FID + Def.SEPARATOR_FIRST + FMID;
    }

    /**
     *
     * @param timeFilterClause String
     * @return comparison term for time filter int
     */
    public static int getComparisonTerm(String timeFilterClause) {

        int out = ComparisonTerm.GE;

        if (timeFilterClause.equalsIgnoreCase("EQ") ||
                timeFilterClause.equalsIgnoreCase("GT") ||
                timeFilterClause.equalsIgnoreCase("GE")) {
            out = ComparisonTerm.GE;
        } else if (timeFilterClause.equalsIgnoreCase("LT") ||
                timeFilterClause.equalsIgnoreCase("LE")) {
            out = ComparisonTerm.LE;
        }

        return out;
    }

    /**
     * Convert the given Date in an UTC string.
     *
     * @param origDate Date is the object to convert.
     * 
     * @return String is the utc representation of the given Date object.
     */
    public static String UtcFromDate(Date origDate) {
        String out = null;
        DateFormat utcf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        utcf.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (origDate != null) {
            out = utcf.format(origDate);
        }
        return out;
    }

    /**
     * <p>Decode the given <i>text</i> considering it encoded
     * with the given <i>encode type</i>.</p>
     *
     * @param text
     * @param encodeType
     * @return the decoded text
     * @throws MessagingException
     */
    public static String decode(String text, String encodeType)
            throws MessagingException {

        StringBuffer decodedText;
        InputStream inputStream;
        BufferedInputStream buffIn;
        byte[] tempBuff;
        int readLength;

        if (text == null) {
            return null;
        }

        inputStream = new ByteArrayInputStream(text.getBytes());

        if (Def.ENCODE_QUOTED_PRINTABLE.equals(encodeType)) {

            InputStream is = MimeUtility.decode(inputStream, Def.ENCODE_QUOTED_PRINTABLE);

            buffIn      = new BufferedInputStream(is);
            tempBuff    = new byte[1024];
            decodedText = new StringBuffer();

            try {
                while ((readLength = buffIn.read(tempBuff)) > -1) {
                    decodedText.append(new String(tempBuff, 0, readLength));
                }
            } catch (IOException e) {
                throw new MessagingException(e.getMessage(), e);
            }

        }

        return text;
    }


    /**
     * Checks if it's possible to optimize the syncml message
     * dimension.
     * For instance: an email in the inbox with only the 'read' flag updated
     * and already on the client.
     *
     * If we have a filter with the value "ID" we have to return false
     * because the client is asking all the emails.
     *
     * @param status  status of the item; char
     * @param FID     parent id of the item: String
     * @param filter  the email filter
     * @return true if it's possible to optimize the syncml dimension
     *
     */
    public static boolean needsFlagOptimization(char status,
                                                String FID,
                                                EmailFilter filter){

        boolean opt = false;

        if (filter != null){
           String filterID = filter.getId();
           if (filterID != null){
               //do not need optimization
               opt = false;
           } else {
               //
               if (
                    status == SyncItemState.UPDATED
                    &&
                    (Def.FOLDER_INBOX_GUID.equalsIgnoreCase(FID)  ||
                     Def.FOLDER_OUTBOX_GUID.equalsIgnoreCase(FID) ||
                     Def.FOLDER_SENT_GUID.equalsIgnoreCase(FID)   ||
                     Def.FOLDER_TRASH_GUID.equalsIgnoreCase(FID)   )
                   ){
                    opt = true;
               }
           }
        }

        return opt;
    }


   /**
    * returns an object with the flags: read, replied , flagged, forwarded.
    *
    * @param email Email
    * @return FlagProperties
    *
    */
   public static FlagProperties getFlags(Email email){

        FlagProperties fp = new FlagProperties();

        String  flagS     = "false";
        boolean read      = false;
        boolean replied   = false;
        boolean flagged   = false;
        boolean forwarded = false;

        if (email != null) {
            //
            flagS = (String)email.getRead().getPropertyValue();
            if (flagS != null){
               read = (Boolean.valueOf(flagS)).booleanValue();
            }
            //
            flagS = (String)email.getReplied().getPropertyValue();
            if (flagS != null){
               replied = (Boolean.valueOf(flagS)).booleanValue();
            }
            //
            flagS = (String)email.getFlagged().getPropertyValue();
            if (flagS != null){
               flagged = (Boolean.valueOf(flagS)).booleanValue();
            }
            //
            flagS = (String)email.getForwarded().getPropertyValue();
            if (flagS != null){
               forwarded = (Boolean.valueOf(flagS)).booleanValue();
            }

        }

        fp.setSeen(read);
        fp.setAnswered(replied);
        fp.setFlagged(flagged);
        fp.setForwarded(forwarded);

        return fp;

   }

   /**
    * returns an object with the flags: read, replied , flagged, forwarded.
    *
    * @param msg Message
    * @return FlagProperties
    *
    */
   public static FlagProperties getFlags(Message msg){

        FlagProperties fp = new FlagProperties();

        boolean read      = false;
        boolean replied   = false;
        boolean flagged   = false;
        boolean forwarded = false;

        if (msg != null) {
            try {
                read      = msg.isSet(Flags.Flag.SEEN);
                replied   = msg.isSet(Flags.Flag.ANSWERED);
                flagged   = msg.isSet(Flags.Flag.FLAGGED);
                forwarded = isForwarded(msg, Def.IS_FORWARDED_FLAG);                
            } catch (MessagingException me){
                log.error("Error getting the flags from the Message", me);
            }

        }

        fp.setSeen(read);
        fp.setAnswered(replied);
        fp.setFlagged(flagged);
        fp.setForwarded(forwarded);

        return fp;

   }

  /**
   * compare 2 date
   * @param d1
   * @param d2
   * @return true if d1 >= d2
   */
  public static boolean d1Afterd2(Date d1, Date d2){
      long d1t = d1.getTime();
      long d2t = d2.getTime();
      boolean check = (d1t >= d2t);
      return  check ;
  }

  /**
   * compare 2 date
   * @param d1
   * @param d2
   * @return true if d1 < d2
   */
  public static boolean d1Befored2(Date d1, Date d2){
      long d1t = d1.getTime();
      long d2t = d2.getTime();
      boolean check = (d1t < d2t);
      return  check ;
  }


  /**
   * utility method for the test classes
   */
   public static String readFile(String filename) throws Exception{

      String content = "";
      byte[] bytes = null;
      int offset = 0;
      int numRead = 0;

      try {
         String sContent = "";

         File file = new File(filename);
         InputStream is = new FileInputStream(file);

         // Get the size of the file
         long length = file.length();
         if (length > Integer.MAX_VALUE) {
             throw new Exception ("file too long");
         }

         // Create the byte array to hold the data
         bytes = new byte[(int)length];

         // Read in the bytes
         while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
             offset += numRead;
         }

         // Ensure all the bytes have been read in
         if (offset < bytes.length) {
             throw new Exception("Could not completely read file "+file.getName());
         }
         is.close();
      } catch (Exception e) {
         log.error("Error in method readFile", e);
      }

      return  new String(bytes);

   }

   /**
    * for testing purpose
    */
   public static void printMessage(Message msg){
       try {
           OutputStream os = new ByteArrayOutputStream();
           msg.writeTo(os);
           String s = os.toString();
           System.out.println(s);
       } catch (Exception e){
           log.error("Error in method printMessage", e);
       }
   }

   /**
    * for testing purpose
    *  default file name
    * "D:\\development\\funambol_v6\\v6\\modules\\emailconnector\\src\\java\\com\\funambol\\email\\test\\A.txt";
    *
    */
   public static void printMessageInFile(Message msg, String fileName){


        String fn ="C:\\"+fileName+".txt";

    try {

            OutputStream os = new ByteArrayOutputStream();
            msg.writeTo(os);
            String msgS = os.toString();
            
            FileOutputStream out = new FileOutputStream(fn);
            PrintStream p = new PrintStream( out );
        p.println (msgS);
        p.close();

    } catch (Exception e){
            log.error("Error in method printMessageInFile", e);
    }

   }


  /**
   * Used in the ConsoleManager.
   * From the UI we get a value in the minute format
   * but we have to set in the DB a millisecond format
   */
  public static long setMillis(long minutes) {
      long ms = (60 * minutes) * 1000;
      return ms;
  }

  /**
   * Used in the ConsoleDAO.
   * in the DB there is a value in milliSecond format
   * and we have to show in the UI a minute format
   */
  public static long setMinutes(long millis) {
      long minutes = (millis/1000) / 60;
      if (minutes == 0){
          minutes = 1;
      }
      return minutes;
  }


  /**
   * used for debug purpose in the MailServer connection
   *
   */
  public static String getMailServerIP(String server){
      String ip = "unknown ip";
      try  {
         InetAddress ina = InetAddress.getByName(server);
         ip = ina.getHostAddress();
      } catch (UnknownHostException uhe){
          // do nothing
      }
      return ip;
  }

    /**
     * Creates a <code>FlagProperties</code> object which represents the flags
     * for a given message.
     * @param msg message from which extract flags
     * @return a <code>FlagProperties</code> object or <code>null</code>, if some
     * exception occurs while reading message.
     */
    public static FlagProperties createFlagProperties(Message msg) {

        FlagProperties flagProperties = null;

        try {

            boolean isSeen      = msg.isSet(Flags.Flag.SEEN);
            boolean isAnswered  = msg.isSet(Flags.Flag.ANSWERED);
            boolean isDeleted   = msg.isSet(Flags.Flag.DELETED);
            boolean isDraft     = msg.isSet(Flags.Flag.DRAFT);
            boolean isFlagged   = msg.isSet(Flags.Flag.FLAGGED);
            boolean isForwarded = isForwarded(msg, Def.IS_FORWARDED_FLAG);

            flagProperties = new FlagProperties(isSeen, 
                    isAnswered,
                    isDeleted, 
                    isDraft,
                    isFlagged,
                    isForwarded);
        } catch (MessagingException me) {
             // do nothing
        }

        return flagProperties;
    }

  /**
   * add the prefix 'recent:' for a gmail serrver
   *
   * @param msa MailServerAccount
   * @return username
   *
   */
  public static String getLogin(MailServerAccount msa){

      String userNew     = "";

      String type        = msa.getMailServer().getMailServerType();
      String loginFromDB = msa.getMsLogin();

      if (Def.SERVER_TYPE_GMAIL.equals(type) &&
          Def.PROTOCOL_POP3.equals(msa.getMailServer().getProtocol())) {
          
          // check the prefix
          if (loginFromDB.startsWith("recent:")){
              userNew = loginFromDB;
          } else {
              userNew = "recent:" + loginFromDB;
          }
      } else {
          userNew = loginFromDB;
      }

      return userNew;

  }

  // ----------------------------------------------------------- Private methods
  
    /**
     * This method tries to assert if a message has been forwarded or not.
     * <p/>
     * It looks in the user flags list for the string that represent the forwarded
     * flag (comparison is case insensitive). Note that is up to the mail server
     * to write in the user list information that sais if the message has been
     * forwarded and also choose what string to put. Thus this method may not
     * return the correct information, if the mail server uses a flag different
     * from <code>forwardedFlag</code> or does not put any information at all.
     *
     * @see EntityManager.createMessage for how to set forwarded flag for incoming
     * mails.
     *
     * @param message email message
     * @param forwardedFlag string that represent the forwarded flag
     * @return <code>true</code> if the <code>forwardedFlag</code> is contained
     * in the user flag list, <code>false</code> otherwise.
     * @throws javax.mail.MessagingException
     */
    private static boolean isForwarded(Message message, String forwardedFlag)
    throws MessagingException {
        
        String[] userFlags = message.getFlags().getUserFlags();

        if (userFlags == null) {
            return false;
        }
        
        for (String flag : userFlags) {
            if (flag.equalsIgnoreCase(forwardedFlag)) {
                return true;
            }
        }
        
        return false;
    }
    
}
