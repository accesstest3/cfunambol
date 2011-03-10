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

package com.funambol.framework.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

import com.funambol.framework.core.*;

/**
 * This class contains some utility methods used to deal with core classes
 *
 */
public class CoreUtil {
    
    /** Filters the given commands based on the given class
     *
     * @param commands commands to be filtered
     * @param filterClass selector
     *
     * @return a java.util.List containing the selected commands
     *
     */
    public static ArrayList filterCommands(AbstractCommand[] commands, Class filterClass) {
        ArrayList filteredCommands = new ArrayList();

        for (int i=0; i<commands.length; ++i) {
            if (filterClass.isInstance(commands[i])) {
                filteredCommands.add(commands[i]);
            }
        }

        return filteredCommands;
    }

    /**
     * Filters the given commands based on the given command id. A command is
     * selected if it a ResponseCommand (only ResponseCommand subclasses have
     * got the reference command id) and its command id ref matches <i>cmdId</i>.
     *
     * @param commands commands to be filtered
     * @param cmdId selector
     *
     * @return a java.util.List containing the selected commands
     *
     */
    public static ArrayList filterCommands(AbstractCommand[] commands,
                                           CmdID cmdId               ) {
        ArrayList filteredCommands = new ArrayList();

        for (int i=0; i<commands.length; ++i) {
            if (  (commands[i] instanceof ResponseCommand)
               && ((((ResponseCommand)commands[i]).getCmdID()).equals(cmdId))) {
                filteredCommands.add(commands[i]);
            }
        }

        return filteredCommands;
    }

    /**
     * Filters the given commands based on the given command type and command id.
     * It combines <i>filterCommands(commands, filterClass)</i> and
     * <i>filterCommands(commands, cmdId)</i> returning only the commands
     * that respect both requirements.
     *
     * @param commands commands to be filtered
     * @param filterClass class type selector
     * @param cmdId selector
     *
     * @return a java.util.List containing the selected commands
     *
     */
    public static ArrayList filterCommands(AbstractCommand[] commands   ,
                                           Class             filterClass,
                                           CmdID cmdId                  ) {
        //
        // Since filtering on command identifier seems more selective,
        // filterCommands(commands, cmdId) is called first and than
        // filterCommands(..., filterClass) is called with the returned values.
        //
        ArrayList list = filterCommands(commands, cmdId);
        int size = list.size();
        AbstractCommand [] aCommands = new AbstractCommand[size];
        for (int i=0; i < size; i++) {
            aCommands[i] = (AbstractCommand)list.get(i);
        }
        return filterCommands(aCommands, filterClass);
    }

    /**
     * Filters a list of commands extracting the ones of the given types.
     *
     * @param commands the list of command to be filtered
     * @param types the command types to extract
     *
     * @return an array of the selected commmands
     */
    public static List filterCommands(List commands, String[] types) {
        StringBuffer sb = new StringBuffer(",");

        for (int i = 0; ((types != null) && (i < types.length)); ++i) {
            sb.append(types[i]).append(',');
        }

        ArrayList selectedCommands = new ArrayList();
        AbstractCommand command = null;
        Iterator i = commands.iterator();
        while (i.hasNext()) {
            command = (AbstractCommand) i.next();

            if (sb.indexOf(',' + command.getName() + ',') >= 0) {
                selectedCommands.add(command);
            }
        }

        return selectedCommands;
    }

    /**
     * Filters the given commands based on the given command type and command name.
     * It combines <i>filterCommands(commands, filterClass)</i> and
     * <i>filterCommands(commands, cmdId)</i> returning only the commands
     * that respect both requirements.
     *
     * @param commands commands to be filtered
     * @param filterClass class type selector
     * @param cmd the command name selector
     *
     * @return a java.util.List containing the selected commands
     *
     */
    public static AbstractCommand filterCommands(AbstractCommand[] commands,
                                                 Class    filterClass      ,
                                                 String   cmd              ) {

        ArrayList all = filterCommands(commands, filterClass);
        for (int i=0; i<all.size(); ++i) {
            if (((Status)all.get(i)).getCmd().equals(cmd)) {
                return (Status)all.get(i);
            }
        }
        return null;
    }

    /**
     * Filters the given commands returning all commands of all types but the
     * type corresponding to the given class
     *
     * @param commands commands to be filtered
     * @param filterClass selector
     *
     * @return a java.util.List containing the selected commands
     *
     */
    public static ArrayList inverseFilterCommands(AbstractCommand[] commands, Class filterClass) {
        ArrayList filteredCommands = new ArrayList();

        for (int i=0; i<commands.length; ++i) {
            if (!filterClass.isInstance(commands[i])) {
                filteredCommands.add(commands[i]);
            }
        }

        return filteredCommands;
    }
    
        /**
     * Translates a Target object to a Source object
     *
     * @param target the target object - NULL
     *
     * @return a Source object with the same URI and local name of <i>target</i>
     */
    public static Source target2Source(Target target) {
        if (target == null) {
            return null;
        }
        return new Source(target.getLocURI(), target.getLocName());
    }

    /**
     * Translates a Source object to a Target object
     *
     * @param source the source object - NULL
     *
     * @return a Target object with the same URI and local name of <i>source</i>
     */
    public static Target source2Target(Source source) {
        if (source == null) {
            return null;
        }
        return new Target(source.getLocURI(), source.getLocName());
    }

    /**
     * Extracts the target and source refs from an array of items
     *
     * @param items the items to inspect. If null targetRefs and sourceRefs
     *              remain unchanged
     * @param targetRefs a reference to an array that will contain the references
     *                   to the items' targets
     * @param sourceRefs a reference to an array that will contain the references
     *                   to the items' sources
     *
     */
    public static void extractRefs(Item[] items          ,
                                   TargetRef[] targetRefs,
                                   SourceRef[] sourceRefs) {
        if (items == null) {
            return;
        }

        Target t = null;
        Source s = null;
        for (int i=0; i<items.length; ++i) {
            t = items[i].getTarget();
            s = items[i].getSource();

            targetRefs[i] = (t != null) ? new TargetRef(t) : null;
            sourceRefs[i] = (s != null) ? new SourceRef(s) : null;
        }
    }

    /**
     * Checks if a message require a response.<p>
     * A message requires a response if its body contains commands other than:
     * <ul>
     *  <li>Status</i>
     *  <li>Map</i>
     * </ul>
     *
     * @param msg the message to check - NOT NULL and properly constructed
     *
     * @return true if the message requires a response, false otherwise
     *
     */
    public static boolean noMoreResponse(SyncML msg) {
        AbstractCommand[] commands =
        (AbstractCommand[])msg.getSyncBody().getCommands().toArray(
        new AbstractCommand[0]);

        for(int i=0; ((commands != null) && (i<commands.length)); ++i) {
            if (!((commands[i] instanceof Status) ||
                  (commands[i] instanceof com.funambol.framework.core.Map))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the Chal element included in the header status if there is any or
     * null if no chal is given.
     *
     * @param msg the SyncML message object
     *
     * @return Chal element included in the header status if there is any or
     *         null if no chal is given.
     */
    public static Chal getStatusChal(SyncML msg) {
        ArrayList cmdList = msg.getSyncBody().getCommands();

        cmdList = filterCommands(
                      (AbstractCommand[])cmdList.toArray(new AbstractCommand[cmdList.size()]),
                      Status.class,
                      new CmdID("1")
                  );

        if (cmdList.size() == 0) {
            return null;
        }

        return ((Status)cmdList.get(0)).getChal();
    }

    /**
     * Returns the DevInf (Device Information) element.
     *
     * @param msg the SyncML message object
     *
     * @return DevInf elements
     */
    public static DevInf getDevInf(SyncML msg) {
        ArrayList cmdList = msg.getSyncBody().getCommands();

        cmdList = filterCommands(
        (AbstractCommand[])cmdList.toArray(new AbstractCommand[cmdList.size()]),
        Put.class
        );

        if (cmdList.size() <= 0) {
            return null;
        }

        ArrayList itemList = ((Put)cmdList.get(0)).getItems();

        if (itemList == null) {
            return null;
        }

        DevInfItem devInfItem = (DevInfItem)itemList.get(0);
        if (devInfItem == null) {
            return null;
        }
        return devInfItem.getDevInfData().getDevInf();
    }

    /*
     * Returns item that contains large object (moreData == true).
     * Only last command can have more data.
     *
     * @param commands AbstractCommand[]
     * @return item that contains large object or null if
     *         there aren't items with moreData == true
     */
    public static Item getLargeObject(List commands) {
        if (commands.size() == 0) {
            return null;
        }

        AbstractCommand command = null;
        ArrayList items = null;
        Item item = null;

        command = (AbstractCommand)(commands.get(commands.size() -1));

        if (!(command instanceof ItemizedCommand)) {
            return null;
        }
        items = ((ItemizedCommand)command).getItems();
        item = (Item)items.get(items.size() - 1);
        if (item.isMoreData()) {
            return item;
        }
        return null;
    }

    /**
     * Checks if the given command contains a item with more data
     * @param command AbstractCommand
     * @return true if the given command contains a item with more data, false otherwise
     */
    public static boolean hasLargeObject(AbstractCommand command) {
        if (! (command instanceof ItemizedCommand)) {
            return false;
        }
        ArrayList items = ((ItemizedCommand)command).getItems();
        Iterator iItems = items.iterator();
        Item item = null;
        while (iItems.hasNext()) {
            item = (Item)iItems.next();
            if (item.isMoreData()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public static Item getSyncItem(Sync[] syncs, String itemPath) {
        String uri = null;
        int i = 0;
        for (i=0; i<syncs.length; ++i) {
            uri = syncs[i].getTarget().getLocURI() + '/';
            if (itemPath.startsWith(uri)) {
                break;
            }
        }

        if (i == syncs.length) {
            return null;
        }

        String test = null;
        Iterator j = syncs[i].getCommands().iterator();
        while (j.hasNext()) {
            Item item = (Item)((ItemizedCommand)j.next()).getItems().get(0);

            test = uri
                 + ( (item.getSource() != null)
                   ? item.getSource().getLocURI()
                   : item.getTarget().getLocURI())
                 ;

            if (test.equals(itemPath)) {
                return item;
            }
        }

        return null;
    }

    /**
     * Sort an array of Status object in according to their cmdRef
     *
     * @param statusToSort an array of Status object
     *
     * @return an array of sorted Status object
     */
    public static AbstractCommand[] sortStatusCommand(AbstractCommand[] statusToSort) {
        StatusComparator comparator = new StatusComparator();
        Arrays.sort(statusToSort, comparator);
        return statusToSort;
    }

    /**
     * Sort an array of Status object in according to their cmdRef
     *
     * @param statusToSort an array of Status object
     *
     * @return an array of sorted Status object
     */
    public static AbstractCommand[] sortStatusCommand(List statusToSort) {
        AbstractCommand[] array =
            (AbstractCommand[])statusToSort.toArray(new AbstractCommand[statusToSort.size()]);

        StatusComparator comparator = new StatusComparator();
        Arrays.sort(array, comparator);
        return array;
    }

    /**
     * Checks if the given message has any initialization element.
     * Initialization elements are:
     * <ul>
     *   <li>SyncHeader (credentials)</li>
     *   <li>Put (client capabilities)</li>
     *   <li>Get (server capabilities)</li>
     *   <li>Alert (database alerting)</li>
     * </ul>
     *
     * @param msg the message
     *
     * @return true if the message contains initialization elements, false otherwise.
     */
    public static boolean isInitMessage(SyncML msg) {

        Iterator i = msg.getSyncBody().getCommands().iterator();
        while(i.hasNext()) {
            AbstractCommand c = (AbstractCommand)i.next();

            if ((c instanceof Put) || (c instanceof Get)) {
                return true;
            } else if (c instanceof Alert) {
                //
                // Alert 222 and 224 are not init commands
                //
                if (((Alert)c).getData() != AlertCode.NEXT_MESSAGE &&
                    ((Alert)c).getData() != AlertCode.SUSPEND        ) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the given message has any sync command. Sync commands are:
     * <ul>
     *  <li>Sync</li>
     *  <li>Add</li>
     *  <li>Replace</li>
     *  <li>Delete</li>
     *  <li>Alert 222</li>
     * </ul>
     *
     * @param msg the message
     *
     * @return true if the message contains at least one sync elements, false otherwise.
     */
    public static boolean isSyncMessage(SyncML msg) {
        Iterator i = msg.getSyncBody().getCommands().iterator();
        while(i.hasNext()) {
            AbstractCommand c = (AbstractCommand)i.next();

            if ((c instanceof Sync)    ||
                (c instanceof Add)     ||
                (c instanceof Replace) ||
                (c instanceof Delete)   ) {
                return true;
            }

            if ((c instanceof Alert) &&
                (((Alert)c).getData() == AlertCode.NEXT_MESSAGE)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given message has any Map command.
     *
     * @param msg the message
     *
     * @return true if the message contains at least one Map elements, false otherwise.
     */
    public static boolean isMapMessage(SyncML msg) {
        Iterator i = msg.getSyncBody().getCommands().iterator();
        while(i.hasNext()) {
            AbstractCommand c = (AbstractCommand)i.next();

            if (c instanceof com.funambol.framework.core.Map) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method assures that the given list of commands will contain only
     * one SyncHdr Status. All other ones will be removed.
     *
     * @param commands list of commands
     */
    public static void removeHeaderStatus(List commands) {
        boolean first = true;
        ArrayList remove = new ArrayList();

        Iterator i = commands.iterator();
        while (i.hasNext()) {
            AbstractCommand c = (AbstractCommand)i.next();
            if (c instanceof Status) {
                if (SyncHdr.COMMAND_NAME.equals(((Status)c).getCmd())) {
                    if (first == true) {
                        first = false;
                    } else {
                        remove.add(c);
                    }
                }
            }
        }

        commands.removeAll(remove);
    }

    /**
     * Given a list of commands, if there are more than one Sync commands
     * of the same database, all commands are merged in one.
     *
     * @param commands the list of commands to process
     *
     */
    public static void mergeSyncCommands(List commands) {
        ArrayList remove = new ArrayList();

        List syncs = filterCommands(commands, new String[] { Sync.COMMAND_NAME });

        HashMap h = new HashMap();

        String uri = null;
        Sync sync1 = null, sync2 = null;

        Iterator i = syncs.iterator();
        while (i.hasNext()) {
            sync1 = (Sync)i.next();
            uri = sync1.getTarget().getLocURI();
            sync2 = (Sync)h.get(uri);
            if (sync2 == null) {
                h.put(uri, sync1);
            } else {
                sync2.getCommands().addAll(sync1.getCommands());
                //
                // If NumberOfChanges is not null, then this property is 
                // supported by device and so it must be set with the number 
                // of commands to send by server
                //
                if (sync2.getNumberOfChanges() != null) {
                    sync2.setNumberOfChanges(new Long(sync2.getCommands().size()));
                }                
                remove.add(sync1);
            }
        }

        commands.removeAll(remove);
    }

    /**
     * Returns the Meta Size if specified. If not specified, it returns null.
     * Note that the meta information can be specified at the command level or
     * at the item level. In the case of the item level, if there are more than
     * one item, the one that matters is the last one, because it is supposed
     * that being chunked, it is the last one that fits in the message.
     * In any case the size specified at the item level overwrite the size at
     * the command level.
     *
     * @param cmd the command to check for the size
     *
     * @return the Meta Size if specified, null otherwise
     */
    public static Long getLOSize(ModificationCommand cmd) {
        Meta meta = null;
        Long size = null;

        //
        // First check the item.
        //
        List items = cmd.getItems();
        int l = items.size();

        if (l>0) {
            meta = ((Item)items.get(l-1)).getMeta();

            if (meta != null) {
                size = meta.getSize();
            }

            if (size != null) {
                return size;
            }
        }

        //
        // No size found at the item level, let's check the command level
        //
        meta = cmd.getMeta();

        return (meta == null) ? null : meta.getSize();
    }
    
    /**
     * Returns the Meta Size if specified. If not specified, it returns null.
     * Note that the meta information can be specified at the command level or
     * at the item level. In any case the size specified at the item level 
     * overwrites the size at the command level.
     *
     * @param cmdMeta the Meta of the command
     * @param item the item to check for the size
     *
     * @return the Meta Size if specified, null otherwise
     */
    public static Long getItemSize(Meta cmdMeta, Item item) {
        Long size = null;

        //
        // First check the item
        //
        Meta itemMeta = item.getMeta();

        if (itemMeta != null) {
            size = itemMeta.getSize();
        }

        if (size != null) {
            return size;
        }

        //
        // No size found at the item level, let's check the command level
        //
        return (cmdMeta == null) ? null : cmdMeta.getSize();
    }

    /**
     * Reassign ordered cmdId to all commands in the given list.
     *
     * @param commands AbstractCommand[]
     */
    public static void updateCmdId(List commandsList) {
        updateCmdId(commandsList, 1);
    }

    /**
     * Reassign ordered cmdId to all commands in the given list.
     * <br>To the first command is assigned cmdID=startId.
     *
     * @param commands List
     * @param startId the first id to use
     * @return the last id used + 1
     */
    public static int updateCmdId(List commands, int startId) {
        Iterator iCommand = commands.iterator();
        AbstractCommand command = null;
        int id = startId;
        while (iCommand.hasNext()) {
            command = (AbstractCommand)iCommand.next();
            command.setCmdID(new CmdID(id++));
            if (command instanceof Sync) {
                id = updateCmdId( ( (Sync)command).getCommands(), id);
            } else if (command instanceof Atomic) {
                id = updateCmdId( ( (Atomic)command).getCommands(), id);
            } else if (command instanceof Sequence) {
                id = updateCmdId( ( (Sequence)command).getCommands(), id);
            }
        }
        return id;
    }

    /**
     * Checks if the given message contains a SUSPEND alert code
     * @param message SyncML
     * @return true is the message contains a SUSPEND alert code, false otherwise
     */
    public static boolean isSuspendRequired(SyncML message) {
        List commands = message.getSyncBody().getCommands();
        AbstractCommand[] cmds = (AbstractCommand[])commands.toArray(new AbstractCommand[0]);
        Alert alert = getSuspendAlert(cmds);
        if (alert != null) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the given commands contain a SUSPEND alert and return it
     * @param message SyncML
     * @return the alert to suspend the session. <code>null</code> if not found
     */
    public static Alert getSuspendAlert(AbstractCommand[] commands) {
        List alerts   = filterCommands(commands, Alert.class);
        if (alerts != null && alerts.size() > 0) {
            Iterator it        = alerts.iterator();
            Alert    alert     = null;
            int      alertCode = 0;

            while (it.hasNext()) {
                alert = (Alert)it.next();
                alertCode = alert.getData();
                if (AlertCode.SUSPEND == alertCode) {
                    return alert;
                }
            }
        }

        return null;
    }

    // --------------------------------------------------------- Private methods

    /**
     * This class compares two Status object in according to their cmdRef
     */
    private static class StatusComparator implements java.util.Comparator {
        private StatusComparator() {
        }

        public int compare(Object o1, Object o2) {

          Object msgRef1 = null;
          Object msgRef2 = null;

          Object cmdRef1 = null;
          Object cmdRef2 = null;

          msgRef1 = new Integer(((Status)o1).getMsgRef());
          msgRef2 = new Integer(((Status)o2).getMsgRef());

          cmdRef1 = new Integer( ( (Status)o1).getCmdRef());
          cmdRef2 = new Integer( ( (Status)o2).getCmdRef());

          int msgRefCompare = ((Integer)msgRef1).compareTo((Integer)msgRef2);

          //
          // msgRef1 == msgRef2
          //
          if ( msgRefCompare == 0 ) {
              return ( (Integer)cmdRef1).compareTo((Integer)cmdRef2);
          }

          return msgRefCompare;
      }

    }

    
}
