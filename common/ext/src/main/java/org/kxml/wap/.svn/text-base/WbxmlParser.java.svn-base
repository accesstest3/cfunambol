// (c) 2001 Stefan Haustein
// Contributors: Bjorn Aadland

package org.kxml.wap;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.kxml.Attribute;
import org.kxml.Xml;
import org.kxml.io.ParseException;
import org.kxml.parser.AbstractXmlParser;
import org.kxml.parser.ParseEvent;
import org.kxml.parser.StartTag;
import org.kxml.parser.Tag;

/** Still Todo:
 * <ul>
 * <li>implement Processing Instructions</li>
 * <li>implement support for more than one codepages</li>
 * </ul>
 */

public class WbxmlParser extends AbstractXmlParser {

    InputStream in;

    // all known code pages and their tables
    private Hashtable otherAttrStartTables = new Hashtable();
    private Hashtable otherAttrValueTables = new Hashtable();
    private Hashtable otherTagTables = new Hashtable();

    private String[] attrStartTable;
    private String[] attrValueTable;
    private String[] tagTable;
    private String stringTable;

    private int version;
    private int publicIdentifierId;
    private String publicIdentifier=null;
    private String charset;


    private StartTag current;
    private ParseEvent next;
    private boolean whitespace;

    private PublicIDEntry documentPubID=null;
    private PublicIDEntry currentPubID=null;

    public WbxmlParser(InputStream in) throws Exception {
        this(in, -1, null);
    }


    public WbxmlParser(InputStream in, int expectedVersion) throws Exception {
        this(in, expectedVersion, null);
    }


    public WbxmlParser(InputStream in, String charset) throws Exception {
        this(in, -1, charset);
    }

    /**
     * The charset sensitive charset Wbxml parser constructor.
     * The charset is handled with the following priority:
     * 1) The charset defined in the mimetype.
     * 2) The charset defined in the wbxml itself, which is
     *    a IANA defined charset number.
     * 3) UTF-8.
     * @param in The inputstream to be parsed
     * @param expectedVersion The version of wbxml to be parsed.
     * @param charset The charset as provided with the mimetype, if it
     * was not defined the value is 'null'.
     */
    public WbxmlParser(InputStream in, int expectedVersion, String charset) throws Exception {
        
        int publicIdentifierOffset=-1;

        this.in = in;

        version = readByte();
        if ((expectedVersion >= 0) && (version != expectedVersion)) {
            throw new VersionMismatchException();
        }
        publicIdentifierId = readInt();

        if (publicIdentifierId == 0)
            publicIdentifierOffset=readInt();

        if (charset != null) {
            if (!Charset.isSupported(charset)) {
                throw new UnsupportedCharsetException(charset + " is not supported");
            }
            this.charset = charset;
            readInt(); // If charset is already known, this must be ignored.
        } else {
            switch (readInt()) { // charset 
            case Wbxml.WBXML_CHARSET_ISO_8859_1:
                this.charset = "ISO-8859-1";
                break;
            case Wbxml.WBXML_CHARSET_UTF8:
            case Wbxml.WBXML_CHARSET_NOTSPECIFIED: // Also UTF-8.
            default: // also to UTF-8, system default may cuase problems.
                this.charset = "UTF-8";
                break;
            }
        }

        int strTabSize = readInt();

        StringBuffer buf = new StringBuffer(strTabSize);

        for (int i = 0; i < strTabSize; i++)
            buf.append((char)readByte());

        stringTable = buf.toString();

        // initialisation of tables and public identifier objects
        WbxmlInitialiser initialiser=null;
        if(publicIdentifierId!=0) {
            initialiser=WbxmlInitialiserFactory.getInitialiserByPublicIdentifierCode(publicIdentifierId);
        }
        else {
            if(publicIdentifierOffset>=0) {
                extractInlinePublicID(publicIdentifierOffset);
                if (publicIdentifier!=null) {
                    initialiser=WbxmlInitialiserFactory.getInitialiserByPublicIdentifier(publicIdentifier);
                }
            }
        }
        if(initialiser!=null) {
            initialiser.initialise(this);
        }

        // load the public identifier
        if(publicIdentifier==null) {
            documentPubID=getPublicIDEntryByCode(publicIdentifierId);
        }
        else {
            documentPubID=getPublicIDEntry(publicIdentifier);
        }

    }

    private void extractInlinePublicID(int anOffset) {
        publicIdentifier=this.extractFromStrT(anOffset);
    }

    public ParseEvent peek() throws IOException {

        String s;

        if (next != null)
            return next;

        if (current != null && current.getDegenerated()) {
            next = new Tag
            (Xml.END_TAG, current,
            current.getNamespace(),
            current.getName());

            current = current.getParent();
            return next;
        }

        ParseEvent result = null;

        do {
            int id = in.read();
            switch (id) {
                case -1:
                    if (current != null)
                        throw new RuntimeException("unclosed elements: " + current);

                    next = new ParseEvent(Xml.END_DOCUMENT, null);
                    break;

                case Wbxml.SWITCH_PAGE:
                    int page = readByte();
                    switchPage(page);
                    break;

                case Wbxml.END:
                    next = new Tag(Xml.END_TAG, current,
                    current.getNamespace(),
                    current.getName());

                    current = current.getParent();
                    break;

                case Wbxml.ENTITY:
                    next = new ParseEvent(Xml.TEXT, "" + (char)readInt());
                    break;

                case Wbxml.STR_I: {
                    s = readStrI();
                    next = new ParseEvent(whitespace ? Xml.WHITESPACE : Xml.TEXT, s);
                    break;
                }

                case Wbxml.EXT_I_0:
                case Wbxml.EXT_I_1:
                case Wbxml.EXT_I_2:
                case Wbxml.EXT_T_0:
                case Wbxml.EXT_T_1:
                case Wbxml.EXT_T_2:
                case Wbxml.EXT_0:
                case Wbxml.EXT_1:
                case Wbxml.EXT_2:
                case Wbxml.OPAQUE:
                    next = parseWapExtension(id);
                    break;

                case Wbxml.PI:
                    throw new RuntimeException("PI curr. not supp.");
                    // readPI;
                    // break;

                case Wbxml.STR_T: {
                    int pos = readInt();
                    int end = stringTable.indexOf('\0', pos);
                    next = new ParseEvent
                    (Xml.TEXT, stringTable.substring(pos, end));
                    break;
                }

                default:
                    next = parseElement(id);
            }
        }
        while (next == null);

        return next;
    }

    public ParseEvent read() throws IOException {
        if (next == null)
            peek();
        ParseEvent result = next;
        next = null;
        return result;
    }


    /**
     * Switches to code page <code>page</code>
     *
     * @param page the page to switch to
     */
    private void switchPage(int page){
        Integer ipage = new Integer(page);
        attrStartTable = (String[]) otherAttrStartTables.get(ipage);
        attrValueTable = (String[]) otherAttrValueTables.get(ipage);
        tagTable = (String[]) otherTagTables.get(ipage);

        currentPubID=getPublicIDEntryByIndex(page);
    }

    /** For handling wap extensions in attributes, overwrite this
    method, call super and return a corresponding TextEvent. */

    public ParseEvent parseWapExtension(int id) throws IOException {

        switch (id) {
            case Wbxml.EXT_I_0:
            case Wbxml.EXT_I_1:
            case Wbxml.EXT_I_2:
                return new WapExtensionEvent(id, readStrI());

            case Wbxml.EXT_T_0:
            case Wbxml.EXT_T_1:
            case Wbxml.EXT_T_2:
                return new WapExtensionEvent(id, new Integer(readInt()));

            case Wbxml.EXT_0:
            case Wbxml.EXT_1:
            case Wbxml.EXT_2:
                return new WapExtensionEvent(id, null);

            case Wbxml.OPAQUE: {
                int len = readInt();
                byte[] buf = new byte[len];

                for (int i = 0; i < len; i++)   // enhance with blockread!
                    buf[i] = (byte)readByte();

                return new WapExtensionEvent(id, buf);
            } // case OPAQUE
        } // SWITCH

        throw new IOException("illegal id!");
    }

    public Vector readAttr() throws IOException {

        Vector result = new Vector();

        int id = readByte();

        while (id != 1) {

            if (id == Wbxml.SWITCH_PAGE) {
                int page = readByte();
                switchPage(page);
                id = readByte();
                continue;
            }


            String name = resolveId(attrStartTable, id, 0x05);

            StringBuffer value;

            int cut = name.indexOf('=');

            if (cut == -1)
                value = new StringBuffer();
            else {
                value = new StringBuffer(name.substring(cut + 1));
                name = name.substring(0, cut);
            }

            id = readByte();
            while (id > 128 || id == Wbxml.ENTITY || id == Wbxml.SWITCH_PAGE
            || id == Wbxml.STR_I || id == Wbxml.STR_T
            || (id >= Wbxml.EXT_I_0 && id <= Wbxml.EXT_I_2)
            || (id >= Wbxml.EXT_T_0 && id <= Wbxml.EXT_T_2)) {

                switch (id) {

                    case Wbxml.SWITCH_PAGE:
                        int page = readByte();
                        switchPage(page);
                        id = readByte();
                        break;

                    case Wbxml.ENTITY:
                        value.append((char)readInt());
                        break;

                    case Wbxml.STR_I:
                        value.append(readStrI());
                        break;

                    case Wbxml.EXT_I_0:
                    case Wbxml.EXT_I_1:
                    case Wbxml.EXT_I_2:
                    case Wbxml.EXT_T_0:
                    case Wbxml.EXT_T_1:
                    case Wbxml.EXT_T_2:
                    case Wbxml.EXT_0:
                    case Wbxml.EXT_1:
                    case Wbxml.EXT_2:
                    case Wbxml.OPAQUE:

                        ParseEvent e = parseWapExtension(id);
                        if (!(e.getType() != Xml.TEXT
                        && e.getType() != Xml.WHITESPACE))
                            throw new RuntimeException("parse WapExtension must return Text Event in order to work inside Attributes!");

                        value.append(e.getText());

                        break;

                    case Wbxml.STR_T:
                        value.append(readStrT());
                        break;

                    default:
                        value.append(resolveId(attrValueTable, id, 0));
                }

                id = readByte();
            }

            result.addElement(new Attribute(null, name, value.toString()));
        }

        return result;
    }

    String resolveId(String[] tab, int id, int offset) throws IOException {

        int idx = (id & 0x07f) - offset;
        if (idx == -1)
            return readStrT();

        if (idx < 0 || tab == null
        || idx >= tab.length || tab[idx] == null)
            throw new IOException("id " + id + " undef.");

        return tab[idx];
    }

    StartTag parseElement(int id) throws IOException {

        String tag = resolveId(tagTable, id & 0x03f, 0x05);

        String ns = null;

        Vector attributes = null;

        // load the attributes if appropriate
        if((id & 128) != 0) {
            attributes=readAttr();
        }

        // now pickup the current namespace
        if(documentPubID!=null) {
            if(documentPubID!=currentPubID) {
                if(currentPubID==null) { currentPubID=documentPubID; }
                ns=currentPubID.getNameSpace();
            }
        }

        if(ns!=null) {
            if(attributes==null) {
                attributes=new Vector();
            }
            int i=0;
            Attribute attr=null;
            for(i=0;i<attributes.size();i++) {
                attr = (Attribute)attributes.elementAt(i);
                if(attr.getName().equals("xmlns")) {
                    break;
                }
            }

            // if i less than the length of the Vector than we've
            // found a namespace
            if(i<attributes.size()) {
                attributes.remove(i);
                attributes.insertElementAt(new Attribute(null, "xmlns", ns),i);
            }
            else {
                attributes.insertElementAt(new Attribute(null, "xmlns", ns),0);
            }

        }

        // ok, now let's care about attrs etc

        try {
            current = new StartTag
            (current, // previous
            ns, // namespace
            tag, // name
            (Vector)attributes, // attributes
            (id & 64) == 0, // degenerated
            processNamespaces);  // processing
        }
        catch (Exception e) {
            throw new ParseException(null, e, -1, -1);
        }

        return current;
    }

    int readByte() throws IOException {
        int i = in.read();
        if (i == -1)
            throw new IOException("Unexpected EOF");
        return i;
    }

    int readInt() throws IOException {
        int result = 0;
        int i;

        do {
            i = readByte();
            result = (result << 7) | (i & 0x7f);
        }
        while ((i & 0x80) != 0);

        return result;
    }

    String readStrI() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        boolean wsp = true;
        while (true) {
            int i = in.read();
            if (i == -1)
                throw new IOException("Unexpected EOF");
            if (i == 0)
                break;
            if (i > 32)
                wsp = false;
            buf.write(i);
        }
        this.whitespace = wsp;
        return buf.toString(charset);
    }

    String readStrT() throws IOException {
        int pos = readInt();
        return extractFromStrT(pos);
    }

    private String extractFromStrT(int pos) {
        int end = stringTable.indexOf('\0', pos);

        end=end<0?stringTable.length():end;

        return stringTable.substring(pos, end);
    }

    /**
     * Sets the tag table for a given page.
     *  The first string in the array defines tag 5, the second tag 6 etc.
     *  Currently, only page 0 is supported
     *
     * @param page the page referring to
     * @param tagTable a table filled with tags
     */
    public void setTagTable(int page, String [] tagTable)  {
        otherTagTables.put(new Integer(page),tagTable);
        if(page == 0){
            this.tagTable = tagTable;
        }
    }
    
    /**
     * Returns the publicIdentifier
     * @return the publicIdentifier
     */
    public String getPublicIdentifier() {
        return publicIdentifier;
    }


    /**
     * Sets the attribute start Table for a given page.
     *  The first string in the array defines attribute
     *  5, the second attribute 6 etc.
     *  Please use the character '=' (without quote!) as delimiter
     *  between the attribute name and the (start of the) value
     *
     * @param page the page referring to
     * @param attrStartTable a table filled with attribute names
     */
    public void setAttrStartTable(int page, String [] attrStartTable) {
        otherAttrStartTables.put(new Integer(page),attrStartTable);
        if(page == 0){
            this.attrStartTable = attrStartTable;
        }
    }

    /**
     * Sets the attribute value Table for a given page.
     *  The first string in the array defines attribute value 0x80,
     *  the second attribute value 0x81 etc.
     *
     * @param page the page referring to
     * @param attrValueTable a table filled with attribute values
     */
    public void setAttrValueTable(int page, String [] attrValueTable) {
        otherAttrValueTables.put(new Integer(page),attrValueTable);
        if(page == 0){
            this.attrValueTable = attrValueTable;
        }
    }

    /* Public Identifier information lookup */
    private HashMap myPublicIDMap=null;
    private HashMap myPublicIDCodeMap=null;
    private HashMap myPublicIDIndexMap=null;

    void addPublicIDEntry(PublicIDEntry anEntry) {
        if(myPublicIDMap==null) { myPublicIDMap=new HashMap(); }
        if(myPublicIDCodeMap==null) { myPublicIDCodeMap=new HashMap(); }
        if(myPublicIDIndexMap==null) { myPublicIDIndexMap=new HashMap(); }

        if(anEntry.getPublicIdentifier()!=null) {
            myPublicIDMap.put(anEntry.getPublicIdentifier().toUpperCase(), anEntry);
        }

        if(anEntry.getPublicIDCode()!=null) {
            myPublicIDMap.put(anEntry.getPublicIDCode(), anEntry);
        }

        myPublicIDIndexMap.put(new Integer(anEntry.getStrTableIndex()), anEntry);

    }

    public PublicIDEntry getPublicIDEntry(String anIdentifier) {
        return (PublicIDEntry) myPublicIDMap.get(anIdentifier.toUpperCase());
    }

    public PublicIDEntry getPublicIDEntryByCode(int aCode) {
        if (myPublicIDCodeMap == null) {
            return null;
        }
        return (PublicIDEntry) myPublicIDCodeMap.get(new Integer(aCode));
    }

    public PublicIDEntry getPublicIDEntryByIndex(int anIndex) {
        if (myPublicIDIndexMap == null) {
            return null;
        }
        return (PublicIDEntry) myPublicIDIndexMap.get(new Integer(anIndex));
    }

    public String getCharSet() {
        return charset;
    }
    
    public int getPublicIdentifierId() {
        return publicIdentifierId;
    }
}






