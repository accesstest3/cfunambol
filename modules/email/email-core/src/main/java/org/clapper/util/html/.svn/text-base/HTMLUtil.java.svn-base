/*---------------------------------------------------------------------------*\
  $Id: HTMLUtil.java,v 1.4 2008-06-16 13:34:50 piter_may Exp $
  ---------------------------------------------------------------------------
  This software is released under a BSD-style license:

  Copyright (c) 2004-2007 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1.  Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.

  2.  The end-user documentation included with the redistribution, if any,
      must include the following acknowlegement:

        "This product includes software developed by Brian M. Clapper
        (bmc@clapper.org, http://www.clapper.org/bmc/). That software is
        copyright (c) 2004-2007 Brian M. Clapper."

      Alternately, this acknowlegement may appear in the software itself,
      if wherever such third-party acknowlegements normally appear.

  3.  Neither the names "clapper.org", "clapper.org Java Utility Library",
      nor any of the names of the project contributors may be used to
      endorse or promote products derived from this software without prior
      written permission. For written permission, please contact
      bmc@clapper.org.

  4.  Products derived from this software may not be called "clapper.org
      Java Utility Library", nor may "clapper.org" appear in their names
      without prior written permission of Brian M.a Clapper.

  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
  NO EVENT SHALL BRIAN M. CLAPPER BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
\*---------------------------------------------------------------------------*/

package org.clapper.util.html;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.axis.encoding.ser.ArraySerializer;
import org.clapper.util.text.TextUtil;
import org.clapper.util.text.Unicode;
import org.clapper.util.text.XStringBuffer;
import org.clapper.util.text.XStringBuilder;

/**
 * Static class containing miscellaneous HTML-related utility methods.
 *
 * @version <tt>$Revision: 1.4 $</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public final class HTMLUtil
{
    /*----------------------------------------------------------------------*\
                            Private Constants
    \*----------------------------------------------------------------------*/

    /**
     * Resource bundle containing the character entity code mappings.
     */
    private static final String BUNDLE_NAME = "org.clapper.util.html.HTMLUtil";

    /**
     * Number of newline sequences to hold during html translation.
     */
    private static final int NEWLINES_TO_HOLD = 1;

    /*----------------------------------------------------------------------*\
                            Private Data Items
    \*----------------------------------------------------------------------*/

    private static ResourceBundle resourceBundle = null;

    /**
     * For regular expression substitution. Instantiated first time it's
     * needed.
     */
    private static Pattern entityPattern = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    private HTMLUtil()
    {
        // Can't be instantiated
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Translates a String containing html to a text.
     * @param s html to be translated
     * @return text String
     */
    public static String HTMLToText (String s)
    {
        char[]  ch = s.toCharArray();
        
        boolean inElement         = false;
        boolean inAttributeValue  = false;
        boolean isStartTag        = true;  
        boolean isEndTag          = false;
        boolean isTagNameComplete = false;
        boolean isNewLine         = false;
        
        int ignoreLevel    = 0;
        int newLineCounter = 0;
        
        String[] tagsToExclude = {"style", "head", "!DOCTYPE"};
        
        StringBuilder  tagName = new StringBuilder();
        XStringBuilder buf     = new XStringBuilder();

        char c      = ' ';
        char c_prec = ' ';
        
        String name = null;
        
        for (int i = 0; i < ch.length; i++)
        {
            c = ch[i];
            
            switch (c)
            {
                case '<':
                    //
                    // Start of a start/end tag.
                    //
                    isNewLine         = false;                    
                    inElement         = true;                    

                    // By default a tag is a start tag
                    isStartTag        = true;
                    isEndTag          = false;
                    
                    isTagNameComplete = false;
                    
                    tagName           = new StringBuilder();                                          
                    
                    break;

                case '/':
                    if (inElement &&  c_prec == '<'){
                        //
                        // An end tag has been recognized.
                        //
                        isStartTag = false;
                        isEndTag   = true;
                        
                    } else if (inElement && c_prec != '<'){
                        //
                        // A single tag (eg: <br/>) has been recognized.
                        //
                        isEndTag   = true;
                    } else {
                        //
                        // save the / because is in the text
                        //
                        buf.append (c);                        
                    }
                    break;
                
                case '>':
                    
                    //
                    // Checks if the '>' char occurs within am attribute value.
                    // If this is the case, then the end of the tag has not been 
                    // reached.
                    //
                    if (inAttributeValue){
                        break;
                    }
                    
                    //
                    // End of a start/end tag has been reached
                    //
                    if (inElement){
                        inElement         = false;
                        isTagNameComplete = true;
                     
                        //
                        // Is this tag's content to be ignored ?
                        //
                        
                        name = tagName.toString().toLowerCase();
                        if(isInArray(name, tagsToExclude)){
                            if (isStartTag){
                                ignoreLevel++;
                                break;
                            }
                            if (isEndTag) {
                                ignoreLevel--;
                                break;
                            }
                        }
                            
                        //
                        // Tag substituitions
                        //
                        if (isStartTag && !isEndTag){
                            newLineCounter = replaceStartTag(name, buf, newLineCounter);
                        }

                        if (isEndTag && !isStartTag){
                            newLineCounter = replaceEndTag(name, buf, newLineCounter);
                        }
                        
                        if (isEndTag && isStartTag){
                            newLineCounter = replaceSingleTag(name, buf, newLineCounter);
                        }
                    }
                    break;
                case '\"':
                    
                    if (inElement && !inAttributeValue){
                        inAttributeValue = true;
                        break;
                    }
                    
                    if (inElement && inAttributeValue){
                        inAttributeValue = false;
                        break;
                    }
                    
                    if (ignoreLevel > 0){
                        break;
                    }
                    
                    buf.append(c);
                    
                    break;
                    
                case '\r':
                    if(!inElement && c_prec != '>'){
                        buf.append(' ');
                    }                    
                    break;
                case '\n':
                    isNewLine = true;                    
                    break;
                case 0x20: // single white space
                case 0xA0: // non-breaking space character
                    //
                    // Strips any white space that follows a newline.
                    //
                    if (isNewLine){
                        break;
                    }
                default:
                    isNewLine = false;
                    
                    if (!inElement){
                        
                        if (ignoreLevel > 0){
                            break;
                        }
                                                
                        newLineCounter = 0;                                                         
                        
                        //
                        // Content enclosed by a start and an end tag is put in
                        // output buffer.
                        //
                        buf.append (c);
                        
                    } else {
                        
                        //
                        // Retrieve tag name. Any tag attribute is ignored.
                        //
                        if (c != ' ' && !isTagNameComplete){
                            tagName.append(ch[i]);
                        } else {
                            isTagNameComplete = true;
                        }
                    }
                    break;
            }
            c_prec = c;
        }
        
        //
        // Trim any leading sequence of the following characters: '\r', '\n', ' '
        //
        
        int startIndex = 0;
        for (int i = 0; i < buf.length(); i++) {
            c = buf.charAt(i);
            if (c == '\r' || c == '\n' || c == 0x20 || c == 0xA0){
                continue;
            }
            startIndex = i;
            break;
        }
        
        return buf.substring(startIndex);
    }

    /**
     * Removes all HTML element tags from a string, leaving just the character
     * data. This method does <b>not</b> touch any inline HTML character
     * entity codes. Use
     * {@link #convertCharacterEntities convertCharacterEntities()}
     * to convert HTML character entity codes.
     *
     * @param s  the string to adjust
     *
     * @return the resulting, possibly modified, string
     *
     * @see #convertCharacterEntities
     */
    public static String stripHTMLTags (String s)
    {
        char[]         ch = s.toCharArray();
        boolean        inElement = false;
        XStringBuilder buf = new XStringBuilder();

        for (int i = 0; i < ch.length; i++)
        {
            switch (ch[i])
            {
                case '<':
                    inElement = true;
                    break;

                case '>':
                    if (inElement)
                        inElement = false;
                    else
                        buf.append (ch[i]);
                    break;

                default:
                    if (! inElement)
                        buf.append (ch[i]);
                    break;
            }
        }

        return buf.toString();
    }

    /**
     * Converts all inline HTML character entities (c.f.,
     * <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">http://www.w3.org/TR/REC-html40/sgml/entities.html</a>)
     * to their Unicode character counterparts, if possible.
     *
     * @param s the string to convert
     *
     * @return the resulting, possibly modified, string
     *
     * @see #stripHTMLTags
     * @see #makeCharacterEntities
     */
    public static String convertCharacterEntities (String s)
    {
        // The resource bundle contains the mappings for symbolic entity
        // names like "amp". Note: Must protect matching and MatchResult in
        // a critical section, for thread-safety. See javadocs for
        // Perl5Util.

        synchronized (HTMLUtil.class)
        {
            try
            {
                if (entityPattern == null)
                    entityPattern = Pattern.compile ("&(#?[^; \t]+);");
            }

            catch (PatternSyntaxException ex)
            {
                // Should not happen unless I've screwed up the pattern.
                // Throw a runtime error.

                assert (false);
            }
        }

        ResourceBundle bundle = getResourceBundle();
        XStringBuffer buf = new XStringBuffer();
        Matcher matcher = null;

        synchronized (HTMLUtil.class)
        {
            matcher = entityPattern.matcher (s);
        }

        for (;;)
        {
            String match = null;
            String preMatch = null;
            String postMatch = null;

            if (! matcher.find())
                break;

            match = matcher.group (1);
            preMatch = s.substring (0, matcher.start (1) - 1);
            postMatch = s.substring (matcher.end (1) + 1);

            if (preMatch != null)
                buf.append (preMatch);

            if (match.charAt (0) == '#')
            {
                if (match.length() == 1)
                    buf.append ('#');

                else
                {
                    // It might be a numeric entity code. Try to parse it
                    // as a number. If the parse fails, just put the whole
                    // string in the result, as is. Be sure to handle both
                    // the decimal form (e.g., &#8482;) and the hexadecimal
                    // form (e.g., &#x2122;).

                    int cc;
                    boolean isHex = (match.length() > 2) &&
                                    (match.charAt(1) == 'x');
                    boolean isLegal = false;
                    try
                    {
                        if (isHex)
                            cc = Integer.parseInt(match.substring(2), 16);
                        else
                            cc = Integer.parseInt(match.substring(1));

                        // It parsed. Is it a valid Unicode character?

                        if (Character.isDefined((char) cc))
                        {
                            buf.append((char) cc);
                            isLegal = true;
                        }
                    }

                    catch (NumberFormatException ex)
                    {
                    }

                    if (! isLegal)
                    {
                        buf.append("&#");
                        if (isHex)
                            buf.append('x');
                        buf.append(match + ";");
                    }
                }
            }

            else
            {
                // Not a numeric entity. Try to find a matching symbolic
                // entity.

                try
                {
                    String rep = bundle.getString ("html_" + match);
                    buf.append (rep);
                }

                catch (MissingResourceException ex)
                {
                    buf.append ("&" + match + ";");
                }
            }

            if (postMatch == null)
                break;

            s = postMatch;
            matcher.reset (s);
        }

        if (s.length() > 0)
            buf.append (s);

        return buf.toString();
    }

    /**
     * Converts appropriate Unicode characters to their HTML character entity
     * counterparts (c.f.,
     * <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">http://www.w3.org/TR/REC-html40/sgml/entities.html</a>).
     *
     * @param s the string to convert
     *
     * @return the resulting, possibly modified, string
     *
     * @see #stripHTMLTags
     *
     * @see #convertCharacterEntities
     */
    public static String makeCharacterEntities (String s)
    {
        // First, make a character-to-entity-name map from the resource bundle.

        ResourceBundle bundle = getResourceBundle();
        Map<Character,String> charToEntityName =
            new HashMap<Character,String>();
        Enumeration<String> keys = bundle.getKeys();
        XStringBuffer buf = new XStringBuffer();

        while (keys.hasMoreElements())
        {
            String key = keys.nextElement();
            String sChar = bundle.getString (key);
            char c = sChar.charAt (0);

            // Transform the bundle key into an entity name by removing the
            // "html_" prefix.

            buf.clear();
            buf.append (key);
            buf.delete ("html_");

            charToEntityName.put (c, buf.toString());
        }

        char[] chars = s.toCharArray();
        buf.clear();

        for (int i = 0; i < chars.length; i++)
        {
            char c = chars[i];

            String entity = charToEntityName.get (c);
            if (entity == null)
            {
                if (! TextUtil.isPrintable(c))
                {
                    buf.append("&#");
                    buf.append(Integer.valueOf(c));
                    buf.append(';');
                }
                else
                {
                    buf.append(c);
                }
            }

            else
            {
                buf.append ('&');
                buf.append(entity);
                buf.append(';');
            }
        }

        return buf.toString();
    }

    /**
     * Convenience method to convert embedded HTML to text. This method:
     *
     * <ul>
     *   <li> Converts HTML via a call to {@link #HTMLToText #HTMLToText()}
     *   <li> Uses {@link #convertCharacterEntities convertCharacterEntities()}
     *        to convert HTML entity codes to appropriate Unicode characters.
     *   <li> Converts certain Unicode characters in a string to plain text
     *        sequences.
     * </ul>
     *
     * @param s  the string to parse
     *
     * @return the resulting, possibly modified, string
     *
     * @see #convertCharacterEntities
     * @see #HTMLToText
     */
    public static String textFromHTML (String s)
    {
        String        stripped = convertCharacterEntities (HTMLToText (s));
        char[]        ch = stripped.toCharArray();
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < ch.length; i++)
        {
            switch (ch[i])
            {
                case Unicode.LEFT_SINGLE_QUOTE:
                case Unicode.RIGHT_SINGLE_QUOTE:
                    buf.append ('\'');
                    break;

                case Unicode.LEFT_DOUBLE_QUOTE:
                case Unicode.RIGHT_DOUBLE_QUOTE:
                    buf.append ('"');
                    break;

                case Unicode.EM_DASH:
                    buf.append ("--");
                    break;

                case Unicode.EN_DASH:
                    buf.append ('-');
                    break;

                case Unicode.TRADEMARK:
                    buf.append ("[TM]");
                    break;

                default:
                    buf.append (ch[i]);
                    break;
            }
        }

        return buf.toString();
    }

    /*----------------------------------------------------------------------*\
                              Private Methods
    \*----------------------------------------------------------------------*/

    /**
     * Load the resource bundle, if it hasn't already been loaded.
     */
    private static ResourceBundle getResourceBundle()
    {
        synchronized (HTMLUtil.class)
        {
            if (resourceBundle == null)
                resourceBundle = ResourceBundle.getBundle (BUNDLE_NAME);
        }

        return resourceBundle;
    }

    /**
     * Replaces a start tag with its translation, if necessary.
     *
     * @param tagName name of the last tag picked up.
     * @param buf output buffer
     * @param newLineCounter current number of newline hold in output
     * @return updated current number of newline hold
     */
    private static int replaceStartTag(
            String         tagName,
            XStringBuilder buf,
            int            newLineCounter){

        int newlineCounterTmp = newLineCounter;

        if (tagName.equals("br")){
            newlineCounterTmp = addNewline(newLineCounter, buf);
        } else if (tagName.equals("h1")){
            //buf.append("(T1)");
        } else if (tagName.equals("h2")){
            //buf.append("(T2)");
        } else if (tagName.equals("table")){
            newlineCounterTmp = addNewline(newLineCounter, buf);
        } else if (tagName.equals("li")){
            buf.append(" ** ");
        }
        return newlineCounterTmp;
    }
    
    /**
     * Replaces an end tag with its translation, if necessary.
     *
     * @param tagName name of the last tag picked up.
     * @param buf output buffer
     * @param newLineCounter current number of newline hold in output
     * @return updated current number of newline hold
     */
    private static int replaceEndTag(
            String         tagName,
            XStringBuilder buf,
            int            newLineCounter){
        
        int newlineCounterTmp = newLineCounter;
        
        if (tagName.equals("h1")){
            //buf.append("(T1)");
            newlineCounterTmp = addNewline(newLineCounter, buf);            
        } else if (tagName.equals("h2")){
            //buf.append("(T2)");
            newlineCounterTmp = addNewline(newLineCounter, buf);
        } else if (tagName.equals("td")){
            buf.append(' ');
        } else if (tagName.equals("tr")){
            newlineCounterTmp = addNewline(newLineCounter, buf);
        } else if (tagName.equals("li")){
            newlineCounterTmp = addNewline(newLineCounter, buf);
        } else if (tagName.equals("a")){
            buf.append(' ');
        }
        return newlineCounterTmp;
    }
    
    /**
     * Replaces a single tag (eg: <code><br/></code>) with its translation, 
     * if necessary.
     *
     * @param tagName name of the last tag picked up.
     * @param buf output buffer
     * @param newLineCounter current number of newline hold in output
     * @return updated current number of newline hold
     */
    private static int replaceSingleTag(
            String         tagName,
            XStringBuilder buf,
            int            newLineCounter){
        
        int newLineCounterTmp = newLineCounter;
        
        if (tagName.equals("br")){
            newLineCounterTmp = addNewline(newLineCounter, buf);
        }
        return newLineCounterTmp;
    }
    
    /**
     * Checks if the given String is contained in the given array.
     *
     * @param item
     * @param array
     * @return 
     */
    private static boolean isInArray(String item, String[] array){
        for (int i = 0; i < array.length; i++) {
            if (item.equals(array[i])){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Some tag substitutions add a newline (that is the string "\r\n") in order 
     * to improve clarity of the translated text. Thus it may happen that long 
     * sequences of newline could appear. In order to avoid these long sequences
     * the <code>addNewline</code> method adds a newline only if the number of
     * the newlines already inserted is less or equal a predefined number
     * (<code>NEWLINES_TO_HOLD</code>).
     *
     * @param newlineCounter number of newlines already inserted
     * @param buf uotput buffer
     * @return current number of newlines inserted
     */
    private static int addNewline(int newlineCounter, XStringBuilder buf){
        if (newlineCounter <= NEWLINES_TO_HOLD) {
            buf.append("\r\n");
        }
        return ++newlineCounter;
    }
}
