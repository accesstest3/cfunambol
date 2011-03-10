/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.framework.protocol;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import com.funambol.framework.core.AbstractCommand;
import com.funambol.framework.core.Alert;
import com.funambol.framework.core.Anchor;
import com.funambol.framework.core.Authentication;
import com.funambol.framework.core.CTInfo;
import com.funambol.framework.core.CmdID;
import com.funambol.framework.core.Constants;
import com.funambol.framework.core.Cred;
import com.funambol.framework.core.DataStore;
import com.funambol.framework.core.DevInf;
import com.funambol.framework.core.DevInfData;
import com.funambol.framework.core.DevInfItem;
import com.funambol.framework.core.Ext;
import com.funambol.framework.core.Get;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.Meta;
import com.funambol.framework.core.Put;
import com.funambol.framework.core.SessionID;
import com.funambol.framework.core.Source;
import com.funambol.framework.core.SourceRef;
import com.funambol.framework.core.StatusCode;
import com.funambol.framework.core.SyncBody;
import com.funambol.framework.core.SyncCap;
import com.funambol.framework.core.SyncHdr;
import com.funambol.framework.core.SyncType;
import com.funambol.framework.core.Target;
import com.funambol.framework.core.VerDTD;
import com.funambol.framework.core.VerProto;
import java.util.Iterator;

/**
 * SyncInitialization's test
 * @version $Id: SyncInitializationTest.java 33301 2010-01-15 14:40:44Z luigiafassina $
 */
public class SyncInitializationTest extends TestCase {

    /**
     * Why this ?
     * SyncInitialization reads the ds-server version from pom.properties file contained
     * in the ds-server-x.y.z.jar. In order to test the reading method we need to add to
     * the classpath a fake-ds-server-x.y.z.jar; unfortunately there is not a way to do
     * that using maven plugins. There is "resource" plugin that it seemed to do this
     * work, but actually it copies the resources (or testResource) in output/classes (or
     * output/test-classes); those directories are in the classpath but indeed the jar name must
     * be added (i.e. the directory that contains the jar is not enough).
     *
     * In this way the fake jar is added dynamically to the classloader so that SyncInitialization
     * class can find the properties file in the classpath.
     */
    static {
        File f = new File("src/test/data/com/funambol/protocol/SyncInitialization",
                          "fake-ds-server-1.2.3.jar");
        try {
            addFileToClasspath(f);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Private data
    private SyncInitialization init = null;

    // ------------------------------------------------------------ Constructors
    public SyncInitializationTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createDefaultInitialization();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of getResponseHeader method, of class SyncInitialization.
     */
    public void testInitVersion() throws Exception {
        String version = (String) PrivateAccessor.getField(SyncInitialization.class, "DS_VERSION_MSG");
        String unknownVersion = (String) PrivateAccessor.getField(SyncInitialization.class, "UNKNOWN_DS_VERSION");
        System.out.println("Version:  " + version);
        System.out.println("unknownVersion:  " + unknownVersion);
        assertTrue("Version should be not 'unknown'", version.indexOf(unknownVersion) == -1);
        assertTrue("Version should contain '$Revision'", version.indexOf("$Revision") != -1);
    }

    public void testGetResponseCommands_ClientRequiresServerCaps() throws Exception {
        // test when client sends a Get command
        Meta metaGet = new Meta();
        metaGet.setType("application/vnd.syncml-devinf+xml");
        Item[] items = new Item[1];
        items[0] = new DevInfItem(null, new Source("./devinf12"), null, null);
        Get cmd = new Get(new CmdID(2),
                          false /* no response */,
                          null  /* language    */,
                          null  /* credentials */,
                          metaGet                ,
                          items);

        init.getSyncBody().getCommands().add(cmd);

        init.setAuthorizedStatusCode(StatusCode.AUTHENTICATION_ACCEPTED);

        PrivateAccessor.setField(init, "serverCapabilitiesRequest", cmd);

        assertFalse(init.isServerCapsContainedInList());
        List response = init.getResponseCommands("1");
        assertNotNull(response);

        // The response has to contain the Results command with the server
        // capabilitie but not the Put command
        List resultsCmds = 
            ProtocolUtil.filterCommands(response, new String[]{"Results"});
        assertTrue(resultsCmds.size() == 1);
        List putCmds =
            ProtocolUtil.filterCommands(response, new String[]{"Put"});
        assertTrue(putCmds.size() == 0);
        assertTrue(init.isServerCapsContainedInList());
    }

    public void testGetResponseCommands_SentServerCapsTrue() throws Exception {

        // Client doesn't request the server capabilities
        // and sentServerCaps is true: the server must not send its capabilities
        init.setAuthorizedStatusCode(StatusCode.AUTHENTICATION_ACCEPTED);
        init.setSentServerCaps(true);

        assertFalse(init.isServerCapsContainedInList());
        List response = init.getResponseCommands("1");
        assertTrue(response.isEmpty());
        List resultsCmds =
            ProtocolUtil.filterCommands(response, new String[]{"Results"});
        assertTrue(resultsCmds.isEmpty());
        List putCmds =
            ProtocolUtil.filterCommands(response, new String[]{"Put"});
        assertTrue(putCmds.isEmpty());
        assertFalse(init.isServerCapsContainedInList());
    }

    public void testGetResponseCommands_SentServerCapsFalse() throws Exception {

        // Client doesn't request the server capabilities
        // and sentServerCaps is false: the server must send its capabilities
        // in a Put command
        init.setAuthorizedStatusCode(StatusCode.AUTHENTICATION_ACCEPTED);
        init.setSentServerCaps(false);

        assertFalse(init.isServerCapsContainedInList());
        List response = init.getResponseCommands("1");
        assertFalse(response.isEmpty());
        List resultsCmds =
            ProtocolUtil.filterCommands(response, new String[]{"Results"});
        assertTrue(resultsCmds.isEmpty());
        List putCmds =
            ProtocolUtil.filterCommands(response, new String[]{"Put"});
        assertFalse(putCmds.isEmpty());
        assertTrue(init.isServerCapsContainedInList());

        Meta meta = new Meta();
        meta.setType(Constants.MIMETYPE_SYNCML_DEVICEINFO_XML);

        DevInfData data = new DevInfData(getServerCapabilities());
        Source source = new Source("./devinf12");
        DevInfItem[] capabilities =
            new DevInfItem[] {new DevInfItem(null, source, null, data)};

        Put expected = new Put(init.getIdGenerator().next(),
                               false, // NoResp
                               null,  // Lang
                               null,  // Cred
                               meta,
                               capabilities);

        comparePut(expected, (Put)putCmds.get(0));
    }

    // --------------------------------------------------------- Private methods

    /**
     * Adds the given file to the system classpath
     * @param f the file to add
     * @throws java.lang.Exception if an error occurs
     */
    private static void addFileToClasspath(File f) throws Exception {

        URL url = f.toURI().toURL();

        URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class loaderClass = URLClassLoader.class;

        try {
            Method method = loaderClass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(loader, new Object[]{url});
        } catch (Throwable t) {
            throw new Exception("Error adding " + f + " to system classloader");
        }

    }

    private void createDefaultInitialization() throws Exception {
        // test when client sends a Get command
        Meta credMeta = new Meta();
        credMeta.setType("syncml:auth-basic");
        SyncHdr header = new SyncHdr (new VerDTD("1.2"),
                                      new VerProto("SyncML/1.2"),
                                      new SessionID("123456788990"),
                                      "1", // MsgID
                                      new Target("http://localhost:8080/funambol/ds"),
                                      new Source("IMEI:23432432"),
                                      null  /* response URI */ ,
                                      true                    ,
                                      new Cred(new Authentication(credMeta, "Z3Vlc3Q6Z3Vlc3Q=")),
                                      null
        );
        AbstractCommand[] cmds = new AbstractCommand[1];

        Source source = new Source("cal");
        Target target = new Target("cal");
        Meta meta = new Meta();
        meta.setType("text/x-vcalendar");
        meta.setAnchor(new Anchor("0", "1259331645"));
        Item item = new Item(source, target, meta, null, false);
        cmds[0] = new Alert(new CmdID(1), false, null, 201, new Item[]{item});

        SyncBody body = new SyncBody(cmds, false);

        init = new SyncInitialization(header, body);

        init.setServerCapabilities(getServerCapabilities());
        CommandIdGenerator idGenerator = new CommandIdGenerator();
        PrivateAccessor.setField(init, "idGenerator", idGenerator);

    }

    private DevInf getServerCapabilities() {
        DevInf devInf = new DevInf();

        devInf.setVerDTD(new VerDTD("1.2"));
        devInf.setMan("Funambol");
        devInf.setMod("DS Server");
        devInf.setSwV("v8.5");
        devInf.setDevID("funambol");
        devInf.setDevTyp("server");
        devInf.setUTC(true);
        devInf.setSupportLargeObjs(true);
        devInf.setSupportNumberOfChanges(true);
        ArrayList dataStores = new ArrayList();
        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("cal"));
        dataStore.setDisplayName("cal");
        dataStore.setMaxGUIDSize(32L);
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        SyncType[] syncTypes = new SyncType[7];
        syncTypes[0] = new SyncType(1);
        syncTypes[1] = new SyncType(2);
        syncTypes[2] = new SyncType(3);
        syncTypes[3] = new SyncType(4);
        syncTypes[4] = new SyncType(5);
        syncTypes[5] = new SyncType(6);
        syncTypes[6] = new SyncType(7);
        dataStore.setSyncCap(new SyncCap(syncTypes));
        dataStores.add(dataStore);
        devInf.setDataStores(dataStores);
        Ext[] exts = new Ext[1];
        exts[0] = new Ext("X-funambol-smartslow", new String[] {"true"});
        devInf.setExts(exts);

        return devInf;
    }

    private void comparePut(Put expected, Put result) throws Exception {

        assertNotNull("Meta is null", result.getMeta());
        assertEquals("Wrong Meta type",
                     expected.getMeta().getType(), result.getMeta().getType());

        assertTrue("There is no the item that with the DevInf",
                   result.getItems().size() == 1);
        DevInfItem itExp = (DevInfItem)expected.getItems().get(0);
        DevInfItem itRes = (DevInfItem)result.getItems().get(0);
        assertEquals("Wrong Source LocURI", itExp.getSource().getLocURI(),
                     itRes.getSource().getLocURI());

        DevInf diExp = itExp.getDevInfData().getDevInf();
        DevInf diRes = itRes.getDevInfData().getDevInf();
        assertEquals("Wrong VerDTD", diExp.getVerDTD().getValue(),
                     diRes.getVerDTD().getValue());
        assertEquals("Wrong Man", diExp.getMan(), diRes.getMan());
        assertEquals("Wrong Mod", diExp.getMod(), diRes.getMod());
        assertEquals("Wrong SwV", diExp.getSwV(), diRes.getSwV());
        assertEquals("Wrong DevID", diExp.getDevID(), diRes.getDevID());
        assertEquals("Wrong DevTyp", diExp.getDevTyp(), diRes.getDevTyp());
        assertEquals("Wrong UTC", diExp.isUTC(), diRes.isUTC());
        assertEquals("Wrong SupportLargeObjs", diExp.isSupportLargeObjs(),
                     diRes.isSupportLargeObjs());
        assertEquals("Wrong SupportNumberOfChanges",
                     diExp.isSupportNumberOfChanges(),
                     diRes.isSupportNumberOfChanges());

        ArrayList dssExp = diExp.getDataStores();
        ArrayList dssRes = diRes.getDataStores();
        assertEquals("Wrong number of DataStore", dssExp.size(), dssRes.size());

        DataStore dsExp = null;
        DataStore dsRes = null;
        Iterator itDsExp = dssExp.iterator();
        Iterator itDsRes = dssRes.iterator();
        while(itDsExp.hasNext()) {
            dsExp = (DataStore)itDsExp.next();
            boolean found = false;
            while(itDsRes.hasNext()) {
                dsRes = (DataStore)itDsRes.next();
                if (dsExp.getDisplayName().equals(dsRes.getDisplayName())) {
                    found = true;
                    compareDataStore(dsExp, dsRes);
                }
            }
            if (!found) {
                fail("DataStore '" +dsExp.getDisplayName() +"' not found");
                return;
            }
        }

        ArrayList extsExp = diExp.getExts();
        ArrayList extsRes = diRes.getExts();
        assertEquals("Wrong number of Ext", extsExp.size(), extsRes.size());

        Ext extExp = null;
        Ext extRes = null;
        Iterator itExtExp = extsExp.iterator();
        Iterator itExtRes = extsRes.iterator();
        while(itExtExp.hasNext()) {
            extExp = (Ext)itExtExp.next();
            boolean found = false;
            while(itExtRes.hasNext()) {
                extRes = (Ext)itExtRes.next();
                if (extExp.getXNam().equals(extRes.getXNam())) {
                    found = true;
                    compareExt(extExp, extRes);
                }
            }
            if (!found) {
                fail("Ext '" +extExp.getXNam() +"' not found");
                return;
            }
        }

    }

    private void compareDataStore(DataStore expected, DataStore result) {
        assertEquals("Wrong DataStore DisplayName",
                     expected.getDisplayName(), result.getDisplayName());
        assertEquals("Wrong DataStore MaxGUIDSize", expected.getMaxGUIDSize(),
                     result.getMaxGUIDSize());
        assertEquals("Wrong DataStore SourceRef",
                     expected.getSourceRef().getValue(),
                     result.getSourceRef().getValue());

        CTInfo rxPrefExp = expected.getRxPref();
        CTInfo rxPrefRes = result.getRxPref();
        assertEquals("Wrong DataStore RxPref CTType", rxPrefExp.getCTType(),
                     rxPrefRes.getCTType());
        assertEquals("Wrong DataStore RxPref VerCT", rxPrefExp.getVerCT(),
                     rxPrefRes.getVerCT());

        CTInfo txPrefExp = expected.getTxPref();
        CTInfo txPrefRes = result.getTxPref();
        assertEquals("Wrong DataStore TxPref CTType", txPrefExp.getCTType(),
                     txPrefRes.getCTType());
        assertEquals("Wrong DataStore TxPref VerCT", txPrefExp.getVerCT(),
                     txPrefRes.getVerCT());

        assertEquals("Wrong number of SyncType into DataStore SyncCap",
                     expected.getSyncCap().getSyncType().size(),
                     result.getSyncCap().getSyncType().size());
    }

    private void compareExt(Ext expected, Ext result) {
        assertEquals("Wrong Ext XNam", expected.getXNam(), result.getXNam());
        assertEquals("Wrong number of Ext XVal", expected.getXVal().size(),
                     result.getXVal().size());
    }
}

