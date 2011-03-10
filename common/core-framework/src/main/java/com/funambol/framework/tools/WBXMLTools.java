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
package com.funambol.framework.tools;

import java.io.*;
import java.util.Vector;

import org.kxml.parser.ParseEvent;
import org.kxml.parser.XmlParser;
import org.kxml.parser.Tag;
import org.kxml.wap.SyncMLWriter;
import org.kxml.wap.SyncMLParser;
import org.kxml.wap.WapExtensionEvent;
import org.kxml.Xml;
import org.kxml.Attribute;

import com.funambol.framework.core.SyncML;
import com.funambol.framework.core.Util;
import com.funambol.framework.core.Sync4jException;

import org.jibx.runtime.*;
import org.jibx.runtime.impl.*;

import org.apache.commons.lang.StringUtils;

/**
 *  Utility class for WBXML stuff
 *
 *
 * @version $Id: WBXMLTools.java,v 1.4 2008-02-25 13:07:12 luigiafassina Exp $
 */
public class WBXMLTools {

    // --------------------------------------------------------------- Constants
    public static final String WELL_KNOWN_NS = ",DevInf,";

    // ---------------------------------------------------------- Public methods

    /**
     * Converts a string to a WBXML message.
     *
     * @param s the String to convert - NOT NULL
     * @param verDTD the DTD version
     * @return the WBXML message
     *
     * @throws Sync4jException in case of parser errors
     */
    public static byte[] toWBXML(final String s, final String verDTD)
    throws Sync4jException {
        SyncMLWriter writer = null;
        try {
            writer = new SyncMLWriter(verDTD);
            XmlParser xml = new XmlParser(new StringReader(s));
            traverseXML(xml, writer, verDTD);
        } catch (IOException e) {
            throw new Sync4jException(e.getMessage(), e);
        } finally {
            if (writer != null) try {writer.close();} catch (Exception e) {}
        }

        return writer.getBytes();
    }

    /**
     * Converts a string to a WBXML message.
     *
     * @param s the String to convert - NOT NULL
     *
     * @return the WBXML message
     *
     * @throws Sync4jException in case of parser errors
     */
    public static byte[] toWBXML(final String s)
    throws Sync4jException {
        return toWBXML(s, null);
    }
    
    /**
     * Encodes a <i>Message</i> to WBXML
     * <p>
     * The message is fixed before encoding in order to get a converted message
     * that makes sense. For instance, the Meta type of a Results element (if
     * there is any), must be changed from <code>application/vnd.syncml-devinf+xml</code>
     * to <code>application/vnd.syncml-devinf+wbxml</code>
     *
     * @param msg the message to encode
     *
     * @return the encoded stream of bytes (as a byte[] buffer).
     *
     * @throws Sync4jException in case of errors
     */
    public static byte[] toWBXML(SyncML msg)
    throws Sync4jException {

        String verDTD = msg.getSyncHdr().getVerDTD().getValue();

        try {

            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            IBindingFactory f = BindingDirectory.getFactory("binding", SyncML.class);
            IMarshallingContext c = f.createMarshallingContext();
            c.setIndent(0);
            c.marshalDocument(msg, "UTF-8", null, bout);

            String inputXml = new String(bout.toByteArray());
            
            return toWBXML(inputXml, verDTD);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts a WBXML message into the corresponding XML message.
     *
     * The character set used for the encoding is determined by
     * wbxml defined h set (see WBXM standard) and if it
     * is not defined UTF-8 is ised.
     * @param msg the message to convert - NOT NULL
     *
     * @return the XML message or NULL if an error occurred
     *
     * @throws Sync4jException in case of parser errors
     */
    public static String wbxmlToXml(final byte[] msg)
    throws Sync4jException {
        return wbxmlToXml(msg, null);
    }

    /**
     * Converts a WBXML message into the corresponding XML message.
     *
     * @param msg the message to convert - NOT NULL
     * @param charset the characte set used for the encoding. If the
     * value is null, the character set defined in the wbxml is used
     * otherwise, UTF-8 is ised.
     *
     * @return the XML message or NULL if an error occurred
     *
     * @throws Sync4jException in case of parser errors
     */
    public static String wbxmlToXml(final byte[] msg, String charset)
    throws Sync4jException {

        ByteArrayInputStream in = null;
        try {
            in = new ByteArrayInputStream(msg);

            SyncMLParser parser = new SyncMLParser(in, charset);

            return parseWBXML(parser);
        } catch (Throwable t) {
            throw new Sync4jException(t.getMessage(), t);
        }
    }

    public static boolean isWellKnownNamespace(String ns) {
        return (WELL_KNOWN_NS.indexOf(',' + ns + ',') >= 0);
    }

    // --------------------------------------------------------- Private methods
    
    /**
     *
     * @param parser the XmlParser
     * @param writer the SyncMLWriter
     * @param verDTD the DTD version
     * @throws IOException
     */
    private static void traverseXML(XmlParser    parser,
                                    SyncMLWriter writer,
                                    String       verDTD)
    throws IOException {
        //
        // NOTE: when the namespace changes in one of the namespaces listed
        //       in WELL_KNOWN_NS, a well known document must be inserted;
        //       therefore a new inner writer is created when the tag is opened
        //       and its content flushed in the original writer when the tag is
        //       closed
        //

        boolean leave = false;

        do {
            ParseEvent event = parser.read();

            switch (event.getType()) {
                case Xml.START_TAG:
                    SyncMLWriter tagWriter = null;

                    String name = event.getName();

                    if (isWellKnownNamespace(name)) {
                        tagWriter = new SyncMLWriter(((Tag)event).getNamespace(), verDTD);
                    } else {
                        tagWriter = writer;
                    }

                    // see API doc of StartTag for more access methods
                    tagWriter.startTag(name);
                    traverseXML(parser, tagWriter, verDTD); // recursion

                    if (tagWriter != writer) {
                        tagWriter.close();
                        writer.writeOpaque(new String(tagWriter.getBytes()));
                        tagWriter = null;
                    }
                    break;

                case Xml.END_TAG:
                    writer.endTag();
                    leave = true;
                    break;

                case Xml.END_DOCUMENT:
                    leave = true;
                    break;

                case Xml.TEXT:
                    writer.write(event.getText());
                    break;

                case Xml.WHITESPACE:
                    break;

                default:
            }
        } while (!leave);
    }

    private static String parseWBXML(SyncMLParser parser) throws IOException {
        boolean[] inTag = new boolean[6];
        return parseWBXML(parser, inTag);
    }

    private static String parseWBXML(SyncMLParser parser, boolean[] inTag) throws IOException{
        /**
         * inTag[0]: flag for tag <Put> or <Results>
         * inTag[1]: flag for tag <Item> not in Put or Results
         * inTag[2]: flag for tag <Data> (inside a Item not in Put or Results)
         * inTag[3]: flag for tag <Cred>
         * inTag[4]: set if tag Meta inside Cred contains "b64"
         * inTag[5]: set if tag Meta inside Cred contains "auth-md5"
         */
        StringBuffer buf=new StringBuffer();
        boolean leave = false;

        String tagName = null;
        String text    = null;

        do {
            ParseEvent event = parser.read();
            switch (event.getType()) {
                case Xml.START_TAG:
                    tagName = event.getName();

                    buf.append("<");
                    buf.append(tagName);
                    Vector attrs=event.getAttributes();
                    if(attrs!=null){
                        for(int i=0;i<attrs.size();i++){
                            Attribute attr=(Attribute)attrs.elementAt(i);
                            buf.append(" ");
                            buf.append(attr.getName());
                            buf.append("='");
                            buf.append(attr.getValue());
                            buf.append("'");
                        }
                    }
                    buf.append(">");

                    //
                    //This is util for replace the Data content if contains
                    //illegal character
                    //
                    if (!inTag[0]) {
                        inTag[0] = ("Put".equals(tagName) || "Results".equals(tagName));
                    }

                    if (!inTag[0]) {
                        if (!inTag[1]) {
                            inTag[1] = "Item".equals(tagName);
                        } else if (inTag[1]) {
                            inTag[2] = "Data".equals(tagName);
                        }
                    }

                    //
                    //This is util to establish if the auth-md5 credential are
                    //encoded in Base64
                    //
                    if (!inTag[3]) {
                        inTag[3] = "Cred".equals(tagName);
                    }

                    text = parseWBXML(parser, inTag);

                    if (inTag[3]) {
                        if ("Meta".equals(tagName)) {
                            inTag[4] = (text.indexOf("b64") >= 0);
                            inTag[5] = (text.indexOf("auth-md5") >= 0);
                            buf.append(text);
                            text = parseWBXML(parser, inTag);
                        }
                    }

                    buf.append(text);
                    break;

                case Xml.END_TAG:
                    tagName = event.getName();
                    if (tagName != null) {
                        if (tagName.equals("Put") || tagName.equals("Results")) {
                            if (inTag[0]) {
                                inTag[0] = false;
                            }
                        } else if (tagName.equals("Cred")) {
                            if (inTag[3]) {
                                inTag[3] = false;
                                inTag[4] = false;
                                inTag[5] = false;
                            }
                        } else if (tagName.equals("Item")) {
                            if (inTag[1]) {
                                inTag[1] = false;
                            }
                        } else if (tagName.equals("Data")) {
                            if (inTag[2]) {
                                inTag[2] = false;
                            }
                        }
                    }
                    buf.append("</");
                    buf.append(event.getName());
                    buf.append(">");
                    leave = true;
                    break;

                case Xml.END_DOCUMENT:
                    leave = true;
                    break;

                case Xml.TEXT:
                    text = event.getText();

                    if (!inTag[0] && inTag[1] && inTag[2]) {
                        text = replaceDataContent(text);
                    }
                    buf.append(text);
                    break;

                case Xml.WAP_EXTENSION:
                    text = event.getText();

                    if (!inTag[0] && inTag[1] && inTag[2]) {
                        text = replaceDataContent(text);
                    }

                    if (event instanceof WapExtensionEvent) {
                        WapExtensionEvent e = (WapExtensionEvent)event;
                        Object content = e.getContent();

                        if (inTag[5] && !inTag[4] && content != null) {
                            if (content instanceof byte[]) {
                                text = new String(Base64.encode((byte[])content));
                            }
                        }
                    }

                    buf.append(text);
                    break;

                case Xml.WHITESPACE:
                    break;

                default:
            }
        } while (!leave);

        return buf.toString();
    }

    /**
     * Replace not permitted characters with their HTML codification.
     *
     * @param text the data content
     *
     * @return text the data content modified
     */
    private static String replaceDataContent(String text) {
        text = StringUtils.replace(text, "&", "&amp;");
        text = StringUtils.replace(text, "<", "&lt;");
        text = StringUtils.replace(text, ">", "&gt;");
        text = StringUtils.replace(text, "\"", "&quot;");
        return text;
    }
}
