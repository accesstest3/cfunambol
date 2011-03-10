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
package com.funambol.email.test;


import com.funambol.email.exception.EntityException;
import com.funambol.email.items.manager.MessageCreator;
import com.funambol.email.items.manager.MessageCreatorMethodWrappers;
import com.funambol.email.items.manager.MessageParser;
import com.funambol.email.model.InternalPart;
import com.funambol.email.pdi.mail.Ext;
import com.funambol.email.util.Def;
import com.funambol.email.util.UtilityDate;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;


/**
 * Static util class for use in tests.
 *
 * $Id: ManagerUtility.java,v 1.4 2008-06-24 11:08:17 testa Exp $
 */
public class ManagerUtility {
    
    // ------------------------------------------------------------ Private data
    
    /** Test resources path. */
    private static String resourcePath;
    
    static {
        
        resourcePath = System.getProperty(Constants.RESOURCE_PATH_PROPERTY_NAME);

        MimetypesFileTypeMap mimetypes =  (MimetypesFileTypeMap)MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes("text/calendar ics ICS");
        MailcapCommandMap mailcap = (MailcapCommandMap)MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");
    }
    
    // ------------------------------------------------------------ Constructors
    
    private ManagerUtility() {
    }
    
    // ---------------------------------------------------------- Public methods
    
    /**
     * Creates a <code>javax.mail.Message</code> from a string containing a
     * RFC2822 message.
     *
     * @param rfc2822MessageString a string containing a RFC2822 message
     * @param session javamail session
     * @return a <code>javax.mail.Message</code> object corresponding to
     * rfc2822MessageString
     * @throws java.io.UnsupportedEncodingException
     * @throws javax.mail.MessagingException
     */
    public static Message createMessage(
            String rfc2822MessageString,
            Session session)
            throws UnsupportedEncodingException, MessagingException {
        
        byte[] b = rfc2822MessageString.getBytes("UTF-8");
        InputStream is = new ByteArrayInputStream(b);
        Message msg = new MimeMessage(session, is);
        
        return msg;
    }
    
    /**
     * Read a file and returns its content as a string.
     *
     * @param fileName full path of the file to read
     * @return file content as string
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.IOException
     */
    private static String getContent(String fileName)
    throws FileNotFoundException, UnsupportedEncodingException, IOException {
        
        StringBuffer buffer = new StringBuffer();
        FileInputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis, "UTF8"); //UTF8
        Reader in = new BufferedReader(isr);
        int ch;
        
        while ((ch = in.read()) > -1) {
            buffer.append((char)ch);
        }
        
        in.close();
        String out = buffer.toString();
        
        return out;        
    }
    
    /**
     * this method is not general purpose and it's used just for the 
     * text/calendar unit test
     * 
     * @param p
     * @throws java.lang.Exception
     */
    public static String getBodyForTextCalendar(Part p) throws Exception {
        
        if (p.isMimeType("text/plain")) {
            return (String)p.getContent();
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            return (String)mp.getBodyPart(0).getContent();
        } else {            
            Object o = p.getContent();
            if (o instanceof String) {
                return (String)o;
            } else if (o instanceof InputStream) {                
                InputStream is = (InputStream)o;
                int c;
                OutputStream os = new ByteArrayOutputStream();
                while ((c = is.read()) != -1){
                    os.write(c);
                }
                String s = os.toString();
                return s;
            } else {
                return o.toString();
            }
        }
        

    }
    
    
    /**
     * Convenient method to set up an <code>Ext</code> object. A value of -1
     * must be passed for the Xsize or XvalBody parameter to set empty value.
     *
     * @param Xsize
     * @param XvalBody
     * @param truncated
     * @param XvalAttach
     * @return an <code>Ext</code> object set with given parameters.
     */
    public static Ext getExt(
            int     Xsize,
            int     XvalBody,
            boolean truncated,
            List    XvalAttach) {
        
        Ext ext = new Ext();
        
        ext.setXnam      ("");
        ext.setXsize     (Xsize == -1 ? "" : Integer.toString(Xsize));
        ext.setXvalBody  (XvalBody == -1 ? "" : Integer.toString(XvalBody));
        ext.setTruncated(truncated);
        ext.setXvalAttach(XvalAttach);
        
        return ext;
    }
    
    /**
     * Gets the size of the text part body of a message, possibly truncated at a 
     * specified length.
     *
     * @param msg mail message
     * @param numBytes truncation size
     * @return text part body length, if truncation size il greater than text part 
     * body length or is equal to -1, truncation size otherwise
     * @throws java.io.IOException 
     * @throws javax.mail.MessagingException 
     */
    public static int getXsize(Message msg, boolean isHorHB, int numBytes) 
    throws IOException, MessagingException{

        int textPartSize = getTextPartSize(msg, isHorHB);

        if (numBytes == -1) {
            return textPartSize;
        }
        
        int xvalbody = textPartSize;
        if (textPartSize > numBytes) {
            xvalbody = textPartSize - numBytes;
        }
        
        return xvalbody;
    }
    
    /**
     * Convenient method to set up an element of the <code>XvalAttach</code>
     * property of an <code>Ext</code> object.
     *
     * @param name attachment name
     * @param size attachment size
     * @param mime attachment mime type
     * @param url  attachment url for the content provider
     * @return <code>XvalAttach</code> item
     */
    public static String[] getXvalAttachItem(String name, int size, String mime, String url) {
        String[] extItems = new String[4];
        extItems[0] = name;
        extItems[1] = Integer.toString(size);
        if (mime != null && url != null) {
            extItems[2] = mime;
            extItems[3] = url;
        }
        return extItems;
    }
    
    /**
     * Convenient method to read a test message from a text file.
     *
     * @param fileName name of the text file which contain the email
     * @param session
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.IOException
     * @throws javax.mail.MessagingException
     * @return a <code>Message</code> object which represent the mail in the file
     */
    public static Message getTestMessage(String fileName, Session session)
    throws FileNotFoundException, UnsupportedEncodingException, IOException, MessagingException {
        
        String rfc2822TestMessageString = getContent(
                resourcePath + File.separator +
                Constants.TEST_EMAIL_DIR + File.separator + fileName);
        
        Message testMsg = createMessage(rfc2822TestMessageString, session);
        
        return testMsg;
    }

    
    /**
     * Convenient method to read a test message from a text file.
     *
     * @param fileName name of the text file which contain the email
     * @param session
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.IOException
     * @throws javax.mail.MessagingException
     * @return a <code>Message</code> object which represent the mail in the file
     */
    public static String getContentFromFile(String fileName)
    throws FileNotFoundException, UnsupportedEncodingException, IOException, MessagingException {
        
        String attachment = getContent(
                resourcePath + File.separator +
                Constants.TEST_EMAIL_DIR + File.separator + fileName);
                
        return attachment;
    }
    
    public static void saveContentToFile(String content, String fileName)
    throws FileNotFoundException, UnsupportedEncodingException, IOException, MessagingException {
        
        String path = resourcePath + File.separator +
                Constants.TEST_EXPECTED_EMAIL_DIR + File.separator + fileName;
        FileWriter writer = new FileWriter(path);
        writer.append(content);
        writer.flush();
        writer.close();
    }

    /**
     * Convenient method to read an expected mail message from a text file.
     *
     * @param fileName name of the text file which contain the email
     * @param session
     * @throws java.io.FileNotFoundException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.IOException
     * @throws javax.mail.MessagingException
     * @return a <code>Message</code> object which represent the mail in the file
     */
    public static Message getExpectedMessage(String fileName, Session session)
    throws FileNotFoundException, UnsupportedEncodingException, IOException, MessagingException {
        
        String rfc2822ExpectedMessageString = getContent(
                resourcePath + File.separator +
                Constants.TEST_EXPECTED_EMAIL_DIR + File.separator + fileName);
        
        Message expectedMsg = createMessage(rfc2822ExpectedMessageString, session);
        
        return expectedMsg ;
    }
    
    /**
     * Gets the size of the message header.
     *
     * @param msg message
     * @throws javax.mail.MessagingException
     * @return size of the message header
     */
    public static int getHeadersSize_old(Message msg) throws MessagingException {
        
        int size = 0;
        
        for (String headerName : Def.HEADER_MINIMUM) {
            
            String[] header = msg.getHeader(headerName);
            
            if (header != null) {
                String value = header[0];
                String headerLine = headerName + ": " + value;
                
                size += headerLine.getBytes().length;
            }
        }
        
        return size;
    }
    
    public static int getHeadersSize(Message oldMsg) throws EntityException {

        String   date             = "";
        int      headersDimension = 0;
        int      headerlength     = 0;
        Header   h                = null;
        String   name             = null;
        String   value            = null;
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

                // create the new header step by step
                //if(isDevice){
                if (MessageCreatorMethodWrappers.isHeaderMinimum(name)) {
                    // encode value (QP)
                    value = MessageCreatorMethodWrappers.encodeHeaderField(name, value);
                    headerlength =MessageCreatorMethodWrappers.getFieldLength(name, value);
                    if (name.equals(Def.HEADER_RECEIVED)) {
                        if (!isReceivedInTheHeader) {
                            // save just the first received value
                            isReceivedInTheHeader = true;
                            //newMsg.addHeader(name, value);
                            headersDimension = headersDimension + headerlength;
                        }
                    } else {
                        //newMsg.addHeader(name, value);
                        headersDimension = headersDimension + headerlength;
                    }

                }
            }

            // add the size of the
            // Content-Type: text/plain; charset=UTF-8
            // Content-Transfer-Encoding
            headersDimension = headersDimension + 100;

        } catch (MessagingException me) {
            throw new EntityException(me);
        }

        return headersDimension;
    }
    
    
    /**
     * Calculate the size of a the parts of a message.
     *
     * @param msg message for which to calculate the size
     * @throws java.io.IOException
     * @throws javax.mail.MessagingException
     * @return
     */
    public static int getPartsSize(Message msg, boolean isHorHB)
    throws IOException, MessagingException {
        
        ArrayList parts = MessageParser.getAllPartsOfMessage(msg, isHorHB);
        
        int size = 0;
        
        for (Object part : parts) {
            
            if (part instanceof InternalPart) {
                InternalPart internalPart = (InternalPart)part;
                size += internalPart.getSize();
            }
        }
        
        return size;
    }
    
    /**
     * Calculate the size of the text part of a message.
     *
     * @param msg message for which to calculate the size
     * @throws java.io.IOException
     * @throws javax.mail.MessagingException
     * @return
     */
    public static int getTextPartSize(Message msg, boolean isHorHB)
    throws IOException, MessagingException {
        
        ArrayList parts = MessageParser.getAllPartsOfMessage(msg, isHorHB);
        
        int size = 0;
        
        for (Object part : parts) {
            
            // text part of a message is the first part of all the parts of a message
            if (part instanceof InternalPart) {
                InternalPart internalPart = (InternalPart)part;
                size += internalPart.getSize();
                break;
            }
        }
        
        return size;
    }

    /**
     * Returns the content of a message, eventually truncated. If the content of 
     * the message is smaller than truncation size or truncation size value is
     * -1 then content is not modifed.
     * 
     * @param msg message whose content has to be truncated
     * @param numBytes truncation size (that is: the final size of the message
     * content)
     * @throws javax.mail.MessagingException 
     * @throws java.io.IOException 
     */
    public static void truncateContent(Message msg, int numBytes) 
    throws MessagingException, IOException {
        
        if (numBytes == -1) {
            return;
        }
        
        Object content = msg.getContent();
        if (!(content instanceof String)){
            throw new IllegalArgumentException("Content is not of String");
        }
        
        byte[] contentByte =
                msg.getDataHandler().getContent().toString().getBytes();
        
        if (contentByte.length < numBytes) {
            return;
        }
        
        String newContent = new String(contentByte, 0, numBytes);
        msg.setText(newContent);        
    }
    
    /**
     * Save a message in the expected email directory (specified by
     * <code>TEST_EXPECTED_EMAIL_DIR</code>).
     * 
     * @param message message to be saved
     * @param name simple file name
     * @throws Exception
     */
    public static void saveMessageAsExpectedEmail(Message message, String name) 
    throws Exception {
        String path = resourcePath + File.separator +
                Constants.TEST_EXPECTED_EMAIL_DIR + File.separator + name;
        message.writeTo(new FileOutputStream(path));
    }
    
    /**
     * Get the size of an <code>InputStream</code>. Given stream must have a finite
     * size.
     * @param stream 
     * @return <code>stream size</code>
     * @throws java.lang.Exception
     */
    public static int getSize(InputStream stream) throws Exception {
        
        int c = -1;
        int count = 0;
        while((c = stream.read()) != -1){
            count++;
        }
        
        return count;
    }
    
    /**
     * Get the size of a <code>String</code>. 
     * 
     * @param s 
     * @return <code>string size</code>
     * @throws java.lang.Exception
     */
    public static int getSize(String s) throws Exception {                
        return s.length();
    }    
    
    public static String contentProviderURL(String basePath, String fileName,
            String token, String userName, int index) throws Exception {
        
        StringBuilder attachmentUrl = new StringBuilder();
        attachmentUrl.append(basePath);
        attachmentUrl.append("/");
        attachmentUrl.append(URLEncoder.encode(fileName, "UTF-8"));            

        attachmentUrl.append("?");
        attachmentUrl.append(MessageCreator.TOKEN_PARAM_NAME);
        attachmentUrl.append("=");
        attachmentUrl.append(URLEncoder.encode(token, "utf-8"));

        attachmentUrl.append("&");
        attachmentUrl.append(MessageCreator.USERID_PARAM_NAME);
        attachmentUrl.append("=");
        attachmentUrl.append(URLEncoder.encode(userName, "utf-8"));

        attachmentUrl.append("&");
        attachmentUrl.append(MessageCreator.INDEX_PARAM_NAME);
        attachmentUrl.append("=");
        attachmentUrl.append(Integer.toString(index));
        
        return attachmentUrl.toString();
    }
}
