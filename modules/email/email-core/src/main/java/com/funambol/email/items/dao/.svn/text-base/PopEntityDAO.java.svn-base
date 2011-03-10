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
package com.funambol.email.items.dao;

import com.funambol.email.transport.PopMailServerWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.UIDFolder;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import javax.sql.DataSource;

import com.sun.mail.pop3.POP3Folder;

import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.id.DBIDGenerator;

import com.funambol.email.exception.EntityException;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.ItemMessage;
import com.funambol.email.util.Def;
import com.funambol.email.util.DescendingComparator;
import com.funambol.email.util.Query;
import com.funambol.email.util.Utility;

/**
 * This class implements methods to access data
 * in Mail Server datastore.
 *
 * @version $Id: PopEntityDAO.java,v 1.3 2008-06-03 09:26:28 testa Exp $
 */
public class PopEntityDAO extends EntityDAO {

    // ------------------------------------------------------------ Constructors

    /**
     *
     */
    public PopEntityDAO() {
    }

    /**
     * @param serverType
     * 
     */
    public PopEntityDAO(String serverType) throws EntityException {
        super(serverType);
    }
    
    /**
     * @param jndiDataSourceName String
     * 
     * @deprecated Since v66, the db has been splitted in core db and user db so
     * the jndi names are now hardcoded. Use the constructor without jndiDataSourceName
     */
    public PopEntityDAO(String jndiDataSourceName, String serverType) throws EntityException {
        this(serverType);
    }

    // -----------------------------------------------------------Public Methods

    /**
     * insert an email in the fnbl_email_sentpop table
     *
     * @param FID         IMAPFolder ID
     * @param messageID   header Message-ID
     * @param msg         Message
     * @param idSentSpace The GUID generator
     * @param source_uri  The SyncSource URI.
     * @param principalId The principal ID.
     * @param username    The username
     * @return GUID String
     * @throws EntityException
     */
    public String addSentEmail(String FID,
                               String messageID,
                               Message msg,
                               DBIDGenerator idSentSpace,
                               String source_uri,
                               long principalId,
                               String username) throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start addItemInLocalDB folder " + FID);
        }

        String GUID = null;
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = userDataSource.getRoutedConnection(username);
            ps = connection.prepareStatement(Query.SENT_INSERT_SENT);

            GUID = Utility.createPOPGUID(FID, ""+idSentSpace.next());

            ps.setString(1, GUID);
            ps.setString(2, source_uri);
            ps.setLong(3, principalId);
            ps.setBytes(4, mail2db(msg));
            ps.setString(5, messageID);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }
        if (log.isTraceEnabled()) {
            log.trace("DAO end insertItem UID " + GUID);
        }

        return GUID;
    }


    /**
     * gets all Messages
     * NOTE: in the pop protocol this feature doesn't work
     * <br>SearchTerm st = new ReceivedDateTerm(ComparisonTerm.GE, filter.getTime());
     * <br>messages = f.search(st);
     * <br>so we have to use a manual filter
     *
     * @param f POP3Folder
     * @return Message[]
     * @throws EntityException
     */
    public Message[] getAllEmailsInbox(POP3Folder f)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start getAllEmailsInbox ");
        }

        Message[] messages;

        try {
             messages = f.getMessages();
        }
        catch (MessagingException me) {
            throw new EntityException(me);
        }

        if (log.isTraceEnabled()) {
            if (messages != null) {
                log.trace("DAO end getAllEmailsInbox - number of items " + messages.length);
            } else {
                log.trace("DAO end getAllEmailsInbox - number of items 0");
            }
        }

        return messages;

    }

    /**
     * @param filter
     * @param source_uri  the SyncSource URI.
     * @param principalId The principal ID.
     * @param username the username
     * @return The items fulfilling the filter requirements as an Array of Strings.
     * @throws EntityException
     */
    public String[] getAllEmailsSent(EmailFilter filter,
                                     String source_uri,
                                     long principalId,
                                     String username)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start getAllEmailsSent ");
        }

        String[] ids = null;
        Connection        connection = null;
        PreparedStatement ps = null;
        ResultSet         rs = null;
        
        ArrayList rows = new ArrayList();

        try {
            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.SENT_SELECT_ALL_SENT);

            ps.setString(1, source_uri);
            ps.setLong  (2, principalId);
            ps.setString(3, Def.FOLDER_SENT_ID + Def.SEPARATOR_FIRST + "%");

            rs = ps.executeQuery();

            rows = this.createSentList(rs, filter.getMaxSentNum());

            ids = (String[]) rows.toArray(new String[0]);

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }
        if (ids != null) {
            if (log.isTraceEnabled()) {
                log.trace("number of items " + ids.length);
            }
        }

        return ids;
    }

    /**
     * @param msg Message
     * @throws EntityException
     */
    public void removeEmail(Message msg) throws EntityException {
        try {
            msg.setFlag(Flags.Flag.DELETED, true);
        } catch (MessagingException me) {
            throw new EntityException(me);
        }
    }

    /**
     * @param f   IMAPFolder
     * @param uid long
     * @throws EntityException
     */
    public void removeEmail(POP3Folder f, String uid)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("POP DAO start removePopItem ");
        }

        try {
            Message[] messages = f.getMessages();
            if (messages != null && messages.length > 0){
                Message msg = getMessageByUID(f, messages, uid);
                if (msg != null) {
                    msg.setFlag(Flags.Flag.DELETED, true);
                }
            }
        } catch (MessagingException me) {
            throw new EntityException(me);
        }
    }

    /**
     * @param f POP3Folder
     * @throws EntityException
     */
    public void removeAllEmail(POP3Folder f)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("POP3 DAO start removeAllEmail ");
        }

        Message[] messages;
        try {

            messages = f.getMessages();

            for (int i = 0; i < messages.length; i++) {
                messages[i].setFlag(Flags.Flag.DELETED, true);
            }

        } catch (MessagingException e) {
            throw new EntityException(e);
        }

    }

    /**
     * serch a document using UID; for POP3  protocol UID is String
     * I have to use a custome getMessageByUID because
     * there is not a folder.getMessageByUID method like in the imap protocol
     *
     * @param f   POP3Folder
     * @param uid String
     * @return Message
     * @throws EntityException
     */
    public Message getEmailInboxFromUID(POP3Folder f,
                                        Message[] messages,
                                        String uid)
      throws EntityException {
        Message msg = null;
        try {
            if (messages != null && messages.length > 0){
               msg = getMessageByUID(f, messages, uid);
            }
        } catch (MessagingException me) {
            throw new EntityException(me);
        }
        return msg;
    }

    /**
     * @param GUID        the Global Unique ID.
     * @param s           The session info to the backend.
     * @param source_uri  the SyncSource URI.
     * @param principalId The principal ID.
     * @param username    The username
     * @return Message
     * @throws EntityException
     */
    public Message getEmailSentFromUID(String GUID,
                                       Session s,
                                       String source_uri,
                                       long principalId,
                                       String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("POPDAO start getItemByUIDInSent GUID: " + GUID);
        }

        Message message = null;
        Connection        connection = null;
        PreparedStatement ps         = null;
        ResultSet         rs         = null;

        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.SENT_SELECT_SENT);
            ps.setString(1, GUID       );
            ps.setString(2, source_uri );
            ps.setLong  (3, principalId);
            rs = ps.executeQuery();
            while (rs.next()) {
                message = db2mail(getBytesFromColumn(1, rs), s);
            }
        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return message;
    }

    /**
     * @param GUID        the Global Unique ID.
     * @param source_uri  the SyncSource URI.
     * @param principalId The principal ID.
     * @throws EntityException
     */
    public void removeEmail(String GUID,
                            String source_uri,
                            long principalId,
                            String username)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("POP DAO start removeItemInSent UID " + GUID);
        }

        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = userDataSource.getRoutedConnection(username);
            ps = connection.prepareStatement(Query.SENT_DELETE_SENT);
            ps.setString(1, GUID);
            ps.setString(2, source_uri);
            ps.setLong  (3, principalId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        if (log.isTraceEnabled()) {
            log.trace("POP DAO end removeItemInSent UID " + GUID);
        }

    }

    /**
     * serch a document using the header Message-ID
     *
     * @param message_id  String
     * @param s           Session
     * @param source_uri  String
     * @param principalId long
     * @param username the username
     * @return ItemMessage
     * @throws EntityException
     */
    public ItemMessage getEmailFromMessageID(String message_id,
                                             Session s,
                                             String source_uri,
                                             long principalId,
                                             String username)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start getItemByMessageIDInSent Header Message-ID: " +
                    message_id);
        }

        ItemMessage eisMessage = null;
        String GUID = null;
        Message javaMessage = null;
        Connection        connection = null;
        PreparedStatement ps         = null;
        ResultSet         rs         = null;

        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.SENT_SELECT_TWIN_SENT);
            ps.setString(1, message_id);
            ps.setString(2, source_uri);
            ps.setLong(3, principalId);
            rs = ps.executeQuery();
            while (rs.next()) {
                GUID = rs.getString(1);
                javaMessage = db2mail(getBytesFromColumn(2, rs), s);
            }
            eisMessage = new ItemMessage(GUID, "S", message_id, javaMessage,
                                         null, null, null, null, null, null,
                                         null, null, null, null, null);
        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        if (log.isTraceEnabled()) {
            log.trace("DAO end getItemByMessageIDInSent Header Message-ID: " +
                    message_id);
        }

        return eisMessage;
    }

    //---------------------------------------------------------- Private methods

    /**
     * used by the SENT/OUTBOX synchronization
     *
     * @param msg Message
     * @return byte[]
     * @throws Exception
     */
    private static byte[] mail2db(Message msg) throws Exception {
        OutputStream baos = new ByteArrayOutputStream();
        msg.writeTo(baos);
        String msgS = baos.toString();
        return msgS.getBytes();
    }

    /**
     * used by the SENT/OUTBOX synchronization
     *
     * @param b byte[]
     * @param s Session
     * @return Message
     * @throws Exception
     */
    private Message db2mail(byte[] b, Session s) throws Exception {
        InputStream is = new ByteArrayInputStream(b);
        return new MimeMessage(s, is);
    }

    /**
     *
     * get the email from the blob column in the DB
     *
     * @param  colIndex
     * @param  rs
     * @return email from the DB byte[]
     * @throws SQLException
     */
    private byte[] getBytesFromColumn(int colIndex, ResultSet rs)
            throws SQLException {

        Object obj = rs.getObject(colIndex);
        if (obj instanceof Blob) {
            return ((Blob) obj).getBytes(1, (int) ((Blob) obj).length());
        }
        if (obj instanceof Clob) {
            return ((Clob) obj).getSubString(1, (int) ((Clob) obj).length()).getBytes();
        }
        if (obj instanceof byte[]) {
            return (byte[]) obj;
        }
        if (obj instanceof String) {
            return ((String) obj).getBytes();
        }
        throw new ClassCastException("Unexpected type " + obj.getClass().getName() + ".");
    }


    /**
     * workaround.
     * In the POP folder there is not the method
     * getMessageByUID
     *
     * @param f POP3Folder
     * @param uid String
     * @return Message
     * @throws MessagingException
     */
    private Message getMessageByUID(POP3Folder f, Message[] messages, String uid)
     throws MessagingException{

        Message msgOut = null;
        Message msgTmp = null;
        String uidTmp  = null;
        boolean check  = false;

        //for (int i=0; i<messages.length; i++) {
        for (int i=(messages.length-1); i>=0; i--){
            msgTmp = messages[i];
            uidTmp = f.getUID(msgTmp);
            if (uidTmp != null){
                if (uidTmp.equalsIgnoreCase(uid)) {
                    check = true;
                    break;
                }
            }
        }

        if (check){
            msgOut = msgTmp;
        }

        return msgOut;
  }

   /**
    *
    */
   private ArrayList createSentList(ResultSet rs, int max)
   throws SQLException{

        ArrayList rows  = new ArrayList();
        TreeMap rowsTmp = new TreeMap(new DescendingComparator());

        // create a sorted Map with all values
        String GUID = null;
        String sFMID = null;
        int FMID = 0;

        while (rs.next()) {
            GUID  = rs.getString(1);
            try {
               sFMID = Utility.getKeyPart(GUID, 2);
               FMID  = Integer.parseInt(sFMID);
               rowsTmp.put(new Integer(FMID), GUID);
            } catch (Exception e){
               // do nothing
            }
        }

        // get only max items
        int counter = 1;
        String id = null;
        Iterator values = rowsTmp.values().iterator();
        while (values.hasNext()) {
            id = (String)values.next();
            rows.add(id);
            counter++;
            if (counter > max){
                break;
            }
        }

        return rows;
   }

   
    /**
     *
     * Used in the stardard process (called by PopEntityManager.getEmailFormUID)
     *
     * @param FMID        mail id (in the folder, not unique)
     * @param max max number of email
     * @param threshold_1 max number for the first loop
     * @param threshold_2  max number for the second loop
     * @return Message
     * @throws EntityException
     */
    public Message getItem(String FMID,
                            int max,
                            int threshold_1,
                            int threshold_2,
                            PopMailServerWrapper pmsw)
    throws EntityException {

        Message msg     = null;
        int     counter = 1;
        boolean loop    = true;

        /*
         * see EntityDAO.getAllEmailsInbox, InboxListenerManager.setInfosInDB
         */
        max = max + Def.MAX_MAIL_NUMBER_OFFSET;
        
        if (log.isTraceEnabled()) {
            log.trace("getItem Start");
        }

        if (this.serverType.equalsIgnoreCase(Def.SERVER_TYPE_GMAIL)){

            try {
                if (pmsw.messagesInboxOpened == null){

                     int mNumber = pmsw.folderInboxOpened.getMessageCount();

                     pmsw.messagesInboxOpened = getItemLoop0(
                                                       pmsw.folderInboxOpened,
                                                       mNumber,
                                                       max);
                }

                msg = getEmailInboxFromUID(pmsw.folderInboxOpened,
                                               pmsw.messagesInboxOpened,
                                               FMID);
            } catch (MessagingException me){
                throw new EntityException("Error getting Gmail emails (" +
                                          me.getMessage() + ")");
            }

        } else {
            long timeStart = System.currentTimeMillis();

            while(loop){

                if (pmsw.messagesInboxOpened == null){
                     pmsw.messagesInboxOpened = getItemLoopManager(
                                                       pmsw.folderInboxOpened,
                                                       max, counter,
                                                       threshold_1, threshold_2);
                }

                msg = getEmailInboxFromUID(pmsw.folderInboxOpened,
                                               pmsw.messagesInboxOpened,
                                               FMID);

                if (msg != null){
                    // msg found
                    loop = false;
                } else {
                    // msg null; Not found
                    if (counter == 4){
                        loop = false;
                    } else {
                        counter++;
                        pmsw.messagesInboxOpened = null;
                    }
                }

            }

            long timeStop = System.currentTimeMillis();
            if (log.isTraceEnabled()) {
                log.trace("getItem (with loop optimiztion) Execution Time: " +
                             (timeStop - timeStart) + " ms");
            }
        }

        return msg;

    }
   

    /**
     *
     * return all the emails in the inbox
     * for getSyncItemFromUID:
     * first  cycle : get just 20 mails (if max > 20)
     * second cycle : get just 40 mails (if max > 40)
     * third  cycle : get Max emails
     * for removeSyncItem:
     * first  cycle : get just 20 mails (if max > 20)
     * second cycle : get just 40 mails (if max > 40)
     * third  cycle : get Max emails
     *
     * @param inbox folder
     * @param max number of email in the inbox folder
     * @param cycle
     * @param threshold 1
     * @param threshold 2
     * @return The all items fulfilling the filter requirements as an array of Strings.
     * @throws EntityException
     */
    private Message[] getItemLoopManager(POP3Folder inbox,
                                         int max,
                                         int loop,
                                         int threshold_1,
                                         int threshold_2)
      throws EntityException {

        Message[] messages;

        if (log.isTraceEnabled()) {
            log.trace("getItemLoopManager Start (loop: " + loop + "; max: "+max+
                       "; t_1: "+threshold_1+"; t_2 "+ threshold_2 +";)");
        }

        try {

            int mNumber = inbox.getMessageCount();

            if        (loop == 1){
                messages = this.getItemLoop1_2(inbox,mNumber,max,threshold_1);
            } else if (loop == 2){
                messages = this.getItemLoop1_2(inbox,mNumber,max,threshold_2);
            } else if (loop == 3){
                messages = this.getItemLoop3(inbox,mNumber,max);
            } else {
                messages = this.getItemLoop4(inbox);
            }

        } catch (Exception e) {
            throw new EntityException("Error Getting Max Number Inbox emails (" +
                    e.getMessage() + ")");
        }

        return messages;

    }

    /**
     *
     *
     * @param inbox folder
     * @param max number of email in the inbox folder
     * @param threshold 1
     * @return The all items fulfilling the filter requirements as an array of Strings.
     * @throws EntityException
     */
    private Message[] getItemLoop1_2(POP3Folder inbox,
                                     int mNumber,
                                     int max,
                                     int threshold)
      throws EntityException {

        Message[] messages;

        try {

           if (mNumber > threshold){
               if (max > threshold){
                    messages = inbox.getMessages((mNumber-(threshold-1)),mNumber);
               } else {
                    messages = inbox.getMessages((mNumber-(max-1)),mNumber);
               }
           } else {
               messages = inbox.getMessages();
           }

        } catch (Exception e) {
            throw new EntityException("Error Getting Max Number Inbox emails (" +
                    e.getMessage() + ")");
        }


        return messages;

    }



    /**
     *
     * @param inbox folder
     * @param max number of email in the inbox folder
     * @return The all items fulfilling the filter requirements as an array of Strings.
     * @throws EntityException
     */
    private Message[] getItemLoop3(POP3Folder inbox,
                                    int mNumber,
                                    int max)
      throws EntityException {

        Message[] messages;

        try {

            if (mNumber > max) {
                messages = inbox.getMessages((mNumber-(max-1)),mNumber);
            } else {
                messages = inbox.getMessages();
            }

        } catch (Exception e) {
            throw new EntityException("Error Getting Max Number Inbox emails (" +
                    e.getMessage() + ")");
        }

        return messages;

    }


    /**
     *
     * get all ( < max ) the messages without looping
     *
     * @param inbox folder
     * @param max number of email in the inbox folder
     * @return The all items fulfilling the filter requirements as an array of Strings.
     * @throws EntityException
     */
    private Message[] getItemLoop0(POP3Folder inbox,
                                   int mNumber,
                                   int max)
      throws EntityException {

        Message[] messages;

        try {
            
            if (mNumber > max) {                
                messages = inbox.getMessages((mNumber-(max-1)),mNumber);
            } else {
                messages = inbox.getMessages();
            }

            // Fetch the UID's and only the UIDs using the cool JavaMail FetchProfile.
            FetchProfile fp = new FetchProfile();
            fp.add(UIDFolder.FetchProfileItem.UID);
            inbox.fetch(messages, fp);

        } catch (Exception e) {
            throw new EntityException("Error Getting Max Number Inbox emails (" +
                    e.getMessage() + ")");
        }

        return messages;

    }


    /**
     *
     *
     * @param inbox folder
     * @param max number of email in the inbox folder
     * @param cycle
     * @param threshold 1
     * @param threshold 2
     * @return The all items fulfilling the filter requirements as an array of Strings.
     * @throws EntityException
     */
    private Message[] getItemLoop4(POP3Folder inbox)
      throws EntityException {

        Message[] messages;

        try {

            messages = inbox.getMessages();

        } catch (Exception e) {
            throw new EntityException("Error Getting Max Number Inbox emails (" +
                    e.getMessage() + ")");
        }

        return messages;

    }



}