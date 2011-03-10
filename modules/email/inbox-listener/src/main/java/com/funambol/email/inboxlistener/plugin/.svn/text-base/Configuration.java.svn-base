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


/**
 * @version $Id: Configuration.java,v 1.1.1.1 2007-12-11 08:17:19 nichele Exp $
 */
public class Configuration  {

    private String multicastGroup;

    public String getMulticastGroup() {
        return multicastGroup;
    }

    public void setMulticastGroup(String multicastGroup) {
        this.multicastGroup = multicastGroup;
    }

    private int multicastPort;

    public int getMulticastPort() {
        return multicastPort;
    }

    public void setMulticastPort(int _multicastPort) {
        this.multicastPort = _multicastPort;
    }

    private int unicastPort;

    public int getUnicastPort() {
        return unicastPort;
    }

    public void setUnicastPort(int unicastPort) {
        this.unicastPort = unicastPort;
    }


    private int maxThread;

    public int getMaxThread() {
        return maxThread;
    }

    public void setMaxThread(int maxThread) {
        this.maxThread = maxThread;
    }

    private String taskBeanFile;

    public String getTaskBeanFile() {
        return taskBeanFile;
    }

    public void setTaskBeanFile(String taskBeanFile) {
        this.taskBeanFile = taskBeanFile;
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("\nmulticastGroup:");sb.append(getMulticastGroup());
        sb.append("\nmulticastPort:");sb.append(getMulticastPort());
        sb.append("\nunicastPort:");sb.append(getUnicastPort());
        sb.append("\nmaxThread:");sb.append(getMaxThread());
        sb.append("\ntaskBeanFile:");sb.append(getTaskBeanFile());
        return sb.toString();
    }

}
