/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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
package com.funambol.common.media.file.parser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.funambol.common.media.file.*;
import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.StringTools;

/**
 * @responsibility marshal FileDataObject into XML documents
 *
 * @version $Id$
 */
public class FileDataObjectMarshaller {

    // --------------------------------------------------------------- Constants

    protected static final String XML_VERSION =
            "<?xml version=\"1.0\" encoding=\""
            + System.getProperty("file.encoding")
            + "\"?>";

    // ---------------------------------------------------------- Public Methods

    /**
     * Marshall the FileDataObject into a XML document (already in byte[])
     * @param fileDataObject The file data object to be marshalled
     * @return the XML document representing the file dataa object
     * @throws com.funambol.common.media.file.parser.FileDataObjectParsingException
     */
    public byte[] marshall(FileDataObject fileDataObject)
            throws FileDataObjectParsingException {

        long len = fileDataObject.getSize();
        long encLen = (len * 4 / 3 + 1000);
        if (encLen > Integer.MAX_VALUE) {
            throw new FileDataObjectParsingException("Unable to marshall items bigger than 2Gb");
        }
        int bufLen = (int)encLen;

        ByteArrayOutputStream bout = new ByteArrayOutputStream(bufLen);

        File f = null;
        FileInputStream fin = null;

        try {
            bout.write(XML_VERSION.getBytes());

            appendStartTag(bout, FileDataObjectXMLTags.TAG_FILE, null, null);
            bout.write('\n');

            appendElement(bout, FileDataObjectXMLTags.TAG_NAME, fileDataObject.getName(), null, null, true);
            appendElement(bout, FileDataObjectXMLTags.TAG_CREATED, fileDataObject.getCreated(), null, null, true);
            appendElement(bout, FileDataObjectXMLTags.TAG_MODIFIED, fileDataObject.getModified(), null, null, true);
            appendElement(bout, FileDataObjectXMLTags.TAG_ACCESSED, fileDataObject.getAccessed(), null, null, true);

            if (fileDataObject.getHidden() != null || fileDataObject.getSystem() != null || fileDataObject.getArchived() != null || fileDataObject.getWritable() != null || fileDataObject.getReadable() != null || fileDataObject.getExecutable() != null) {
                appendStartTag(bout, FileDataObjectXMLTags.TAG_ATTRIBUTES, null, null);
                bout.write('\n');
                appendElement(bout, FileDataObjectXMLTags.TAG_HIDDEN, fileDataObject.getHidden());
                appendElement(bout, FileDataObjectXMLTags.TAG_SYSTEM, fileDataObject.getSystem());
                appendElement(bout, FileDataObjectXMLTags.TAG_ARCHIVED, fileDataObject.getArchived());
                appendElement(bout, FileDataObjectXMLTags.TAG_DELETED, fileDataObject.getDeleted());
                appendElement(bout, FileDataObjectXMLTags.TAG_WRITABLE, fileDataObject.getWritable());
                appendElement(bout, FileDataObjectXMLTags.TAG_READABLE, fileDataObject.getReadable());
                appendElement(bout, FileDataObjectXMLTags.TAG_EXECUTABLE, fileDataObject.getExecutable());
                appendEndTag(bout, FileDataObjectXMLTags.TAG_ATTRIBUTES);
            }

            appendElement(bout, FileDataObjectXMLTags.TAG_CTTYPE, fileDataObject.getContentType(), null, null, true);

            long size = 0;
            if (fileDataObject.hasBodyFile()) {
                f = fileDataObject.getBodyFile();
                size = f.length();
            }

            if (size > 0) {
                fin = new FileInputStream(f);
                appendStartTag(bout, FileDataObjectXMLTags.TAG_BODY, FileDataObjectXMLTags.ATTR_ENC, FileDataObjectXMLTags.ENC_BASE64);

                Base64.encode(fin, bout);
            
                appendEndTag(bout, FileDataObjectXMLTags.TAG_BODY);
            } else {
                appendEmptyTag(bout, FileDataObjectXMLTags.TAG_BODY);
            }

            appendElement(bout, FileDataObjectXMLTags.TAG_SIZE, fileDataObject.getSize());
            appendEndTag(bout, FileDataObjectXMLTags.TAG_FILE);

            bout.flush();
            
        } catch (IOException ex) {
            throw new FileDataObjectParsingException("Error marshalling file data object", ex);
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException ex) {
                    // nothing to do
                }
            }
        }
        
        return bout.toByteArray();
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Writes to a ByteArrayOutputStream the start tag with the specified name and
     * attribute
     *
     * @param bout The ByteArrayOutputStream where to write the start tag
     * @param tag the tag name of the XML element. must be not empty
     * @param attrNames The attribute name of the element (only one attribute is
     * allowed)
     * @param attrVal The attribute value
     */
    private static void appendStartTag(ByteArrayOutputStream bout, String tag,
            String attrNames, String attrVal) throws IOException {

        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("Invalid empty tag");
        }
        bout.write('<');
        bout.write(tag.getBytes());

        if (attrNames != null && attrVal != null) {
            bout.write(' ');
            bout.write(attrNames.getBytes());
            bout.write('=');
            bout.write('"');
            bout.write(attrVal.getBytes());
            bout.write('"');
        }
        bout.write('>');
    }

    /**
     * Append to a StringBuilder the XML element with the specified name, value
     * and attribute
     *
     * @param output The StringBuilder where to append the XML element
     * @param tag the tag name of the XML element. must be not empty
     * @param value the value of the XML elemen
     * @param attrNames The attribute name of the element (only one attribute is
     * allowed)
     * @param attrVal The attribute value
     * @param escape must the value be escaped ?
     * @return The StringBuilder passed as argument
     */
    public static void appendElement(ByteArrayOutputStream output,
                                     String tag,
                                     String value,
                                     String attrNames,
                                     String attrVal,
                                     boolean escape) throws IOException {

        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("Invalid empty tag");
        }

        if (value == null) {
            return ;
        } else if (value.length() > 0) {
            appendStartTag(output, tag, attrNames, attrVal);
            if (escape) {
                value = StringTools.escapeBasicXml(value);
            }
            output.write(value.getBytes());

            appendEndTag(output, tag);
        } else {
            appendEmptyTag(output, tag);
        }
    }

    /**
     * Append to a ByteArrayOutputStream the XML element with the specified name and
     * value
     *
     * @param output The ByteArrayOutputStream where to append the XML element
     * @param tag the tag name of the XML element. must be not empty
     * @param value the value of the XML elemen
     */
    private static void appendElement(ByteArrayOutputStream output,
                                      String tag,
                                      byte[] value,
                                      String attrNames,
                                      String attrVal) throws IOException {


        if (value == null) {
            value = new byte[0];
        }

        if (value.length > 0) {
            appendStartTag(output, tag, attrNames, attrVal);
            output.write(value);
            appendEndTag(output, tag);
        } else {
            appendEmptyTag(output, tag);
        }
    }

    /**
     * Append to a ByteArrayOutputStream the XML element with the specified name and
     * value
     *
     * @param output The ByteArrayOutputStream where to append the XML element
     * @param tag the tag name of the XML element. must be not empty
     * @param value the value of the XML elemen
     */
    private static void appendElement(ByteArrayOutputStream output,
                                      String tag,
                                      Integer value) throws IOException {

        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("Invalid empty tag");
        }
        if (value == null) {
            value = 0;
        }

        appendStartTag(output, tag, null, null);
        output.write(String.valueOf(value).getBytes());
        appendEndTag(output, tag);
    }

    /**
     * Append to a ByteArrayOutputStream the XML element with the specified name and
     * value
     *
     * @param output The ByteArrayOutputStream where to append the XML element
     * @param tag the tag name of the XML element. must be not empty
     * @param value the value of the XML elemen
     */
    private static void appendElement(ByteArrayOutputStream output,
                                      String tag,
                                      Long value) throws IOException {

        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("Invalid empty tag");
        }
        if (value == null) {
            value = 0L;
        }

        appendStartTag(output, tag, null, null);
        output.write(String.valueOf(value).getBytes());
        appendEndTag(output, tag);
    }

    /**
     * Append to a ByteArrayOutputStream the XML element with the specified name and
     * value
     *
     * @param output The ByteArrayOutputStream where to append the XML element
     * @param tag the tag name of the XML element. must be not empty
     * @param value the value of the XML elemen
     */
    private static void appendElement(ByteArrayOutputStream output,
                                      String tag,
                                      Boolean value) throws IOException {

        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("Invalid empty tag");
        }

        if (Boolean.TRUE.equals(value)) {
            appendStartTag(output, tag, null, null);
            output.write("true".getBytes());
            appendEndTag(output, tag);
        } else {
            appendStartTag(output, tag, null, null);
            output.write("false".getBytes());
            appendEndTag(output, tag);
        }
    }

    /**
     * Append to a ByteArrayOutputStream the end tag with the specified name
     *
     * @param output The ByteArrayOutputStream where to append the end tag
     * @param tag the tag name of the XML element. must be not empty
     */
    private static void appendEndTag(ByteArrayOutputStream output, String tag) throws IOException {

        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("Invalid empty tag");
        }
        output.write('<');
        output.write('/');
        output.write(tag.getBytes());
        output.write('>');
        output.write('\n');
    }

    /**
     * Append to a ByteArrayOutputStream the empty tag with the specified name
     *
     * @param output The ByteArrayOutputStream where to append the empty tag
     * @param tag the tag name of the XML element. must be not empty
     */
    private static void appendEmptyTag(ByteArrayOutputStream output, String tag) throws IOException {
        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("Invalid empty tag");
        }
        output.write('<');
        output.write(tag.getBytes());
        output.write('/');
        output.write('>');
        output.write('\n');
    }
}
