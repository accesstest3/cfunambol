/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.email.inboxlistener.plugin.submitter;

import com.funambol.email.inboxlistener.plugin.parser.IUDPMessageParser;
import com.funambol.email.inboxlistener.plugin.parser.KeyAccount;
import com.funambol.email.inboxlistener.plugin.parser.UDPMessageParserException;
import com.funambol.email.inboxlistener.service.InboxListenerTask;
import com.funambol.email.util.Def;
import com.funambol.framework.tools.beans.BeanException;
import com.funambol.framework.tools.beans.BeanTool;
import com.funambol.pushlistener.framework.PushListenerException;
import com.funambol.pushlistener.framework.PushListenerInterface;
import com.funambol.pushlistener.service.BaseThreadFactory;
import com.funambol.pushlistener.service.config.PushListenerConfiguration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * This submitter can be used to decouple request for task insertion in the
 * push listener queue from task submission to the queue itself.
 *
 * Each time a request for a task is submitted, it is inserted in a temporary queue.
 * A pool of threads is responsable for creating a task and and push it to the
 * push listener queue. Thus frequency of task request and submission can be different.
 *
 * @version $Id: BufferedSubmitter.java,v 1.1.1.1 2007-12-11 08:17:19 nichele Exp $
 */
public class BufferedSubmitter implements ISubmitter {
    
    // ------------------------------------------------------------ Private data
    
    /**
     * Pool of threads which are responsable for extracting tasks from the queue
     * and push them to the push listener queue.
     */
    private ThreadPoolExecutor poolExecutor;
    
    private Logger log = Logger.getLogger(Def.LOGGER_NAME);
    
    // -------------------------------------------------------------- Properties
    
    /** Max number of the threads in the pool. */
    private int maxThread;
    
    /**   */
    private String prefix;
    
    // ---------------------------------------------------- Properties accessors
    
    public void setMaxThread(int maxThread) {
        this.maxThread = maxThread;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    // ---------------------------------------------------------- Public methods
    
    /**
     * Creates a new instance of <code>BufferedSubmitter</code> .
     */
    public BufferedSubmitter() {
    }
    
    /**
     * Each thread in the pool extracts a request from the temporary queue, parse
     * it, creates a task of the specified class and sets its properties using
     * data from the parsed request. Then the task is submitted to the push
     * listener.
     */
    public void submit(
            final String taskBeanFile,
            final byte[] input,
            final IUDPMessageParser parser,
            final PushListenerInterface pushListenerInterface){
        
        if (log.isTraceEnabled()) {
            log.trace("Entering submit");
        }
        
        if (poolExecutor == null){
            if (log.isTraceEnabled()) {
                log.trace("creating pool executor");
            }
            poolExecutor = new ThreadPoolExecutor(1, maxThread,
                    60, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    new BaseThreadFactory(prefix));
        }

        final BeanTool beanTool = BeanTool.getBeanTool(
                PushListenerConfiguration.getConfigPath());
        
        poolExecutor.execute(new Runnable() {
            public void run() {
                try {
                    
                    if (log.isTraceEnabled()) {
                        String logMsg = new String(input);
                        if (logMsg != null){
                            logMsg = logMsg.trim();
                        }
                        log.trace("Start parsing message [" + logMsg + "]") ;
                    }
                    
                    // parse
                    KeyAccount key = parser.parse(input);
                    
                    if (log.isTraceEnabled()) {
                        log.trace("Submitting to the Push Listener Framework Queue " +
                                "the task related to the parsed message [" + key.toString() + "]") ;
                    }

                    InboxListenerTask task = 
                            (InboxListenerTask)beanTool.getNewBeanInstance(taskBeanFile);
                    
                    task.setKey(key);
                    
                    // submit on pushlistener queue
                    pushListenerInterface.executeTask(task);
                    
                    if (log.isTraceEnabled()) {
                        log.trace("Task submitted.");
                    }
                    
                } catch (BeanException e)  {
                    log.error("Error creating inbox listener task for taskBeanFile: " +
                            taskBeanFile, e);
                } catch (PushListenerException e) {
                    log.error("Error while executing inbox listener task", e);
                } catch (UDPMessageParserException e) {
                    log.error("Error parsing message: " + input, e);
                } catch (Exception e)  {
                    log.error("Generic error while creating inbox listener task", e);
                }
            }
        });
        
        if (log.isTraceEnabled()) {
            log.trace("End submit");
        }
    }
}
