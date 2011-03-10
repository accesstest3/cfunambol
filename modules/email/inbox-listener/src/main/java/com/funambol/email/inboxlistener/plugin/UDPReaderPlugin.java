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
package com.funambol.email.inboxlistener.plugin;

import com.funambol.email.inboxlistener.plugin.parser.IUDPMessageParser;
import com.funambol.email.inboxlistener.plugin.submitter.ISubmitter;
import com.funambol.email.util.Def;
import com.funambol.pushlistener.framework.PushListenerInterface;
import com.funambol.pushlistener.framework.plugin.PushListenerPluginAdapter;
import com.funambol.pushlistener.framework.plugin.PushListenerPluginException;
import org.apache.log4j.Logger;


/**
 * This push listener plugin performs the following task: listens for udp packets;
 * as a packet is received, an instance of a InboxListenerTask is created and pushed
 * into the push listener tasks' queue for one time execution.
 *
 * All the job is performed by a <code>UDPUnicastReaderThread</code> object,
 * while this class reads configuration bean and pass it to the service object.
 *
 * Submission to the push listener queue is decoupled from packet receiving by
 * a <code>ISubmitter</code> object.
 *
 * @version $Id: UDPReaderPlugin.java,v 1.1.1.1 2007-12-11 08:17:19 nichele Exp $
 */
public class UDPReaderPlugin extends PushListenerPluginAdapter {

    // --------------------------------------------------------------- Constants

    private Logger log = Logger.getLogger(Def.LOGGER_NAME);

    // ------------------------------------------------------------ Private data

    /** Service object. */
    private UDPUnicastReaderThread readerThread;

    /** Service object. */
    private UDPMulticastReaderThread multicastReaderThread;

    /** Push listener object to which InboxListenerTasks have to be submitted. */
    private PushListenerInterface pushListenerInterface;

    // -------------------------------------------------------------- Properties

    /** enable the plug-in */
    private boolean enabled;

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** enable the unicast listener */
    private boolean enableUnicast;

    public void setEnableUnicast(boolean enableUnicast) {
        this.enableUnicast = enableUnicast;
    }

    /** multicast port */
    private int unicastPort;

    public int getUnicastPort() {
        return unicastPort;
    }

    public void setUnicastPort(int unicastPort) {
        this.unicastPort = unicastPort;
    }



    /** enable the multicast listener */
    private boolean enableMulticast;

    public void setEnableMulticast(boolean enableMulticast) {
        this.enableMulticast = enableMulticast;
    }

    /** multicastGroup for the multicast reader */
    private String multicastGroup;

    public String getMulticastGroup() {
        return multicastGroup;
    }

    public void setMulticastGroup(String _multicastGroup) {
        this.multicastGroup = _multicastGroup;
    }

    /** multicast port */
    private int multicastPort;

    public int getMulticastPort() {
        return multicastPort;
    }

    public void setMulticastPort(int multicastPort) {
        this.multicastPort = multicastPort;
    }

    /** */
    private String taskBeanFile;

    public String getTaskBeanFile() {
        return taskBeanFile;
    }

    public void setTaskBeanFile(String taskBeanFile) {
        this.taskBeanFile = taskBeanFile;
    }

    /** Submitter */
    private ISubmitter submitter;

    public void setSubmitter(ISubmitter submitter) {
        this.submitter = submitter;
    }

    /** parser */
    private IUDPMessageParser parser;

    public void setParser(IUDPMessageParser parser) {
        this.parser = parser;
    }

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of <code>UDPUnicastReaderPlugin</code>. */
    public UDPReaderPlugin(){
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Reads the configuration for this plugin, creates the service object to
     * which job is delegated and starts it as a thread.
     */
    @Override
    public void startPlugin() throws PushListenerPluginException {

        Configuration conf = new Configuration();
        conf.setMulticastGroup(multicastGroup);
        conf.setMulticastPort(multicastPort);
        conf.setUnicastPort(unicastPort);
        conf.setTaskBeanFile(taskBeanFile);

        if (log.isTraceEnabled()) {
            log.trace("Starting " + this.getClass().getName() +
                    " Plugin with: [" + conf.toString() + "]");
        }

        try {
            // run the listener for the external source
            if(enableUnicast){
                readerThread = new UDPUnicastReaderThread(conf,
                        submitter, parser, pushListenerInterface);
                readerThread.start();
            }

            // run the listener for the external source
            if(enableMulticast){
                multicastReaderThread = new UDPMulticastReaderThread(conf,
                        submitter, parser, pushListenerInterface);
                multicastReaderThread.start();
            }
        } catch (Exception e) {
            throw new PushListenerPluginException(e);
        }

    }

    @Override
    public void stopPlugin() {
        if (log.isTraceEnabled()) {
            log.trace("Stopping " + this.getClass().getName() + " Plugin");
        }
        readerThread.stopReading();
    }

    @Override
    public void setPushListenerInterface(PushListenerInterface _pushListenerInterface) {
        if (log.isTraceEnabled()) {
            log.trace("Setting pushlistener interface in "
                    + this.getClass().getName() + " Plugin: " + pushListenerInterface);
        }
        this.pushListenerInterface = _pushListenerInterface;
    }

    /**
     *
     */
    public boolean isEnabled() {
        return this.enabled;
    }

}