/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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
package com.funambol.framework.engine.pipeline;

import com.funambol.framework.core.Sync4jException;

/**
 * This exception is used by message processing pipeline components to tell the
 * pipeline manager that the processing of the next components should be skipped.
 * Note that this is a suggestion for the manager, who could determine to 
 * execute anyway the components following in the pipeline. See the documentation
 * of the manager to see how the StopProcessingException is handled.
 *
 *
 *
 * @version $Id: StopProcessingException.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class StopProcessingException extends Sync4jException {
    
    /** 
     * Creates a new instance of StopProcessingException 
     *
     * @param msg additional information to provide to the manager
     */
    public StopProcessingException(String msg) {
        super(msg);
    }
}
