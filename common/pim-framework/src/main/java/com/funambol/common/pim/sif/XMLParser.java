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
import javax.xml.parsers.*;

import com.funambol.common.pim.contact.*;
import com.funambol.common.pim.calendar.*;

/**
 * A Parser that converts an xml representation of a vcard into an
 * object representation.
 *
 * @version $Id: XMLParser.java,v 1.2 2007-11-28 11:14:06 nichele Exp $
 */
public abstract class XMLParser {

    // ------------------------------------------------------------ Private data

    protected Element root = null;

    // ---------------------------------------------------------- Public methods

    /**
     * Parse the XML doc the XML parser for a VCard or ICalendar object.
     * @param xmlStream the input stream in XML format
     */
    public XMLParser(InputStream xmlStream) throws SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setCoalescing(false);
        factory.setIgnoringElementContentWhitespace(true);
        Document doc = null;

        try {

            //
            // Parse the xml document
            //
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(xmlStream);

        }catch (ParserConfigurationException pce) {
            throw new SAXException(pce.getMessage());
        }

        //
        // Getting the root element
        //
        root=doc.getDocumentElement();
    }

    /**
     * Return a string with the node text content
     */
    public String getNodeContent(Node child) {
        NodeList childNodes=child.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            Node childNode=childNodes.item(i);
            if (childNode instanceof Text) {
                return (childNode.getNodeValue());
            }
        }
        return ("");
    }

    /**
     * Return an Integer with the node text content
     */
    public Integer getIntWrapNodeContent(Node child) {
        NodeList childNodes=child.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            Node childNode=childNodes.item(i);
            if (childNode instanceof Text) {
                return new Integer(childNode.getNodeValue());
            }
        }
        return null;
    }

    /**
     * Return an int with the node text content
     */
    public int getIntNodeContent(Node child) {
        Integer content = getIntWrapNodeContent(child);
        if (content == null) {
            return 0;
        }
        return content.intValue();
    }

    /**
     * Return a Short with the node text content
     */
    public Short getShortWrapNodeContent(Node child) {
        NodeList childNodes=child.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            Node childNode=childNodes.item(i);
            if (childNode instanceof Text) {
                return new Short(childNode.getNodeValue());
            }
        }
        return null;
    }

    /**
     * Return a short with the node text content
     */
    public short getShortNodeContent(Node child) {
        Short content = getShortWrapNodeContent(child);
        if (content == null) {
            return 0;
        }
        return content.shortValue();
    }
}
