/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
package com.funambol.framework.engine.pipeline;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.funambol.framework.core.SyncML;
import com.funambol.framework.core.Sync4jException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.SyncMLUtil;

/**
 * This class represents the manager of the pipeline. It supplies a hook for
 * adding additional processing and manipulation of the message between the
 * server and the client.
 * <p>
 * This pipeline manager handles the <i>StopProcessingException</i> event
 * stopping the input or output message processing. No other actions are taken.
 *
 * @version $Id: PipelineManager.java,v 1.3 2008-02-25 14:25:35 luigiafassina Exp $
 */
public class PipelineManager
implements InputMessageProcessor, OutputMessageProcessor, Serializable {

    // ------------------------------------------------------------ Private data
    private static final FunambolLogger log =
        FunambolLoggerFactory.getLogger("engine.pipeline");

    private static final boolean DEBUG_MODE =
        Boolean.parseBoolean(System.getProperty("funambol.debug", "false"));

    // -------------------------------------------------------------- Properties
    private InputMessageProcessor  inputProcessors[]  = null;
    private OutputMessageProcessor outputProcessors[] = null;

    /** Getter for property inputProcessors.
     * @return Value of property inputProcessors.
     */
    public InputMessageProcessor[] getInputProcessors() {
        return this.inputProcessors;
    }

    /** Setter for property inputProcessors.
     * @param inputProcessors New value of property inputProcessors.
     */
    public void setInputProcessors(InputMessageProcessor[] inputProcessors) {
        this.inputProcessors = inputProcessors;
    }

    /** Getter for property outputProcessors.
     * @return Value of property outputProcessors.
     */
    public OutputMessageProcessor[] getOutputProcessors() {
        return this.outputProcessors;
    }

    /** Setter for property outputProcessors
     * @param outputProcessors New value of property outputProcessors.
     */
    public void setOutputProcessors(OutputMessageProcessor[] outputProcessors) {
        this.outputProcessors = outputProcessors;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Process the message with the input processors.
     *
     * @param processingContext message processing context
     * @param message the message to be processed
     */
    public void preProcessMessage(MessageProcessingContext processingContext,
                                  SyncML                   message          ) {
        if (log.isTraceEnabled()) {
            log.trace("Starting preprocessing");
        }

        int size = inputProcessors.length;
        for (int i=0; i<size; i++) {
            try {
                if(inputProcessors[i] == null){
                    if (log.isErrorEnabled()) {
                        log.error("It was not possible to create the input" +
                                  " synclet in position " + i +
                                  ". Check your pipeline configuration");
                    }
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Calling " + inputProcessors[i]
                                             + ".preProcessMessage(...)");
                    }
                    inputProcessors[i].preProcessMessage(processingContext, message);
                }
            } catch (StopProcessingException e) {
                if (log.isInfoEnabled()) {
                    log.info("Input processing stopped by "
                             + inputProcessors[i]
                             + " (reason: "
                             + e.getMessage()
                             + ")"
                             );
                }
                break;
            } catch(Sync4jException e) {
                log.error("PreProcessMessage error", e);
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("Preprocessed message: " +
                      SyncMLUtil.toXML(message, isDebugMode()));
        }
    }

    /**
     * Process the message with the output processors.
     *
     * @param processingContext message processing context
     * @param message the message to be processed
     */
    public void postProcessMessage(MessageProcessingContext processingContext,
                                   SyncML                   message          ) {
        if (log.isTraceEnabled()) {
            log.trace("Starting postprocessing");
            log.trace("Returning message to process: " +
                      SyncMLUtil.toXML(message, isDebugMode()));
        }

        int size = outputProcessors.length;
        for (int i=0; i<size; i++) {
            try {
                if(outputProcessors[i] == null){
                    if (log.isErrorEnabled()) {
                        log.error("It was not possible to create the output" +
                                  " synclet in position " + i +
                                  ". Check your pipeline configuration");
                    }
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Calling " + outputProcessors[i] + ".postProcessMessage(...)");
                    }
                    outputProcessors[i].postProcessMessage(processingContext, message);
                }
            } catch (StopProcessingException e) {
                if (log.isInfoEnabled()) {
                    log.info("Output processing stopped by "
                             + outputProcessors[i]
                             + " (reason: "
                             + e.getMessage()
                             + ")"
                             );
                }
                break;
            } catch(Sync4jException e) {
                log.error("PostProcessMessage error", e);
            }
        }
    }

    /**
     * Adds the given InputProcessors list to inputProcessors
     * @param inputProcessorsToAdd the input processors to add
     */
    public void addInputProcessors(List inputProcessorsToAdd) {
        List inputProcessorsList = null;
        if (inputProcessors == null) {
            inputProcessorsList = new ArrayList();
        } else {
            inputProcessorsList = new ArrayList(Arrays.asList(inputProcessors));
        }
        inputProcessorsList.addAll(inputProcessorsToAdd);
        inputProcessors =
            (InputMessageProcessor[])inputProcessorsList.toArray(
                         new InputMessageProcessor[inputProcessorsList.size()]);
    }

    /**
     * Adds the given InputProcessors list to inputProcessors
     * @param outputProcessorsToAdd the output processors to add
     */
    public void addOutputProcessors(List outputProcessorsToAdd) {
        List outputProcessorsList = null;
        if (outputProcessors == null) {
            outputProcessorsList = new ArrayList();
        } else {
            outputProcessorsList = new ArrayList(Arrays.asList(outputProcessors));
        }
        outputProcessorsList.addAll(outputProcessorsToAdd);
        outputProcessors =
            (OutputMessageProcessor[])outputProcessorsList.toArray(
                       new OutputMessageProcessor[outputProcessorsList.size()]);

    }

    // --------------------------------------------------------- Private methods
    /**
     * Returns true if funambol is in debug mode, false otherwise.
     * If funambol is not in debug mode, the sensitive data are hidden.
     *
     * @return true if funambol is in debug mode, false otherwise
     */
    private boolean isDebugMode() {
        return DEBUG_MODE;
    }
}
