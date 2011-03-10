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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import java.util.Stack;
import java.util.TimeZone;

import org.kxml.Attribute;
import org.kxml.Xml;
import org.kxml.parser.ParseEvent;
import org.kxml.parser.XmlParser;

import com.funambol.common.media.file.*;
import com.funambol.common.pim.utility.TimeUtils;

/**
 * @responsibility marshall file data object XML document into a FileDataObject
 * object
 *
 * @version $Id$
 */
public class FileDataObjectXMLParser {

    // -------------------------------------------------------- Member Variables
    /**
     * The stack where the parser holds the element handlers
     */
    private Stack stack;

    /**
     * The FileDataObject resulting from the parsing
     */
    private FileDataObject fileDataObject;

    /**
     * The time zone to be used to convert local times into UTC.
     */
    private TimeZone timeZone;

    // ------------------------------------------------------------ Constructors

    /**
     * Constructor
     * @param timeZone The time zone information for date/time conversions.
     * May be null if no conversion is required (date/times already in UTC)"
     */
    public FileDataObjectXMLParser(TimeZone timeZone) {
        this.stack = new Stack();
        this.timeZone = timeZone;
    }

    // ---------------------------------------------------------- Public Methods
    /**
     * Create a FileDataObject from the XML doc
     * If the client sends an update with an empty body, it has to specify the
     * size otherwise the body is considered empty.
     * @param fileDataObjectDoc
     * @return the FileDataObject corresponding to the XML document
     * @throws com.funambol.common.media.file.parser.FileDataObjectParsingException
     */
    public FileDataObject parse(String fileDataObjectDoc)
            throws FileDataObjectParsingException {

        Reader reader = null;
        try {
            reader = new StringReader(fileDataObjectDoc);
            return parse(reader);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                // an error message like "Unable to close Reader for the
                // FileDataObjectXMLTags" should be logged but there is no logger.
            }
        }
    }

    /**
     * Create a FileDataObject from the XML doc
     * @param content
     * @return the FileDataObject corresponding to the XML document
     * @throws com.funambol.common.media.file.parser.FileDataObjectParsingException
     */
    public FileDataObject parse(byte[] content)
            throws FileDataObjectParsingException {

        Reader reader = null;
        try {
            reader = new InputStreamReader(new ByteArrayInputStream(content)/*, "US-ASCII"*/);
            return parse(reader);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
            // an error message like "Unable to close Reader for the
            // FileDataObjectXMLTags" should be logged but there is no logger.
            }
        }
    }

    /**
     * Create a FileDataObject from the XML doc
     * @param reader
     * @return the FileDataObject corresponding to the XML document
     * @throws com.funambol.common.media.file.parser.FileDataObjectParsingException
     */
    public FileDataObject parse(Reader reader)
            throws FileDataObjectParsingException {
        try {

            fileDataObject = new FileDataObject();
            stack.push(new RootHandler());

            XmlParser parser = new XmlParser(reader);
            ParseEvent event = null;
            while ((event = parser.read()).getType() != Xml.END_DOCUMENT) {
                handleEvent(event);
            }

            return fileDataObject;

        } catch (IOException ex) {
            throw new FileDataObjectParsingException("Unable to unmarshall " +
                    "file data object", ex);
        }
    }

// ------------------------------------------------------------- Private Methods

    private void handleEvent(ParseEvent event) throws FileDataObjectParsingException {

        if (event.getType() == Xml.START_TAG || event.getType() == Xml.END_TAG) {
            if (event.getName() == null) {
                throw new FileDataObjectParsingException("Empty tag name found");
            }
            if (event.getType() == Xml.START_TAG) {
                startElement(event);
            } else if (event.getType() == Xml.END_TAG) {
                endElement(event);
            }
        } else if (event.getType() == Xml.TEXT) {
            text(event);
        }
    }

    private void startElement(ParseEvent event)
            throws FileDataObjectParsingException {

        ElementHandler childHandler = currentHandler().createChild(event.getName());
        stack.push(childHandler);
        childHandler.handleStartElement(event);
    }

    private void endElement(ParseEvent event)
            throws FileDataObjectParsingException {

        ElementHandler currentHandler = currentHandler();
        currentHandler.handleEndElement(event);
        stack.pop();
    }

    private void text(ParseEvent event)
            throws FileDataObjectParsingException {

        ElementHandler currentHandler = currentHandler();
        currentHandler.handleText(event);
    }

    private ElementHandler currentHandler() {
        return (FileDataObjectXMLParser.ElementHandler) stack.peek();
    }

    // -------------------------------------------- The ElementHandler hierarchy

    abstract class ElementHandler {

        private String elementName = null;

        // ------------------------------------------------------ Public Methods

        abstract public ElementHandler createChild(String elementName)
                throws FileDataObjectParsingException;

        public void handleStartElement(ParseEvent event)
                throws FileDataObjectParsingException {
            elementName = event.getName();
        }

        abstract public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException;

        abstract public void handleText(ParseEvent event)
                throws FileDataObjectParsingException;

        public String getElementName() {
            return elementName;
        }
    }

    abstract class NotFinalElementHandler extends ElementHandler {

        @Override
        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            return;
        }

        @Override
        public void handleText(ParseEvent event)
                throws FileDataObjectParsingException {
            return;  // handleText is ignored
        }
    }

    abstract class FinalElementHandler extends ElementHandler {

        StringBuilder elemValBuilder = new StringBuilder("");;

        public ElementHandler createChild(String childElementName)
                throws FileDataObjectParsingException {
            throw new FileDataObjectParsingException("Element '" + getElementName() +
                    "': unexpected child '" + childElementName + "'");
        }

        public void handleText(ParseEvent event) {
            elemValBuilder.append(event.getText());
        }
    }

    abstract class FinalStringElementHandler extends FinalElementHandler {
        protected String getVal() {
            return elemValBuilder.toString();
        }
    }
    abstract class FinalBooleanElementHandler extends FinalElementHandler {
        protected Boolean getVal() {
            return Boolean.valueOf(elemValBuilder.toString());
        }
    }
    abstract class FinalLongElementHandler extends FinalElementHandler {

        protected Long getVal() {
            Long val;
            try {
                val = Long.valueOf(elemValBuilder.toString());
            } catch (NumberFormatException ex) {
                val = null;
            }
            return val;
        }
    }
    abstract class FinalIntegerElementHandler extends FinalElementHandler {

        protected Integer getVal() {
            Integer val;
            try {
                val = Integer.valueOf(elemValBuilder.toString());
            } catch (NumberFormatException ex) {
                val = null;
            }
            return val;
        }
    }

    // -------------------------------------------------------------------------

    class RootHandler extends NotFinalElementHandler {

        public ElementHandler createChild(String childElementName)
                throws FileDataObjectParsingException {

            ElementHandler childHandler;

            if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_FILE)) {
                childHandler = new FileHandler();
            } else {
                throw new FileDataObjectParsingException(
                        FileDataObjectParsingException.UNEXPECTED_ROOT_ELEMENT +
                        " '" + childElementName + "'");
            }
            return childHandler;
        }

        @Override
        public void handleText(ParseEvent event)
                throws FileDataObjectParsingException {
            throw new FileDataObjectParsingException(
                    FileDataObjectParsingException.MISSING_START_ELEMENT);
        }
    }

    class FileHandler extends NotFinalElementHandler {

        public ElementHandler createChild(String childElementName)
                throws FileDataObjectParsingException {

            ElementHandler childHandler;

            if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_NAME)) {
                childHandler = new NameHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_CREATED)) {
                childHandler = new CreatedHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_MODIFIED)) {
                childHandler = new ModifiedHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_ACCESSED)) {
                childHandler = new AccessedHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_ATTRIBUTES)) {
                childHandler = new AttributesHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_CTTYPE)) {
                childHandler = new ContentTypeHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_BODY)) {
                childHandler = new BodyHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_SIZE)) {
                childHandler = new SizeHandler();
            } else {
                throw new FileDataObjectParsingException("Element '" + getElementName() +
                        "': unexpected child '" + childElementName + "'");
            }
            return childHandler;
        }
    }

    class NameHandler extends FinalStringElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            fileDataObject.setName(getVal());
        }
    }

    class CreatedHandler extends FinalStringElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            try {
                String created = TimeUtils.convertLocalDateToUTC(getVal(), timeZone);
                fileDataObject.setCreated(created);
            } catch (Exception e) {
                throw new FileDataObjectParsingException(e);
            }
        }
    }

    class ModifiedHandler extends FinalStringElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            try {
                String modified = TimeUtils.convertLocalDateToUTC(getVal(), timeZone);
                fileDataObject.setModified(modified);
            } catch (Exception e) {
                throw new FileDataObjectParsingException(e);
            }
        }
    }

    class AccessedHandler extends FinalStringElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            try {
                String accessed = TimeUtils.convertLocalDateToUTC(getVal(), timeZone);
                fileDataObject.setAccessed(accessed);
            } catch (Exception e) {
                throw new FileDataObjectParsingException(e);
            }
        }
    }

    class AttributesHandler extends NotFinalElementHandler {

        public ElementHandler createChild(String childElementName)
                throws FileDataObjectParsingException {

            ElementHandler childHandler;

            if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_HIDDEN)) {
                childHandler = new HiddenHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_SYSTEM)) {
                childHandler = new SystemHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_ARCHIVED)) {
                childHandler = new ArchivedHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_DELETED)) {
                childHandler = new DeletedHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_WRITABLE)) {
                childHandler = new WritableHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_READABLE)) {
                childHandler = new ReadableHandler();
            } else if (childElementName.equalsIgnoreCase(FileDataObjectXMLTags.TAG_EXECUTABLE)) {
                childHandler = new ExecutableHandler();
            } else {
                throw new FileDataObjectParsingException("Element '" + childElementName +
                        "': Unexpected child '" + childElementName + "'");
            }
            return childHandler;
        }
    }

    class HiddenHandler extends FinalBooleanElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            fileDataObject.setHidden(getVal());
        }
    }

    class SystemHandler extends FinalBooleanElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            fileDataObject.setSystem(getVal());
        }
    }

    class ArchivedHandler extends FinalBooleanElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            fileDataObject.setArchived(getVal());
        }
    }

    class DeletedHandler extends FinalBooleanElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            fileDataObject.setDeleted(getVal());
        }
    }

    class WritableHandler extends FinalBooleanElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            fileDataObject.setWritable(getVal());
        }
    }

    class ReadableHandler extends FinalBooleanElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            fileDataObject.setReadable(getVal());
        }
    }

    class ExecutableHandler extends FinalBooleanElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            fileDataObject.setExecutable(getVal());
        }
    }

    class ContentTypeHandler extends FinalStringElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            fileDataObject.setContentType(getVal());
        }
    }

    class BodyHandler extends FinalElementHandler {

        /**
         * The mechanism used to encode the content of the body element.
         */
        protected String bodyEncoding = "";

        @Override
        public void handleStartElement(ParseEvent event)
                throws FileDataObjectParsingException {
            super.handleStartElement(event);
            setBodyEncoding(event);
        }

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            //
            // Ignoring body....it should be streamed by the unmarshaller
            //
        }

        /**
         * @param event The start body tag event
         * @prerequisit event.getName() equals FileDataObjectXMLTags.TAG_BODY
         * @prerequisit stack.peek() instanceof FileDataObject
         */
        private void setBodyEncoding(ParseEvent event) {
            Attribute encAttr = event.getAttribute(FileDataObjectXMLTags.ATTR_ENC);
            if (encAttr != null) {
                bodyEncoding = encAttr.getValue();
            }
        }
    }

    class SizeHandler extends FinalLongElementHandler {

        public void handleEndElement(ParseEvent event)
                throws FileDataObjectParsingException {
            fileDataObject.setSize(getVal());
        }
    }
}
