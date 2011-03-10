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

package com.funambol.framework.tools;

import junit.framework.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.funambol.framework.core.*;

/**
 *
 * @author ste
 */
public class CoreUtilTest extends TestCase {
    
    //
    // This list does not represent a real case or something anyway possible
    // in SyncML.
    //
    public final AbstractCommand[] commandList1 = new AbstractCommand[] {
        new Alert(new CmdID("1"), true, null, 200, null), 
        new Sync(new CmdID("2"), true, null, null, null, null, null, new AbstractCommand[0]),
        new Add(new CmdID("3"), true, null, null, null),
        new Add(new CmdID("4"), true, null, null, null),
        new Delete(new CmdID("5"), true, false, false, null, null, null),
        new Replace(new CmdID("6"), true, null, null, null),
        new Status(new CmdID("7"), "1", "1", "Add", new TargetRef[0], new SourceRef[0], null, null, new ComplexData("200"), null)
    };
    
    public CoreUtilTest(String testName) {
        super(testName);
    }
    
    public void testFilterCommandsByClassName() {
        //
        // POSITIVES
        //
        List ret = CoreUtil.filterCommands(commandList1, Add.class);
        
        assertEquals(2, ret.size());
        assertEquals(ret.get(0).getClass(), Add.class);
        assertEquals(ret.get(1).getClass(), Add.class);
        
        ret = CoreUtil.filterCommands(commandList1, ItemizedCommand.class);
        Assert.assertEquals(6, ret.size());
        Iterator i = ret.iterator(); Object o = null;
        while (i.hasNext()) {
            o = i.next();
            if (!(o instanceof ItemizedCommand)) {
                fail("" + o + " is not an instance of ItemizedCommand");
            }
        }
        
        //
        // NEGATIVES
        //
        ret = CoreUtil.filterCommands(commandList1, Exec.class);
        Assert.assertEquals(0, ret.size());
    }
    
    public void testFilterCommandsByCommandID() {
        //
        // POSITIVES
        //
        CmdID id = new CmdID("7");
        List ret = CoreUtil.filterCommands(commandList1, id);
        
        assertEquals(1, ret.size());
        assertEquals(((AbstractCommand)ret.get(0)).getCmdID(), id);
        if (!(ret.get(0) instanceof ResponseCommand)) {
            Assert.fail(ret.get(0) + " is not of type ResponseCommand");
        }
        
        //
        // NEGATIVES
        //
        id = new CmdID("10");
        ret = CoreUtil.filterCommands(commandList1, id);
        
        assertEquals(0, ret.size());
    }
    
    public void testFilterCommandsByIDAndClassName() {
        //
        // POSITIVES
        //
        CmdID id = new CmdID("7");
        List ret = CoreUtil.filterCommands(commandList1, Status.class, id);
        
        assertEquals(1, ret.size());
        assertEquals(((AbstractCommand)ret.get(0)).getCmdID(), id);
        if (!(ret.get(0) instanceof ResponseCommand)) {
            Assert.fail(ret.get(0) + " is not of type ResponseCommand");
        }
        
        //
        // NEGATIVES
        //
        assertEquals(0, CoreUtil.filterCommands(commandList1, Status.class, new CmdID("10")).size());
        assertEquals(0, CoreUtil.filterCommands(commandList1, Alert.class, id).size());
    }
    
    public void testFilterCommandsByClassNames() {
        //
        // POSITIVES
        //
        List ret = CoreUtil.filterCommands(
                Arrays.asList(commandList1), 
                new String[] {"Add", "Replace", "Delete"}
        );
        
        assertEquals(4, ret.size());
        assertEquals(ret.get(0).getClass(), Add.class);
        assertEquals(ret.get(1).getClass(), Add.class);
        assertEquals(ret.get(3).getClass(), Replace.class);
        assertEquals(ret.get(2).getClass(), Delete.class);
        
        ret = CoreUtil.filterCommands(
                Arrays.asList(commandList1), 
                new String[] {"Add", "Replace", "Exit"}
        );
        Assert.assertEquals(3, ret.size());
        assertEquals(ret.get(0).getClass(), Add.class);
        assertEquals(ret.get(1).getClass(), Add.class);
        assertEquals(ret.get(2).getClass(), Replace.class);
        
        //
        // NEGATIVES
        //
        ret = CoreUtil.filterCommands(
                Arrays.asList(commandList1), 
                new String[0]
        );
        Assert.assertEquals(0, ret.size());
        
        ret = CoreUtil.filterCommands(
                Arrays.asList(commandList1), 
                new String[] {"Exec", "SyncML"}
        );
        Assert.assertEquals(0, ret.size());
    }

    public void testInverseFilterCommands() {
        //
        // POSITIVES
        //
        List ret = CoreUtil.inverseFilterCommands(commandList1, Add.class);
        
        assertEquals(5, ret.size());
        assertEquals(ret.get(0).getClass(), Alert.class  );
        assertEquals(ret.get(1).getClass(), Sync.class   );
        assertEquals(ret.get(2).getClass(), Delete.class );
        assertEquals(ret.get(3).getClass(), Replace.class);
        assertEquals(ret.get(4).getClass(), Status.class );

        //
        // NEGATIVES
        //
        ret = CoreUtil.inverseFilterCommands(commandList1, Exec.class);
        Assert.assertEquals(7, ret.size());
        Iterator i = ret.iterator(); int c = 0;
        while (i.hasNext()) {
            assertEquals(i.next().getClass(), commandList1[c++].getClass());
        }
    }

    public void testTarget2Source() {
    }

    public void testSource2Target() {
    }

    public void testExtractRefs() {
    }

    public void testNoMoreResponse() {
    }

    public void testGetStatusChal() {
    }

    public void testGetDevInf() {
    }

    public void testGetLargeObject() {
    }

    public void testHasLargeObject() {
    }

    public void testGetSyncItem() {
    }

    public void testSortStatusCommand() {
    }

    public void testIsInitMessage() {
    }

    public void testIsSyncMessage() {
    }

    public void testIsMapMessage() {
    }

    public void testRemoveHeaderStatus() {
    }

    public void testMergeSyncCommands() {
    }

    public void testGetLOSize() {
    }

    public void testUpdateCmdId() {
    }

    public void testIsSuspendRequired() {
    }

    public void testGetSuspendAlert() {
    }

    /**
     * Test of filterCommands method, of class com.funambol.framework.tools.CoreUtil.
     */
    public void testFilterCommands() {
    }

    /**
     * Test of getItemSize method, of class com.funambol.framework.tools.CoreUtil.
     */
    public void testGetItemSize() {
    }
    
    public static void main(String[] args) throws Exception {
        CoreUtilTest t = new CoreUtilTest(CoreUtil.class.getName());
    }

}
