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

package com.funambol.pushlistener.framework.plugin;

import com.funambol.pushlistener.framework.PushListenerInterface;

/**
 * A PushListener plugin is an object that can be attached to the push listener
 * and that interacts with it receiving notification for the main events and submitting
 * task to performe.
 * <p>The PushListener framework notifies the plugins for the following events:
 * <ui>
 *    <li>pushlistener starting: the push listener is starting (see method
 *        <CODE>startPushListener</CODE>)
 *    </li>
 *    <li>pushlistener ready for events from the plugin: the push listener is running
 *        and ready for events from the plugin (see method <CODE>startPlugin</CODE>)
 *    </li>
 *    <li>pushlistener shutting down: the push listener is shutting down so the plugin
 *        must stop its requests/events (see method <CODE>stopPlugin</CODE>)
 *    </li>
 *    <li>pushlistener shuts down: the push listener stops its activity
 *        (see method <CODE>shutdownPushListener</CODE>)
 *    </li>
 * </ui>
 * <p>
 * Note that a plugin doesn't work directly with a PushListener instance but
 * with a <CODE>com.funambol.pushlistener.framework.PushListenerInterface</CODE>.
 *
 * @version $Id: PushListenerPlugin.java,v 1.2 2007-11-28 11:14:54 nichele Exp $
 */
public interface PushListenerPlugin {

    /**
     * Is the plugin enabled ?
     * @return true if the plug in is enabled, false otherwise. Disabled plugins 
     *  will be ignored by the PushListener
     */
    public boolean isEnabled();

    /**
     * Called by the PushListener to notify the Plugin that it is starting
     * @throws PushListenerPluginException Used by the plugin to notify the PushListener
     * that an error occurs so it doesn't want to be notified anymore.
     */
    public void startPushListener() throws PushListenerPluginException;

    /**
     * Called from the PushListener framework to start the plugin. It is called
     * when the PushListener is already started
     * @throws PushListenerPluginException Used by the plugin to notify the PushListener
     * tha an error occurs so it doesn't want to be notified anymore.
     */
    public void startPlugin() throws PushListenerPluginException;

    /**
     * Called from the PushListener framework to stop the plugin. It is called
     * when the PushListener starts the shutting down procedure
     * @throws PushListenerPluginException Used by the plugin to notify the PushListener that 
     * an error occurs so it doesn't want to be notified anymore.
     */
    public void stopPlugin() throws PushListenerPluginException;

    /**
     * Called when the PushListener shuts down
     * @throws PushListenerPluginException Used by the plugin to notify the PushListener
     *         that an error occurs
     */
    public void shutdownPushListener() throws PushListenerPluginException;

    /**
     * Called to give an instance of the PushListenerInterface to the plugin so
     * that it can interact with the push listener
     *
     * @param pushListenerInterface the PushListenerInterface
     */
    public void setPushListenerInterface(PushListenerInterface pushListenerInterface);

}
