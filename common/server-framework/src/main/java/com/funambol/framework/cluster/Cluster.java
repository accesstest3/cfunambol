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

package com.funambol.framework.cluster;

import java.lang.management.ManagementFactory;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.jmx.JmxConfigurator;

/**
 *
 * @version $Id: Cluster.java 36592 2011-02-01 09:26:08Z nichele $
 */
public class Cluster extends ReceiverAdapter implements ClusterMBean {

    // ------------------------------------------------------------ Private data

    /** The instance for the singleton pattern */
    private static Cluster instance = new Cluster();

    /** The logger */
    private final static Logger log = Logger.getLogger("funambol.cluster");

    /** The JGroups channel */
    private Channel channel = null;

    /** The listeners to notify for changes in the cluster */
    private List<ClusterListener> listeners = null;

    /** The cluster configuraiton used initializing the cluster */
    private ClusterConfiguration config = null;

    // -------------------------------------------------------------- Properties

    /** Is the cluster initialized ? */
    private boolean initialized = false;

    // ----------------------------------------------------- Private constructor
    /**
     * Creates a new instance of Cluster
     */
    private Cluster() {
        listeners = new ArrayList<ClusterListener>();
    }

    // --------------------------------------------------- Public static methods

    /**
     * Return the unique cluster instance
     * @return the unique cluster instance
     */
    public static Cluster getCluster() {
        return instance;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Initializes the cluster instance with the given <code>ClusterConfiguration</code>.
     *
     * @param configuration the configuration to use
     * @throws com.funambol.framework.cluster.ClusterException if an
     *         error occurs in the cluster initialization or if the cluster is already
     *         initialized
     */
    public synchronized void init(ClusterConfiguration configuration) throws ClusterException {

        if (initialized) {
            throw new ClusterException("Cluster already initialized");
        }

        if (configuration == null) {
            throw new IllegalArgumentException("The ClusterConfiguration object must be not null");
        }

        configuration.validateConfiguration();

        this.config = configuration;
        if (log.isInfoEnabled()) {
            log.info("Starting cluster with: " + configuration);
        }

        try {
            if (configuration.getConfigurationFile() != null) {
                if (log.isTraceEnabled()) {
                    log.trace("Reading cluster configuration file: " + configuration.getConfigurationFile());
                }
                channel = new JChannel(configuration.getConfigurationFile());
            } else {
                channel = new JChannel(configuration.getProperties());
            }

            channel.setOpt(Channel.AUTO_RECONNECT, true);
            channel.setOpt(Channel.LOCAL,          false);

        } catch (ChannelException ex) {
            throw new ClusterException("Error creation JGroups channel", ex);
        }

        if (log.isInfoEnabled()) {
            log.info("Cluster initialization...");
        }

        try {
            channel.connect(configuration.getClusterName());
        } catch (ChannelException ex) {
            throw new ClusterException("Error connecting to the JGroups channel (name: " +
                                       configuration.getClusterName() + ")",
                                       ex);
        }

        channel.setReceiver(this);

        if (log.isInfoEnabled()) {
            StringBuilder sb = new StringBuilder("Cluster members: ");
            sb.append(getMemberList());
            log.info(sb);
        }
        if (log.isTraceEnabled()) {
            View clusterView = channel.getView();
            log.trace("Cluster view details: " + clusterView);
        }

        //
        // Publishing ClusterMBean
        //
        if (log.isInfoEnabled()) {
            log.info("Registering MBean: " + MBEAN_NAME);
        }
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {

            // Construct the ObjectName for the MBean we will register
            ObjectName name = new ObjectName(MBEAN_NAME);

            mbs.registerMBean(this, name);
        } catch (Exception ex) {
            log.error("Error registering mbean '" + MBEAN_NAME + "'", ex);
        }

        if (channel instanceof JChannel) {
            //
            // Publishing JGroups MBeans
            //
            try {
                JmxConfigurator.registerChannel((JChannel) channel,
                                                mbs,
                                                "com.funambol.channel",
                                                null,  // clusterName (null means use channel.getClusterName()
                                                true); // register protocol
            } catch (Exception ex) {
                log.warn("Unable to register JGroups MBean (" + ex + ")");
            }
        }
        initialized = true;
    }

    /**
     * Returns true if the cluster has been initialized, false otherwise
     * @return true if the cluster has been initialized, false otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Returns the list of the members of the cluster. The list is in this form:
     * <p><code>[address1,address2,address3]</code> and if the Cluster is not initialized
     * just <code>[]</code> is returned.
     * @return the list of the members of the cluster in the form
     *         <code>[address1,address2,...]</code>. If the cluster is not initialized,
     *         <code>[]</code> is returned.
     */
    public String getMemberList() {
        return Utility.getMemberList(channel);
    }

    /**
     * Returns the number of the servers in the cluster
     * @return the number of the servers in the cluster or 1 if the cluster is not
     *         initialized
     */
    public int getClusterSize() {
        if (!initialized) {
            //
            // We are not in a cluster, so just 1 member
            //
            return 1;
        }
        return channel.getView().getMembers().size();
    }

    /**
     * Returns the index of the servers in the cluster (starting from 0)
     * @return the index of the servers in the cluster or 0 if the cluster is not
     *         initialized
     */
    public int getServerIndex() {
        if (!initialized) {
            //
            // We are not in a cluster, so just 1 member with index 0
            //
            return 0;
        }
        return channel.getView().getMembers().indexOf(channel.getLocalAddress());
    }

    /**
     * Returns the cluster name, null if the cluster is not initialized
     * @return the cluster name, null if the cluster is not initialized
     */
    public String getClusterName() {
        if (!initialized) {
            //
            // We are not in a cluster, so no name
            //
            return null;
        }
        return channel.getClusterName();
    }

    /**
     * Adds a new <code>ClusterListener</code>
     * @param listener the listener to add
     */
    public void addClusterListener(ClusterListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a new <code>ClusterListener</code>
     * @param listener the listener to add
     * @return true if the listener has been removed, false otherwise
     */
    public boolean removeClusterListener(ClusterListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public void viewAccepted(View view) {

        if (log.isInfoEnabled()) {
            log.info("Detected change in the cluster. New member list: " + getMemberList());
        }
        if (log.isTraceEnabled()) {
            log.trace("Cluster view details: " + view);
        }

        //
        // Notifyng listeners
        //
        int myIndex         = getServerIndex();
        int numberOfMembers = getClusterSize();

        for (ClusterListener listener : listeners) {
            listener.clusterChanged(numberOfMembers, myIndex);
        }
    }

    /**
     * Shutdowns the cluster closing the channel
     */
    public void shutdown() {
        if (!initialized) {
            return;
        }

        if (log.isInfoEnabled()) {
            log.info("Shutting down cluster '" + config.getClusterName() + "'");
        }
        if (channel != null && channel.isConnected()) {
            if (log.isInfoEnabled()) {
                log.info("Closing channel");
            }
            channel.close();
        }
        initialized = false;
    }

}
