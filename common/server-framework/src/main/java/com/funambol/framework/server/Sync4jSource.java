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
package com.funambol.framework.server;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represent an addressable SyncML source registered to Funambol.
 * It is not intended to store specific SyncSource information.
 *
 * @version $Id: Sync4jSource.java,v 1.2 2007-11-28 11:15:38 nichele Exp $
 */
public class Sync4jSource {

    private String uri;          // the source uri
    private String config;       // the source configuration file
    private String sourceTypeId; // the source type id
    private String sourceName;   //the source name

    /** Creates a new instance of Sync4jSyncSource */
    public Sync4jSource() {
        this(null, null, null, null);
    }

    public Sync4jSource(String uri) {
        this(uri, null, null, null);
    }

    public Sync4jSource(String uri, String config) {
        this(uri,config, null, null);
    }

    public Sync4jSource(String uri         ,
                        String config      ,
                        String sourceTypeId,
                        String sourceName  ) {
        this.uri    = uri   ;
        this.config = config;
        this.sourceTypeId = sourceTypeId;
        this.sourceName = sourceName;
    }

    /**
     * Getter for property uri.
     * @return Value of property uri.
     */
    public String getUri() {
        return uri;
    }

    /**
     * Setter for property uri.
     * @param uri New value of property uri.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Getter for property config.
     * @return Value of property config.
     */
    public String getConfig() {
        return config;
    }

    /**
     * Setter for property config.
     * @param config New value of property config.
     */
    public void setConfig(String config) {
        this.config = config;
    }

    /**
     * Getter for property sourceTypeId.
     * @return Value of property sourceTypeId.
     */
    public String getSourceTypeId() {
        return sourceTypeId;
    }

    /**
     * Setter for property sourceTypeId.
     * @param sourceTypeId New value of property sourceTypeId.
     */
    public void setSourceTypeId(String sourceTypeId) {
        this.sourceTypeId = sourceTypeId;
    }

    /**
     * Getter for property sourceName.
     * @return Value of property sourceName.
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * Setter for property sourceName.
     * @param sourceName New value of property sourceName.
     */
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);

         sb.append("uri",          uri);
         sb.append("config",       config);
         sb.append("sourceTypeId", sourceTypeId);
         sb.append("sourceName",   sourceName);

         return sb.toString();
    }

}
