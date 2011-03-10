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

package com.funambol.framework.core;

import java.util.ArrayList;
import java.util.Iterator;

import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

/**
 * This is a JiBX unmarshaller for DevInf V1 's CTCap. The way CTCap is
 * defined in SyncML DevInf v1.1 cannot be supported by a normal JiBX binding.
 * This unmarshaller/marshaller parses the XML provided by JiBX into or from
 * a CTCap object.
 *
 * @version $Id: CTCapV1Handler.java,v 1.2 2007/07/30 22:10:07 stefano_fornari Exp $
 */
public class CTCapV1Handler
implements IUnmarshaller, IMarshaller, IAliasable {

    //---------------------------------------------------------------- Constants

    public static final String TAG_CTTYPE      = "CTType"     ;
    public static final String TAG_PROPNAME    = "PropName"   ;
    public static final String TAG_VALENUM     = "ValEnum"    ;
    public static final String TAG_DATATYPE    = "DataType"   ;
    public static final String TAG_SIZE        = "Size"       ;
    public static final String TAG_DISPLAYNAME = "DisplayName";
    public static final String TAG_PARAMNAME   = "ParamName"  ;

    // ------------------------------------------------------------------ Fields

    /** Root element namespace URI. */
    private final String uri;

    /** Namespace URI index in binding. */
    private final int index;

    /** Root element name. */
    private final String name;

    /**
     * Default constructor.
     */
    public CTCapV1Handler() {
        uri = null;
        index = -1;
        name = null;
    }

    /**
     * Aliased constructor. This takes a name definition for the element. It'll
     * be used by JiBX when a name is supplied by the mapping which references
     * this custom marshaller/unmarshaller.
     *
     * @param uri namespace URI for the top-level element
     * @param index namespace index corresponding to the defined URI within the
     * marshalling context definitions
     * @param name local name for the top-level element
     */
    public CTCapV1Handler(String uri, int index, String name) {
        // save the simple values
        this.uri = uri;
        this.index = index;
        this.name = name;
    }


    /**
     * Unmarshalling the given object in the given context
     *
     * @param ctx - unmarshalling context
     *
     * @return true if expected parse data found, false if not
     *
     * @throws JiBXException - on error in unmarshalling process
     *
     * @see IUnmarshaller
     */
    public Object unmarshal(Object obj, IUnmarshallingContext ictx)
    throws JiBXException {
        // make sure we're at the appropriate start tag
        UnmarshallingContext ctx = (UnmarshallingContext)ictx;
        if (!ctx.isAt(uri, name)) {
            ctx.throwStartTagNameError(uri, name);
        }

        //
        // discarding CTCap
        //
        ctx.parsePastStartTag(uri, name);
        ctx.next();

        ArrayList caps = new ArrayList();

        while (ctx.isAt(uri, TAG_CTTYPE)) {
            //
            // Parsing the CTType elements
            //
            caps.add(parseCTType(ctx));
        }

        ctx.parsePastEndTag(uri, name);

        return new CTCapV1(caps);
    }

    /**
     * Check if instance present in XML.
     *
     * @param ctx - unmarshalling context
     * @return true if expected parse data found, false if not
     * @throws JiBXException - on error in unmarshalling process
     * @see IUnmarshaller
     */
    public boolean isPresent(IUnmarshallingContext ctx)
    throws JiBXException {
         return ctx.isAt(uri, name);
    }

    /**
     * Unmarshalling the given object in the given context
     *
     * @param ctx - marshalling context
     *
     * @throws JiBXException - on error in unmarshalling process
     *
     * @see IUnmarshaller
     */
    public void marshal(Object obj, IMarshallingContext ictx)
    throws JiBXException {
        // make sure the parameters are as expected
        if (!(obj instanceof CTCapV1)) {
            throw new JiBXException("Invalid object type for marshaller");
        }

        ArrayList list = null; // used later

        // start by generating start tag for container
        MarshallingContext ctx = (MarshallingContext)ictx;
        CTCapV1 cap = (CTCapV1)obj;
        ctx.startTag(index, name);

        //
        // loop through all CTCap's CTTypes
        //
        list = cap.getCTTypes();

        if (list == null) {
            return;
        }

        Iterator i1 = list.iterator();
        while (i1.hasNext()) {
            CTType t = (CTType)i1.next();

            //
            // CTType
            //
            ctx.startTag(index, TAG_CTTYPE)
               .content(t.getCTType())
               .endTag(index, TAG_CTTYPE);

            //
            // CTProperties
            //
            list = t.getCTProperties();
            if (list != null) {
                Iterator i2 = list.iterator();
                while (i2.hasNext()) {
                    CTProperty p = (CTProperty)i2.next();

                    marshalParameter(ctx, p); // marhsalling the property itself

                    //
                    // marshalling all parameter
                    //
                    list = p.getParameters();
                    if (list == null) {
                        continue;
                    }
                    Iterator i3 = list.iterator();
                    while (i3.hasNext()) {
                        marshalParameter(ctx, (CTParameter)i3.next());
                    }
                }
            }
        }

        // finish with end tag for container element
        ctx.endTag(index, name);
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IMarshaller#isExtension(int)
     */
    public boolean isExtension(int index) {
        return false;
    }

    // --------------------------------------------------------- Private methods

    /**
     * Parses the given XML accordingly to the following DTD:
     *
     * (CTType, (PropName, (ValEnum+ | (DataType, Size?))?,DisplayName?,
     *  (ParamName, (ValEnum+ | (DataType, Size?))?,DisplayName?)*)+)+
     *
     * @param ctx - the unmarshalling context
     *
     * @return the created CTType
     *
     * @throws JiBXException in the case of parsing errors
     */
    private CTType parseCTType(UnmarshallingContext ctx)
    throws JiBXException {
        String type = null;
        ArrayList props = null;

        //
        // CTType
        //
        if (ctx.isAt(uri, TAG_CTTYPE)) {
            ctx.parsePastStartTag(uri, TAG_CTTYPE);
            type = ctx.parseContentText();
            ctx.parsePastEndTag(uri, TAG_CTTYPE); ctx.next();
        } else {
            throw new JiBXException("Expected <" + TAG_CTTYPE + "> found <" + ctx.getName() + ">");
        }

        //
        // (PropName, (ValEnum+ | (DataType, Size?))?,DisplayName?,
        //  (ParamName, (ValEnum+ | (DataType, Size?))?,DisplayName?)*)
        //
        props = parseProperties(ctx);

        return new CTType(type, props);
    }

    /**
     * Parses the given XML accordingly to the following DTD:
     *
     * PropName, (ValEnum+ | (DataType, Size?))?, DisplayName?,
     *  (ParamName, (ValEnum+ | (DataType, Size?))?,DisplayName?)*
     *
     * @param ctx - the unmarshalling context
     *
     * @return an ArrayList of CTPropParam
     *
     * @throws JiBXException in the case of parsing errors
     */
    private ArrayList parseProperties(UnmarshallingContext ctx)
    throws JiBXException {
        ArrayList properties = new ArrayList();

        while(ctx.isAt(uri, TAG_PROPNAME)) {
            //
            // PropName
            //
            ctx.parsePastStartTag(uri, TAG_PROPNAME);
            CTProperty property = new CTProperty(ctx.parseContentText());
            ctx.parsePastEndTag(uri, TAG_PROPNAME); ctx.next();

            //
            // (ValEnum+ | (DataType, Size?))?
            //
            parseValuesOrType(ctx, property);

            //
            // DisplayName
            //
            if (ctx.isAt(uri, TAG_DISPLAYNAME)) {
                ctx.parsePastStartTag(uri, TAG_DISPLAYNAME);
                property.setDisplayName(ctx.parseContentText());
                ctx.parsePastEndTag(uri, TAG_DISPLAYNAME); ctx.next();
            }

            //
            // (ParamName, (ValEnum+ | (DataType, Size?))?,DisplayName?)*
            //
            property.setParameters(parseParameters(ctx));

            properties.add(property);
        }

        return properties;
    }

    /**
     * Parses the given XML accordingly to the following DTD:
     *
     * (ValEnum+ | (DataType, Size?))?
     *
     * @param ctx - the unmarshalling context
     * @param p - the CTParameter to fill
     *
     * @throws JiBXException in the case of parsing errors
     */
    private void parseValuesOrType(UnmarshallingContext ctx, CTParameter p)
    throws JiBXException {
        if (ctx.isAt(uri, TAG_VALENUM)) {
            do {
                //
                // ValEnum
                //
                ctx.parsePastStartTag(uri, TAG_VALENUM);
                p.addValue(ctx.parseContentText());
                ctx.parsePastEndTag(uri, TAG_VALENUM); ctx.next();
            } while (ctx.isAt(uri, TAG_VALENUM));
        } else if (ctx.isAt(uri, TAG_DATATYPE)) {
            //
            // TAG_DATATYPE
            //
            ctx.parsePastStartTag(uri, TAG_DATATYPE);
            p.setDataType(ctx.parseContentText());
            ctx.parsePastEndTag(uri, TAG_DATATYPE); ctx.next();

            //
            // Size
            //
            if (ctx.isAt(uri, TAG_SIZE)) {
                ctx.parsePastStartTag(uri, TAG_SIZE);
                p.setSize(Long.parseLong(ctx.parseContentText()));
                ctx.parsePastEndTag(uri, TAG_SIZE); ctx.next();
            }
        } else if (ctx.isAt(uri, TAG_SIZE)) {
            //
            // This case is not expected by the specs since Size should be
            // preceded by DataType but some phones (i.e. SE W910) send it without
            // DataType.
            // See bug #307939 on objectweb
            //
            ctx.parsePastStartTag(uri, TAG_SIZE);
            p.setSize(Long.parseLong(ctx.parseContentText()));
            ctx.parsePastEndTag(uri, TAG_SIZE); ctx.next();
        }
    }

    /**
     * Parses the given XML accordingly to the following DTD:
     *
     * (ParamName, (ValEnum+ | (DataType, Size?))?,DisplayName?)*
     *
     * @param ctx - the unmarshalling context
     *
     * @return an ArrayList of CTParameter
     *
     * @throws JiBXException in the case of parsing errors
     */
    private ArrayList parseParameters(UnmarshallingContext ctx)
    throws JiBXException {
        ArrayList parameters = new ArrayList();

        CTParameter parameter = null;
        while (ctx.isAt(uri, TAG_PARAMNAME)) {
            //
            // ParamName
            //
            ctx.parsePastStartTag(uri, TAG_PARAMNAME);
            parameter = new CTParameter(ctx.parseContentText());
            ctx.parsePastEndTag(uri, TAG_PARAMNAME); ctx.next();

            //
            // (ValEnum+ | (DataType, Size?))?
            //
            parseValuesOrType(ctx, parameter);

            //
            // DisplayName
            //
            if (ctx.isAt(uri, TAG_DISPLAYNAME)) {
                ctx.parsePastStartTag(uri, TAG_DISPLAYNAME);
                parameter.setDisplayName(ctx.parseContentText());
                ctx.parsePastEndTag(uri, TAG_DISPLAYNAME); ctx.next();
            }

            parameters.add(parameter);
        }

        return parameters;
    }

    /**
     * Marshals the given CTParameter
     *
     * @param ctx - the marshalling context
     * @param p - the parameter to marshal
     *
     * @throws JiBXException
     */
    private void marshalParameter(MarshallingContext ctx, CTParameter p)
    throws JiBXException {
        String tagName = (p instanceof CTProperty)
                       ? TAG_PROPNAME
                       : TAG_PARAMNAME
                       ;

        ctx.startTag(index, tagName)
           .content(p.getName())
           .endTag(index, tagName);

        if (p.getDataType() != null) {
            ctx.startTag(index, TAG_DATATYPE)
               .content(p.getDataType())
               .endTag(index, TAG_DATATYPE);

            if (p.getSize() != null) {
                ctx.startTag(index, TAG_SIZE)
                   .content(String.valueOf(p.getSize()))
                   .endTag(index, TAG_SIZE);
            }
        } else {
            ArrayList list = p.getValEnum();
            if (list == null) {
                return;
            }
            Iterator i = list.iterator();
            while (i.hasNext()) {
                ctx.startTag(index, TAG_VALENUM)
                   .content(String.valueOf(i.next()))
                   .endTag(index, TAG_VALENUM);
            }
        }

        if (p.getDisplayName() != null) {
            ctx.startTag(index, TAG_DISPLAYNAME)
               .content(p.getDisplayName())
               .endTag(index, TAG_DISPLAYNAME);
        }
    }

}
