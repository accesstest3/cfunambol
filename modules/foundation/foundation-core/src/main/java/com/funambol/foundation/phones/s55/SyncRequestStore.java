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
package com.funambol.foundation.phones.s55;

import java.util.ArrayList;

import com.funambol.framework.core.*;
import com.funambol.framework.engine.pipeline.InputMessageProcessor;
import com.funambol.framework.engine.pipeline.MessageProcessingContext;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.CoreUtil;

/**
 * This class store the synchronization alert code into processingContext.
 *
 * @version $Id: SyncRequestStore.java,v 1.1.1.1 2008-03-20 21:38:41 stefano_fornari Exp $
 */
public class SyncRequestStore implements InputMessageProcessor {

    // ------------------------------------------------------------ Private data
    private static final FunambolLogger log =
            FunambolLoggerFactory.getLogger("engine.pipeline");


    // ---------------------------------------------------------- Public methods

    /**
     * Process input message and set MessageProcessingContext property.
     *
     * @param processingContext the message processing context
     * @param message the message to be processed
     * @throws Sync4jException
     */
    public void preProcessMessage(MessageProcessingContext processingContext,
                                  SyncML message) throws Sync4jException {
        if (log.isTraceEnabled()) {
            log.trace("preProcessMessage: SyncRequestStore of input message");
        }

        Put clientCapabilities = null;

        try {
            AbstractCommand[] allCmd =
            (AbstractCommand[])message.getSyncBody().getCommands().toArray(
                                                        new AbstractCommand[0]);
            //
            //Extract only alert commands
            //
            for (int i=0; (allCmd != null) && (i<allCmd.length); i++) {
                if (Alert.COMMAND_NAME.equals(allCmd[i].getName())) {
                    Alert alertCmd = (Alert)allCmd[i];
                    int code = alertCmd.getData();
                    processingContext.setRequestProperty(SlowFastSyncManagement.PROPERTY_CODE, String.valueOf(code));
                }//end if cmds
            }//end allCmd

            //
            //Extract the type of device
            //
            ArrayList clientCapabilitiesList = 
                CoreUtil.filterCommands(allCmd, Put.class);

            if (clientCapabilitiesList.size()>0) {
                clientCapabilities = (Put)clientCapabilitiesList.get(0);

                if (log.isTraceEnabled()) {
                    log.trace("preProcessMessage: name clientCapabilities "
                                                + clientCapabilities.getName());
                }

                DevInfItem item = (DevInfItem)clientCapabilities.getItems().get(0);
                DevInf di  = item.getDevInfData().getDevInf();
                String man = di.getMan();
                processingContext.setRequestProperty(SlowFastSyncManagement.PROPERTY_MANTYPE, man);
            }
        } catch(Exception e) {
            log.error("Errore preprocessing the request", e);
        }
    }

}
