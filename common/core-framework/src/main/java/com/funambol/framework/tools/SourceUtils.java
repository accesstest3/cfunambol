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
package com.funambol.framework.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import java.util.zip.CRC32;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version $Id: SourceUtils.java,v 1.2 2007-11-28 11:13:24 nichele Exp $
 */
public class SourceUtils {

    //---------------------------------------------------------------- Constants
    public static final String ROOT_NAME = "__root__name__";

    public static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\""
                                           + System.getProperty("file.encoding")
                                           + "\"?>" ;
    /*
    * Test main
    */
    public static void main(String args[]) throws Exception{

        SourceUtils s = new SourceUtils();
        String str = new String();
        str += "<contact><doc1>doc11</doc1><doc2>docu2</doc2></contact>";
        HashMap hash = new HashMap();
        hash = s.xmlToHashMap(str);
        String s2 = new String();
        s2 = s.hashMapToXml(hash);
        System.out.println(s2);

    }

    //----------------------------------------------------------- Public methods

   /**
    *
    * Make a HashMap of fieldName - fieldValue
    *
    * tagName   -> key
    * tagValue  -> value
    *
    * @param content the content to parse
    * @param removeTagCData if true the tag CDATA will be removed
    * 
    * @return HashMap of fieldName - fieldValue
    * 
    * @throws IOException
    * @throws ParserConfigurationException
    * @throws SAXException 
    **/
    public static HashMap xmlToHashMap (String content, boolean removeTagCData)
    throws IOException, ParserConfigurationException, SAXException {

        DocumentBuilderFactory  docBuilderFactory  = null ;
        DocumentBuilder         docBuilder         = null ;
        Document                docXml             = null ;
        NodeList                lstChildren        = null ;
        Element                 el                 = null ;
        Node                    node               = null ;

        HashMap                 fields             = null ;
        String                  nodeValue          = null ;
        String                  rootName           = null ;

        InputStream             is                 = null ;


        if (removeTagCData) {
            content = dropTagCData(content);
        }

        int p = content.indexOf("?>");
        if (p>0) {
            content = content.substring(p+2);
            content = XML_VERSION + content;
        }

        is = new ByteArrayInputStream(content.getBytes());

        docBuilderFactory = DocumentBuilderFactory.newInstance() ;
        docBuilder = docBuilderFactory.newDocumentBuilder()    ;
        docXml = docBuilder.parse(is)                          ;

        el = docXml.getDocumentElement()                       ;

        rootName = el.getTagName();

        lstChildren = el.getChildNodes()                       ;

        fields = new HashMap();

        for (int i=0, l = lstChildren.getLength(); i <  l; i++) {

            node = lstChildren.item(i);

            if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

                if (node.getChildNodes().item(0) != null) {
                    nodeValue = node.getChildNodes().item(0).getNodeValue();
                } else {
                    nodeValue = "";
                }

                fields.put(node.getNodeName(), nodeValue);

            }

        }

        //
        // Put the rootName in the hashMap with key ROOT_NAME that is a
        // conventional name to store the name of the root
        //
        if (rootName != null) {
            fields.put(ROOT_NAME, rootName);
        }
        return fields;
    }

   /**
    *
    * Make a HashMap of fieldName - fieldValue
    *
    * tagName   -> key
    * tagValue  -> value
    *
    * @param content
    * @return HashMap of fieldName - fieldValue
    * @throws IOException
    * @throws ParserConfigurationException
    * @throws SAXException
    **/
    public static HashMap xmlToHashMap (String content)
    throws IOException, ParserConfigurationException, SAXException {
        return xmlToHashMap(content, true);
    }

     /**
     * Make xml from an HashMap.
     * key   -> tagName
     * value -> tagValue
     *
     * @param fields
     * @return String with the xml
     * @throws Exception
     **/
    public static String hashMapToXml (Map fields) throws Exception {
        String fieldName  = null;
        String fieldValue = null;
        String rootName   = null;
        String message    = "";

        Iterator i = fields.keySet().iterator();;
        while(i.hasNext()) {

            fieldName  = (String) i.next()              ;
            fieldValue = StringTools.escapeBasicXml((String)fields.get((String)fieldName));

            if (fieldName.equals (ROOT_NAME)) {
                rootName = fieldValue;
            } else {
                message =  message  +
                       "<"             +
                       fieldName       +
                       ">"             +
                       fieldValue      +
                       "</"            +
                       fieldName       +
                       ">" ;
        }
        }

        // using CDATA
        // message = "<![CDATA[" + "<" + rootName + ">" + message;
        // message = message  + "</" + rootName + ">]]>"          ;

        // without CDATA
        message = "<" + rootName + ">" + message  ;
        message = message  + "</" + rootName + ">";

        return XML_VERSION + message;
    }

    //--------------------------------------------------------- Private methods

    /**
     * @param content
     * @return input string by drop CData tag
     **/
    private static String dropTagCData(String content) {

        int startData = 0;
        int endData   = 0;

        if (content.indexOf("<![CDATA[") != - 1) {

            startData = content.indexOf("<![CDATA[") + "<![CDATA[".length();

            //
            // for server bug: server create end CDATA with ]]]]>
            //
            endData   = content.lastIndexOf("]]]]>");

            if (endData == -1) {
               endData   = content.lastIndexOf("]]>");
            }

            content = content.substring(startData, endData);
        }
            return content;
        }

    /**
     * Returns the CRC value for the given byte[]
     *
     * @param value
     * @return CRC of value
     **/
    public static long computeCRC(byte[] value) {

        CRC32 checksumEngine = new CRC32();

        checksumEngine.update(value, 0, value.length);
        long checksumMessage = checksumEngine.getValue();
        checksumEngine.reset();

        return checksumMessage;
    }
}
