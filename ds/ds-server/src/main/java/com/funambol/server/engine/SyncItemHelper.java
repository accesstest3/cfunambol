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

package com.funambol.server.engine;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.ComplexData;
import com.funambol.framework.core.Constants;
import com.funambol.framework.core.Meta;
import com.funambol.framework.core.Target;
import com.funambol.framework.core.Source;
import com.funambol.framework.core.Sync4jException;
import com.funambol.framework.engine.AbstractSyncItem;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.transformer.DataTransformerManager;
import com.funambol.framework.engine.transformer.TransformationInfo;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.security.Sync4jPrincipal;

/**
 * Helper class that contains usefull methods used to convert <i>SyncItem</i>s
 * into <i>Item</i>s and vice versa.
 *
 * @version  $Id: SyncItemHelper.java,v 1.1.1.1 2008-02-21 23:35:50 stefano_fornari Exp $
 */
public class SyncItemHelper {

    // ---------------------------------------------------------------- Costants

    public static FunambolLogger log = FunambolLoggerFactory.getLogger(Sync4jEngine.LOG_NAME);

    // ---------------------------------------------------------- Public methods

    /**
     * Converts a generic <i>SyncItem</i> into an <i>Item</i> object.
     * @return the <i>Item</i> object
     * @param syncItem the syncitem to convert
     * @param key the key the item is known by the client agent
     * @param includeTarget <i>true</i> if the target must be included
     * @param includeSource <i>true</i> if the source must be included
     * @param includeData <i>true</i> if data must be included
     */
    public static Item toItem(String     key                  ,
                              SyncItem   syncItem             ,
                              boolean    includeTarget        ,
                              boolean    includeSource        ,
                              boolean    includeData          ) {

        Target target = includeTarget ? new Target(key) : null;
        Source source = includeSource ? new Source(key) : null;
        ComplexData data = null;

        Item item = new Item(source,
                             target,
                             null,   // meta
                             data,
                             false); //moreData


        if (includeData) {
            item.setIncompleteInfo(true);
        }

        return item;
    }


    /**
     * Converts a generic <i>SyncItem</i> into an <i>Item</i> object.
     * for a different Engine
     *
     * @return the <i>Item</i> object
     * @param syncItem the syncitem to convert
     * @param key the key the item is known by the client agent
     * @param includeTarget <i>true</i> if the target must be included
     * @param includeSource <i>true</i> if the source must be included
     * @param includeData <i>true</i> if data must be included
     */
    public static Item toItem(String                 key                   ,
                              SyncItem               syncItem              ,
                              boolean                includeTarget         ,
                              boolean                includeSource         ,
                              boolean                includeData           ,
                              DataTransformerManager dataTransformerManager,
                              Sync4jPrincipal        principal             ,
                              String                 mimeType) {

        // set data (<DATA>) field
        ComplexData data = null;
        try {
            if (includeData){ 
                if (syncItem != null){

                    applyDataTransformationsOnOutgoingItem(syncItem, dataTransformerManager, principal);

                    // The mimeType is xml...escaping the item's content
                    if (Constants.MIMETYPE_SYNCMLDS_XML.equals(mimeType)) {
                        String content = new String(syncItem.getContent());
                        content = escapeData(content);
                        syncItem.setContent(content.getBytes());
                    }

                    data = new ComplexData(new String(syncItem.getContent()));

                }
            }
        } catch (Sync4jException ex) {
            /**
             * @todo
             */
            log.error("Error converting item with key: " + key, ex);
        }


        // set target and source
        Target target = null;
        Source source = null;
        if (includeTarget) {
            target = new Target(key);
        }

        if (includeSource) {
            source = new Source(key);
        }


        // set meta information
        String itemFormat = syncItem.getFormat();
        String itemType   = syncItem.getType();
        Meta meta = null;
        if (itemFormat != null || itemType != null) {
            meta = new Meta();
            if (itemFormat != null) {
                meta.setFormat(itemFormat);
            }
            if (itemType != null) {
                meta.setType(itemType);
            }
        }


        //
        SyncItemKey parentKey = syncItem.getParentKey();

        //
        Item item = new Item(source,
                             target,
                             meta,   // meta
                             data,
                             false); //moreData


        // anyway we set "uncomplete" because in the
        // method ***Engine.completeInfo there are other info
        // to be completed
        item.setIncompleteInfo(true);

        return item;
    }

    /**
     * Create a new <i>SyncItem</i> with the content of an existing one. The
     * old SyncItem's key becomes the mappedKey of the new SyncItem.
     * @param key the key
     * @param fromSyncItem the original item
     * @return the new item
     */
    public static SyncItem newMappedSyncItem(SyncItemKey key, AbstractSyncItem fromSyncItem) {
        AbstractSyncItem syncItem = (AbstractSyncItem) fromSyncItem.cloneItem();

        syncItem.setKey(new SyncItemKey(key.getKeyAsString()));
        syncItem.setMappedKey(new SyncItemKey(fromSyncItem.getKey().getKeyAsString()));

        return syncItem;
    }

    /**
     * Create the resolving client syncItem in case of conflict resolved with
     * server data.
     * @param syncItemServer
     * @param syncItemClient
     * @return
     */
    public static AbstractSyncItem newResolvingClientItem(AbstractSyncItem syncItemServer,
                                                          AbstractSyncItem syncItemClient) {

        AbstractSyncItem resolvingItem = (AbstractSyncItem) syncItemServer.cloneItem();
        resolvingItem.setKey(new SyncItemKey(syncItemClient.getMappedKey().getKeyAsString()));
        resolvingItem.setMappedKey(null);
        resolvingItem.setState(SyncItemState.UNKNOWN);

        return resolvingItem;
    }

    /**
     * Escapes the given string replacing:
     * <ui>
     *   <li><CODE>&amp;</CODE> with <CODE>&amp;amp;</CODE></li>
     *   <li><CODE>&lt;</CODE> with <CODE>&amp;lt;</CODE></li>
     *   <li><CODE>]]&gt;</CODE> with <CODE>]]&amp;gt;</CODE></li>
     * </ui>
     * <br/>Note that if the given string is a CDATA (it starts with <CODE>&lt;![CDATA[ </CODE>
     * and ends with <CODE>]]&gt;</CODE>) no replace
     * is done
     * @param data the data to escape
     * @return the escaped data
     */
    public static String escapeData(String data) {
        if (data == null) {
            return null;
        }
        int len = data.length();
        if (len == 0) {
            return data;
        }
        //
        // CDATA is not escaped
        //
        if (data.startsWith("<![CDATA[") && data.endsWith("]]>")) {
            return data;
        }
        //
        // creating a new StringBuilder increasing the size of 10%...this is
        // just an assumption
        //
        StringBuilder sb = new StringBuilder((int)(len * 1.1));

        for (int i = 0; i < len; i++) {
            char chr = data.charAt(i);
            if (chr == '&') {
                sb.append("&amp;");
            } else if (chr == '<') {
                sb.append("&lt;");
            } else if (chr == '>' && i > 2  &&
                       data.charAt(i-1) == ']' &&
                       data.charAt(i-2) == ']') {
                sb.append("&gt;");
            } else {
                sb.append(chr);
            }
        }
        return sb.toString();
    }


    /**
     * Applies the required data transformation on the go out items
     *
     * @param item the syncitem to transform
     * @param dataTransformerManager
     * @param principal
     * @throws com.funambol.framework.core.Sync4jException
     */
    private static void applyDataTransformationsOnOutgoingItem(SyncItem item,
                                                               DataTransformerManager dataTransformerManager,
                                                               Sync4jPrincipal principal)
      throws Sync4jException {

        TransformationInfo info = new TransformationInfo();

        info.setCredentials (principal.getEncodedCredentials());
        info.setUsername    (principal.getUsername()          );
        info.setUserPassword(principal.getUser().getPassword());

        dataTransformerManager.transformOutgoingItem(info, item);
        
    }

}
