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
 * Contributor(s): Paul Palaszewski, Michael Wallbaum, Nicola Fankhauser
 *
 * */


package org.kxml.wap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.kxml.PrefixMap;
import org.kxml.io.AbstractXmlWriter;
import org.kxml.io.State;

/**
 * A class for converting ("binary encoding") XML to WBXML.
 *  Todo:
 *  <ul>
 *  <li>Add support for processing instructions
 *  <li>Add support for WBXML extensions
 *  </ul>
 * @author Stefan Haustein, Paul Palaszewski, Michael Wallbaum, Nicola Fankhauser
 * @version $Id: WbxmlWriter.java,v 1.2 2008-01-03 09:29:32 nichele Exp $
 */
public class WbxmlWriter extends AbstractXmlWriter {

    private Hashtable stringTable = new Hashtable();

    protected OutputStream out;

    protected ByteArrayOutputStream buf = new ByteArrayOutputStream();
    private ByteArrayOutputStream stringTableBuf = new ByteArrayOutputStream();

    private String pending;
    private Vector attributes = new Vector();

    private Hashtable attrStartTable = new Hashtable();
    private Hashtable attrValueTable = new Hashtable();
    private Hashtable tagTable = new Hashtable();

    private int currentPage = 0;
    private Hashtable otherAttrStartTables = new Hashtable();
    private Hashtable otherAttrValueTables = new Hashtable();
    private Hashtable otherTagTables = new Hashtable();

    public WbxmlWriter(OutputStream out) throws IOException {

        this.out = out;

        buf = new ByteArrayOutputStream();
        stringTableBuf = new ByteArrayOutputStream();

    }


    /** ATTENTION: flush cannot work since Wbxml documents cannot
     * need buffering. Thus, this call does nothing. */

    public void flush() {
    }

    /**
     * Writes out the whole WBXML stream.
     *
     * @exception IOException if an error occurs while writing
     */
    public void close() throws IOException {
        writeHeader();
        writeInt(out, stringTableBuf.size());
        out.write(stringTableBuf.toByteArray());
        out.write(buf.toByteArray());
        out.flush(); // ready!
    }

    /**
     * Writes out WBXML headers, override for other behaviour / values
     *
     * @exception IOException if an error occurs while writing
     */
    protected void writeHeader() throws IOException {
        out.write(Wbxml.WBXML_VERSION_12  ); // version
        out.write(Wbxml.WBXML_PUBLICID    ); // public identifier
        out.write(Wbxml.WBXML_CHARSET_UTF8); // UTF-8
    }

    /**
     * Searches in all available code pages for a certain token. If it finds
     * a page, it switches to it and returns the index for the token. If no suiting page
     * is found, <code>null</code> is returned.
     *
     * @param pending the token to search for
     * @param table the table to search through for token
     * @return the index of the token or null (if not found)
     */
    private Integer searchToken(String pending, Hashtable table){
        Integer idx = null;

        for (Enumeration e = table.keys() ; e.hasMoreElements() ;) {
            Integer page = (Integer) e.nextElement();
            Hashtable h = (Hashtable) table.get(page);
            idx = (Integer) h.get(pending);
            if(idx != null){
                switchPage(page);
                break;
            }
        }

        return idx;

    }

    /**
     * Switches page, sets all arrays accordingly and writes out the WBXML <code>SWITCH_PAGE</code>
     * token
     *
     * @param page which page to switch to
     */
    private void switchPage(Integer page) {
        this.currentPage = page.intValue();
        tagTable = (Hashtable) otherTagTables.get(page);
        attrStartTable = (Hashtable) otherAttrStartTables.get(page);
        attrValueTable = (Hashtable) otherAttrValueTables.get(page);

        // make sure that after switch every token table is at least an empty hashtable, and not null
        if(tagTable == null) { tagTable = new Hashtable(); }
        if(attrStartTable == null) { attrStartTable = new Hashtable(); }
        if(attrValueTable == null) { attrValueTable = new Hashtable(); }

        buf.write(Wbxml.SWITCH_PAGE);
        buf.write(page.intValue());
    }


    public void checkPending(boolean degenerated) throws IOException {
        if (pending == null) return;

        int len = attributes.size();

        Integer idx = (Integer) tagTable.get(pending);

        if(idx == null) {
            idx = searchToken(pending, otherTagTables);
        }


        // if no entry in known table, then add as literal
        if(idx == null) {
            buf.write
            (len == 0
            ? (degenerated ? Wbxml.LITERAL : Wbxml.LITERAL_C)
            : (degenerated ? Wbxml.LITERAL_A : Wbxml.LITERAL_AC));

            writeStrT(pending);
        } else {
            buf.write
            (len == 0
            ? (degenerated ? idx.intValue() : idx.intValue() | 64 )
            : (degenerated ? idx.intValue() | 128 : idx.intValue() | 192 ));

        }

        /**
         * Search for attributte=value in the all attrStartTables. If value is found then
         *   write appropriate code
         *      else
         * search the attribute name in all attrStartTables if value is found then
         *   write appropriate code, else write the string
         *      then
         * search the attribute value in all attrStartTables if value is found then
         *   write appropriate code, else write the string
         */
        for (int i = 0; i < len;i++) {

            // search attribute=value in the actual table (for actual code page)
            idx = (Integer)attrStartTable.get(attributes.elementAt(i) + "=" +
                                              attributes.elementAt(i + 1));
            if (idx == null) {
                // search attribute=value in the other tables (for other code page)
                idx = searchToken( (String)attributes.elementAt(i) + "=" +
                                  (String)attributes.elementAt(i + 1), otherAttrStartTables);
            }

            if (idx != null) {
                // attribute=value found!
                buf.write(idx.intValue());

                i++;  // skip value in attributes
            } else {

                // search attribute in the actual table (for actual code page)
                idx = (Integer)attrStartTable.get(attributes.elementAt(i));

                if (idx == null) {
                    // search attribute in the other tables (for other code page)
                    idx = searchToken( (String)attributes.elementAt(i), otherAttrStartTables);
                }

                if (idx == null) {
                    // attribute not found in the tables

                    buf.write(Wbxml.LITERAL);
                    writeStrT( (String)attributes.elementAt(i));
                } else {
                    buf.write(idx.intValue());
                }

                // search value in the actual table (for actual code page)
                idx = (Integer)attrValueTable.get(attributes.elementAt(++i));

                if (idx == null) {
                    // search value in the other tables (for other code page)
                    idx = searchToken( (String)attributes.elementAt(i), otherAttrValueTables);
                }

                if (idx == null) {
                    // value non found in the tables
                    buf.write(Wbxml.STR_I);
                    writeStrI(buf, (String)attributes.elementAt(i));
                } else {
                    buf.write(idx.intValue());
                }
            }
        }

        if (len > 0) {
            buf.write(Wbxml.END);
        }

        pending = null;
        attributes.removeAllElements();
    }

    public void startTag(PrefixMap prefixMap,
    String name) throws IOException {

        current = new State(current, prefixMap, name);

        checkPending(false);
        pending = name;
    }


    public void attribute(String name, String value) {
        attributes.addElement(name);
        attributes.addElement(value);
    }

    public void write(byte [] bytes) throws IOException {
         write(bytes, 0, bytes.length);
     }

    public void write(byte [] bytes, int start, int len) throws IOException {
        checkPending(false);

       buf.write(Wbxml.OPAQUE);
       writeInt(buf, len);
       writeOpaque(buf, bytes, start, len);
    }


    public void write(char [] chars, int start, int len) throws IOException {
        write(chars, start, len, false);
    }

    public void write(char[] chars,
                      int start, int len, boolean opaque) throws IOException {

        checkPending(false);

        if (opaque) {
            buf.write(Wbxml.OPAQUE);

            String data = new String(chars, start, len);
            byte[] dataBytes = data.getBytes();

            writeInt(buf, dataBytes.length);
            writeOpaque(buf, dataBytes, 0, dataBytes.length);
        } else {
            buf.write(Wbxml.STR_I);
            writeStrI(buf, new String(chars, start, len));
        }
    }


    public void endTag() throws IOException {

        current = current.prev;

        if (pending != null)
            checkPending(true);
        else
            buf.write(Wbxml.END);
    }


    /** currently ignored! */

    public void writeLegacy(int type, String data) {
    }




    // ------------- internal methods --------------------------


    static void writeInt(OutputStream out, int i) throws IOException {
        byte [] buf = new byte [5];
        int idx = 0;

        do {
            buf [idx++] = (byte) (i & 0x7f);
            i = i >> 7;
        } while (i != 0);

        while (idx > 1) {
            out.write(buf [--idx] | 0x80);
        }
        out.write(buf [0]);
    }

    static void writeStrI(OutputStream out, String s)
    throws IOException {
       byte[] rawBytes = s.getBytes();
       out.write(rawBytes);

       out.write(0);
    }

    static void writeOpaque(OutputStream out, byte[] bytes, int start, int length) throws IOException {
        for (int i=start; i < length; i++) {
            out.write(bytes[i]);
        }
    }

    static void writeOpaque(OutputStream out, char[] chars, int start, int length) throws IOException {
        String data = new String(chars, start, length);
        out.write(data.getBytes());
    }

    static void writeBytes(OutputStream out, byte[] bytes, int start, int length) throws IOException {
        for (int i=start; i < length; i++) {
            out.write(bytes[i]);
        }
    }


    void writeStrT(String s) throws IOException {

        Integer idx = (Integer) stringTable.get(s);

        if (idx == null) {
            idx = new Integer(stringTableBuf.size());
            stringTable.put(s, idx);
            writeStrI(stringTableBuf, s);
            stringTableBuf.flush();
        }

        writeInt(buf, idx.intValue());
    }

    /**
     * Copies a user-given token-<code>table</code> with given <code>offset</code> into a hashtable
     * and inserts this hashtable into the <code>otherTables</code> with
     * <code>page</code> as key.
     *
     * @param table user-given array with tokens
     * @param offset where to locate the tokens regarding the conversion to WBXML
     * @param page which code page to change
     * @param otherTables where to store the newly created hashtable with the tokens (for page switches)
     * @return returns a <code>Hashtable</code> with tokens
     */
    private Hashtable processTable(String[] table, int offset, int page, Hashtable otherTables) {
        Hashtable h = new Hashtable();
        // fill up the new hashtable
        for(int i=0;i < table.length;i++) {
            if(table[i] != null) {
                Integer idx = new Integer(i+offset);
                h.put(table[i],idx);
            }
        }
        // save the new hashtable
        otherTables.put(new Integer(page),h);
        return h;
    }

    /**
     * Sets the tag table for a given page.
     *  The first string in the array defines tag 5, the second tag 6 etc.
     *
     * @param page the code page the token table refers to
     * @param tagTable a table filled with tokens
     */
    public void setTagTable(int page, String [] tagTable)  {
        Hashtable temp = processTable(tagTable, 5, page, otherTagTables);

        if(page == 0) {
            this.tagTable = temp;
        }
    }


    /**
     * Sets the attribute start Table for a given page.
     *  The first string in the array defines attribute
     *  5, the second attribute 6 etc.
     *  Please use the character '=' (without quote!) as delimiter
     *  between the attribute name and the (start of the) value
     *
     * @param page  the code page the token table refers to
     * @param attrStartTable a table filled with tokens
     */
    public void setAttrStartTable(int page, String [] attrStartTable) {
        Hashtable temp = processTable(attrStartTable, 5, page, otherAttrStartTables);

        if(page == 0) {
            this.attrStartTable = temp;
        }
    }

    /**
     * Sets the attribute value Table for a given page.
     *  The first string in the array defines attribute value 0x80,
     *  the second attribute value 0x81 etc.
     *
     * @param page  the code page the token table refers to
     * @param attrValueTable  a table filled with tokens
     */
    public void setAttrValueTable(int page, String [] attrValueTable) {
        Hashtable temp = processTable(attrValueTable, 0x80, page, otherAttrValueTables);

        if(page == 0) {
            this.attrValueTable = temp;
        }
    }
}
