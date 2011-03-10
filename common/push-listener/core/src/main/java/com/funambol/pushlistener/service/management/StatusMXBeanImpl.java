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

package com.funambol.pushlistener.service.management;


import com.funambol.framework.cluster.Cluster;
import com.funambol.framework.management.AbstractStatusMXBean;
import com.funambol.framework.management.StatusMXBean;

import com.funambol.pushlistener.service.PushListener;

/**
 * StatusMXBean exposed by the push-framework
 * @version $Id: StatusMXBeanImpl.java,v 1.5 2007-11-28 11:15:03 nichele Exp $
 */
public class StatusMXBeanImpl extends AbstractStatusMXBean implements StatusMXBean  {

    private PushListener pushListener = null;

    //-------------------------------------------------------------- Constructor
    public StatusMXBeanImpl(PushListener pushListener){
        this.pushListener = pushListener;

        setVersion(pushListener.getServiceDescriptor().getVersion());
        setServiceName(pushListener.getServiceDescriptor().getServiceName());
    }

    @Override
    public String getStatus() {
        cleanStatus();
        if (Cluster.getCluster().isInitialized()) {
            Cluster cluster = Cluster.getCluster();
            String clusterName = cluster.getClusterName();
            String memberLister = cluster.getMemberList();
            setProperty("cluster", clusterName + memberLister + "[" + cluster.getServerIndex() + "]");
        } else {
            setProperty("cluster", "<N/A>");
        }
        setProperty("loadFactor",  String.valueOf(pushListener.getLoadFactor()));
        setProperty("instantLoad", String.valueOf(pushListener.getInstantLoad()));

        return super.getStatus();
    }
}


