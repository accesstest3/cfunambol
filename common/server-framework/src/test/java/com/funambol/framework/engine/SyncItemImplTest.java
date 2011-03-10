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

package com.funambol.framework.engine;

import com.funambol.framework.engine.source.SyncSource;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import java.sql.Timestamp;

/**
 *
 * @author testa
 */
public class SyncItemImplTest extends TestCase {

    private final static String firstPropertyValue = new String("first-value");
    private final static String secondPropertyValue = new String("second-value");
    private final static String thirdPropertyValue = new String("third-value");

    private final static String firstPropertyKey = new String("first-key");
    private final static String secondPropertyKey = new String("second-key");
    private final static String thirdPropertyKey = new String("third-key");

    private final static SyncProperty firstProperty = new SyncProperty(firstPropertyKey, firstPropertyValue);
    private final static SyncProperty secondProperty = new SyncProperty(secondPropertyKey, secondPropertyValue);
    private final static SyncProperty thirdProperty = new SyncProperty(thirdPropertyKey, thirdPropertyValue);

    
    public SyncItemImplTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of setProperties method, of class SyncItemImpl.
     */
    public void testSetProperties() {

        SyncItemImpl instance = new SyncItemImpl();

        Map properties = getProperties();
        instance.setProperties(properties);

        SyncProperty expectedFirstProperty = instance.getProperty(firstProperty.getName());
        assertEquals(expectedFirstProperty.getName(), firstProperty.getName());
        assertEquals(expectedFirstProperty.getValue().toString(), firstProperty.getValue().toString());

        SyncProperty expectedSecondProperty = instance.getProperty(secondProperty.getName());
        assertEquals(expectedSecondProperty.getName(), secondProperty.getName());
        assertEquals(expectedSecondProperty.getValue().toString(), secondProperty.getValue().toString());

        SyncProperty expectedThirdProperty = instance.getProperty(thirdProperty.getName());
        assertEquals(expectedThirdProperty.getName(), thirdProperty.getName());
        assertEquals(expectedThirdProperty.getValue().toString(), thirdProperty.getValue().toString());
    }

    public void testSetProperties_NotSyncProperty() {
        Map properties = new HashMap<String, Object>();

        properties.put(firstPropertyKey,  firstPropertyValue);
        properties.put(secondPropertyKey, secondPropertyValue);
        properties.put(thirdPropertyKey,  thirdPropertyValue);

        SyncItemImpl instance = new SyncItemImpl();
        instance.setProperties(properties);

        SyncProperty expectedFirstProperty = instance.getProperty(firstPropertyKey);
        assertEquals(expectedFirstProperty.getName(), firstPropertyKey);
        assertEquals(expectedFirstProperty.getValue().toString(), firstPropertyValue.toString());

        SyncProperty expectedSecondProperty = instance.getProperty(secondPropertyKey);
        assertEquals(expectedSecondProperty.getName(), secondPropertyKey);
        assertEquals(expectedSecondProperty.getValue().toString(), secondPropertyValue.toString());

        SyncProperty expectedThirdProperty = instance.getProperty(thirdPropertyKey);
        assertEquals(expectedThirdProperty.getName(), thirdPropertyKey);
        assertEquals(expectedThirdProperty.getValue().toString(), thirdPropertyValue.toString());
    }

    /**
     * Test of cloneItem method, of class SyncItemImpl.
     */
    public void testClone() {

        SyncSource syncSource = new FakeSyncSource("MySyncSource", "myDb");
        Object key = new String("key");
        Object parentKey = new String("parentKey");
        Object mappedKey = new String("mappedKey");
        char state = 'N';
        byte[] content = new String("data data data").getBytes();
        String format = new String("text/plain");
        String type = new String("type");
        Timestamp timestamp = new Timestamp(123456789);

        SyncItemImpl instance = new SyncItemImpl(syncSource, key, parentKey,
                mappedKey, state, content, format, type, timestamp);

        instance.setProperties(getProperties());

        Object result = instance.cloneItem();

        assertEquals(instance.getSyncSource().getName(), ((SyncItem)result).getSyncSource().getName());
        assertEquals(instance.getKey().getKeyValue(), ((SyncItem)result).getKey().getKeyValue());

        SyncProperty expectedFirstProperty = instance.getProperty(firstProperty.getName());
        assertEquals(expectedFirstProperty.getName(), firstProperty.getName());
        assertEquals(expectedFirstProperty.getValue().toString(), firstProperty.getValue().toString());

        SyncProperty expectedSecondProperty = instance.getProperty(secondProperty.getName());
        assertEquals(expectedSecondProperty.getName(), secondProperty.getName());
        assertEquals(expectedSecondProperty.getValue().toString(), secondProperty.getValue().toString());

        SyncProperty expectedThirdProperty = instance.getProperty(thirdProperty.getName());
        assertEquals(expectedThirdProperty.getName(), thirdProperty.getName());
        assertEquals(expectedThirdProperty.getValue().toString(), thirdProperty.getValue().toString());

    }

    /**
     * Test of copy method, of class SyncItemImpl.
     */
    public void testCopy() {

        SyncSource syncSource = new FakeSyncSource("MySyncSource", "myDb");
        Object key = new String("key");
        Object parentKey = new String("parentKey");
        Object mappedKey = new String("mappedKey");
        char state = 'N';
        byte[] content = new String("data data data").getBytes();
        String format = new String("text/plain");
        String type = new String("type");
        Timestamp timestamp = new Timestamp(123456789);

        SyncItemImpl syncItem = new SyncItemImpl(syncSource, key, parentKey,
                mappedKey, state, content, format, type, timestamp);

        syncItem.setProperties(getProperties());

        SyncItemImpl instance = new SyncItemImpl();
        instance.copy(syncItem);

        assertEquals(instance.getSyncSource().getName(), syncSource.getName());
        assertEquals(instance.getKey().getKeyValue(), key);

        SyncProperty expectedFirstProperty = instance.getProperty(firstProperty.getName());
        assertEquals(expectedFirstProperty.getName(), firstProperty.getName());
        assertEquals(expectedFirstProperty.getValue().toString(), firstProperty.getValue().toString());

        SyncProperty expectedSecondProperty = instance.getProperty(secondProperty.getName());
        assertEquals(expectedSecondProperty.getName(), secondProperty.getName());
        assertEquals(expectedSecondProperty.getValue().toString(), secondProperty.getValue().toString());

        SyncProperty expectedThirdProperty = instance.getProperty(thirdProperty.getName());
        assertEquals(expectedThirdProperty.getName(), thirdProperty.getName());
        assertEquals(expectedThirdProperty.getValue().toString(), thirdProperty.getValue().toString());

    }

    private Map getProperties() {
        Map properties = new HashMap<String, Object>();

        properties.put(firstProperty.getName(), firstProperty);
        properties.put(secondProperty.getName(), secondProperty);
        properties.put(thirdProperty.getName(), thirdProperty);

        return properties;
    }


}
