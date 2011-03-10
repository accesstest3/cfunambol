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
package com.funambol.framework.engine.transformer;

import java.util.HashMap;
import java.util.Map;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * A DataTransformer is a component that given some data in any format
 * as input, it generates a transformation of the source data in output.
 * Examples of transformations are:
 *
 * - base64 encoding/decoding
 * - encryption/decription
 */
public class DataTransformerManager {

    // ------------------------------------------------------------ Private data
    /**
     * The logger to use
     */
    private static final FunambolLogger log = FunambolLoggerFactory.getLogger("engine");

    /**
     * Contains the source uri and the relative transformations required
     */
    private Map sourceUriTrasformationsRequired;

    /**
     * Contains the transformer configured for the incoming items
     */
    private Map dataTransformersIn;

    /**
     * Contains the transformer configured for the outgoing items
     */
    private Map dataTransformersOut;

    // ------------------------------------------------------------ Constructors


    /** Creates a new instance of DataTransformerManager */
    public DataTransformerManager() {
        sourceUriTrasformationsRequired = new HashMap();
        dataTransformersIn  = new HashMap();
        dataTransformersOut = new HashMap();
    }

    // ---------------------------------------------------------- Public methods

    public Map getSourceUriTrasformationsRequired() {
        return sourceUriTrasformationsRequired;
    }

    public void setSourceUriTrasformationsRequired(Map sourceUriTrasformationsRequired) {
        this.sourceUriTrasformationsRequired = sourceUriTrasformationsRequired;
    }

    public Map getDataTransformersIn() {
        return dataTransformersIn;
    }


    public void setDataTransformersIn(Map dataTransformersIn) {
        this.dataTransformersIn = dataTransformersIn;
    }


    public Map getDataTransformersOut() {
        return dataTransformersOut;
    }


    public void setDataTransformersOut(Map dataTransformersOut) {
        this.dataTransformersOut = dataTransformersOut;
    }


    /**
     * Applies on the given item the transformations required based on its format
     * using the data contained in the given info.
     * @param info the transformation info to use
     * @param item the item to convert
     * @return the item converted
     * @throws TransformerException if some errors occur
     */
    public SyncItem transformIncomingItem(TransformationInfo info, SyncItem item)
    throws TransformerException {

        String format = item.getFormat();
        if (format == null) {
            //
            // No transformations are applicable
            //
            return item;
        }

        // textplain;des;b64
        
        String[]        tokens      = format.split(";");
        int             numToken    = tokens.length;
        DataTransformer dt          = null;
        byte[]          content     = null;
        for (int i=numToken-1; i>=0; i--) {

            if (log.isTraceEnabled()) {
                log.trace("Applying transformation: '" + tokens[i] + "'");
            }

            dt = getDataTransformerIn(tokens[i]);

            if (dt != null) {
                //
                // item.getContent() is called just if needed since otherwise, with StreamingItem,
                // we could waste a lot of memory
                //
                content = item.getContent();
                content = dt.transform(content, info);
                item.setContent(content);

                int tokenIndex = format.lastIndexOf(tokens[i]);
                format = format.substring(0, tokenIndex);
            } else {
                //
                // Found a format with no data transformer
                //
                if (log.isTraceEnabled()) {
                    log.trace("DataTransformer for '" + tokens[i] +
                              "' for incoming items not found. Stopping data transformation");
                }
                break;
            }
        }

        if (format.length() > 1 && format.charAt(0) == ';') {
            format = format.substring(1, format.length());
        }

        item.setFormat(format);

        return item;
    }

    /**
     * Applies on the given item the transformations required based on its source uri
     * using the data contained in the given info.
     * @param info the transformation info to use
     * @param item the item to convert
     * @return the item converted
     * @throws TransformerException if some errors occur
     */
    public SyncItem transformOutgoingItem(TransformationInfo info, SyncItem item)
    throws TransformerException {

        String format = item.getFormat();
        String uri    = item.getSyncSource().getSourceURI();

        String transformerNames = (String)sourceUriTrasformationsRequired.get(uri);
        if (transformerNames == null) {
            //
            // no transformations are required
            //
            return item;
        }

        //
        // We have to apply the transformes specifies in the transformerNames
        // in inverse order
        //
        if (log.isTraceEnabled()) {
            log.trace("Applying transformations '" + transformerNames + "'");
        }

        // textplain;des;b64
        if (format == null) {
            format = "";
        }
        StringBuffer    sb       = new StringBuffer(format);
        byte[]          content  = item.getContent();
        String[]        tokens   = transformerNames.split(";");
        int             numToken = tokens.length;
        DataTransformer dt       = null;
        for (int i=0; i<numToken; i++) {

            dt = getDataTransformerOut(tokens[i]);

            if (dt != null) {
                content = dt.transform(content, info);
                if (sb.length() != 0) {
                    sb.append(";");
                }
                sb.append(tokens[i]);

            } else {
                throw new TransformerException("DataTransformer for '" + tokens[i] +
                                               "' for outgoing item not found");
            }
        }
        item.setContent(content);
        item.setFormat(sb.toString());

        return item;
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Returns the DataTransformer given the name for incoming items
     *
     * @return the data transformer
     * @param name the transformer name
     * @throws com.funambol.framework.engine.transformer.TransformerException
     */
    private DataTransformer getDataTransformerIn(String name)
    throws TransformerException {
        String          dtClass = (String)dataTransformersIn.get(name);
        DataTransformer dt      = null;

        if (dtClass != null) {
            try {
                dt = (DataTransformer) Class.forName(dtClass).newInstance();
            } catch (Exception ex) {
                log.error("Error getting a new instance of " + dtClass, ex);
                throw new TransformerException("Unable to create an instance of '" + dtClass + "'");
            }
        }
        return dt;
    }


    /**
     * Returns the DataTransformer given the name for outgoing items
     * @param name the transformer name
     * @return the data transformer
     * @throws if some errors occur
     */
    private DataTransformer getDataTransformerOut(String name)
    throws TransformerException {
        String          dtClass = (String)dataTransformersOut.get(name);
        DataTransformer dt      = null;

        if (dtClass != null) {
            try {
                dt = (DataTransformer) Class.forName(dtClass).newInstance();
            } catch (Exception ex) {
                log.error("Error getting a new instance of " + dtClass, ex);
                throw new TransformerException("Unable to create an instance of '" + dtClass + "'");
            }
        }
        return dt;
    }
}
