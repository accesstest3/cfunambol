package com.funambol.job.plugin;
/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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



import com.funambol.job.config.JobExecutorConfiguration;
import com.funambol.job.utils.Def;
import com.funambol.pushlistener.framework.PushListenerInterface;
import com.funambol.pushlistener.framework.plugin.PushListenerPlugin;
import com.funambol.pushlistener.framework.plugin.PushListenerPluginException;
import org.apache.log4j.Logger;
import org.jgroups.ChannelException;

/**
 * This push listener plugin performs the following task: listens for udp packets;
 * as a packet is received, an instance of a OXListenerTask is created and pushed
 * into the push listener tasks' queue for one time execution.
 *
 * All the job is performed by a <code>UDPMulticastReaderThread</code> object,
 * while this class reads configuration bean and pass it to the service object.
 *
 * Submission to the push listener queue is decoupled from packet receiving by
 * a <code>ISubmitter</code> object.
 *
 * @version $Id: ChannelReceiver.java 35046 2010-07-14 14:22:58Z pfernandez $
 */
public class JobExecutorPlugin implements PushListenerPlugin {

    // --------------------------------------------------------------- Constants
    /** The logger */
    private final static Logger log = Logger.getLogger(Def.LOG_NAME);
    // ------------------------------------------------------------ Private data
    private NotificationManager manager = null;
    /** Push listener object to which InboxListenerTasks have to be submitted. */
    private PushListenerInterface pushListenerInterface;
    // -------------------------------------------------------------- Properties
    /**
     * push-listener tool parameter
     * enable the plug-in
     */
    private boolean enabled;
  

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
  

    // ------------------------------------------------------------ Constructors
    /** Creates a new instance of <code>UDPReaderPlugin</code>. */
    public JobExecutorPlugin() {

        if (log.isTraceEnabled()) {
            log.trace("Instantiating UDPReaderPlugin ");
        }

    }

    // ---------------------------------------------------------- Public methods
    /**
     * Reads the configuration for this plugin, creates the service object to
     * which job is delegated and starts it as a thread.
     */
    public void startPlugin() throws PushListenerPluginException {

        if (log.isTraceEnabled()) {
            log.trace("Starting UDPReaderPlugin " + JobExecutorConfiguration.getJobExecutorConfigBean().getNotificationGroupName() + " , "
                    + JobExecutorConfiguration.getJobExecutorConfigBean().getNotificationGroupConfigFileName());
        }
        try {
            manager = new NotificationManager(JobExecutorConfiguration.getJobExecutorConfigBean().getNotificationGroupName(),
                    JobExecutorConfiguration.getJobExecutorConfigBean().getNotificationGroupConfigFileName());
            manager.setReceiverPushListener(this.pushListenerInterface);

            manager.start();
        } catch (ChannelException ex) {
            ex.printStackTrace();
            log.error("Error starting jgroups channel ", ex);
        } catch (NotificationProviderException ex) {
                        ex.printStackTrace();

            log.error("Error starting notification manager ", ex);
        }

    }

    public void stopPlugin() {
        if (manager != null) {
            manager.stop();
        }
    }

    public void setPushListenerInterface(PushListenerInterface _pushListenerInterface) {
        if (log.isTraceEnabled()) {
            log.trace("Setting pushlistener interface in "
                    + this.getClass().getName() + " Plugin: " + _pushListenerInterface);

        }



        this.pushListenerInterface = _pushListenerInterface;
    }

    /**
     *
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    public void startPushListener() throws PushListenerPluginException {
        if (log.isTraceEnabled()) {
            log.trace("startPushListener pushlistener interface in ");
        }
    }

    public void shutdownPushListener() throws PushListenerPluginException {
        if (log.isTraceEnabled()) {
            log.trace("shutdownPushListener pushlistener interface in ");
        }
    }
}
