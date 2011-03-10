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
package com.funambol.email.pdi.parser;


import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.sif.XMLParser;
import com.funambol.email.pdi.mail.Email;
import com.funambol.email.pdi.mail.IEMAIL;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

 /**
  * This objects represents a list of Mail property parameters.
  * The list is based on the informations contained in a list of parser tokens.
  *
  * @version $Id: XMLMailParser.java,v 1.1 2008-03-25 11:25:35 gbmiglia Exp $
  */
 public class XMLMailParser extends XMLParser implements IEMAIL {

     /**
      * Parse the XML doc the XML parser for a VCard or ICalendar object.
      *
      * @param xmlStream InputStream. the input stream in XML format
      * @throws SAXException
      * @throws IOException
      */
     public XMLMailParser(InputStream xmlStream)
     throws SAXException, IOException {
         super(xmlStream);
     }


     /**
      * Parse the xml document and returns a iCalendar object.
      *
      * @return Email. event the object Event
      * @throws SAXException
      */
     public Email parse() throws SAXException {
         return parse(new Email());
     }

     /**
      * Parse the xml document and returns a iCalendar object.
      *
      * @param mail Email
      * @return email the object Email.
      * @throws SAXException
      */
     public Email parse(Email mail) throws SAXException {

         if (root == null) {
             throw new SAXException("No root tag available");
         }

         if (!root.getTagName().equals(ROOT_TAG)) {
             throw new SAXException("Incorrect root tag " + root.getTagName() +
                                    ", expected " + ROOT_TAG );
         }

         Node child;
         String nodeName;
         Property prop;

         //getting child nodes
         NodeList children = root.getChildNodes();

         for (int i = 0; i < children.getLength(); i++) {

             child = children.item(i);

             if (!(children.item(i) instanceof Element)) {
                 continue;
             }

             nodeName = child.getNodeName();

             if (READ.equals(nodeName)) {
                 prop=createPropertyFromTag(child);
                 if (prop!=null) {
                     mail.getRead().setPropertyValue(prop.getPropertyValue());
                 }
             } else if (FORWARDED.equals(nodeName)) {
                 prop=createPropertyFromTag(child);
                 if (prop!=null) {
                     mail.getForwarded().setPropertyValue(prop.getPropertyValue());
                 }
             } else if (REPLIED.equals(nodeName)) {
                 prop=createPropertyFromTag(child);
                 if (prop!=null) {
                     mail.getReplied().setPropertyValue(prop.getPropertyValue());
                 }
             } else if (RECEIVED.equals(nodeName)) {
                 prop=createPropertyFromTag(child);
                 if (prop!=null) {
                     mail.getReceived().setPropertyValue(prop.getPropertyValue());
                 }
             } else if (CREATED.equals(nodeName)) {
                 prop=createPropertyFromTag(child);
                 if (prop!=null) {
                     mail.getCreated().setPropertyValue(prop.getPropertyValue());
                 }
             } else if (MODIFIED.equals(nodeName)) {
                 prop=createPropertyFromTag(child);
                 if (prop!=null) {
                     mail.getModified().setPropertyValue(prop.getPropertyValue());
                 }
             } else if (DELETED.equals(nodeName)) {
                 prop=createPropertyFromTag(child);
                 if (prop!=null) {
                     mail.getDeleted().setPropertyValue(prop.getPropertyValue());
                 }
             } else if (FLAGGED.equals(nodeName)) {
                 prop=createPropertyFromTag(child);
                 if (prop!=null) {
                     mail.getFlagged().setPropertyValue(prop.getPropertyValue());
                 }
             } else if (EMAILITEM.equals(nodeName)) {
                 prop=createPropertyFromCDATA(child);
                 if (prop!=null) {
                     mail.getEmailItem().setPropertyValue(prop.getPropertyValue());
                     if (child.getAttributes()!=null && child.getAttributes().getNamedItem("enc")!=null){
                         mail.getEmailItem().setEncoding(child.getAttributes().getNamedItem("enc").getNodeValue());
                     }

                 }
             }


         } // for

         return (mail);
     }

    /**
     * create a Property object from a XML node
     *
     * @param child Node
     * @return Property
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
                 if (attribute.getNodeName().equals("ENC")) {
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


     /**
      * create a Property object from a XML node
      *
      * @param child Node
      * @return Property
      */
     private Property createPropertyFromCDATA(Node child) {
          Property prop=new Property();

          String value = "";
          NodeList childNodes = child.getChildNodes();
          for (int i = 0; i < childNodes.getLength(); i++) {
              Node childNode = childNodes.item(i);
              if (childNode instanceof CDATASection) {
                  value = childNode.getNodeValue();
              }
          }

          prop.setPropertyValue(value);


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
