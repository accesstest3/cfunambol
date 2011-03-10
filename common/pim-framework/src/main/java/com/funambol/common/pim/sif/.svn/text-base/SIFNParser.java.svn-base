/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
package com.funambol.common.pim.sif;

import org.w3c.dom.*;
import org.xml.sax.*;
import java.io.*;
import java.util.HashMap;
import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.note.Note;
import com.funambol.common.pim.note.SIFN;

/**
 * This objects represents a Note.
 *
 * @version $Id: SIFNParser.java,v 1.3 2008-04-29 07:20:56 piter_may Exp $
 */
public class SIFNParser extends XMLParser implements SIFN {

    /**
     * Parse the XML doc the XML parser for a SIFN object.
     * @param xmlStream the input stream in XML format
     */
    public SIFNParser(InputStream xmlStream)
    throws SAXException, IOException {
        super(xmlStream);
    }


    /**
     * Parse the xml document and returns a Note object.
     * @return note the object Note
     */
    public Note parse() throws SAXException {
        return parse(new Note());
    }

    /**
     * Parse the xml document and returns a iCalendar object.
     * @return event the object Event
     */
    public Note parse(Note note) throws SAXException {
        if (root == null) {
            throw new SAXException("No root tag available");
        }
        if (!root.getTagName().equals(SIFN.ROOT_TAG)) {
            throw new SAXException("Incorrect root tag " + root.getTagName() + ", expected " + SIFN.ROOT_TAG);
        }

        //getting child nodes
        NodeList children = root.getChildNodes();
        Node child = null;
        String content  = null;
        String nodeName = null;
        Property prop = null;

        
        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);

            if (!(children.item(i) instanceof Element)) {
                continue;
            }

            nodeName = child.getNodeName();

            if       (SUBJECT.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    note.setSubject(prop);
                }
            } else if (BODY.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    note.setTextDescription(prop);
                }
            } else if (CATEGORIES.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    note.setCategories(prop);
                }
            } else if (DATE.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    note.setDate(prop);
                }
            } else if (FOLDER.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    note.setFolder(prop);
                }
            } else if (COLOR.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    note.setColor(prop);
                }
            } else if (HEIGHT.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    note.setHeight(prop);
                }
            } else if (WIDTH.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    note.setWidth(prop);
                }
            } else if (TOP.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    note.setTop(prop);
                }
            } else if (LEFT.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    note.setLeft(prop);
                }
            } 
            
            /*
            if (CATEGORIES.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                note.getCategories().setPropertyValue(prop.getPropertyValue());
                note.getCategories().setEncoding(prop.getEncoding());
                note.getCategories().setGroup(prop.getGroup());
                note.getCategories().setCharset(prop.getCharset());
                note.getCategories().setLanguage(prop.getLanguage());
                note.getCategories().setValue(prop.getValue());
                continue;
            }
            if (LANGUAGE.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    note.setLanguages(content);
                }
                continue;
            }
            if (VERSION.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.setVersion(prop);
                }
            }    
            if (nodeName.startsWith("X-")) {
                note.addXTag(createNewXTag(child));
                continue;
            }
            */           

        } // for

        return (note);
    }

   /**
    * create a Property object from a XML node
    * @todo check the getNodeName (this parser it's calendar object oriented)
    */
    private Property createPropertyFromTag(Node child) {
        Property prop=new Property();
        prop.setPropertyValue(getNodeContent(child));
        prop.setTag(child.getNodeName());
        HashMap hash = new HashMap();
        //setting attributes
        NamedNodeMap attributes= child.getAttributes();
        Node attribute;
        for (int i=0;i<attributes.getLength();i++) {
            if (attributes.item(i) instanceof Attr) {
                attribute=attributes.item(i);
                if (attribute.getNodeName().equals("ENCODING")) {
                    prop.setEncoding(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("LANGUAGE")) {
                    prop.setLanguage(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("VALUE")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("ALTREP")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("CN")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("CUTYPE")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("DELEGATED-FROM")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("DELEGATED-TO")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("DIR")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("MEMBER")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("PARTSTAT")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("RELATED")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("SENT-BY")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().startsWith("X-")) {
                    hash.put(attribute.getNodeName(), attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("cal")) {
                    hash.put(attribute.getNodeName(), attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("evt")) {
                    hash.put(attribute.getNodeName(), attribute.getNodeValue());
                }
            }
        }
         if (hash.size() > 0) {
           prop.setXParams(hash);
        }
        return (prop);
    }

}
