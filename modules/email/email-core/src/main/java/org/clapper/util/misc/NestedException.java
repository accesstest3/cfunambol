/*---------------------------------------------------------------------------*\
  $Id: NestedException.java,v 1.1 2008-03-25 11:25:32 gbmiglia Exp $
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

package org.clapper.util.misc;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import java.util.Locale;

/**
 * <p><tt>NestedException</tt> defines a special <tt>Exception</tt> class
 * that permits exceptions to wrap other exceptions. Much of the
 * functionality of this class has been subsumed by the "chained exception"
 * handling introduced in JDK 1.4. However, this class is retained for
 * two reasons:</p>
 *
 * <ul>
 *   <li> backward compatibility
 *   <li> it has constructors that support internationalization and
 *        localization of exception messages
 * </ul>
 *
 * <p>While <tt>NestedException</tt> can be used directly, it is most useful
 * as a base class for other exceptions classes.</p>
 *
 * @version <tt>$Revision: 1.1 $</tt>
 *
 * @author Copyright &copy; 2004-2007 Brian M. Clapper
 */
public class NestedException extends Exception
{
    /*----------------------------------------------------------------------*\
                         Private Static Variables
    \*----------------------------------------------------------------------*/

    /**
     * See JDK 1.5 version of java.io.Serializable
     */
    private static final long serialVersionUID = 1L;

    /*----------------------------------------------------------------------*\
                             Private Variables
    \*----------------------------------------------------------------------*/

    private String    resourceBundleName = null;
    private String    bundleMessageKey   = null;
    private String    defaultMessage     = null;
    private Object[]  messageParams      = null;

    /*----------------------------------------------------------------------*\
                                Constructor
    \*----------------------------------------------------------------------*/

    /**
     * Default constructor, for an exception with no nested exception and
     * no message.
     */
    public NestedException()
    {
        super();
    }

    /**
     * Constructs an exception containing another exception, but no message
     * of its own.
     *
     * @param exception  the exception to contain
     */
    public NestedException(Throwable exception)
    {
        this(exception.getMessage(), exception);
    }

    /**
     * Constructs an exception containing an error message, but no
     * nested exception.
     *
     * @param message  the message to associate with this exception
     */
    public NestedException(String message)
    {
        super(message);
    }

    /**
     * Constructs an exception containing another exception and a message.
     *
     * @param message    the message to associate with this exception
     * @param exception  the exception to contain
     */
    public NestedException(String message, Throwable exception)
    {
        super(message, exception);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, and a default message (in case the resource bundle can't be
     * found). Using this constructor is equivalent to calling the
     * {@link #NestedException(String,String,String,Object[])}
     * constructor, with a null pointer for the <tt>Object[]</tt> parameter.
     * Calls to {@link #getMessage(Locale)} will attempt to retrieve
     * the top-most message (i.e., the message from this exception, not
     * from nested exceptions) by querying the named resource bundle.
     * Calls to {@link #printStackTrace(PrintWriter,Locale)} will do the same,
     * where applicable. The message is not retrieved until one of those
     * methods is called, because the desired locale is passed into
     * <tt>getMessage()</tt> and <tt>printStackTrace()</tt>, not this
     * constructor.
     *
     * @param bundleName  resource bundle name
     * @param messageKey  the key to the message to find in the bundle
     * @param defaultMsg  the default message
     *
     * @see #NestedException(String,String,String,Object[])
     * @see #getMessage(Locale)
     */
    public NestedException(String bundleName,
                           String messageKey,
                           String defaultMsg)
    {
        this(bundleName, messageKey, defaultMsg, null, null);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, a default message format (in case the resource bundle can't be
     * found), and arguments to be incorporated in the message via
     * <tt>java.text.MessageFormat</tt>. Using this constructor is
     * equivalent to calling the
     * {@link #NestedException(String,String,String,Object[],Throwable)}
     * with a null <tt>Throwable</tt> parameter. Calls to
     * {@link #getMessage(Locale)} will attempt to retrieve the top-most
     * message (i.e., the message from this exception, not from nested
     * exceptions) by querying the named resource bundle. Calls to
     * {@link #printStackTrace(PrintWriter,Locale)} will do the same, where
     * applicable. The message is not retrieved until one of those methods
     * is called, because the desired locale is passed into
     * <tt>getMessage()</tt> and <tt>printStackTrace()</tt>, not this
     * constructor.
     *
     * @param bundleName  resource bundle name
     * @param messageKey  the key to the message to find in the bundle
     * @param defaultMsg  the default message
     * @param msgParams   parameters to the message, if any, or null
     *
     * @see #NestedException(String,String,String,Object[])
     * @see #getMessage(Locale)
     */
    public NestedException(String   bundleName,
                           String   messageKey,
                           String   defaultMsg,
                           Object[] msgParams)
    {
        this(bundleName, messageKey, defaultMsg, msgParams, null);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, a default message (in case the resource bundle can't be found), and
     * another exception. Using this constructor is equivalent to calling the
     * {@link #NestedException(String,String,String,Object[],Throwable)}
     * constructor, with a null pointer for the <tt>Object[]</tt>
     * parameter. Calls to {@link #getMessage(Locale)} will attempt to
     * retrieve the top-most message (i.e., the message from this
     * exception, not from nested exceptions) by querying the named
     * resource bundle. Calls to
     * {@link #printStackTrace(PrintWriter,Locale)} will do the same, where
     * applicable. The message is not retrieved until one of those methods
     * is called, because the desired locale is passed into
     * <tt>getMessage()</tt> and <tt>printStackTrace()</tt>, not this
     * constructor.
     *
     * @param bundleName  resource bundle name
     * @param messageKey  the key to the message to find in the bundle
     * @param defaultMsg  the default message
     * @param exception   the exception to nest
     *
     * @see #NestedException(String,String,String,Object[])
     * @see #getMessage(Locale)
     */
    public NestedException(String    bundleName,
                           String    messageKey,
                           String    defaultMsg,
                           Throwable exception)
    {
        this(bundleName, messageKey, defaultMsg, null, exception);
    }

    /**
     * Constructs an exception containing a resource bundle name, a message
     * key, a default message format (in case the resource bundle can't be
     * found), arguments to be incorporated in the message via
     * <tt>java.text.MessageFormat</tt>, and another exception.
     * Calls to {@link #getMessage(Locale)} will attempt to retrieve the
     * top-most message (i.e., the message from this exception, not from
     * nested exceptions) by querying the named resource bundle. Calls to
     * {@link #printStackTrace(PrintWriter,Locale)} will do the same, where
     * applicable. The message is not retrieved until one of those methods
     * is called, because the desired locale is passed into
     * <tt>getMessage()</tt> and <tt>printStackTrace()</tt>, not this
     * constructor.
     *
     * @param bundleName  resource bundle name
     * @param messageKey  the key to the message to find in the bundle
     * @param defaultMsg  the default message
     * @param msgParams   parameters to the message, if any, or null
     * @param exception   exception to be nested
     *
     * @see #NestedException(String,String,String,Object[])
     * @see #getMessage(Locale)
     */
    public NestedException(String    bundleName,
                           String    messageKey,
                           String    defaultMsg,
                           Object[]  msgParams,
                           Throwable exception)
    {
        super();
        initCause(exception);
        this.resourceBundleName = bundleName;
        this.bundleMessageKey   = messageKey;
        this.defaultMessage     = defaultMsg;
        this.messageParams      = msgParams;
    }

    /*----------------------------------------------------------------------*\
                              Public Methods
    \*----------------------------------------------------------------------*/

    /**
     * Returns the error message string for this exception. If the
     * exception was instantiated with a message of its own, then that
     * message is returned. Otherwise, this method returns the class name,
     * along with the class name of the first nested exception, if any.
     * Unlike the parent <tt>Exception</tt> class, this method will never
     * return null.
     *
     * @return  the error message string for this exception
     */
    public String getMessage()
    {
        return getMessage(Locale.getDefault());
    }

    /**
     * Returns the error message string for this exception. If the
     * exception was instantiated with a message of its own, then that
     * message is returned. Otherwise, this method returns the class name,
     * along with the class name of the first nested exception, if any.
     * Unlike the parent <tt>Exception</tt> class, this method will never
     * return null. If a localized version of the message is available, it
     * will be returned.
     *
     * @param locale the locale to use, or null for the default
     *
     * @return  the error message string for this exception
     */
    public String getMessage(Locale locale)
    {
        StringBuilder buf = new StringBuilder();
        String        msg = null;

        if ((resourceBundleName != null) && (bundleMessageKey != null))
        {
            msg = BundleUtil.getMessage(resourceBundleName,
                                        locale,
                                        bundleMessageKey,
                                        defaultMessage,
                                        messageParams);
        }

        if (msg == null)
        {
            msg = defaultMessage;
            if (msg == null)
                msg = super.getMessage();
        }

        if (msg != null)
            buf.append (msg);

        else
        {
            Throwable containedException = getCause();
            while ((containedException != null) && (msg == null))
            {
                if (containedException instanceof NestedException)
                {
                    // It'll drill in itself.

                    msg = ((NestedException) containedException)
                              .getMessage(locale);
                    break;
                }

                else
                {
                    msg = containedException.getMessage();
                }

                containedException = containedException.getCause();
            }

            if (msg != null)
                buf.append (msg);

            else
            {
                buf.append(this.getClass().getName());
                if (containedException != null)
                {
                    buf.append(" (contains ");
                    buf.append(containedException.getClass().getName());
                    buf.append(")");
                }
            }
        }

        return buf.toString();
    }

    /**
     * Get all the messages of all the nested exceptions, as one
     * string, with each message on a separate line. To run all the messages
     * together into one line, use {@link #getMessages(boolean)}, with a
     * parameter of <tt>true</tt>.
     *
     * @return the aggregated messages
     *
     * @see #getMessages(boolean)
     */
    public String getMessages()
    {
        return getMessages(false);
    }

    /**
     * Get all the messages of all the nested exceptions, as one string. If
     * the <tt>elideNewlines</tt> parameter is <tt>true</tt>, then the
     * messages are joined so that there are no newlines in the resulting
     * string. Otherwise, (a) any existing newlines in the messages are
     * preserved, and (b) each nested message occupies its own line.
     *
     * @param elideNewlines  whether to elide newlines or not
     *
     * @return the aggregated messages
     */
    public String getMessages(boolean elideNewlines)
    {
        return getMessages(elideNewlines, null);
    }

    /**
     * Get all the messages of all the nested exceptions, as one string. If
     * the <tt>elideNewlines</tt> parameter is <tt>true</tt>, then the
     * messages are joined so that there are no newlines in the resulting
     * string. Otherwise, (a) any existing newlines in the messages are
     * preserved, and (b) each nested message occupies its own line.
     *
     * @param elideNewlines  whether to elide newlines or not
     * @param locale         the locale to use, or null for the default
     *
     * @return the aggregated messages
     */
    public String getMessages(boolean elideNewlines, Locale locale)
    {
        StringWriter  sw = new StringWriter();
        PrintWriter   pw = new PrintWriter(sw);
        Throwable     ex = this;
        StringBuffer  buf = null;

        if (locale == null)
            locale = Locale.getDefault();

        if (elideNewlines)
            buf = new StringBuffer();

        // Note: Last exception message dumped should not have a newline.

        while (ex != null)
        {
            String s;

            if (ex instanceof NestedException)
                s = ((NestedException) ex).getMessage(locale);
            else
                s = ex.getMessage();

            if (s == null)
                s = ex.toString();

            if (elideNewlines)
            {
                // Must remove any newlines in this message,

                try
                {
                    LineNumberReader r =
                        new LineNumberReader(new StringReader(s));
                    String line;
                    String sep = "";

                    buf.setLength (0);
                    while ((line = r.readLine()) != null)
                    {
                        buf.append(sep);
                        buf.append(line);
                        sep = " ";
                    }

                    s = buf.toString();
                }

                catch (IOException ioEx)
                {
                    // Shouldn't happen. If it does, just use the original
                    // string.
                }
            }

            // Add the message to the buffer

            pw.print (s);

            ex = ex.getCause();
            if (ex != null)
            {
                if (elideNewlines)
                    pw.print(": ");
                else
                    pw.println();
            }
        }

        return sw.getBuffer().toString();
    }

    /**
     * Gets the exception that's nested within this <tt>NestedException</tt>,
     * if any.
     *
     * @return the nested exception, or null
     *
     * @deprecated Use <tt>java.lang.Throwable.getCause()</tt> instead
     */
    public Throwable getNestedException()
    {
        return getCause();
    }

    /**
     * Returns a short description of this exception. If this object was
     * created with an error message string, then the result is the
     * concatenation of three strings:
     *
     * <ul>
     *   <li> The name of the actual class of this object
     *   <li> ": " (a colon and a space)
     *   <li> The result of the {@link #getMessage} method for this object
     * </ul>
     *
     * If this object was created with no error message string, then the
     * name of the actual class of this object is returned.
     *
     * @return  a string representation of this object
     */
    public String toString()
    {
        String s = getClass().getName();
        String message = getMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    /**
     * Print a stack trace to standard error.
     *
     * @see #printStackTrace(Locale)
     * @see #printStackTrace(PrintWriter)
     * @see #printStackTrace(PrintStream)
     */
    public void printStackTrace()
    {
        this.printStackTrace(System.err);
    }

    /**
     * Print a stack trace to standard error, using the specified locale.
     *
     * @param locale  the locale to use, or null for the default
     *
     * @see #printStackTrace(Locale)
     * @see #printStackTrace(PrintWriter)
     * @see #printStackTrace(PrintStream)
     */
    public void printStackTrace (Locale locale)
    {
        this.printStackTrace(System.err, locale);
    }

    /**
     * Print a stack trace.
     *
     * @param out  where to dump the stack trace
     *
     * @see #printStackTrace()
     * @see #printStackTrace(PrintWriter,Locale)
     * @see #printStackTrace(PrintStream)
     */
    public void printStackTrace(PrintWriter out)
    {
        super.printStackTrace(out);
    }

    /**
     * Print a stack trace, using a specific locale for the output.
     *
     * @param out     where to dump the stack trace
     * @param locale  the locale to use, or null for the default
     *
     * @see #printStackTrace()
     * @see #printStackTrace(PrintWriter)
     * @see #printStackTrace(PrintStream,Locale)
     */
    public void printStackTrace(PrintWriter out, Locale locale)
    {
        if (locale == null)
            super.printStackTrace(out);

        else
        {
            Locale oldLocale = Locale.getDefault();
            Locale.setDefault(locale);

            super.printStackTrace(out);
            out.flush();

            Locale.setDefault(oldLocale);
        }
    }

    /**
     * Print a stack trace.
     *
     * @param out  where to dump the stack trace
     *
     * @see #printStackTrace()
     * @see #printStackTrace(PrintStream,Locale)
     * @see #printStackTrace(PrintWriter)
     */
    public void printStackTrace(PrintStream out)
    {
        super.printStackTrace(out);
    }

    /**
     * Print a stack trace, using a specific locale for the output.
     *
     * @param out     where to dump the stack trace
     * @param locale  the locale to use, or null for the default
     *
     * @see #printStackTrace()
     * @see #printStackTrace(PrintStream)
     * @see #printStackTrace(PrintWriter,Locale)
     */
    public void printStackTrace(PrintStream out, Locale locale)
    {
        this.printStackTrace(new PrintWriter(out), locale);
    }
}
