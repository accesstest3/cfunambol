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

/**
 * MBean exposes by the Cluster
 * @version $Id: ClusterMBean.java 36592 2011-02-01 09:26:08Z nichele $
 */
public interface ClusterMBean {

    // --------------------------------------------------------------- Constants
    /** The MBean name */
    public final static String MBEAN_NAME = "com.funambol:type=Cluster";

    /**
     * Returns the list of the members of the cluster. The list is in this form:
     * <p><code>[address1,address2,address3]</code> and if the Cluster is not initialized
     * just <code>[]</code> is returned.
     * @return the list of the members of the cluster in the form
     *         <code>[address1,address2,...]</code>. If the cluster is not initialized,
     *         <code>[]</code> is returned.
     */
    public String getMemberList();

    /**
     * Returns the cluster name, null if the cluster is not initialized
     * @return the cluster name, null if the cluster is not initialized
     */
    public String getClusterName();

    /**
     * Returns the index of the servers in the cluster (starting from 0)
     * @return the index of the servers in the cluster or 0 if the cluster is not
     *         initialized
     */
    public int getServerIndex();
}
