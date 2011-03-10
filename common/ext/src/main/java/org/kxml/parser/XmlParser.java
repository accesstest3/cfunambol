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
 * Contributor(s): Paul Palaszewski, Wilhelm Fitzpatrick, 
 *                 Eric Foster-Johnson, Michael Angel, Jan Andrle
 *
 * */

package org.kxml.parser;

import java.io.*;
import java.util.*;

import org.kxml.*;
import org.kxml.io.ParseException;


/** A simple, pull based "Common XML" parser. Attention: This class has
 * been renamed from DefaultParser for consitency with the org.kxml.io
 * package. */

public class XmlParser extends AbstractXmlParser {

    static final String UNEXPECTED_EOF = "Unexpected EOF"; 
    
    private char [] buf;
    private boolean eof;
    private int bufPos;
    private int bufCount;
    private Reader reader;
    private boolean relaxed;
    private int line = 1;
    private int column = 1;
    
    Vector qNames = new Vector ();
    
    boolean immediateClose = false;
    StartTag current;
    String closePending;
    
    /** The next event. May be null at startup. */

    protected ParseEvent next;
    

    int peekChar () throws IOException {
	if (eof) return -1;
	
	if (bufPos >= bufCount) {

            if (buf.length == 1) {
                int c = reader.read ();
                if (c == -1) {
                    eof = true;
                    return -1;
                }

                bufCount = 1;
                buf [0] = (char) c;
            }
            else {
                bufCount = reader.read (buf, 0, buf.length);

                if (bufCount == -1) {
                    eof = true;
                    return -1;
                }
            }

	    bufPos = 0;
	}
	
	return buf[bufPos];
    }       


    
    int readChar () throws IOException {
       int p = peekChar ();
       bufPos++;
       column++;
       if (p == 10) {
	   line++;
	   column = 1;
       }
       return p;
    }
    

    void skipWhitespace () throws IOException {
        while (!eof && peekChar () <= ' ')
            readChar ();
    }
    
    
    
    public String readName () throws IOException {

        int c = readChar ();
        if (c < 128 && c != '_' && c != ':' 
            && (c < 'a' || c > 'z')
            && (c < 'A' || c > 'Z'))
            throw new ParseException ("name expected!", null, XmlParser.this.line, XmlParser.this.column);


        StringBuffer buf = new StringBuffer ();	
        buf.append ((char) c);

        while (!eof) {
            c = peekChar ();

            if (c < 128 && c != '_' && c != '-' && c != ':' && c != '.' 
            && (c < '0' || c > '9')
            && (c < 'a' || c > 'z')
            && (c < 'A' || c > 'Z'))

            break;

            buf.append ((char) readChar ());
        }

        return buf.toString ();
    }
    

    /** Reads chars to the given buffer until the given stopChar 
     * is reached. The stopChar itself is not consumed. */

    public StringBuffer readTo (char stopChar,
				StringBuffer buf) throws IOException {
	
        while (!eof && peekChar () != stopChar) 
            buf.append ((char) readChar ());

        return buf;
    }

    public XmlParser (Reader reader) throws IOException {
        this (reader, 
              Runtime.getRuntime ().freeMemory () >= 1048576 ? 8192 : 1);
    }

    public XmlParser (Reader reader, int bufSize) throws IOException {
	
        this.reader = reader; //new LookAheadReader (reader); 
        buf = new char [bufSize];
    }
    
    public String resolveCharacterEntity (String name) throws IOException {
        throw new ParseException ("Undefined: &"+name+";", null, XmlParser.this.line, XmlParser.this.column);
    }
    
    /* precondition: &lt;!- consumed */
    
    ParseEvent parseComment () throws IOException {
       
        StringBuffer buf = new StringBuffer ();

        if (readChar () != '-') 
            throw new ParseException ("'-' expected", null, XmlParser.this.line, XmlParser.this.column);

        int cnt;
        int lst;

        while (true) {
            readTo ('-', buf);

            if (readChar () == -1) 
            throw new ParseException (UNEXPECTED_EOF, null, XmlParser.this.line, XmlParser.this.column);

            cnt = 0;

            do {
            lst = readChar ();
            cnt++;  // adds one more, but first is not cnted
            }
            while (lst == '-');

            if (lst == '>' && cnt >= 2) break;

            while (cnt-- > 0) 
            buf.append ('-');

            buf.append ((char) lst);
        }

        while (cnt-- > 2) 
            buf.append ('-');

        return new ParseEvent (Xml.COMMENT, buf.toString ());
        }


        /* precondition: &lt! consumed */ 


        ParseEvent parseDoctype () throws IOException {

        StringBuffer buf = new StringBuffer ();
        int nesting = 1;

        while (true) {
            int i = readChar ();
            switch (i) {
            case -1:
            throw new ParseException (UNEXPECTED_EOF, null, XmlParser.this.line, XmlParser.this.column);
            case '<': 
            nesting++;
            break;
            case '>':
            if ((--nesting) == 0)
                return new ParseEvent (Xml.DOCTYPE, buf.toString ());
            break;
            }
            buf.append ((char) i);
        }
    }
    
    
    ParseEvent parseCData () throws IOException {
	
        StringBuffer buf = readTo ('[', new StringBuffer ());

        if (!buf.toString ().equals ("CDATA")) 
            throw new ParseException ("Invalid CDATA start!", null, XmlParser.this.line, XmlParser.this.column);

        buf.setLength (0);

        readChar (); // skip '['

        int c0 = readChar ();
        int c1 = readChar ();

        while (true) {
            int c2 = readChar ();

            if (c2 == -1) 
            throw new ParseException (UNEXPECTED_EOF, null, XmlParser.this.line, XmlParser.this.column);

            if (c0 == ']' && c1 == ']' && c2 == '>') 
            break;

            buf.append ((char) c0);
            c0 = c1;
            c1 = c2;
        }

        return new ParseEvent (Xml.TEXT, buf.toString ());
    }
    
    
    
    /* precondition: &lt;/ consumed */

    ParseEvent parseEndTag () throws IOException {
	
        skipWhitespace ();
        closePending = readName ();
        skipWhitespace ();

        if (readChar () != '>') 
            throw new ParseException ("'>' expected", null, XmlParser.this.line, XmlParser.this.column);

        return checkEndTag ();
    }


    public ParseEvent checkEndTag () throws IOException {

        int last = qNames.size ();

            if (last == 0) 
                throw new ParseException 
                    ("tagstack empty parsing </"+closePending+">", null, XmlParser.this.line, XmlParser.this.column);


            String qName = (String) qNames.elementAt (--last);
            qNames.removeElementAt (last);

            if (qName.equals (closePending) 
                || (relaxed && qName.toLowerCase ().equals 
                    (closePending.toLowerCase ())))  
                closePending = null;
            else if (!relaxed) 
                throw new ParseException 
                    ("StartTag <"+qName
                     +"> does not match end tag </" + closePending + ">", null, XmlParser.this.line, XmlParser.this.column);

        Tag result = new Tag 
            (Xml.END_TAG, current, current.namespace, current.name);

        current = current.parent;

        return result;
    }

    
    /** precondition: <? consumed */
    
    ParseEvent parsePI () throws IOException {
        StringBuffer buf = new StringBuffer ();
        readTo ('?', buf);
        readChar (); // ?

        while (peekChar () != '>') {
            buf.append ('?');

            int r = readChar ();
            if (r == -1) throw new ParseException 
            (UNEXPECTED_EOF, null, XmlParser.this.line, XmlParser.this.column);

            buf.append ((char) r);
            readTo ('?', buf);
            readChar ();
        }

        readChar ();  // consume >

        return new ParseEvent 
            (Xml.PROCESSING_INSTRUCTION, buf.toString ());
    }
    
    
    StartTag parseStartTag () throws IOException {
        String qname = readName ();

        Vector attributes = null;
        immediateClose = false;

        while (true) { 
            skipWhitespace ();

            int c = peekChar ();
            
            if (c == -1) 
                throw new ParseException (UNEXPECTED_EOF, null, XmlParser.this.line, XmlParser.this.column); 

            if (c == '/') {
                immediateClose = true;
                readChar ();
                skipWhitespace ();
                if (readChar () != '>') 
                    throw new ParseException  
                    ("illegal element termination", null, XmlParser.this.line, XmlParser.this.column);
                break;
            }

            if (c == '>') { 
                readChar ();
                break;
            }

            String attrName = readName ();

            if (attrName.length() == 0)
                throw new ParseException("illegal char / attr", null, XmlParser.this.line, XmlParser.this.column);

            skipWhitespace ();

            if (readChar () != '=') {
                throw new ParseException 
                    ("Attribute name "+attrName
                     +"must be followed by '='!", null, XmlParser.this.line, XmlParser.this.column);
            }

            skipWhitespace ();
            int delimiter = readChar ();

            if (delimiter != '\'' && delimiter != '"') {
                if (!relaxed)
                    throw new ParseException 
                    ("<" + qname + ">: invalid delimiter: " 
                     + (char) delimiter, null, XmlParser.this.line, XmlParser.this.column);  

                delimiter = ' ';
            }

            StringBuffer buf = new StringBuffer ();
            readText (buf, (char) delimiter);
            if (attributes == null) {
                attributes = new Vector ();
            }
            attributes.addElement (new Attribute (null, attrName, buf.toString ()));

            if (delimiter != ' ') {
                readChar ();  // skip endquote
            }
        }       

        try {
            current = new StartTag 
            (current, Xml.NO_NAMESPACE, qname, (Vector)attributes, 
             immediateClose, processNamespaces);
        }       
        catch (Exception e) {
            throw new ParseException (e.toString (), e, XmlParser.this.line, XmlParser.this.column);
        }

        if (!immediateClose)
            qNames.addElement (qname);

        return current;
    }
    
    int readText (StringBuffer buf, char delimiter) throws IOException {
	
        int type = Xml.WHITESPACE;
        int nextChar;


        while (true) {
            nextChar = peekChar ();

            if (nextChar == -1 
            || nextChar == delimiter 
            || (delimiter == ' ' 
                && (nextChar == '>' || nextChar < ' '))) break;

            readChar ();

            if (nextChar == '&') {
                String code = readTo (';', new StringBuffer ()).toString ();
                readChar ();

                if (code.charAt (0) == '#') {
                    nextChar = (code.charAt (1) == 'x' 
                        ? Integer.parseInt (code.substring (2), 16) 
                        : Integer.parseInt (code.substring (1)));

                    if (nextChar > ' ') 
                        type = Xml.TEXT;

                    buf.append ((char) nextChar);           
                } else {
                    if (code.equals ("lt")) buf.append ('<');
                    else if (code.equals ("gt")) buf.append ('>');
                    else if (code.equals ("apos")) buf.append ('\'');
                    else if (code.equals ("quot")) buf.append ('"');
                    else if (code.equals ("amp")) buf.append ('&');
                    else buf.append (resolveCharacterEntity (code));

                    type = Xml.TEXT;
                }
            } else {
                if (nextChar > ' ') 
                    type = Xml.TEXT;
                buf.append ((char) nextChar);
            }
        }

        return type;
    }    
    

    /** precondition: &lt; consumed */

    ParseEvent parseSpecial () throws IOException {

        switch  (peekChar ()) {
        case -1: throw new ParseException 
                     (UNEXPECTED_EOF, null, XmlParser.this.line, XmlParser.this.column); 
            
        case '!':
            readChar ();
            switch (peekChar ()) {
            case '-': 
                readChar ();
                return parseComment ();

            case '[':
                readChar ();
                return parseCData ();
                
            default: 
                return parseDoctype ();
            }
            
        case '?':
            readChar ();
            return parsePI ();
            
        case '/': 
            readChar ();
            return parseEndTag ();
            
        default: 
            return parseStartTag ();
        }
    }


    public ParseEvent read () throws IOException{
        if (next == null) 
            peek ();
        
        ParseEvent result = next;
        next = null;
        return result;
    }
    

    public ParseEvent peek () throws IOException {
        
        if (next == null) {
            
            if (immediateClose) {
                next = new Tag 
                    (Xml.END_TAG, current, current.namespace, current.name);
                current = current.getParent ();
                immediateClose = false;
            }
            else if (closePending != null) {
                next = checkEndTag ();
            }
            else {
                switch (peekChar ()) {
                    
                case '<': 
                    readChar ();
                    next = parseSpecial ();
                    break;
                    
                case -1: 
                    if (current != null && !relaxed) 
                        throw new ParseException 
                            ("End tag missing for: "+current, null, XmlParser.this.line, XmlParser.this.column);
                    next = new ParseEvent (Xml.END_DOCUMENT, null);
                    break;
                    
                    default: {
                        StringBuffer buf = new StringBuffer ();
                        int type = readText (buf, '<');
                        next = new ParseEvent (type, buf.toString ());
                    }
                }
            }
        }
        return next;
    }    
    
    

    /** default is false. Setting relaxed true
     * allows CHTML parsing */

    public void setRelaxed (boolean relaxed) {
        this.relaxed = relaxed;
    }

    public int getLineNumber () {
        return line;
    }

    public int getColumnNumber () {
        return column;
    }

}

