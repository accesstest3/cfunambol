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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import java.sql.Timestamp;

import java.security.Principal;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.core.*;
import com.funambol.framework.database.Database;
import com.funambol.framework.engine.AbstractSyncItem;
import com.funambol.framework.engine.InMemorySyncItem;
import com.funambol.framework.protocol.CommandIdGenerator;
import com.funambol.framework.protocol.ProtocolUtil;
import com.funambol.framework.engine.SyncOperationStatus;
import com.funambol.framework.engine.SyncOperation;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncProperty;
import com.funambol.framework.engine.SyncItemMapping;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.SyncConflict;
import com.funambol.framework.engine.SyncItemFactory;
import com.funambol.framework.engine.source.*;
import com.funambol.framework.engine.transformer.DataTransformerManager;
import com.funambol.framework.server.ClientMapping;
import com.funambol.framework.server.error.MappingException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.security.Sync4jPrincipal;

/**
 * This class collects helper methods used by the engine.
 *
 * @version $Id: EngineHelper.java,v 1.1.1.1 2008-02-21 23:35:45 stefano_fornari Exp $
 */
public class EngineHelper {

    // --------------------------------------------------------------- Constants
    public static final String LAST_ANCHOR_SLOW_SYNC = "0";

    // ------------------------------------------------------------ Private Data
    public static FunambolLogger log = FunambolLoggerFactory.getLogger(Sync4jEngine.LOG_NAME);


    // ---------------------------------------------------------- Public Methods

    /**
     * Status are grouped as per their original commands and status code so
     * that the number of returned status will be minimal. This is done by
     * generating an hash map where the key is a couple
     * <i>(original command id, status code)</i> and the value is the
     * <i>StatusCommand</i> object. Finally the hash map is iterated over to
     * return the status commands array.<br/>
     * If an operationStatus is a <code>Sync4jOperationStatusError</code> the key
     * of the status is composed by
     * <i>(original command id, status code, itemKey, errorMessage)</i>
     * so an error status can be referred just an one item. When the
     * <code>Status</code> is created, the error message is put as
     * <code>Data</code> of the contained item.<br/>
     *
     * Note that Sync4jOperationStatus's command is meaningful only for items
     * that derive from a SyncML command. If the associated command is null,
     * there is no need to generate a status. Of course, also if the command was
     * flagged as "noResponse" no status is required.
     *
     * @param operationStatus the status object returned as the result of
     *        commands execution
     * @param msgId the message id
     * @param the command id generator
     *
     * @return a <i>StatusCommand[]</i> array containing the status to return
     *         to the client
     */
   public static Status[] generateStatusCommands(SyncOperationStatus[] operationStatus,
                                                 String                msgId          ,
                                                 CommandIdGenerator    idGenerator    ) {
       Map   statusMap = new LinkedHashMap();

       ArrayList itemList  = null;
       StatusKey key       = null;

       ModificationCommand cmd          = null;
       String              errorMessage = null;

       for (int i=0; i < operationStatus.length ; ++i) {

           cmd = ((Sync4jOperationStatus)operationStatus[i]).getCmd();

           if ((cmd == null) || (cmd.isNoResp())) {
               continue; // skip the operation status
           }

           //
           // If the item is mapped then used the mapped key like SourceRef.
           //
           AbstractSyncItem itemA =
               (AbstractSyncItem)operationStatus[i].getOperation().getSyncItemA();

           SyncItemKey sourceRefKey = itemA.getMappedKey();
           if (sourceRefKey == null) {
               sourceRefKey = itemA.getKey();
           }

           errorMessage = null;

           if (operationStatus[i] instanceof Sync4jOperationStatusError) {
               Throwable error = ((Sync4jOperationStatusError)operationStatus[i]).
                                 getError();
               if (error != null) {
                   errorMessage = error.getMessage();
               }
           }

           //
           // Check if a status command has been already created for the given
           // original command and status code. If it is found, than a new
           // item is added to the existing ones, otherwise a new item list
           // is created
           //
           if (StringUtils.isEmpty(errorMessage)) {
               key = new StatusKey(
                       cmd,
                       operationStatus[i].getStatusCode()
                     );
           } else {
               key = new StatusKey(
                       cmd,
                       operationStatus[i].getStatusCode(),
                       sourceRefKey,
                       errorMessage
                     );
           }

           itemList = (ArrayList)statusMap.get(key);

           if (itemList == null){
               itemList = new ArrayList();
               statusMap.put(key, itemList);
           }

           itemList.add(sourceRefKey);
       }

       //
       // Now we can loop over the map and create a list of status commands.
       //
       ArrayList ret = new ArrayList();

       Item[]    items     = null;
       SourceRef sourceRef = null;

       Iterator i = statusMap.keySet().iterator();
       while(i.hasNext()) {
           key = (StatusKey)i.next();
           errorMessage = key.errorMessage;

           itemList = (ArrayList)statusMap.get(key);

           items = new Item[itemList.size()];

           for (int j = 0; j<items.length; ++j) {
               items[j] = new Item(
                              new Source(((SyncItemKey)itemList.get(j)).getKeyAsString()),
                              null,
                              null,
                              null,
                              false
                          );

           }

           if (StringUtils.isNotEmpty(errorMessage) && items.length > 0) {
               //
               // We have an error message...set it as item's data
               //
               items[0].setData(new ComplexData(errorMessage));
               sourceRef = null;

           } else {

               sourceRef = (items.length == 1) ? new SourceRef(items[0].getSource()) : null;
               items = (items.length > 1) ? items : new Item[0];

           }

           ret.add(
               new Status(
                   idGenerator.next()                       ,
                   msgId                                    ,
                   key.cmd.getCmdID().getCmdID()            ,
                   key.cmd.getName()                        ,
                   null                                     ,
                   sourceRef                                ,
                   null /* credential */                    ,
                   null /* challenge  */                    ,
                   new Data(key.statusCode)                 ,
                   items
               )
          );
       }
       return (Status[])ret.toArray(new Status[ret.size()]);
   }



    /**
     * Translates an array of <i>SyncOperation</i> objects to an array of
     * <i>(Add,Delete,Replace)Command</i> objects. Only client side operations
     * are translated.
     *
     * @param clientMapping the associated existing client mapping
     * @param operations the operations to be translated
     * @param sourceName the corresponding source name
     * @param idGenerator the ID generator for command ids
     *
     * @return the commands corresponding to <i>operations</i>
     */
    public static
    ItemizedCommand[] operationsToCommands(ClientMapping      clientMapping,
                                           SyncOperation[]    operations   ,
                                           String             sourceName   ,
                                           CommandIdGenerator idGenerator  ,
                                           boolean            needsMoreInfoIntoItem,
                                           DataTransformerManager dataTransformerManager,
                                           Sync4jPrincipal    principal,
                                           String             mimeType) {

        List commands = new ArrayList();

        SyncItem        item    = null;
        AbstractCommand command = null;

        for (int i=0; ((operations != null) && (i<operations.length)); ++i) {

            if (log.isTraceEnabled()) {
                log.trace( "Converting the operation\n"
                          + operations[i]
                          + "\nfor the source "
                          + sourceName
                          );
            }

            item = operations[i].getSyncItemB(); // this is the server-side item

            if ((operations[i].isAOperation() && (item != null)) ||
                operations[i].isDeleteForced()) {

                char op = operations[i].getOperation();

                //
                // Should not translates a NOP operation
                //
                if (op == SyncOperation.NOP && !operations[i].isDeleteForced()) {
                    continue;
                }
                command = operationToCommand(clientMapping,
                                             operations[i],
                                             idGenerator,
                                             needsMoreInfoIntoItem,
                                             dataTransformerManager,
                                             principal,
                                             mimeType);
                commands.add(command);
            }
        }

        return (ItemizedCommand[])commands.toArray(new ItemizedCommand[commands.size()]);
    }

    /**
     * Translates a <i>SyncOperation</i> object to a <i>(Add,Delete,Replace)Command</i>
     * object.
     *
     * @param clientMapping the item ids mapping
     * @param operation the operation to be translated
     * @param command id generator to use to create command ids
     *
     * @return the command corresponding to <i>operation</i>
     */
    public static ItemizedCommand operationToCommand(ClientMapping          clientMapping         ,
                                                     SyncOperation          operation             ,
                                                     CommandIdGenerator     idGenerator           ,
                                                     boolean                needsMoreInfoIntoItem ,
                                                     DataTransformerManager dataTransformerManager,
                                                     Sync4jPrincipal        principal             ,
                                                     String                 mimeType) {
        ItemizedCommand cmd = null;

        if (idGenerator == null) {
            if (log.isTraceEnabled()) {
                log.trace("idGenerator is null. Cannot continue");
            }
            throw new NullPointerException("idGenerator cannot be null!");
        }

        char op = operation.getOperation();

        SyncItem itemA = null;

        //
        // The item key must reflect the value known by the client agent. It
        // thus must be adjusted using the client mapping. If the
        // operation is an addition the client key is generated
        // by the engine (the client will provide the right key with a
        // subsequent Map command).
        //
        String itemKey = operation.getSyncItemB().getKey().getKeyAsString();
        if (op != SyncOperation.NEW) {
            String mappedKey = clientMapping.getMappedValueForGuid(itemKey);
            //
            // Mapped key may be null in the case of slow sync
            // In such case, we get the mappedKey from the syncItemB
            //
            if (mappedKey != null) {
                itemKey = mappedKey;
            } else {

                AbstractSyncItem itemB = (AbstractSyncItem)operation.getSyncItemB();
                SyncItemKey syncItemMappedKey = itemB.getMappedKey();
                if (syncItemMappedKey == null) {
                    itemKey = itemB.getKey().getKeyAsString();
                } else {
                    itemKey =  syncItemMappedKey.getKeyAsString();
                }
            }
        }

        assert (itemKey != null);

        if (operation.isDeleteForced()) {
            SyncItemKey mappedKey = ((AbstractSyncItem)(operation.getSyncItemA())).getMappedKey();
            if (mappedKey != null) {
                itemKey = mappedKey.getKeyAsString();
            } else {
                itemKey = operation.getSyncItemA().getKey().getKeyAsString();
            }

            cmd = new Delete(
                      idGenerator.next(),
                      false             ,
                      false             ,
                      false             ,
                      null              ,
                      null              , // meta
                      new Item[] {
                              //original: SyncItemHelper.toItem(itemKey,operation.getSyncItemA(),true,false,false)
                              SyncItemHelper.toItem(itemKey,operation.getSyncItemA(),true,false,false)
                          }
                  );
        } else if (op == SyncOperation.NEW) {

            Item[] item = null;
            if (needsMoreInfoIntoItem){
                item = new Item[] {
                          SyncItemHelper.toItem(itemKey,operation.getSyncItemA(),false,true,true,
                                  dataTransformerManager, principal, mimeType)
                      };
            } else {
                item = new Item[] {
                          //original: SyncItemHelper.toItem(itemKey,operation.getSyncItemA(),false,true,true)
                          SyncItemHelper.toItem(itemKey,itemA,false,true,true)
                      };
            }

            cmd = new Add(
                      idGenerator.next(),
                      false             ,
                      null              ,
                      null              , // meta
                      item
                  );
        } else  if (op == SyncOperation.DELETE) {
            cmd = new Delete(
                      idGenerator.next(),
                      false             ,
                      false             ,
                      false             ,
                      null              ,
                      null              , // meta
                      new Item[] {
                          //original: SyncItemHelper.toItem(itemKey,operation.getSyncItemA(),true,false,false)
                          SyncItemHelper.toItem(itemKey,operation.getSyncItemA(),true,false,false)
                      }
                  );
        } else  if (op == SyncOperation.UPDATE) {

            Item[] item = null;
            if (needsMoreInfoIntoItem){
                item = new Item[] {
                          SyncItemHelper.toItem(itemKey,operation.getSyncItemA(),true,false,true,
                                  dataTransformerManager, principal, mimeType)
                      };
            } else {
                item = new Item[] {
                          //original: SyncItemHelper.toItem(itemKey,operation.getSyncItemA(),false,true,true)
                          SyncItemHelper.toItem(itemKey,itemA,true,false,true)
                      };
            }

            cmd = new Replace(
                      idGenerator.next(),
                      false             ,
                      null              ,
                      null              ,
                      item
                  );
        } else if (op == SyncOperation.CONFLICT) {
            if (needsMoreInfoIntoItem) {
                if (log.isTraceEnabled()) {
                    log.trace("SyncOperation.CONFLICT. Cannot be evaluated!!");
                }
            } else {
                //
                // Server wins!
                //
                //View the state of syncItemB and then say to client which the operation
                //to do in order to resolve the conflict
                //
                char s = operation.getSyncItemB().getState();

                if (operation.getSyncItemB().getState() == SyncItemState.NOT_EXISTING) {
                    itemKey = operation.getSyncItemA().getKey().getKeyAsString();
                }

                switch (s) {
                    case SyncItemState.UPDATED:
                    case SyncItemState.SYNCHRONIZED:
                        cmd = new Replace(
                                  idGenerator.next(),
                                  false             ,
                                  null              ,
                                  null              ,
                                  new Item[] {
                                      SyncItemHelper.toItem(
                                          itemKey            ,
                                          itemA              ,
                                          true               ,
                                          false              ,
                                          true
                                      )
                                  }
                              );
                        break;
                    case SyncItemState.DELETED:
                    case SyncItemState.NOT_EXISTING:
                        cmd = new Delete(
                                  idGenerator.next(),
                                  false             ,
                                  false             ,
                                  false             ,
                                  null              ,
                                  null              , // meta
                                  new Item[] {
                                      SyncItemHelper.toItem(
                                          itemKey,
                                          operation.getSyncItemA(),
                                          true,
                                          false,
                                          false
                                      )
                                  }
                              );
                        break;
                    case SyncItemState.NEW:
                        if (SyncConflict.STATE_NEW_NEW.equals(((SyncConflict)operation).getType()) ||
                            SyncConflict.STATE_UPDATED_NEW.equals(((SyncConflict)operation).getType())) {
                            //
                            // If we have a conflict new_new the server merges the server data
                            // with the client data and it has to return to the client
                            // a Replace command with the data merged
                            //
                            cmd = new Replace(
                                      idGenerator.next(),
                                      false             ,
                                      null              ,
                                      null              ,
                                      new Item[] {
                                          SyncItemHelper.toItem(
                                              itemKey,
                                              itemA,
                                              true,
                                              false,
                                              true
                                          )
                                      }
                                  );
                        } else {
                            //itemKey == guid
                            itemKey = operation.getSyncItemB().getKey().getKeyAsString();
                            cmd = new Add(
                                    idGenerator.next(),
                                    false             ,
                                    null              ,
                                    null              , // meta
                                    new Item[] {
                                        SyncItemHelper.toItem(
                                            itemKey            ,
                                            itemA              ,
                                            false              ,
                                            true               ,
                                            true
                                        )
                                    }
                                 );
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return cmd;
    }

    /**
     * Converts an array of <i>Item</i> objects belonging to the same
     * <i>SyncSource</i> into an array of <i>SyncItem</i> objects.
     * <p>
     * The <i>Item</i>s created are enriched with an additional property
     * called as in SyncItemHelper.PROPERTY_COMMAND, which is used to bound the
     * newly created object with the original command.
     *
     * @param syncSource the <i>SyncSource</i> items belong to - NOT NULL
     * @param items the <i>Item</i> objects
     * @param state the state of the item as one of the values defined in
     *              <i>SyncItemState</i>
     * @param the timestamp to assign to the last even on this item
     *
     *
     * @return an array of <i>SyncItem</i> objects
     */
    public static SyncItem[] itemsToSyncItems(ClientMapping       clientMapping,
                                              SyncSource          syncSource   ,
                                              ModificationCommand cmd          ,
                                              char                state        ,
                                              long                timestamp,
                                              SyncItemFactory syncItemFactory) {

        Item[] items = (Item[])cmd.getItems().toArray(new Item[0]);

        if ((items == null) || (items.length == 0)) {
            return new SyncItem[0];
        }

        InMemorySyncItem[] syncItems = new InMemorySyncItem[items.length];

        Data   d       = null;
        String content = null, key = null, mappedKey = null, parentKey = null;
        Meta commandMeta = cmd.getMeta();
        for (int i=0; i<items.length; ++i) {

            content   = null;
            key       = null;
            mappedKey = null;
            parentKey = null;

            d = items[i].getData();
            content = (d != null) ? d.getData() : "";

            key       = items[i].getSource().getLocURI();
            if (items[i].getSourceParent() != null) {
                parentKey = items[i].getSourceParent().getLocURI();
            }
            mappedKey = clientMapping.getMappedValueForLuid(key);

            //
            // NOTE: for the purpose of sync items comparison, the mappedKey,
            // when not null, becomes the real item key, whilst the old key
            // becomes the mapped key.
            //
            if (mappedKey != null) {
                String k  = mappedKey;
                mappedKey = key;
                key       = k;
            }

            syncItems[i] = new InMemorySyncItem(syncSource, key, parentKey, mappedKey, state);

            syncItems[i].setProperty(
                new SyncProperty(AbstractSyncItem.PROPERTY_COMMAND, cmd)
            );

            syncItems[i].setContent(content.getBytes());
            syncItems[i].setProperty(
                    new SyncProperty(AbstractSyncItem.PROPERTY_CONTENT_SIZE,
                                     new Long(content.getBytes().length))
             );
            syncItems[i].setTimestamp(new Timestamp(timestamp));

            Meta m = items[i].getMeta();
            String itemFormat = null;
            String itemType   = null;
            if (m != null) {
                itemFormat = m.getFormat();
                itemType   = m.getType();
            }
            if (commandMeta != null) {
                if (itemFormat == null || itemFormat.length() == 0) {
                    itemFormat = commandMeta.getFormat();
                }
                if (itemType == null || itemType.length() == 0) {
                    itemType = commandMeta.getType();
                }
            }

            syncItems[i].setFormat(itemFormat);
            syncItems[i].setType(itemType);

            if (items[i].isMoreData()) {
                syncItems[i].setState(SyncItemState.PARTIAL);
            }
            //
            // Checks always the item size. This because in the synclets it is
            // possible to modify the items content and their size. As workaround,
            // the synclets can set the size also in the next chunks and the engine
            // must use the last one.
            //
            Long size = ProtocolUtil.getItemSize(cmd.getMeta(), items[i]);
            if (size != null) {
                syncItems[i].setProperty(
                    new SyncProperty(AbstractSyncItem.PROPERTY_DECLARED_SIZE, size)
                );
            }

        }

        return syncItems;
    }

    /**
     * Calculate the intersection between the given lists, returning a new list
     * of <i>SyncItemMapping</i>s each containing the corresponding item couple.
     * Items are matched using the <i>get()</i> method of the <i>List</i> object,
     * therefore the rules for matching items are the ones specified by the
     * <i>List</i> interface.
     * BTW, if an item isn't mapped, it is skipped. In this way, two items with
     * the same key are not mapped (now we have the twins for that).
     *
     * @param a the first list - NOT NULL
     * @param b the seconf list - NOT NULL
     *
     * @return the intersection mapping list.
     */
    public static List intersect(final List a, final List b) {
        int      n     = 0;
        SyncItem itemA = null;
        List     ret   = new ArrayList();
        SyncItemMapping mapping = null;

        Iterator i = a.iterator();
        while(i.hasNext()) {
            itemA = (SyncItem)i.next();
            //
            // If an item isn't mapped, it is skipped.
            // In this way, two items with the same key are not mapped
            // (now we have the twins for that)
            //
            if (((AbstractSyncItem)itemA).getMappedKey() != null) {
                n = b.indexOf(itemA);
                if (n >= 0) {
                    mapping = new SyncItemMapping(itemA.getKey());
                    mapping.setMapping(itemA, (SyncItem) b.get(n));
                    ret.add(mapping);
                }
            }
        }

        return ret;
    }

    /**
     * Remove from the given items the items already used in the operations. If isItemA is true,
     * we check if the items are used as itemA, otherwise we check itemB
     * @param items SyncItem[]
     * @param operations List
     * @return SyncItem[]
     */
    public static SyncItem[] removeItemsInOperations(SyncItem[] items, List operations, boolean isItemA) {

        if (operations == null || operations.isEmpty()) {
            return items;
        }

        List  itemsList = new ArrayList(Arrays.asList(items));

        SyncOperation operation = null;
        SyncItem      item      = null;

        Iterator itOperations = operations.iterator();
        while (itOperations.hasNext()) {
            operation = (SyncOperation)itOperations.next();

            if (isItemA) {
                item = operation.getSyncItemA();
            } else {
                item = operation.getSyncItemB();
            }

            if (itemsList.indexOf(item) != -1) {
                if (log.isTraceEnabled()) {
                    log.trace("The item " + item
                              + " is already used in an operation as "
                              + (isItemA ? "itemA" : "itemB")
                              + ". Removing it from the changed items list"
                    );
                }
                itemsList.remove(item);
            }
        }
        return (SyncItem[])itemsList.toArray(new SyncItem[itemsList.size()]);
    }

    /**
     * Calculate and build the cross set Am(B-Bm) that is the set of synchronized
     * items of the source B interested by the modifications of source A.
     *
     * @param items source A items modified in A but not in B
     * @param source the B source
     * @param principal the pricipal data are related to
     *
     * @return a list of <i>SyncItemMapping</i> representing the item set
     */
    public static List buildAmBBm(List       items    ,
                                  SyncSource source   ,
                                  Principal  principal) {
        SyncItem itemA, itemB;
        SyncItemMapping mapping = null;

        ArrayList ret = new ArrayList();

        Iterator i = items.iterator();
        while (i.hasNext()) {
            itemA = (AbstractSyncItem)i.next();

            //
            // If the mapped key is null, it means that this item is not
            // on the server, therefore, it cannot be in B.
            //
            if (((AbstractSyncItem)itemA).getMappedKey() == null) {
                continue;
            }

            try {
                char state = SyncItemState.SYNCHRONIZED;
                //
                // If the source isn't a FilteableSyncSource, we know the state
                // of the server item because we know that it isn't in Bm so the item
                // isn't changed. BTW, if the source is a FilteableSyncSource, we have
                // to call to the syncsource in order to know the state because
                // maybe the server item is changed but it is outside the filter
                // criteria and so it isn't in Bm.
                //
                if (source instanceof FilterableSyncSource) {
                    state =
                        ((FilterableSyncSource)source).getSyncItemStateFromId(itemA.getKey());
                }
                SyncItemKey parentKey = itemA.getParentKey();
                itemB = new InMemorySyncItem(source,
                                             itemA.getKey().getKeyAsString(),
                                             (parentKey != null ? parentKey.getKeyAsString() : null),
                                             state);
                if (itemB != null) {
                    mapping = new SyncItemMapping(itemA.getKey());
                    mapping.setMapping(itemA, itemB);
                    ret.add(mapping);
                }
            } catch (SyncSourceException e) {
                String msg = "Error retrieving an item by its key "
                           + itemA.getKey()
                           + " from source "
                           + source
                           + ": "
                           + e.getMessage();
                log.error(msg, e);
            }
        }

        return ret;
    }

     /**
     * Calculate and build the cross set (A-Am)Bm that is the set of synchronized
     * items of the source A interested by the modifications of source B.
     *
     * @param items source B items
     * @param source the A source
     * @param principal the pricipal data are related to
     *
     * @return a list of <i>SyncItemMapping</i> representing the item set
     */
    public static List buildAAmBm(List       items    ,
                                  SyncSource source   ,
                                  Principal  principal) {
        SyncItem itemA, itemB;
        SyncItemMapping mapping = null;

        List ret = new ArrayList();

        Iterator i = items.iterator();
        while (i.hasNext()) {
            itemB = (SyncItem)i.next();

            try {
                itemA = source.getSyncItemFromId(itemB.getKey());

                if (itemA != null) {
                    itemA.setState(SyncItemState.SYNCHRONIZED);
                    mapping = new SyncItemMapping(itemB.getKey());
                    mapping.setMapping(itemA, itemB);
                    ret.add(mapping);
                }
            } catch (SyncSourceException e) {
                String msg = "Error retrieving an item by its key "
                           + itemB.getKey()
                           + " from source "
                           + source
                           + ": "
                           + e.getMessage();
                log.error(msg, e);
            }
        }

        return ret;
    }

    /**
     * Update the mapping given an array of operations. This is used for
     * mappings sent by the client.
     *
     * @param clientMappings the client mappings for current synchronization
     * @param operations the operation performed
     * @param slowSync is true if slow synchronization
     * @param databases a Map of Database. It is required to get the lasnAnchor to use
     *        updating the mapping
     *
     */
    public static void updateClientMappings(Map             clientMappings,
                                            SyncOperation[] operations    ,
                                            boolean         slowSync      ,
                                            Map             databases     )
    throws MappingException {

        ClientMapping clientMapping = null;
        String guid = null, luid = null;
        SyncSource clientSource  = null;
        String     sourceUri     = null;
        String     lastAnchor    = null;

        for (int i=0; ((operations != null) && (i<operations.length)); ++i) {
            //
            // Ignore conflicts
            //
            if (operations[i] instanceof SyncConflict) {
                continue;
            }

            clientSource = operations[i].getSyncItemA().getSyncSource();
            if (clientSource == null) {
                //
                // clientSource is null for unexsisting items
                //
                continue;
            }
            sourceUri  = clientSource.getSourceURI();

            lastAnchor = getLastAnchor(sourceUri, databases);

            //
            // If the operation isn't to do neither on A or on B, then not
            // modify the mapping
            //
            if (!operations[i].isAOperation() &&
                !operations[i].isBOperation()) {
                continue;
            }

            clientMapping = (ClientMapping)clientMappings.get(clientSource.getSourceURI());

            AbstractSyncItem itemA = null;
            AbstractSyncItem itemB = null;

            itemA = (AbstractSyncItem)operations[i].getSyncItemA();
            itemB = (AbstractSyncItem)operations[i].getSyncItemB();

            //
            //this is the case of slow sync and without mappings into db
            //
            if (slowSync &&
                operations[i].getOperation() != SyncOperation.DELETE) {

                if (itemA != null &&
                    itemB != null) {

                    guid = operations[i].getSyncItemB().getKey().getKeyAsString();

                    SyncItemKey mappedKeyA = itemA.getMappedKey();
                    if (mappedKeyA != null) {
                        luid = mappedKeyA.getKeyAsString();
                    } else {
                        luid = itemA.getKey().getKeyAsString();
                    }
                    clientMapping.updateMapping(guid , luid, lastAnchor);
                    continue;
                }
            } else if (operations[i].getOperation() == SyncOperation.DELETE) {
                guid = itemB.getKey().getKeyAsString();
                if (operations[i].isAOperation()) {
                    clientMapping.removeMappedValuesForGuid(guid, true);
                } else {
                    clientMapping.removeMappedValuesForGuid(guid, false);
                }
            } else if (itemA.getState() == SyncItemState.DELETED &&
                       itemB.getState() == SyncItemState.DELETED ) {
                guid = itemB.getKey().getKeyAsString();

                clientMapping.removeMappedValuesForGuid(guid, false);

            } else if (itemA.getState() == SyncItemState.NEW &&
                       itemB.getState() == SyncItemState.DELETED ) {
                guid = itemB.getKey().getKeyAsString();
                clientMapping.removeMappedValuesForGuid(guid, false);
            }
        }
    }

    /**
     * Updates the LUID-GUIDmapping for the client modifications (that is the
     * items directlry inserted or removed by the server).
     *
     * @param clientMappings the client mappings for current synchronization
     * @param status the status of the performed operations
     * @param slowSync is true if slow synchronization
     * @param databases a Map of Database. It is required to get the lasnAnchor to use
     *        updating the mapping
     */
    public static void updateServerMappings(Map                   clientMappings,
                                            SyncOperationStatus[] status        ,
                                            boolean               slowSync      ,
                                            Map                   databases     )

    throws MappingException {
        char op;
        ClientMapping clientMapping = null;
        String luid = null, guid    = null;
        String     lastAnchor       = null;
        SyncSource serverSource     = null;
        SyncOperation operation;

        for (int i=0; ((status != null) && (i<status.length)); ++i) {
            operation = status[i].getOperation();
            op = operation.getOperation();

            serverSource = status[i].getSyncSource();
            if (serverSource == null) {
                //
                // clientSource is null for unexsisting items
                //
                continue;
            }

            if (status[i] instanceof Sync4jOperationStatusError) {
                continue;
            }

            lastAnchor = getLastAnchor(serverSource.getSourceURI(), databases);

            clientMapping = (ClientMapping)clientMappings.get(serverSource.getSourceURI());

            AbstractSyncItem itemA = null;
            AbstractSyncItem itemB = null;

            itemA = (AbstractSyncItem)operation.getSyncItemA();
            itemB = (AbstractSyncItem)operation.getSyncItemB();

            if (operation.isDeleteForced()) {

               guid = itemB.getKey().getKeyAsString();
               clientMapping.removeMappedValuesForGuid(guid, true);

           } else if (slowSync &&
                (op != SyncOperation.DELETE) && (op != SyncOperation.NEW)) {
                 //
                 //this is the case of slow sync and without mappings into db
                 //
                if (itemA != null && itemB != null) {

                    guid = itemB.getKey().getKeyAsString();

                    SyncItemKey mappedKeyA = itemA.getMappedKey();
                    if (mappedKeyA != null) {
                        luid = mappedKeyA.getKeyAsString();
                    } else {
                        luid = itemA.getKey().getKeyAsString();
                    }

                    if (operation.isAOperation()) {
                        clientMapping.updateMapping(guid, luid, null);
                    } else if (operation.isBOperation()) {
                        clientMapping.updateMapping(guid, luid, lastAnchor);
                    } else {
                        //
                        // This case happens when during a slow sync the client sends
                        // a item twin of a server item but the merging doesn't produce
                        // a new content. So, no operations are required on A (client)
                        // and no operations are required on B (server)
                        //
                        clientMapping.updateMapping(guid, luid, lastAnchor);
                    }

                    continue;
                }

            } else if(op == SyncOperation.DELETE) {

                guid = itemB.getKey().getKeyAsString();
                if (operation.isAOperation()) {
                    clientMapping.removeMappedValuesForGuid(guid, true);
                } else {
                    clientMapping.removeMappedValuesForGuid(guid, false);
                }
            } else if (op == SyncOperation.NEW) {

                if (operation.isBOperation()) {
                    luid = itemA.getKey().getKeyAsString();
                    guid = itemB.getKey().getKeyAsString();
                    clientMapping.updateMapping(guid, luid, lastAnchor);
                }
                if (operation.isAOperation()) {
                    guid = itemB.getKey().getKeyAsString();
                    clientMapping.updateMapping(guid, guid, null);
                }

            } else if (op == SyncOperation.CONFLICT) {

                String conflictType = ((SyncConflict)operation).getType();

                if (SyncConflict.STATE_DELETED_UPDATED.equals(conflictType) ||
                    SyncConflict.STATE_DELETED_NEW.equals(conflictType) ) {

                    if (!operation.isAOperation() && !operation.isBOperation()) {
                        //
                        // This happen when there is a conflict DU and we are
                        // using some filters so we have to remove the mapping
                        // (see Sync4jStragegy, method execConflictDU)
                        //
                        luid = itemA.getKey().getKeyAsString();
                        guid = itemB.getKey().getKeyAsString();
                        clientMapping.removeMappedValuesForGuid(guid, false);
                    } else {
                        guid = itemB.getKey().getKeyAsString();
                        clientMapping.updateMapping(guid, guid, null);
                    }

                } else if (SyncConflict.STATE_UPDATED_DELETED.equals(conflictType)) {

                    SyncItemKey mappedKey = itemA.getMappedKey();
                    if (mappedKey != null) {
                        luid = mappedKey.getKeyAsString();
                    } else {
                        luid = itemA.getKey().getKeyAsString();
                    }
                    String oldGuid = itemA.getKey().getKeyAsString();
                    //
                    // When a conflict U-D is detected, the server calls the
                    // method addSyncItem on the server ss. This method can create
                    // a new item with a new GUID.
                    // But in the mapping we have LUID-Old GUID.
                    // So, we have to remove it before to insert the new mapping
                    //
                    clientMapping.removeMappedValuesForGuid(oldGuid, false);

                    //
                    // Insert the new mapping
                    //
                    guid = itemB.getKey().getKeyAsString();
                    clientMapping.updateMapping(guid, luid, lastAnchor);

                } else if (SyncConflict.STATE_UPDATED_UPDATED.equals(conflictType)) {

                    SyncItemKey mappedKey = itemA.getMappedKey();
                    if (mappedKey != null) {
                        luid = mappedKey.getKeyAsString();
                    } else {
                        luid = itemA.getKey().getKeyAsString();
                    }
                    guid = itemB.getKey().getKeyAsString();
                    if (operation.isAOperation()) {
                        clientMapping.updateMapping(guid, luid, null);
                    } else {
                        clientMapping.updateMapping(guid, luid, lastAnchor);
                    }
                } else if (SyncConflict.STATE_NEW_NEW.equals(conflictType)) {

                    luid = itemA.getKey().getKeyAsString();
                    guid = itemB.getKey().getKeyAsString();
                    if (operation.isAOperation()) {
                        clientMapping.updateMapping(guid, luid, null);
                    } else {
                        clientMapping.updateMapping(guid, luid, lastAnchor);
                    }

                } else if (SyncConflict.STATE_NEW_UPDATED.equals(conflictType)) {

                    luid = itemA.getMappedKey().getKeyAsString();
                    guid = itemB.getKey().getKeyAsString();

                    clientMapping.updateMapping(guid, luid, lastAnchor);

                } else if (SyncConflict.STATE_UPDATED_NEW.equals(conflictType)) {

                    SyncItemKey mappedKey = itemA.getMappedKey();
                    if (mappedKey != null) {
                        luid = mappedKey.getKeyAsString();
                    } else {
                        luid = itemA.getKey().getKeyAsString();
                    }
                    guid = itemB.getKey().getKeyAsString();
                    if (operation.isAOperation()) {
                        clientMapping.updateMapping(guid, luid, null);
                    } else {
                        clientMapping.updateMapping(guid, luid, lastAnchor);
                    }
                } else if (SyncConflict.STATE_NEW_SYNCHRONIZED.equals(conflictType)) {

                    SyncItemKey mappedKey = itemA.getMappedKey();
                    if (mappedKey != null) {
                        luid = mappedKey.getKeyAsString();
                    } else {
                        luid = itemA.getKey().getKeyAsString();
                    }
                    guid = itemB.getKey().getKeyAsString();
                    if (operation.isAOperation()) {
                        clientMapping.updateMapping(guid, luid, null);
                    } else {
                        clientMapping.updateMapping(guid, luid, lastAnchor);
                    }
                } else if (SyncConflict.STATE_DELETED_DELETED.equals(conflictType)) {

                    guid = itemB.getKey().getKeyAsString();
                    clientMapping.removeMappedValuesForGuid(guid, false);

                } else {

                    //
                    // These cases shuold never happen
                    //
                    assert (!SyncConflict.STATE_DELETED_CONFLICT.equals(conflictType));
                    assert (!SyncConflict.STATE_UPDATED_NEW.equals(conflictType));
                    assert (!SyncConflict.STATE_UPDATED_CONFLICT.equals(conflictType));
                    assert (!SyncConflict.STATE_NEW_DELETED.equals(conflictType));
                    assert (!SyncConflict.STATE_NEW_CONFLICT.equals(conflictType));
                    assert (!SyncConflict.STATE_NONE_CONFLICT.equals(conflictType));
                    assert (!SyncConflict.STATE_CONFLICT_DELETED.equals(conflictType));
                    assert (!SyncConflict.STATE_CONFLICT_UPDATED.equals(conflictType));
                    assert (!SyncConflict.STATE_CONFLICT_NEW.equals(conflictType));
                    assert (!SyncConflict.STATE_CONFLICT_CONFLICT.equals(conflictType));
                }
            }
        }
    }

    /**
     * Creates a <i>DataStore</i> from a <i>SyncSourceInfo</i>
     *
     * @param uri the source URI
     * @param name the source name
     * @param info the <i>SyncSourceInfo</i>
     * @param verDTD the version of the DTD to use to encode the datastores
     *
     * @return the corresponding <i>DataStore</i>
     */
    public static DataStore toDataStore(String uri,
                                        String name,
                                        SyncSourceInfo info,
                                        VerDTD verDTD) {

        CTInfo[] supportedContents = null;
        CTInfo   preferredContent  = null;

        boolean supportHierarchicalSync     = false;

        FilterInfo        filterInfo        = null;
        CTInfo[]    filterRxs  = null;
        FilterCap[]       filterCaps        = null;

        if (info != null) {

            supportHierarchicalSync = info.getSupportHierarchicalSync();

            ContentType[] supportedTypes = info.getSupportedTypes();
            ContentType   preferredType  = info.getPreferredType() ;

            int preferred = info.getPreferred();

            if (supportedTypes.length > 1) {
                //
                // From Rx or Tx we have to remove the Rx-Pref and the Tx-Pref
                //
                supportedContents =
                    new CTInfo[supportedTypes.length - 1];
            }

            int cont = 0;
            for (int i=0; i<supportedTypes.length; ++i) {
                //
                // From Rx or Tx we have to remove the Rx-Pref and the Tx-Pref
                //
                if (i != preferred) {
                    supportedContents[cont++] = new CTInfo(
                        supportedTypes[i].type,
                        supportedTypes[i].version
                    );
                }
            }

            preferredContent =  new CTInfo(
                preferredType.type,
                preferredType.version
            );

            if (Constants.DTD_1_2.equals(verDTD)) {
                //
                // Add the filters info
                //
                filterInfo = info.getFilterInfo();
                if (filterInfo != null) {
                    ContentType[] supportedFilterRx = filterInfo.getFilterRx();
                    if (supportedFilterRx != null) {
                        filterRxs = new CTInfo[supportedFilterRx.length];
                        for (int i=0; i<supportedTypes.length; ++i) {
                            filterRxs[i] = new CTInfo(
                                               supportedFilterRx[i].type,
                                               supportedFilterRx[i].version
                                           );
                        }
                    }

                    FilterSpecification[] filterSpecifications   = filterInfo.getFilterCaps();
                    ContentType           filterContentType      = null;
                    CTInfo                filterCTInfo           = null;
                    if (filterSpecifications != null) {
                        filterCaps = new FilterCap[filterSpecifications.length];

                        for (int i=0; i<filterSpecifications.length; ++i) {
                            filterContentType = filterSpecifications[i].getContentType();
                            if (filterContentType != null) {

                                filterCTInfo =  new CTInfo(
                                                             filterContentType.type,
                                                             filterContentType.version
                                                         );
                            }

                            filterCaps[i] = new FilterCap(filterCTInfo,
                                                          filterSpecifications[i].getKeywords(),
                                                          filterSpecifications[i].getPropName()
                                            );
                        }
                    }
                }
            }
        }

        return new DataStore(
            new SourceRef(uri),
            name,
            32,
            preferredContent ,
            supportedContents,
            preferredContent ,
            supportedContents,
            null,
            null,
            supportHierarchicalSync,
            new SyncCap(SyncType.ALL_SYNC_TYPES),
            filterRxs,
            filterCaps
        );
    }

    /**
     * Resets the item state of the items passed in the given collection to
     * the <i>synchronized</i> state, unless an item is in the <i>partial</i>.
     *
     * @param items items collection
     */
    public static void resetState(Collection items) {
        if (items == null) {
            return;
        }
        setState(items, SyncItemState.SYNCHRONIZED);
    }

    /**
     * Sets the item state of the items passed in the given collection to
     * the give state, unless an item is in the <i>partial</i>.
     *
     * @param items items collection
     */
    public static void setState(Collection items, char state) {
        if (items == null) {
            return;
        }

        Iterator i = items.iterator();
        while (i.hasNext()) {
            SyncItem s = (SyncItem)i.next();
            if (s.getState() != SyncItemState.PARTIAL) {
                s.setState(state);
            }
        }
    }

    /**
     * Resets the item state of the items passed in the given collection to
     * the <i>synchronized</i> state, unless an item is in the <i>partial</i>.
     *
     * @param items items collection
     */
    public static void resetState(SyncItem[] items) {
        if ((items == null) || (items.length == 0)) {
            return;
        }
        setState(items, SyncItemState.SYNCHRONIZED);
    }

    /**
     * Sets the item state of the items passed in the given collection to
     * the given state, unless an item is in the <i>partial</i>.
     *
     * @param items items collection
     */
    public static void setState(SyncItem[] items, char state) {
        if ((items == null) || (items.length == 0)) {
            return;
        }

        for (int i=0; i<items.length; ++i) {
            if (items[i].getState() != SyncItemState.PARTIAL) {
                items[i].setState(state);
            }
        }
    }

    /**
     * Checks the state of the given items.
     * If an item isn't mapped and its state is U, the state is changed to N.
     * If an item is mapped and its state is N, the state is changed to U.
     *
     * @param items SyncItem[]
     */
    public static void checkItemsState(SyncItem[] items) {
        if (items == null || items.length == 0) {
            return ;
        }
        for (int i=0; i<items.length; i++) {
            if (!((AbstractSyncItem)items[i]).isMapped() && items[i].getState() == SyncItemState.UPDATED) {
                items[i].setState(SyncItemState.NEW);
            } else if (((AbstractSyncItem)items[i]).isMapped() && items[i].getState() == SyncItemState.NEW) {
                items[i].setState(SyncItemState.UPDATED);
            }
        }
    }


    /**
     * Creates an array of SyncItem with the given keys, state and source
     * @param keys the keys to use in the SyncItem creation. For each key,
     *        a new item is created
     * @param state the state to use in the SyncItem creation
     * @param source the relative SyncSource
     * @param mapping the mapping to use in order to check the lastAnchor
     * @param lastAnchor the lastAnchor to use in order to check if an item is
     *        already handled in a sync with the same lastAnchor (Suspend and
     *        Resume)
     * @return SyncItem[] the created items
     */
    public static SyncItem[] createSyncItems(SyncItemKey[] keys,
                                             char          state,
                                             SyncSource    source,
                                             ClientMapping mapping,
                                             String        lastAnchor) {
        if (keys == null) {
            return null;
        }

        if (log.isTraceEnabled()) {
            log.trace("Checking the keys returned with state '" + state +
                      "' and last anchor '" + lastAnchor + "'");
        }

        int     numItems            = keys.length;
        List    items               = new ArrayList(keys.length);
        String  lastAnchorInMapping = null;
        boolean itemRequired        = true;

        for (int i=0; i<numItems; i++) {
            itemRequired = true;
            if (state != SyncItemState.DELETED) {
                if (mapping != null) {
                    lastAnchorInMapping =
                            mapping.getLastAnchor(keys[i].getKeyAsString());

                    if (StringUtils.equals(lastAnchor, lastAnchorInMapping)) {
                        if (log.isTraceEnabled()) {
                            log.trace("The item with key '" +
                                      keys[i].getKeyAsString() +
                                      "' is already in the mapping with lastAnchor '" +
                                      lastAnchor + "'...skipping it");
                        }
                        itemRequired = false;
                    } else {
                        if (lastAnchorInMapping != null) {
                            if (log.isTraceEnabled()) {
                                log.trace("The item with key '" +
                                          keys[i].getKeyAsString() +
                                          "' is already in the mapping with lastAnchor '" +
                                          lastAnchorInMapping +
                                          "' but the anchor of " +
                                          "this session is '" + lastAnchor +
                                          "'...adding/updating it");
                            }
                        } else {
                            if (log.isTraceEnabled()) {
                                log.trace("The item with key '" +
                                          keys[i].getKeyAsString() +
                                          "' is not in the mapping...adding it");
                            }

                        }
                    }
                }
            }
            if (itemRequired) {
                items.add(new InMemorySyncItem(source, keys[i].getKeyValue(), state));
            }
        }
        return (SyncItem[])items.toArray(new SyncItem[items.size()]);
    }

    /**
     * Returns the last anchor of the database with the given uri searching it in the
     * given map of databases
     * @param uri String
     * @param databases Map
     * @return String
     */
    public static String getLastAnchor(String uri, Map databases) {
        Database db = (Database) databases.get(uri);
        if (db == null) {
            return null;
        }
        String last = db.getLast();
        return ((last != null && !last.equals("")) ? last : LAST_ANCHOR_SLOW_SYNC);
    }

}

class StatusKey {
    public ModificationCommand  cmd          = null;
    public int                  statusCode   = 0;
    public SyncItemKey          itemKey      = null;
    public String               errorMessage = null;

    public StatusKey(ModificationCommand cmd, int statusCode) {
        this.cmd = cmd;
        this.statusCode = statusCode;
    }

    public StatusKey(ModificationCommand cmd,
                     int statusCode,
                     SyncItemKey itemKey,
                     String errorMessage) {
        this(cmd, statusCode);
        this.itemKey = itemKey;
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if ((o != null) && (o instanceof StatusKey)) {
            if (itemKey == null && StringUtils.isEmpty(errorMessage)) {
                return (((StatusKey) o).cmd.getCmdID().equals(this.cmd.getCmdID()))
                        && (((StatusKey) o).statusCode == this.statusCode);
            }
            if (itemKey == null) {
                return (((StatusKey) o).cmd.getCmdID().equals(this.cmd.getCmdID()))
                        && (((StatusKey) o).statusCode == this.statusCode)
                        && ((StatusKey)o).itemKey.equals(itemKey);
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (cmd.getCmdID().getCmdID() + statusCode).hashCode();
    }
}
