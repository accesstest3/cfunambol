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


import com.funambol.email.engine.source.EmailConnectorConfig;
import com.funambol.email.exception.ParsingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.email.model.InternalPart;
import com.funambol.email.util.Def;
import com.funambol.email.util.Utility;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

/**
 *
 * This class is used in order to create a list of the Part of the
 * single email. in the list there will be InternalPart items.
 * this class is an helper for the EntityManagerFilter class
 *
 * @version $Id: MessageParser.java,v 1.9 2008-09-05 14:55:08 testa Exp $
 */
public class MessageParser {

    //---------------------------------------------------------------- CONSTANTS

    /**     */
    private static FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    //----------------------------------------------------------- PUBLIC METHODS

    /**
     * this method returns always a list of part
     * <br>
     * if the javaMail fails to get the main content the method
     * returns a default InternalPart with the "CONTENT_BODY_ALERT" as
     * the body value.
     * <br>
     * if the javaMail fails to get the single parts of the email
     * the method check if it can return some parts. if no parts is
     * taken the method return a default InternalPart with the
     * "CONTENT_BODY_ALERT" as the body value.
     *
     * @param msg Message
     * @return list of email parts ArrayList
     *
     * @param msg
     * @param isHorHB optimize the creation of the list for getH and getHB methods
     * @return
     */
    public static ArrayList getAllPartsOfMessage(Message msg, boolean isHorHB) {

        // contains all the message parts
        ArrayList ips       = new ArrayList();
        boolean error       = false;
        Integer[] partIndex = { new Integer(1) };

        // get all parts
        try {
            createPartListManagingAnyMailServerError(ips, partIndex, msg, isHorHB);
        } catch (Exception me){
            // if javaMail fails to get single parts
            error = true;
            log.error("Error single email parts - Unsupported Encoding", me);
        }

        //
        if (error){
            log.error("Create the default InternalPart - Unsupported Encoding ");
            if (ips.size() > 0){
                // there are some parts
            } else {
                // no parts .. create a default part
                partIndex[0] = 1;
                InternalPart defaultIP = createInternalPart(partIndex,
                                                            Def.CONTENT_BODY_ALERT,
                                                            Def.CONTENT_CONTENTTYPE,
                                                            true,
                                                            false);
                ips.add(defaultIP);
            }
        }

        // check if there is just one part and it's not a body but an attachment
        // (it happens with the Apple Mailer test 038 - 'volo apple')
        // removed the condition on the INLINE (test 042 - gmail)
        if ( ips.size() == 1){
            InternalPart ip = (InternalPart)ips.get(0);
            if (ip.getDisposition() != null &&
                (ip.getDisposition().equalsIgnoreCase(MimeBodyPart.ATTACHMENT))){

                // default txt body internalPart
                partIndex[0] = 1;
                InternalPart defaultIP = createInternalPart(partIndex,
                                                            Def.CONTENT_BODY,
                                                            Def.CONTENT_CONTENTTYPE,
                                                            true,
                                                            false);

                // save the part
                InternalPart attachmentIP = new InternalPart();
                attachmentIP.cloneIP(ip);
                attachmentIP.setIndex(1);

                ips.clear();

                // add the default body
                ips.add(defaultIP);
                // add the attachment
                ips.add(attachmentIP);
            }
        }

        // if the email has just the body with the calendar the
        // system creates an attachment. so that the parts of the email
        // will be two instead of one
        if (ips.size() == 1){
            InternalPart ip = (InternalPart)ips.get(0);
            if (ip.isTextCalendar()){
                // create the attachment and it will add the the list: parts
                try {
                    InternalPart calendarip = createCalendarInternalPart(ip, ip.getIndex() );
                    ips.add(calendarip);
                } catch (Exception e){
                    log.error("Error creating the ICS attachment", e);
                }
            }
        } else {
            //
            // Loop on the parts in order to:
            // - create the "ics" attachment, if at least one part is a
            //   text/calendar type
            // - add to the text/plain part content the text/calendar part
            //   content, if at least one part is a text/calendar type
            //
            boolean calendarAttachmentMustBeCreated = false;
            boolean calendarTextMustBeCreated = false;
            int calPartIndex = 0;
            int calAttachmentIndex = 0;

            for (int i = 0; i < ips.size(); i++) {

                InternalPart ip = (InternalPart) ips.get(i);

                //
                // Calendar is contained in a part of type text/calendar,
                // which is an attachment.
                //
                if (ip.isTextCalendar() &&
                        ip.getDisposition() != null &&
                        ip.getDisposition().equalsIgnoreCase(MimeBodyPart.ATTACHMENT)) {

                    calendarAttachmentMustBeCreated = false;
                    calendarTextMustBeCreated = true;
                    calAttachmentIndex = i;
                    
                    //
                    // If present, the calendar as attachment has always taken
                    // as the invitation, instead of the text calendar, if present.
                    //
                    break;

                } else if (ip.isTextCalendar()) {
                    
                    //
                    // Calendar is contained in a part of type text/calendar,
                    // which is not an attachment.
                    //
                    calendarAttachmentMustBeCreated = true;
                    calendarTextMustBeCreated = true;
                    calPartIndex = i;
                }
            }
            
            InternalPart calendarip = null;
            
            if (calendarAttachmentMustBeCreated) {
                // there are no attachments for the 'i' calendar part
                InternalPart ip = (InternalPart) ips.get(calPartIndex);
                try {
                    calendarip = createCalendarInternalPart(ip, ip.getIndex());
                    ips.add(calendarip);
                    ips.remove(calPartIndex);

                } catch (Exception e) {
                    log.error("Error creating the ICS attachment", e);
                }
            }
            
            if (calendarTextMustBeCreated){

                //
                // If the first part of the message is text/plain then:
                // 
                // - add to the the text plain part content the calendar part 
                //   (or ics attachment) content so that a client will display 
                //   the calendar within the text part.
                //   
                //   This is done in order to preserve the content of hte text
                //   part, which could contain information also contained in the
                //   ics (example: invitation sent by apple) or information not
                //   related to the ics (example: a text email sent by outlook
                //   with a ics attached).
                //
                // Example:
                //   invitations created by thunderbird are made up of 3 parts:
                //
                //   0 - text/plain part containing something like: "You have
                //       been invited by ...." which does not contain useful
                //       information
                //   1 - text/calendar part containing the invitation as a
                //       VCALENDAR
                //   2 - application/ics part containing the invitation as a
                //       VCALENDAR. This is an attachment.
                //
                //   In this case to the text part (0) will be added the content
                //   of the part 1 (which is a part, not an attachment).
                //
                // Example:
                //   invitations created by apple are made up of 2 parts:
                //
                //   0 - text/plain part containing information also stored in
                //       the ics attachment but in a readable format
                //   1 - application/ics part containing the invitation as a
                //       VCALENDAR. This is an attachment.
                //
                //   In this case to the text part (0) will be added the content
                //   of the part 1 (which is an attachment).
                //
                // Example:
                //   email created in thunderbird with ics and other attachment
                //   (a picture for instance).
                //
                //   The email is made up of:
                //
                //   0 - text/plain part containing proper information
                //   1 - application/ics part containing the invitation as a
                //       VCALENDAR, inline disposition.
                //   2 - picture attachment part
                //
                //   In this case to the text part (0) will be added the content
                //   of the part 1 (which is a inline part).
                //
                
                try {
                    
                    InternalPart firstPart = (InternalPart) ips.get(0);
                    if (firstPart.getContentType().contains("text/plain")) {

                        //
                        // If calendar part does not exist, then the calendar attachment 
                        // is taken in order to reaplce the text part.
                        //
                        if (calendarip == null) {
                            InternalPart ip = (InternalPart) ips.get(calAttachmentIndex);
                            calendarip = createCalendarInternalPart(ip, ip.getIndex());
                        }
                        
                        // add to the first part the calendar content
                        StringBuilder tmp = new StringBuilder(
                                (String)firstPart.getPartValue());
                        tmp.append('\n').append(calendarip.getPartValue());
                        firstPart.setPartValue(tmp.toString());
                        
                        // modify size (since content has been changed)
                        firstPart.setSize(tmp.length());
                        
                        firstPart.setIsTextCalendar(true);
                    }
                } catch (Exception e) {
                    log.error("Error creating the ICS attachment", e);
                }
            }
        }

        return ips;

    }

    /**
     * 
     * For some IMAP Servers the isMimeType throws a MessagingException 
     * "Unable to load BODYSTRUCTURE" exception.
     * There is a workaround for this kind of errors, as pointed out in
     * http://java.sun.com/products/javamail/FAQ.html#imapserverbug
     * "Just making a copy of the entire message will fetch the entire 
     * message from the server and avoid this issue."
     * But for emails larger than a configured threshold we prefer not to 
     * download the entire email and try to retrieve only the body part.
     * @param ips
     * @param partIndex
     * @param msg
     * @param isHorHB
     * @throws com.funambol.email.exception.ParsingException
     * @throws java.io.IOException
     * @throws javax.mail.MessagingException 
     */
    private static void createPartListManagingAnyMailServerError(ArrayList ips,
            Integer[] partIndex,
            Part msg,
            boolean isHorHB)
            throws ParsingException, IOException, MessagingException {
        
        
        boolean mailServerErrorWhileGettingMimeType =    false;
        boolean mailServerErrorWhileGettingSize =        false; 
        boolean messageSizeLowerThanMaximumSizeForCopy = true;
        try {
            msg.isMimeType("multipart/*");
        } catch (MessagingException ex) {
            mailServerErrorWhileGettingMimeType = true;
            log.error("Mail Server Error (" + ex.getMessage() + ") getting the mime type.");
            try {
                int messageSize = msg.getSize();
                int messageSizeThreshold = 
                        EmailConnectorConfig.getConfigInstance().getMailSizeThresholdForCopy();
                messageSizeLowerThanMaximumSizeForCopy = (messageSize < messageSizeThreshold);
                if (log.isTraceEnabled()) {
                    log.trace("Message Part size " + messageSize);
                }
            } catch (Exception ex1) {   // TODO ex1 is horrible
                mailServerErrorWhileGettingSize = true;
                log.error("Mail Server Error (" + ex1.getMessage() + ") getting the email size.");
            }
        }

        if (!mailServerErrorWhileGettingMimeType) {
            // this is the main stream. 
            // If no mail server error occours than simply create the Part list 
            // and return
            createPartsList(ips, partIndex, msg, isHorHB);
            return;
            
        } else {
            // in case of Mail Server errors ....
            if (!mailServerErrorWhileGettingSize && messageSizeLowerThanMaximumSizeForCopy) {
                if (log.isTraceEnabled()) {
                    log.trace("Fetch the entire email as workaround for Mail Server Error.");
                }
                msg = new MimeMessage((MimeMessage) msg);
                createPartsList(ips, partIndex, msg, isHorHB);
            } else {
                createPartialPartsList(ips, partIndex, msg, isHorHB);
            }
        }
    }

    /**
     *
     * @param ips
     * @param partIndex
     * @param msg
     * @throws javax.mail.MessagingException
     * @throws com.funambol.email.exception.ParsingException
     * @throws java.io.IOException
     */
    private static void createPartsList(ArrayList ips,
                                        Integer[] partIndex,
                                        Part msg,
                                        boolean isHorHB)
    throws MessagingException, ParsingException, IOException {


        //Using isMimeType to determine the content type avoids
        //fetching the actual content data until we need it.

        if (msg.isMimeType("multipart/*")) {

            // multi part .. it must get all single parts
            Multipart mp = (Multipart)msg.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++){
                createPartsList(ips, partIndex, mp.getBodyPart(i), isHorHB);
            }

        } else if (msg.isMimeType("text/plain")) {

            // single part .. just the body in txt format
            InternalPart ip = null;
            try {
                String disposition = msg.getDisposition();
                if (isHorHB &&
                        (MimeBodyPart.ATTACHMENT.equalsIgnoreCase(disposition) ||
                         MimeBodyPart.INLINE.equalsIgnoreCase(disposition))
                    ) {
                    // optimization for getH and getHB
                    // get the content in the createIP only if the part is the body
                    ip = createInternalPart(partIndex, msg, null);
                } else {
                    // it's the real body
                    String content = (String)msg.getContent();
                    if (!content.equals("")){
                        // some email have an attachment empty issue:4245
                        ip = createInternalPart(partIndex, msg, content);
                    }
                }
            } catch (UnsupportedEncodingException uee){
                // sometimes it may be found an error also in this case (i.e. uft-8)
                log.error("Error getting the content of the email!", uee );
                try {
                    String content = getTextBody(msg);
                    if (!content.equals("")){
                        // note: some email have an attachment empty issue:4245
                        ip = createInternalPart(partIndex,
                                                content,
                                                Def.CONTENT_CONTENTTYPE,
                                                true,
                                                false);
                    }
                } catch (Exception ee){
                    log.error("Error getting the content of the email with the stream; use the default body", uee );
                    ip = createInternalPart(partIndex,
                                            Def.CONTENT_BODY_ALERT,
                                            Def.CONTENT_CONTENTTYPE,
                                            true,
                                            false);
                }
            }
            if (ip != null){
                ips.add(ip);
                partIndex[0] = (partIndex[0] + 1);
            }

        } else if (msg.isMimeType("text/html")) {

            // single part .. just the body in html format
            InternalPart ip = null;
            try {
                String content = (String)msg.getContent();
                if (!content.equals("")){
                    // some email have an attachment empty issue:4245
                    ip = createInternalPart(partIndex, msg, content);
                }
            } catch (UnsupportedEncodingException uee){
                // sometimes it may be found an error also in this case (i.e. uft-8)
                log.error("Error getting the content of the email; use the stream", uee );
                try {
                    String content = getTextBody(msg);
                    if (!content.equals("")){
                        // some email have an attachment empty issue:4245
                        ip = createInternalPart(partIndex,
                                                content,
                                                Def.CONTENT_CONTENTTYPE_HTML,
                                                false,
                                                true);
                    }
                } catch (Exception ee){
                    log.error("Error getting the content of the email with the stream; use the default body", uee );
                    ip = createInternalPart(partIndex,
                                            Def.CONTENT_BODY_ALERT,
                                            Def.CONTENT_CONTENTTYPE,
                                            true,
                                            false);
                }
            }
            if (ip != null){
                ips.add(ip);
                partIndex[0] = (partIndex[0] + 1);
            }

        } else if (msg.isMimeType("message/rfc822")) {

            // nested email
            InternalPart ip = null;
            try {
                Part nestedMsg = (Part)msg.getContent();
                createPartsList (ips, partIndex, nestedMsg, isHorHB);
            } catch (UnsupportedEncodingException uee){
                log.error("Error getting the content of the nested email; use the default body", uee );
                ip = createInternalPart(partIndex,
                                        Def.CONTENT_BODY_ALERT,
                                        Def.CONTENT_CONTENTTYPE,
                                        true,
                                        false);
                ips.add(ip);
                partIndex[0] = (partIndex[0] + 1);
            }

        } else {

            // single part .. attachment, inline, etc.
            String contentType = msg.getContentType().toUpperCase();
            String fileName    = msg.getFileName();
            String disposition = msg.getDisposition();

            if (MimeBodyPart.ATTACHMENT.equalsIgnoreCase(disposition) ||
                MimeBodyPart.INLINE.equalsIgnoreCase(disposition)){
                if (isHorHB && msg.isMimeType("text/*")){
                    // optimization for getH and getHB
                    // get the content in the createIP only if the part is the body
                    InternalPart ip = createInternalPart(partIndex, msg, null);
                    ips.add(ip);
                    partIndex[0] = (partIndex[0] + 1);
                } else {
                    Object content = msg.getContent();
                    InternalPart ip = createInternalPart(partIndex, msg, content);
                    ips.add(ip);
                    partIndex[0] = (partIndex[0] + 1);
                }

            } else if (contentType.indexOf("TEXT/CALENDAR") != -1) {
                String content = (String)msg.getContent();
                InternalPart ip = createInternalPart(partIndex, msg, content);
                ips.add(ip);
                partIndex[0] = (partIndex[0] + 1);
            } else {
                InternalPart ip = createInternalPart(partIndex,
                                                     Def.CONTENT_BODY_ALERT,
                                                     Def.CONTENT_CONTENTTYPE,
                                                     true,
                                                     false);
                ips.add(ip);
                partIndex[0] = (partIndex[0] + 1);
            }

        }

    }

    /**
     * This method is used when the mail server does not respond correctly and 
     * an exception is thrown by javax.mail. The workaround for this issue is to 
     * download the entire emai but for large email this could be too expensive,
     * so the intent of this method is to create only a partial list of Parts 
     * (the body part essentially) avoiding to download them all.
     * @param ips
     * @param partIndex
     * @param msg
     * @param isHorHB
     */
    private static void createPartialPartsList(ArrayList ips,
                                        Integer[] partIndex,
                                        Part msg,
                                        boolean isHorHB) {

        // For now we are not able to get the body part of an email without 
        // downloading the entire email so we set an empty body
        log.error("Error getting the main content - Unsupported Encoding");
        ips.clear();
        InternalPart defaultIP = new InternalPart();
        defaultIP.setPartValue(Def.CONTENT_BODY_ALERT);
        defaultIP.setIsTextBody(true);
        ips.add(defaultIP);
    }    

    /**
     *
     *
     * @param partSize
     * @param partIndex
     * @param contentValue
     * @param part
     * @throws javax.mail.MessagingException
     * @return
     */
    private static InternalPart createInternalPart(Integer[] partIndex,
                                                   Part part,
                                                   Object contentValue)
     throws MessagingException, IOException{


        InternalPart ip = new InternalPart();

        ip.setIndex(partIndex[0].intValue() - 1);

        ip.setPartValue(contentValue);

        ip.setDHandler(part.getDataHandler());

        ip.setFileName(part.getFileName());

        ip.setSize(part.getSize()); // it depends on the encoding

        // isBodyText() handles text/plain and text/calendar
        boolean isbodytxt = checkBodyTxt(part, partIndex, ip);
        if (contentValue == null  && isbodytxt){
            ip.setPartValue(part.getContent());
        }
        ip.setIsTextBody(isbodytxt);

        // isBodyHtml() handles text/html and set the fileName
        boolean isbodyhtml = checkBodyHtml(part, partIndex, ip);
        if (contentValue == null  && isbodyhtml){
            ip.setPartValue(part.getContent());
        }
        ip.setIsHtmlBody(isbodyhtml);

        // hanldes the content type
        if  (ip.isTextBody() &&
                (ip.getPartValue() instanceof String) &&
                (ip.getContentType().toUpperCase().indexOf("TEXT/CALENDAR") == -1)){

            // if body is a string then it sets UTF-8 for all
            // @todo add condition about DEVICE
            ip.setContentType(Def.CONTENT_CONTENTTYPE);
            ip.setTransfertEnc(Def.CONTENT_TRANSFERTENC);
            //  TEST VIEWER
            //new ShowString((String)currentContent, "Conversion Demo");

        } else if (ip.getContentType().toUpperCase().indexOf("TEXT/CALENDAR") != -1){

            // patch for outlook calendar (it will be replace before sending
            // the email to the client, it is used the IsTextCalendar flag)
            if (ip.getFileName() != null){
                // the part is an attachment; leave the original DataHandler and original PartValue
                ip.setIsTextCalendar(true);
                ip.setContentType(ip.getContentType());
                ip.setTransfertEnc(Def.CONTENT_TRANSFERTENC);
                ip.setIsTextBody(false);
            } else {
                // ok is the body (we should check also ip.isTextBody())
                ip.setIsTextCalendar(true);
                ip.setContentType(ip.getContentType());
                ip.setTransfertEnc(Def.CONTENT_TRANSFERTENC);
                ip.setDHandler(null);
                String calendar = Def.CONTENT_BODY_ALERT;
                try {
                    calendar = getTextBody(part);
                } catch (Exception e){
                    // do nothing... leave the alert body content
                    log.error("Error getting text body", e);
                }
                ip.setPartValue(calendar);
            }

        } else {

            // if body is not a string
            ip.setContentType(part.getContentType());
            String transfertEcodingValue = Utility.getHeaderTransfertEncoding(part);
            if (!transfertEcodingValue.equals("")){
                ip.setTransfertEnc(transfertEcodingValue);
            } else {
                ip.setTransfertEnc(Def.CONTENT_TRANSFERTENC);
            }

        }

        // @to be test
        // in some cases the javaMail retuens disposition null
        // we can put the following fix but we have to test
        // by default is an attachement
        ip.setDisposition(part.getDisposition());
        if ((ip.getFileName() != null) && (ip.getDisposition() == null) ){
            ip.setDisposition(MimeBodyPart.ATTACHMENT);
        }

        // test 027
        if ((ip.getFileName() == null)) {
            if (ip.getDisposition() != null && (ip.getDisposition().equalsIgnoreCase(MimeBodyPart.INLINE))){
               if ((!ip.isTextBody()) && (!ip.isHtmlBody())){
                  if (ip.getContentType().indexOf("text/plain") != -1 ||
                      ip.getContentType().indexOf("TEXT/PLAIN") != -1 ){
                         ip.setDisposition(MimeBodyPart.INLINE);
                         ip.setFileName("Part00"+partIndex[0]+".txt");
                  }
               }
            }
        }

        return ip;

    }

    /**
     *
     *
     * @param partSize
     * @param partIndex
     * @param contentValue
     * @param part
     * @throws javax.mail.MessagingException
     * @return
     */
    private static InternalPart createInternalPart(Integer[] partIndex,
                                                   String contentValue,
                                                   String contentType,
                                                   boolean isTextBody,
                                                   boolean isHtmlBody) {

        InternalPart ip = new InternalPart();

        ip.setIndex(partIndex[0].intValue() - 1);

        ip.setPartValue(contentValue);

        ip.setSize(contentValue.length());

        ip.setIsTextBody(isTextBody);

        ip.setIsHtmlBody(isHtmlBody);

        ip.setContentType(contentType);

        ip.setTransfertEnc(Def.CONTENT_TRANSFERTENC);

        return ip;

    }

    /**
     *
     * @param part
     * @param partIndex
     * @param ip
     * @return
     */
    private static boolean checkBodyTxt(Part part,
                                     Integer[] partIndex,
                                     InternalPart ip) {

        boolean isTextBody = false;
        try {
            String contentType = part.getContentType().toUpperCase();
            if (contentType.indexOf("TEXT/PLAIN") != -1){
                if (part.getFileName() == null){
                    if (part.getDisposition() == null){
                        isTextBody = true;
                    } else if (part.getDisposition().equalsIgnoreCase(MimeBodyPart.INLINE) &&
                            partIndex[0] == 1){
                        // test 027 ; test 020
                        isTextBody = true;
                    }
                }
            } else if (contentType.indexOf("TEXT/CALENDAR") != -1){
                // test 029 ; test 030 -- patch for outlook
                isTextBody = true;
                ip.setContentType(part.getContentType());
            } else {
                // do nothing
            }
        } catch (MessagingException me){
            log.error("Error verifying if the Part is a TXT Body part", me);
        }

        return isTextBody;
    }


    /**
     *
     * @param part
     * @param partIndex
     * @param ip
     * @return
     */
    private static boolean checkBodyHtml(Part part,
                                      Integer[] partIndex,
                                      InternalPart ip) {

        boolean isHtmlBody = false;
        try {
            String contentType = part.getContentType().toUpperCase();

            if (contentType.indexOf("TEXT/HTML") != -1 ){

                if (part.getFileName() == null){

                    if (part.getDisposition() == null){
                        isHtmlBody = true;
                    } else if (part.getDisposition().equalsIgnoreCase(MimeBodyPart.INLINE) &&
                            partIndex[0] == 1){
                        // test 027 ; test 020; test 028
                        isHtmlBody = true;
                    } else {
                        // test 027 ; test 020; test 028
                        ip.setFileName("Att00"+partIndex[0]+".htm");
                        isHtmlBody = false;
                    }
                }

            }
        } catch (MessagingException me){
            log.error("Error verifying if the Part is a HTML Body part", me);
        }
        return isHtmlBody;
    }


    /**
     * Utility method for obtaining a string representation of a
     * Message's body
     * @param part
     * @param charset
     * @throws java.lang.Exception
     * @return
     */
    private static String getTextBody(Part part, String charset)
      throws Exception {

        String out = null;

        try {
            StringBuffer buffer = new StringBuffer();
            InputStream is = part.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, charset);
            Reader in = new BufferedReader(isr);
            int ch;
            while ((ch = in.read()) > -1) {
               buffer.append((char)ch);
            }
            out = buffer.toString();
        } catch (Exception e) {
            // do nothing
            throw new Exception("Error getting text using the charset: " + charset);
        }

        return out;

    }

    /**
     * Utility method for obtaining a string representation of a
     * Message's body
     * @param part
     * @throws java.lang.Exception
     * @return
     */
    private static String getTextBody(Part part) throws Exception {

        String       body  = null;
        InputStream  bis   = null;
        OutputStream bos   = null;
        ByteArrayOutputStream bodyOs = new ByteArrayOutputStream();

        byte[] block = new byte[1024];
        int read = 0;

        try {
            bis = part.getInputStream();
            bos = bodyOs;
            while ((read = bis.read(block)) > -1) {
                bos.write(block, 0, read);
            }
            bos.flush();
            body = bodyOs.toString();
        } catch(MessagingException me) {
            throw me;
        } finally {
            bis.close();
        }

        return body;

    }

    /**
     *
     *
     * @param partSize
     * @param partIndex
     * @param contentValue
     * @param part
     * @throws javax.mail.MessagingException
     * @return
     */
    private static InternalPart createCalendarInternalPart(InternalPart ip, int index)
     throws Exception{

        InternalPart calendarip = new InternalPart();

        calendarip.setPartValue(ip.getPartValue());

        // calendar in ics format
        String content  = ((String)ip.getPartValue());
        // no handled by javaMail standard; added a static method in the
        // EmailSyncSource with the new mime type
        String mimeType = "text/calendar";
        calendarip.setDHandler(new DataHandler(new ByteArrayDataSource(content, mimeType)));

        calendarip.setIsTextBody(false);
        calendarip.setIsHtmlBody(false);

        calendarip.setFileName("calendar.ics");
        calendarip.setDisposition(MimeBodyPart.ATTACHMENT);
        calendarip.setContentType(ip.getContentType());
        calendarip.setTransfertEnc(Def.CONTENT_TRANSFERTENC);

        calendarip.setIsTextCalendar(false); // this is the attachment

        calendarip.setSize(((String)ip.getPartValue()).length()); // it depends on the encoding

        calendarip.setIndex(index);

        return calendarip;

    }

}
