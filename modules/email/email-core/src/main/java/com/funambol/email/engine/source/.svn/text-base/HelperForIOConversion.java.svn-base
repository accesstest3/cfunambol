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
package com.funambol.email.engine.source;

import com.funambol.email.exception.EntityException;
import com.funambol.email.pdi.converter.FolderToXML;
import com.funambol.email.pdi.converter.MailToXML;
import com.funambol.email.pdi.folder.Folder;
import com.funambol.email.pdi.mail.Email;
import com.funambol.email.pdi.parser.XMLFolderParser;
import com.funambol.email.pdi.parser.XMLMailParser;
import java.io.ByteArrayInputStream;
import java.util.TimeZone;


/**
 * This class includes the methods to set the Filter Object
 *
 * @version $Id: HelperForIOConversion.java,v 1.1 2008-03-25 11:28:18 gbmiglia Exp $
 *
 */
public class HelperForIOConversion {

        
    // ---------------------------------------------------------- Public Methods

    /**
     * Get Data from XML message converting the xml
     * item into a Folder object
     * It's public for unit-test purpose
     *
     * @param content String
     * @return Folder
     * @throws EntityException
     */
    public static Folder getFoundationFolderFromXML(String content)
       throws EntityException {

        content = handleContentInput(content);

        ByteArrayInputStream buffer;
        XMLFolderParser parser;
        Folder folder;
        try {
            folder = new Folder();
            buffer = new ByteArrayInputStream(content.getBytes());

            if ((content.getBytes()).length > 0) {
                parser = new XMLFolderParser(buffer);
                folder = parser.parse();
            }
        } catch (Exception e) {
            throw new EntityException(e.toString());
        }
        return folder;
    }

    
    /**
     * Get Data from XML message converting the xml
     * item into a Email object
     * It's public for unit-test purpose
     *
     * @param content String
     * @return Email
     * @throws EntityException
     */
    public static Email getFoundationMailFromXML(String content)
            throws EntityException {

        content = handleContentInput(content);

        ByteArrayInputStream buffer;
        XMLMailParser parser;
        Email mail;
        try {
            mail = new Email();
            buffer = new ByteArrayInputStream(content.getBytes());

            if ((content.getBytes()).length > 0) {
                parser = new XMLMailParser(buffer);
                mail = parser.parse();
            }
        } catch (Exception e) {
            throw new EntityException(e.toString());
        }
        return mail;
    }   


   /**
     * Get Data from foundation.pdi.mail.Folder
     * converting the Folder object into an xml item
     *
     * @param folder Folder
     * @return String
     * @throws EntityException
     */
    public static String getXMLFromFoundationFolder(Folder folder,
                                                    TimeZone deviceTimeZone,
                                                    String deviceCharset)
            throws EntityException {

        String xml;
        try {
            FolderToXML m2xml = new FolderToXML(deviceTimeZone, deviceCharset);
            xml = m2xml.convert(folder);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        xml = handleContentOuput(xml);

        return xml;
    }       

    
    /**
     * Get Data from sync4j.foundation.pdi.mail.Email
     * converting the Contact object into an xml item
     *
     * @param mail Email
     * @return String
     * @throws EntityException
     */        
    public static String getXMLFromFoundationMail(Email mail,
                                                  TimeZone deviceTimeZone,
                                                  String deviceCharset)
      throws EntityException {

        String xml;
        try {
            MailToXML m2xml = new MailToXML(deviceTimeZone, deviceCharset);
            xml = m2xml.convert(mail);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        xml = handleContentOuput(xml);

        return xml;
    }
     
    //-------------------------------------------------------- Protected Methods
    
    
    //---------------------------------------------------------- Private Methods
    
    /**
     * this method handles the content in a SyncItem
     * It's used in input direction
     *
     * at the moment it converts the end of the internal CDATA
     *
     * @param content String
     * @return String
     */
    private static String handleContentInput(String content) {

        /*
         * tmp: the encrypt mail contains the external CDATA and \n
         */
        content = content.replaceAll("\\[\n","\\[");
        //content = content.replaceAll("]]&gt;\n","]]&gt;");
        //content = content.replaceAll(">\n", ">");

        if (content.startsWith("<![CDATA[<Email>")){
           int start = content.indexOf("<![CDATA[");
           int stop  =  content.indexOf("</Email>");
           content   = content.substring(start + 9, stop + 8);
        }

        if (content.startsWith("<![CDATA[<Folder>")){
           int start = content.indexOf("<![CDATA[");
           int stop  =  content.indexOf("</Folder>");
           content = content.substring(start + 9, stop + 9);
        }

        //
        content = content.replaceAll("]]&gt;", "]]>");

        return content;

    }

    
    /**
     * this method handles the content in a SyncItem
     * It's used in output direction
     *
     * at the moment the output it's not modified
     *
     * @param stream String
     * @return String
     */
    private static String handleContentOuput(String stream) {
        return stream;
    }    
 
}
