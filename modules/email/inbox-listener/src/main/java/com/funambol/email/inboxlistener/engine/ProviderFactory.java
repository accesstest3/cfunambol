/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.email.inboxlistener.engine;

import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.inboxlistener.plugin.parser.KeyAccount;
import com.funambol.email.model.MailServerAccount;

/**
 * This class represent a facgtory for <code>IMailServerAccountProvider</code>
 * implementations.
 *
 * @version $Id: ProviderFactory.java,v 1.1.1.1 2007-12-11 08:17:19 nichele Exp $
 */
public class ProviderFactory {
    
    /**
     * Creates a new instance of a <code>IMailServerAccountProvider</code> class.
     * <p/>
     * The implementing class is selected as follows using key properties:
     * <p/>
     * id    msAddress msMailboxname username class 
     * -------------------------------------------------------------------
     * !=-1                                   IDAccountProvider
     * -1    not null  null          null     EmailAddressAccountProvider
     * -1    null      not null      null     MailboxnameAccountProvider
     * -1    null      null          not null UsernameAccountProvider
     *
     * <code>null</code> otherwise.
     * 
     */
    public static IMailServerAccountProvider getProvider(
            KeyAccount key,
            String pushRegistryTableName)
    throws InboxListenerException{
        
        long id              = key.getId();
        String msAddress     = key.getMsAddress();
        String msMailboxname = key.getMsMailboxname();
        String username      = key.getUsername();
        
        if (id != -1) {
            
            return new IDAccountProvider(pushRegistryTableName);
            
        } else if (id == -1 && msAddress != null &&
                msMailboxname == null && username == null) {
            
            return new EmailAddressAccountProvider(pushRegistryTableName);
            
        } else if (id == -1 && msAddress == null &&
                msMailboxname != null && username == null) {
            
            return new MailboxnameAccountProvider(pushRegistryTableName);
            
        } else if (id == -1 && msAddress == null &&
                msMailboxname == null && username != null) {
            
            return new UsernameAccountProvider(pushRegistryTableName);
            
        }
        
        return null;
    }
    
}
