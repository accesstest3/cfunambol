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

/**
 * @version $Id: SyncSourceException.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
package com.funambol.framework.engine.source;

import com.funambol.framework.engine.SyncException;

public class SyncSourceException extends SyncException {
   private String uri    = null;
   private String config = null;

   public SyncSourceException(String msg) {
        super(msg);
    }

   public SyncSourceException(String msg, boolean silent) {
        super(msg, silent);
    }

   public SyncSourceException(String msg, int statusCode) {
        super(msg, statusCode);
    }

   public SyncSourceException(String msg, int statusCode, boolean silent) {
        super(msg, statusCode, silent);
    }

   public SyncSourceException(String msg, Throwable cause) {
        super(msg, cause);
    }

   public SyncSourceException(String msg, Throwable cause, boolean silent) {
        super(msg, cause, silent);
    }

   public SyncSourceException(Throwable cause) {
        super(cause);
    }

   public SyncSourceException(Throwable cause, boolean silent) {
        super(cause, silent);
    }

   public SyncSourceException(String uri, String config, Throwable cause) {
        super(cause);
        this.uri    = uri;
        this.config = config;
    }

   public SyncSourceException(String uri, String config, Throwable cause, boolean silent) {
        super(cause, silent);
        this.uri    = uri;
        this.config = config;
    }

   public String getUri() {
        return this.uri;
    }

   public String getConfig() {
        return this.config;
    }   
}
