/* kXML
 *
 * The contents of this file are subject to the Enhydra Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License
 * on the Enhydra web site ( http://www.enhydra.org/ ).
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific terms governing rights and limitations
 * under the License.
 *
 * The Initial Developer of kXML is Stefan Haustein. Copyright (C)
 * 2000, 2001 Stefan Haustein, D-46045 Oberhausen (Rhld.),
 * Germany. All Rights Reserved.
 *
 * Contributor(s): Nicola Fankhauser
 *
 * */

package org.kxml.wap;

import java.io.*;

/**
 * A parser for SyncML built on top of the WbxmlParser by
 * setting the corresponding TagTable defined in the class SyncML
 *
 * @author Nicola Fankhauser
 * @version $Id: SyncMLWriter.java,v 1.1.1.1 2006-10-28 22:09:32 stefano_fornari Exp $
 */
public class SyncMLWriter extends WbxmlWriter {

    // --------------------------------------------------------------- Constants

    // ------------------------------------------------------------ Private data

    private String dtd;
    private String version;

    /**
     * Calls constructor of WbxmlWriter.
     * The same as new <code>SyncMLWriter(SyncML.NS_DEFAULT, SyncML.VER_10)</code>
     * @exception IOException if an error occurs
     */
    public SyncMLWriter(String version) throws IOException {
        this(SyncML.NS_DEFAULT, version);
    }

    /**
     * Calls constructor of WbxmlWriter, sets then the appropriate tag table
     * and puts the requested DTD into string table. The DTD is choosen
     * accordingly to the given namespace.
     *
     * @param namespace the namespace that identifies the DTD to use
     * @param version the DTD version to use
     *
     * @param out an <code>OutputStream</code> value
     * @exception IOException if an error occurs
     */
    public SyncMLWriter(String namespace, String version) throws IOException {
        super(new ByteArrayOutputStream());

        // put used DTD into string table

        if (SyncML.NS_DEVINF.equals(namespace)) {
            if (SyncML.VER_12.equals(version)) {
                dtd = SyncML.DEVINF_DTD_12;
                setTagTable(0, SyncML.tagTableDevInf12);
            } else if (SyncML.VER_11.equals(version)) {
                dtd = SyncML.DEVINF_DTD_11;
                setTagTable(0, SyncML.tagTableDevInf);
            } else {
                dtd = SyncML.DEVINF_DTD_10;
                setTagTable(0, SyncML.tagTableDevInf);
            }
        } else {
            if (SyncML.VER_12.equals(version)) {
                dtd = SyncML.SYNCML_DTD_12;
            } else if (SyncML.VER_11.equals(version)) {
                dtd = SyncML.SYNCML_DTD_11;
            } else {
                dtd = SyncML.SYNCML_DTD_10;
            }
            setTagTable(0, SyncML.tagTableSyncml);
            setTagTable(1, SyncML.tagTableMetainf);
        }
    }

    /**
     * Overrides parent method because the public id needs
     * to be encoded as multi-byte integer
     * (unlike the indexed form of the public id)
     *
     * @exception IOException if an error occurs
     */
    protected void writeHeader() throws IOException {
        out.write(Wbxml.WBXML_VERSION_12); // version
        out.write(0); // FPI for DTD in string table
        out.write(0); // index into string table
        out.write(Wbxml.WBXML_CHARSET_UTF8);
    }

    /**
     * Writes a string as an OPAQUE field. An OPAQUE field is defined by WBXML
     * specification as follows:
     * <pre>
     *  opaque	= OPAQUE length *byte
     * </pre>
     * where OPAQUE is the byte 0xC3.
     *
     * @param str the string to write
     *
     * @throws IOException in case <i>super.write()</i> throws it
     */
    public void writeOpaque(String str) throws IOException {
        char[] chars = new char[(int)str.length()];

        str.getChars(0, chars.length, chars, 0);

        write(chars, 0, chars.length, true);
    }

    /**
     * Writes out the whole WBXML stream.
     *
     * @exception IOException if an error occurs while writing
     */
    public void close() throws IOException {
        writeHeader();
        writeInt(out, dtd.length());
        out.write(dtd.getBytes());
        out.write(buf.toByteArray());
        out.flush(); // ready!
    }

    /**
     * Writes the content of this writer to another writer
     *
     * @param the writer to write to
     *
     */
    public byte[] getBytes() {
        return ((ByteArrayOutputStream)out).toByteArray();
    }
}

