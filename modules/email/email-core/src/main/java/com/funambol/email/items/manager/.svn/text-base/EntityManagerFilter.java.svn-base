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


import com.funambol.email.items.manager.ContentProviderInfo;
import com.funambol.email.exception.EntityException;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.FlagProperties;
import com.funambol.email.model.ItemMessage;
import com.funambol.email.model.InternalPart;
import com.funambol.email.pdi.mail.Ext;
import com.funambol.email.util.Def;
import com.funambol.email.util.Utility;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

/**
 * <p>Filter class. </p>
 * 
 * @version $Id: EntityManagerFilter.java,v 1.5 2008-06-19 16:29:40 gbmiglia Exp $
 */

public class EntityManagerFilter extends EntityManager {


    //----------------------------------------------------------- PUBLIC METHODS


    /**
     * returns only header
     *
     * @param session opened mail server session
     * @param FID     parent folder id
     * @param FMID    mail id in the folder
     * @param msg     Message
     * @param status  status of the item; char
     * @param loc     Locale
     * @param commonInfo could be null
     * @param contentProviderInfos not null List that will contain the AttachmentsInfo
     *        objects related to the attachments for this message. If this message
     *        does not have any atatchment then no AttachmentsInfo object are
     *        created and added to the list
     * @return ItemMessage
     * @throws EntityException
     */
    public ItemMessage getH(Session session,
            String FID,
            String FMID,
            Message msg,
            char status,
            Locale loc,
            EmailFilter filter,
            ContentProviderInfo contentProviderInfo)
            throws EntityException {

        ItemMessage im = null;
        Ext ext = new Ext();

        boolean needOpt = Utility.needsFlagOptimization(status, FID, filter);

        try {

            // save the flag
            FlagProperties fp = Utility.getFlags(msg);

            MimeMessage newMsg = new MimeMessage(session);

            // HEADER
            String[] h = MessageCreator.setNewHeaderIn(newMsg, msg, FID, FMID, Def.IS_DEVICE);
            String mid              = h[0];
            int    headersDimension = Integer.parseInt(h[1]);

            // CONTENT
            try {

                newMsg.setContent(Def.CONTENT_BODY, Def.CONTENT_CONTENTTYPE);
                
                // get the info from original msg
                ArrayList parts = MessageParser.getAllPartsOfMessage(msg, true);
                                                
                ext = MessageCreator.getExtComplete(parts, headersDimension,
                        contentProviderInfo);

            } catch (Exception e){
                log.error("Generic error creating the content .. set the default content", e);
                newMsg.setContent(Def.CONTENT_BODY_ALERT, Def.CONTENT_CONTENTTYPE);
            }

            newMsg.saveChanges();

            // the save message command change the message-ID
            newMsg.setHeader(Def.HEADER_MESSAGE_ID, mid);

            // get encodig for : <emailitem enc="quoted-printable">
            String encodingType = MessageCreator.getEncodingType(newMsg);

            im = setItemMessage(FMID, FID, newMsg, fp, loc, encodingType, ext);

            // re-set the flag on the server
            msg.setFlag(Flags.Flag.SEEN    , fp.getSeen());
            msg.setFlag(Flags.Flag.ANSWERED, fp.getAnswered());
            msg.setFlag(Flags.Flag.FLAGGED , fp.getFlagged());
            //msg.setFlag(Flags.Flag.FORWARDED, fp.getForwarded());

        } catch (MessagingException me) {
            throw new EntityException(me);
        }

        // remove the message if needed (syncml dimension optimization)
        if (needOpt) {
            im.setStreamMessage("");
        }

        return im;

    }


    /**
     *
     * returns header and body with byte quatity limitation
     *
     *
     * @param session  opened mail server session
     * @param FID      parent folder id
     * @param FMID     mail id in the folder
     * @param msg      Message
     * @param status   status of the item; char
     * @param loc      Locale
     * @param numBytes int
     * @param filter   EmailFilter
     * @param contentProviderInfos not null List that will contain the AttachmentsInfo
     *        objects related to the attachments for this message. If this message
     *        does not have any atatchment then no AttachmentsInfo object are
     *        created and added to the list
     * @return ItemMessage
     * @throws EntityException
     */
    public ItemMessage getHB(Session session,
                             String FID,
                             String FMID,
                             Message msg,
                             char status,
                             Locale loc,
                             int numBytes,
                             EmailFilter filter,
                             ContentProviderInfo contentProviderInfo)
      throws EntityException {

        ItemMessage im             = null;
        Ext         ext            = new Ext();

        boolean needOpt = Utility.needsFlagOptimization(status, FID, filter);

        try {

            // save the flag
            FlagProperties fp = Utility.getFlags(msg);

            MimeMessage newMsg = new MimeMessage(session);

            // HEADER
            String[] h = MessageCreator.setNewHeaderIn(newMsg, msg, FID, FMID, Def.IS_DEVICE);
            String mid              = h[0];
            int    headersDimension = Integer.parseInt(h[1]);

            // CONTENT
            try {
                if (needOpt){

                    // set empty content (will be removed)
                    newMsg.setContent(Def.CONTENT_BODY, Def.CONTENT_CONTENTTYPE);

                } else {

                    // get just the body (eventually truncted)
                    ArrayList parts = MessageParser.getAllPartsOfMessage(msg, true);
                                                            
                    InternalPart ip = MessageCreator.getHB_GetPart(parts, numBytes, ext, headersDimension,
                            contentProviderInfo);
                    MessageCreator.getHB_Content(newMsg, msg, ip);

                }
            } catch (Exception e){
                log.error("Generic error creating the content .. set the default content", e);
                newMsg.setContent(Def.CONTENT_BODY_ALERT, Def.CONTENT_CONTENTTYPE);
            }

            newMsg.saveChanges();

            // the save message command change the message-ID
            newMsg.setHeader(Def.HEADER_MESSAGE_ID, mid);

            // get encodig for : <emailitem enc="quoted-printable">
            String encodingType = MessageCreator.getEncodingType(newMsg);

            im = setItemMessage(FMID, FID, newMsg, fp, loc, encodingType, ext);

            // re-set the flag on the server
            msg.setFlag(Flags.Flag.SEEN    , fp.getSeen());
            msg.setFlag(Flags.Flag.ANSWERED, fp.getAnswered());
            msg.setFlag(Flags.Flag.FLAGGED , fp.getFlagged());
            //msg.setFlag(Flags.Flag.FORWARDED, fp.getForwarded());

        } catch (MessagingException me) {
            throw new EntityException(me);
        }

        // remove the message if needed (syncml dimension optimization)
        if (needOpt){
            im.setStreamMessage("");
        }

        return im;

    }

    /**
     *
     * if numBytes = -1 it returns all parts of the message
     * (header, body, attachements) without byte quatity limitation.
     *
     * if messageSize <= numBytes it returns all parts of the message
     * (header, body, attachements)
     *
     * otherwise it returns all parts of the message
     * (header, body, attachements) but there is the byte number limitation
     *
     *
     * @param session  opened mail server session
     * @param FID      parent folder id
     * @param FMID     mail id in the folder
     * @param msg      Message
     * @param status   status of the item; char
     * @param loc      Locale
     * @param numBytes int
     * @param contentProviderInfos not null List that will contain the AttachmentsInfo
     *        objects related to the attachments for this message. If this message
     *        does not have any atatchment then no AttachmentsInfo object are
     *        created and added to the list
     * @return ItemMessage
     * @throws EntityException
     */
    public ItemMessage getHBA(Session session,
            String FID,
            String FMID,
            Message msg,
            char status,
            Locale loc,
            int numBytes,
            EmailFilter filter,
            ContentProviderInfo contentProviderInfo)
            throws EntityException {

        ItemMessage im = null;
        Ext ext = new Ext();

        boolean needOpt = Utility.needsFlagOptimization(status, FID, filter);

        try {

            // save the flag
            FlagProperties fp = Utility.getFlags(msg);

            int messageSize = msg.getSize();

            MimeMessage newMsg = new MimeMessage(session);

            // HEADER
            String[] h = MessageCreator.setNewHeaderIn(newMsg, msg, FID, FMID, Def.IS_DEVICE);
            String mid              = h[0];
            int    headersDimension = Integer.parseInt(h[1]);


            // CONTENT
            try {
                if (needOpt){

                    newMsg.setContent(Def.CONTENT_BODY, Def.CONTENT_CONTENTTYPE);

                } else {
                    
                    // get all the mail without limitation
                    ArrayList parts = MessageParser.getAllPartsOfMessage(msg, false);                    
                    
                    if ( (numBytes == -1) || (messageSize <= numBytes) ){
                        ArrayList ips = MessageCreator.getHBA_GetParts(parts, ext, headersDimension);
                        MessageCreator.getHBA_Content(newMsg, msg, ips);                                                
                    } else {
                        
                        ArrayList ips = MessageCreator.getHBA_GetParts(parts, numBytes, ext, headersDimension,
                                contentProviderInfo);

                        MessageCreator.getHBA_Content(newMsg, msg, ips);
                    }
                    
                }    
                    
                    
            } catch (Exception e){
                log.error("Generic error creating the content .. set the default content", e);
                newMsg.setContent(Def.CONTENT_BODY_ALERT, Def.CONTENT_CONTENTTYPE);
            }

            newMsg.saveChanges();

            // the save message command change the message-ID
            newMsg.setHeader(Def.HEADER_MESSAGE_ID, mid);

            // get encodig for : <emailitem enc="quoted-printable">
            String encodingType = MessageCreator.getEncodingType(newMsg);

            im = setItemMessage(FMID, FID, newMsg, fp, loc, encodingType, ext);

            // re-set the flag on the server
            msg.setFlag(Flags.Flag.SEEN    , fp.getSeen());
            msg.setFlag(Flags.Flag.ANSWERED, fp.getAnswered());
            msg.setFlag(Flags.Flag.FLAGGED , fp.getFlagged());
            //msg.setFlag(Flags.Flag.FORWARDED, fp.getForwarded());

        } catch (MessagingException me) {
            throw new EntityException(me);
        }

        // remove the message if needed (syncml dimension optimization)
        if (needOpt){
            im.setStreamMessage("");
        }

        return im;
    }

    
    //---------------------------------------------------------- Private Methods
    
    
}
