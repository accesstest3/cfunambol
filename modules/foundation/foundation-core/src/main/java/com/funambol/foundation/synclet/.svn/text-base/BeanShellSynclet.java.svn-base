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
package com.funambol.foundation.synclet;

import java.io.*;

import java.net.URL;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bsh.BshClassManager;
import bsh.Interpreter;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.funambol.framework.config.ConfigClassLoader;
import com.funambol.framework.core.Sync4jException;
import com.funambol.framework.core.SyncML;
import com.funambol.framework.engine.pipeline.*;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

import com.funambol.server.config.Configuration;

/**
 * Goal of the BeanShell synclet is to provide a way to develop synclets without
 * the need to follow the build/pack/deploy cycle at any change. This will also
 * minimize the downtime of a running server and allows to apply quick hot
 * fixes. To achieve this, BeanShell synclet is based on the use of a scripting
 * language which is interpreted instead of compiled. The BeanShell scripting
 * language is the scripting used by this synclet, since it is very similar to
 * Java.
 *
 * @version $Id: BeanShellSynclet.java,v 1.1.1.1 2008-03-20 21:38:42 stefano_fornari Exp $
 */
public class BeanShellSynclet
implements InputMessageProcessor, OutputMessageProcessor {

    // ------------------------------------------------------------- Static data

    /**
     * The interpreters hash map
     */
    private static HashMap interpreters;

    /**
     * Logger
     */
    private static final FunambolLogger log =
        FunambolLoggerFactory.getLogger("engine");

    // ------------------------------------------------------------ Private data

    /**
     * The script name
     */
    private String script;

    /**
     * The HTTP user agent pattern to match in order to trigger execution
     */
    private String pattern;

    /**
     * HTTP header the pattern is applied to
     */
    private String header;

    // ---------------------------------------------------------- Public methods

    /**
     * Delegates the call to the script.
     *
     * @param processingContext the message processing context
     * @param message the message to be processed
     *
     * @throws Sync4jException
     */
    public void preProcessMessage(MessageProcessingContext processingContext,
                                  SyncML                   message)
    throws Sync4jException {
        if (!isDeviceToProcess(processingContext)) {
            return;
        }

        try {
            InputMessageProcessor imp = getInputSynclet();
            imp.preProcessMessage(processingContext, message);
        } catch (StopProcessingException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error in processing the input message", e);
        }
    }

    /**
     * Delegates the call to the script.
     *
     * @param processingContext the message processing context
     * @param message the message to be processed
     *
     * @throws Sync4jException
     */
    public void postProcessMessage(MessageProcessingContext processingContext,
                                   SyncML                   message)
    throws Sync4jException {
        if (!isDeviceToProcess(processingContext)) {
            return;
        }

        try {
            OutputMessageProcessor omp = getOutputSynclet();
            omp.postProcessMessage(processingContext, message);
        } catch (StopProcessingException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error in processing the output message", e);
        }
    }

    // --------------------------------------------------------------- Accessors

    /**
     * Sets script
     *
     * @param script the script name
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Returns script
     *
     * @return scripts
     */
    public String getScript() {
        return this.script;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);

        sb.append("script",  script ).
           append("header",  header ).
           append("pattern", pattern);

        return sb.toString();
    }
    // --------------------------------------------------------- Private methods

    /**
     * Returns the interpreter associated to the instance script name. If an
     * interpreter instance is not yet created, it is created before being
     * returned.
     * It also checks if the last modification timestamp of the script is more
     * recent that the last read. If so, the new script is loaded in the
     * interpreter.
     *
     * @return the interpreter instance associated to the instance's script
     */
    private BeanShellEntry getBeanShellEntry() {
        assert (script != null && script.trim().length()>0);

        BeanShellEntry bse = null;
        synchronized (this) {
            bse = (BeanShellEntry)interpreters.get(script);

            if (bse == null) {
                bse = new BeanShellEntry();
                bse.lastModified = -1;

                interpreters.put(script, bse);
            }
        }

        return bse;
    }

    /**
     * Creates and return an Interpreter. Plus, it sets adds the config class
     * loader to the interpreter class loader.
     *
     * @return the created interpreter
     */
    private Interpreter createInterpreter() {
        Interpreter interpreter = new Interpreter();

        ConfigClassLoader cl =
            (ConfigClassLoader)Configuration.getConfiguration().getClassLoader();

        BshClassManager cm = interpreter.getClassManager();
        URL[] urls = cl.getURLs();
        for (int i=0; i<urls.length; ++i) {
            try {
                cm.addClassPath(urls[i]);
            } catch (IOException e) {
                log.error( "Unable to add '"
                          + urls[i]
                          + "' to the classpath.", e);
            }
        }

        return interpreter;
    }

    /**
     * Returns the scripted MessageInputProcessor
     *
     * @return the scripted MessageInputProcessor
     */
    private InputMessageProcessor getInputSynclet()
    throws Sync4jException {

        BeanShellEntry bse = getBeanShellEntry();

        //
        // Checking if the script is more recent that the one interpreted
        //
        File scriptFile = getScriptFile();

        if (bse.lastModified < scriptFile.lastModified()) {
            try {
                Interpreter interpreter = createInterpreter();
                bse.inputSynclet = evalInputScript(interpreter);
                bse.lastModified = scriptFile.lastModified();
                evalInitMethod(interpreter);
            } catch (Exception e) {
                throw new Sync4jException(e);
            }
        }

        return bse.inputSynclet;
    }

    /**
     * Returns the scripted OutputMessageProcessor
     *
     * @return the scripted OutputMessageProcessor
     */
    private OutputMessageProcessor getOutputSynclet()
    throws Sync4jException {
        BeanShellEntry bse = getBeanShellEntry();

        //
        // Checking if the script is more recent that the one interpreted
        //
        File scriptFile = getScriptFile();

        if (bse.lastModified < scriptFile.lastModified()) {
            try {
                Interpreter interpreter = createInterpreter();
                bse.outputSynclet = evalOutputScript(interpreter);
                bse.lastModified = scriptFile.lastModified();
                evalInitMethod(interpreter);
            } catch (Exception e) {
                throw new Sync4jException(e);
            }
        }

        return bse.outputSynclet;
    }

    /**
     * Evaluates the script in the given interpreter and returns the results
     * of the eveluation. In order to make it to return an InputMessageProcessor
     * the command "return (InputMessageProcessor)this" is appended to the
     * script.
     *
     * @param interpreter the interpreter into which evaluate the script
     */
    private InputMessageProcessor evalInputScript(Interpreter interpreter)
    throws Exception {
        FileReader r = new FileReader(getScriptFile());

        interpreter.eval(r);
        return (InputMessageProcessor)interpreter.eval(";\nreturn (InputMessageProcessor)this");
    }

    /**
     * Evaluates the script in the given interpreter and returns the results
     * of the eveluation. In order to make it to return an OutputMessageProcessor
     * the command "return (OutputMessageProcessor)this" is appended to the
     * script.
     *
     * @param interpreter the interpreter into which evaluate the script
     */
    private OutputMessageProcessor evalOutputScript(Interpreter interpreter)
    throws Exception {
        FileReader r = new FileReader(getScriptFile());

        interpreter.eval(r);
        return (OutputMessageProcessor)interpreter.eval(";\nreturn (OutputMessageProcessor)this");
    }

    /**
     * Returns the script file as a File object, which is the combination of the
     * configpath and the value of the script property.
     *
     * @return the script file object
     */
    private File getScriptFile() {
        assert (script != null && script.length()>0);
        return new File(Configuration.getConfiguration().getConfigPath(), script);
    }

    /**
     * Searches a match for the configured <code>pattern</code> with the HTTP
     * header specified by <code>header</code>.
     * If header or pattern is empty, isDeviceToProcess() returns always true.
     * If the client does not provide the specified header, isDeviceToProcess()
     * returns false.
     *
     * @param context message processing context
     *
     * @return true if the device must be proced by this synclet, false otherwise
     */
    private boolean isDeviceToProcess(MessageProcessingContext context) {
        if ((pattern == null || pattern.trim().length() == 0)
        || (header  == null || header.trim().length()  == 0)) {
            return true;
        }

        Map headers =
            (Map)context.getRequestProperty(context.PROPERTY_REQUEST_HEADERS);

        //
        // Search for the requested header value
        //
        String value = null;
        Iterator i = headers.keySet().iterator();
        while (i.hasNext()) {
            String h = (String)i.next();
            if (header.equalsIgnoreCase(h)) {
                value = (String)headers.get(h);
                break;
            }
        }

        if ((value == null) || (value.trim().length() == 0)) {
            //
            // Header not found or not specified
            //
            return false;
        }

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(value);

        return m.find();
    }

    /**
     * Evaluates the init() method
     *
     * @param interpreter the interpreter into which evaluate the script
     */
    private void evalInitMethod(Interpreter interpreter) {
        try {
            interpreter.eval("init()");
        } catch(Exception e) {
            log.error("Error calling the init method", e);
        }
    }

    // --------------------------------------------------- Static initialization

    static {
        interpreters = new HashMap();
    }

    // ----------------------------------------------------------- Inner classes

    /**
     * This class represent the key to use to put the interpreters instances
     * into the hash map. It redefines hashCode() and equals() to the String
     * script name's ones.
     */
    private class BeanShellEntry {
        public long lastModified;
        public InputMessageProcessor inputSynclet;
        public OutputMessageProcessor outputSynclet;
    }
}
