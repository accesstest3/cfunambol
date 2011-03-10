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
package com.funambol.framework.core;

import java.util.*;

/**
 * This class represents the &lt;Search&gt; tag as defined by the SyncML
 * representation specifications.
 *
 * @version $Id: Search.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class Search
extends AbstractCommand
implements java.io.Serializable {

    // --------------------------------------------------------------- Constants

    public static String COMMAND_NAME = "Search";

    // ------------------------------------------------------------ Private data

    private Boolean   noResults;
    private Target    target   ;
    private ArrayList sources = new ArrayList();
    private String    lang     ;
    private Data      data     ;

    // ------------------------------------------------------------ Constructors

    /**
     * For serialization purposes
     */
    protected Search() {}

    /**
     * Creates a new Search object.
     *
     * @param cmdID command identifier - NOT NULL
     * @param noResp is &lt;NoResponse/&gt; required?
     * @param noResults is &lt;NoResults/&gt; required?
     * @param cred  authentication credentials
     * @param target target
     * @param sources sources - NOT NULL
     * @param lang preferred language
     * @param meta meta data - NOT NULL
     * @param data contains the search grammar - NOT NULL
     *
     *  @throws java.lang.IllegalArgumentException if any NOT NULL parameter is null
     *
     */
    public Search(final CmdID    cmdID    ,
                  final boolean  noResp   ,
                  final boolean  noResults,
                  final Cred     cred     ,
                  final Target   target   ,
                  final Source[] sources  ,
                  final String   lang     ,
                  final Meta     meta     ,
                  final Data     data     ) {
        super(cmdID, noResp);

        setCred(cred);
        setMeta(meta);
        setSources(sources);
        setData(data);

        this.noResults = (noResults) ? new Boolean(noResults) : null;
        this.target    = target;
        this.lang      = lang  ;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns noResults
     *
     * @return noResults
     *
     */
    public boolean isNoResults() {
        return (noResults != null);
    }

    /**
     * Sets noResults
     *
     * @param noResults the noResults value
     */
    public void setNoResults(Boolean noResults) {
        this.noResults = (noResults.booleanValue()) ? noResults : null;
    }

    /**
     * Gets the Boolean value of noResults property
     *
     * @return noResults if boolean value is true, otherwise null
     */
    public Boolean getNoResults() {
        if (noResults == null || !noResults.booleanValue()) {
            return null;
        }
        return noResults;
    }

    /**
     * Returns target property
     * @return target the Target property
     */
    public Target getTarget() {
        return target;
    }

    /**
     * Sets target property
     *
     * @param target the target property
     */
    public void setTarget(Target target) {
        this.target = target;
    }

    /**
     * Returns command sources
     * @return command sources
     */
    public ArrayList getSources() {
        return sources;
    }

    /**
     * Sets command sources
     *
     * @param sources the command sources - NOT NULL
     *
     * @throws IllegalArgumentException if sources is null
     */
    public void setSources(Source[] sources) {
        if (sources == null) {
            throw new IllegalArgumentException("sources cannot be null");
        }
        this.sources.clear();
        this.sources.addAll(Arrays.asList(sources));
    }

    /**
     * Returns the preferred language
     *
     * @return the preferred language
     *
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the preferred language
     *
     * @param lang the preferred language
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * Returns data
     *
     * @return data
     *
     */
    public Data getData() {
        return data;
    }

    /**
     * Sets data
     *
     * @param data the command's data - NOT NULL
     *
     * @throws IllegalArgumentException id data is null
     */
    public void setData(Data data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        this.data = data;
    }

    /**
     * Returns the command name
     *
     * @return the command name
     */
    public String getName() {
        return Search.COMMAND_NAME;
    }
}
