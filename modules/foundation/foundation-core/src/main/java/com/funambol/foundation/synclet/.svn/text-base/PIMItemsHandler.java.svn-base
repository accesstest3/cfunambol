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
package com.funambol.foundation.synclet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.funambol.framework.core.AbstractCommand;
import com.funambol.framework.core.Add;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.ItemizedCommand;
import com.funambol.framework.core.Meta;
import com.funambol.framework.core.Replace;
import com.funambol.framework.core.Sync;
import com.funambol.framework.core.Sync4jException;
import com.funambol.framework.core.SyncML;
import com.funambol.framework.engine.pipeline.MessageProcessingContext;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * This class extracts the PIM Items from the SyncML message.
 * It creates a Map with three lists that contain the items separated based on
 * their type. In case of large object, this class is able to recognize the
 * large object item and is able to handle it using a Map cached in the
 * MessageProcessingContext.
 *
 * @version $Id: PIMItemsHandler.java,v 1.1.1.1 2008-03-20 21:38:42 stefano_fornari Exp $
 */
public class PIMItemsHandler {

    // --------------------------------------------------------------- Constants
    private final FunambolLogger logger =
        FunambolLoggerFactory.getLogger("engine.pipeline");

    private static final String BEGIN_VCARD     = "BEGIN:VCARD"                ;
    private static final String END_VCARD_RN    = "END:VCARD\r\n"              ;
    private static final String END_VCARD_N     = "END:VCARD\n"                ;
    private static final String BEGIN_VCALENDAR = "BEGIN:VCALENDAR"            ;
    private static final String END_VEVENT_RN   =
        "END:VEVENT\r\nEND:VCALENDAR\r\n";
    private static final String END_VEVENT_N    = "END:VEVENT\nEND:VCALENDAR\n";
    private static final String END_VTODO_RN    =
        "END:VTODO\r\nEND:VCALENDAR\r\n" ;
    private static final String END_VTODO_N     = "END:VTODO\nEND:VCALENDAR\n" ;

    public static final String KEY_VCARD  = "VCARD" ;
    public static final String KEY_VEVENT = "VEVENT";
    public static final String KEY_VTODO  = "VTODO" ;
    
    private int numVCardItems  = 0;
    private int numVEventItems = 0;
    private int numVTodoItems  = 0;

    // ---------------------------------------------------------- Public methods

    /**
     * Processes input message to extract the items in order to separate them
     * into lists based on their type (vcard, vevent, vtodo).
     *
     * @param pContext the message processing context
     * @param message the message to be processed
     *
     * @return mapItems the map with the three list of items
     * @throws Sync4jException in case of errors
     */
    public Map extractIncomingPIMItems(MessageProcessingContext pContext,
                                       SyncML message)
    throws Sync4jException {

        if (logger.isTraceEnabled()) {
            logger.trace("Starting creation of incoming items lists...");
        }

        //
        // Caches the map with lists of items into request context: in this way,
        // if more synclet act on the same items, is not need to recycle on
        // items of the message
        //

        //
        // Contains the lists of the items
        //
        Map mapItems = null;
        mapItems = (Map)pContext.getRequestProperty("MAP_INCOMING_ITEMS");

        if (mapItems != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("The map with the items already exists: skipping its creation");
            }

            //
            // In this case the first synclet has just created the map and so
            // the next synclets must act on that without recycle in message.
            //
            return mapItems;
        } else {

            if (logger.isTraceEnabled()) {
                logger.trace("Creating the map containing items lists");
            }
            mapItems = new HashMap();
            initializeMapItems(mapItems);
        }

        //
        // Used to handle large object item
        //
        Item firstItem = null;
        Item lastItem  = null;
        //
        // Cache the large object
        //
        Map cache      = null;
        cache = (Map)pContext.getSessionProperty("LARGE_OBJ_INCOMING");

        String syncURI    = null;
        String itemLocURI = null;

        List<AbstractCommand> cmds = message.getSyncBody().getCommands();
        for (AbstractCommand bodyc : cmds) {

            if (bodyc instanceof Sync) {
                syncURI = ((Sync)bodyc).getTarget().getLocURI();

                //
                // Processes incoming commands to identifier and separate the items.
                //
                List<ItemizedCommand> syncCmds = ((Sync)bodyc).getCommands();
                for (ItemizedCommand c : syncCmds) {

                    //
                    // Skip other commands than Add and Replace
                    //
                    if (!(Replace.COMMAND_NAME.equals(c.getName()) ||
                        Add.COMMAND_NAME.equals(c.getName()))      ) {
                        continue;
                    }

                    List<Item> items = c.getItems();
                    for (Item item: items) {
                        handleItemSize(c, item);

                        itemLocURI = ((item.getSource() != null)
                                      ? item.getSource().getLocURI()
                                      : item.getTarget().getLocURI());

                        if (firstItem == null) {
                            firstItem = item;
                        }

                        if (firstItem == item) {
                            if (cache == null || cache.isEmpty()) {
                                if (item.isMoreData()) {
                                    if (logger.isTraceEnabled()) {
                                        logger.trace("The item " + itemLocURI +
                                                     " is a large object");
                                    }
                                    cache = handleCache(cache, item, syncURI);
                                }
                            } else {
                                cache = handleCache(cache, item, syncURI);
                            }
                        } //end if firstItem == item

                        lastItem = item;

                        if (!item.isMoreData()) {
                            mapItems = fillMapItems(mapItems, item);
                        }

                    } //end for items
                } //end for cmds
            } //end if Sync

            if (lastItem != firstItem) {
                if (lastItem.isMoreData()) {
                    cache = handleCache(cache, lastItem, syncURI);
                }
            }
        } //end if cmds

        pContext.setSessionProperty("LARGE_OBJ_INCOMING", cache);
        pContext.setRequestProperty("MAP_INCOMING_ITEMS", mapItems);

        if (logger.isTraceEnabled()) {
            logger.trace("Number of vcard : " + numVCardItems );
            logger.trace("Number of vevent: " + numVEventItems);
            logger.trace("Number of vtodo : " + numVTodoItems );
        }

        return mapItems;
    }

    /**
     * Processes output message to extract the items in order to separate them
     * into lists based on their type (vcard, vevent, vtodo).
     *
     * @param message the message to be processed
     *
     * @return mapItems the map with the three list of items
     * @throws Sync4jException in case of errors
     */
    public Map extractOutgoingPIMItems(SyncML message)
    throws Sync4jException {
        if (logger.isTraceEnabled()) {
            logger.trace("Starting creation of outgoing items lists...");
        }

        Map mapItems = new HashMap();
        initializeMapItems(mapItems);

        List<AbstractCommand> cmds = message.getSyncBody().getCommands();
        for (AbstractCommand bodyc : cmds) {

            if (bodyc instanceof Sync) {

                //
                // Processes incoming commands to identifier and separate the items.
                // Note: the large object items will not be considered.
                //
                List<ItemizedCommand> syncCmds = ((Sync)bodyc).getCommands();
                for (ItemizedCommand c : syncCmds) {

                    //
                    // Skip other commands than Add and Replace
                    //
                    if (!(Replace.COMMAND_NAME.equals(c.getName()) ||
                        Add.COMMAND_NAME.equals(c.getName()))      ) {
                        continue;
                    }

                    List<Item> items = c.getItems();
                    for (Item item: items) {
                        mapItems = fillMapItems(mapItems, item);
                    }//end for i items
                }//end for c commands
            }//end if Sync
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Number of vcard : " + numVCardItems );
            logger.trace("Number of vevent: " + numVEventItems);
            logger.trace("Number of vtodo : " + numVTodoItems );
        }

        return mapItems;
    }

    /**
     * If the item has the size set, probably it is a large object item. In this
     * case, after the synclet modifications, is need to fix the size property
     * with the real dimension of the data's item.
     *
     * @param i the item to fix the size property
     */
    public void fixLargeObjectSize(Item i) {

        Meta m = i.getMeta();
        if (m == null) {
            return;
        }
        if (m.getSize() != null && m.getSize() != 0) {
            int size = i.getData().getData().length();
            if (logger.isTraceEnabled()) {
                logger.trace("Fixing large object item size from " +
                             m.getSize() + " to " + size);
            }
            i.getMeta().setSize(new Long(size));
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Handles large object storing its information into a Map.
     *
     * @param cache The map in which store the large object informations
     * @param item the large object
     * @param source the source with LocURI of item
     *
     * @return the map in which the large object is cached
     */
    private Map handleCache(Map cache, Item item, String source) {

        if (cache == null || cache.isEmpty()) {
            //
            // It is the first chunk of the large object item
            //
            String uri = (item.getSource() != null)
                       ? item.getSource().getLocURI()
                       : item.getTarget().getLocURI()
                       ;
            cache = new HashMap();
            cache.put("itemLocURI", uri);
            cache.put("syncLocURI", source);
            cache.put("data", item.getData().getData());

        } else {

            String cacheData   = (String)cache.get("data");
            String cacheLocURI = (String)cache.get("syncLocURI")
                               + "/"
                               + (String)cache.get("itemLocURI")
                               ;

            String itemLocURI = source + "/" + ((item.getSource() != null)
                                                ? item.getSource().getLocURI()
                                                : item.getTarget().getLocURI());

            if (cacheLocURI.equals(itemLocURI)) {
                cacheData = cacheData + item.getData().getData();

                if (item.isMoreData()) {
                    cache.put("data", cacheData);
                } else {
                    item.getData().setData(cacheData);
                    if (item.getMeta() == null) {
                        item.setMeta(new Meta());
                    }
                    item.getMeta().setSize(new Long(cacheData.length()));
                    cache.clear();
                }
            } else {
                cache.clear();
            }
        }

        return cache;
    }

    /**
     * Fills the maps of items separated based on their type (vcard, vevent,
     * vtodo).
     *
     * @param mapItems the map in which add the list of items separated based on
     *                 them type
     * @param item the item to handle
     *
     * @return the map with lists of items
     */
    private Map fillMapItems(Map mapItems, Item item) {

        List vcardItems  = (List)mapItems.get(KEY_VCARD );
        List veventItems = (List)mapItems.get(KEY_VEVENT);
        List vtodoItems  = (List)mapItems.get(KEY_VTODO );

        String data = item.getData().getData();

        if ( data.startsWith(BEGIN_VCARD) &&
            (data.endsWith(END_VCARD_RN) ||
            data.endsWith(END_VCARD_N)    )) {

            vcardItems.add(item);
            ++numVCardItems;

        } else if (data.startsWith(BEGIN_VCALENDAR)) {
            if (data.endsWith(END_VEVENT_RN) ||
                data.endsWith(END_VEVENT_N)    ) {

                veventItems.add(item);
                ++numVEventItems;

            } else if (data.endsWith(END_VTODO_RN) ||
                data.endsWith(END_VTODO_N)    ) {

                vtodoItems.add(item);
                ++numVTodoItems;

            }
        }

        return mapItems;
    }

    /**
     * Initializes the Map of items.
     *
     * @param mapItems the empty map
     */
    private void initializeMapItems(Map mapItems) {

        List vcardItems  = new ArrayList();
        List veventItems = new ArrayList();
        List vtodoItems  = new ArrayList();

        mapItems.put(KEY_VCARD , vcardItems );
        mapItems.put(KEY_VEVENT, veventItems);
        mapItems.put(KEY_VTODO , vtodoItems );
        
        numVCardItems  = 0;
        numVEventItems = 0;
        numVTodoItems  = 0;
    }

    /**
     * Handles the item size. The item size should be set in the item itself or
     * in the command that contain the item. Some devices are not able to handle
     * this information and they send a wrong size.
     *
     * The possible cases are the following:
     * - item size null:
     *   - command size null: there is nothing to do
     *   - command size 0: set command size to null
     *   - command size not 0: set item size to the command size and then set
     *     command size to null
     * - item size 0:
     *   - command size null: set item size to null
     *   - command size 0: set item and command size to null
     *   - command size not 0: set item size to command size, and then set
     *     command size to null
     * - item size not 0:
     *   - command size null: there is nothing to do
     *   - command size 0: set command size to null
     *   - command size not 0: set command size to null
     *
     * In some cases the item size is set with the command size because in the
     * handler server side is used the item size: if this information does not
     * exist, the command size (if present obviously) is used.
     * When item size and/or command size is 0, it is set to null because if
     * there is an item, it cannot have a size 0.
     *
     * @param cmd the itemized command
     * @param item the item to be processed
     */
    private void handleItemSize(ItemizedCommand cmd, Item item) {

        Long cmdSize  = null;
        Long itemSize = null;

        Meta cmdMeta  = cmd.getMeta();
        Meta itemMeta = item.getMeta();

        if (cmdMeta != null && cmdMeta.getSize() != null) {
            cmdSize = cmdMeta.getSize();
        }
        if (itemMeta != null && itemMeta.getSize() != null) {
            itemSize = itemMeta.getSize();
        }

        if (itemSize == null) {

            if (cmdSize == null) {
                // do nothing
            } else if (cmdSize == 0) {
                setCommandSize(cmd, null);
            } else if (cmdSize != 0) {
                setItemSize(item, cmdSize);
                setCommandSize(cmd, null);
            }

        } else if (itemSize == 0   ) {

            if (cmdSize == null) {
                setItemSize(item, null);
            } else if (cmdSize == 0) {
                setItemSize(item, null);
                setCommandSize(cmd, null);
            } else if (cmdSize != 0) {
                setItemSize(item, cmdSize);
                setCommandSize(cmd, null);
            }

        } else if (itemSize != 0   ) {

            if (cmdSize == null) {
                // do nothing
            } else if (cmdSize == 0) {
                setCommandSize(cmd, null);
            } else if (cmdSize != 0) {
                setCommandSize(cmd, null);
            }

        }

    }

    /**
     * Sets the item size with the input value.
     *
     * @param item the item to be processed
     * @param value the size to be set in the item. This value can be null.
     */
    private void setItemSize(Item item, Long value) {
        Meta meta = item.getMeta();
        if (meta == null) {
            item.setMeta(new Meta());
        }
        item.getMeta().setSize(value);
    }

    /**
     * Sets the command size with the input value.
     *
     * @param cmd the command to be processed
     * @param value the size to be set in the command. This value can be null.
     */
    private void setCommandSize(ItemizedCommand cmd, Long value) {
        Meta meta = cmd.getMeta();
        if (meta == null) {
            cmd.setMeta(new Meta());
        }
        cmd.getMeta().setSize(value);
    }
}
