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
 * Corresponds to the &l;tDevInf&gt; element in the SyncML devinf DTD
 *
 * @version $Id: DevInf.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class DevInf implements java.io.Serializable {

    // ------------------------------------------------------------ Private data
    private VerDTD  verDTD                ;
    private String  man                   ;
    private String  mod                   ;
    private String  oem                   ;
    private String  fwV                   ;
    private String  swV                   ;
    private String  hwV                   ;
    private String  devID                 ;
    private String  devTyp                ;
    private Boolean utc                   ;
    private Boolean supportLargeObjs      ;
    private Boolean supportNumberOfChanges;
    private ArrayList dataStores = new ArrayList();
    private ArrayList ctCapsV1   = new ArrayList();
    private ArrayList exts       = new ArrayList();

    // ------------------------------------------------------------ Constructors
    /**
     * In order to expose the server configuration like WS this constructor
     * must be public
     */
    public DevInf() {}

    /**
     * Creates a new DevInf object with the given parameter
     *
     * @param verDTD The DTD version - NOT NULL
     * @param man    The device manufacturer - NOT NULL
     * @param mod    The device model name or model number - NOT NULL
     * @param oem    The device OEM (Original Equipment Manufacturer)
     * @param fwV    The device firmware version - NOT NULL
     * @param swV    The device software version - NOT NULL
     * @param hwV    The device hardware version - NOT NULL
     * @param devID  The identifier of the source synchronization device - NOT NULL
     * @param devTyp The device type - NOT NULL
     * @param utc    Specify if the device supports UTC based time
     * @param supportLargeObjs Specify if the device supports handling of large objects
     * @param supportNumberOfChanges Specify if the device supports number of changes
     * @param dataStores The array of datastore - NOT NULL
     * @param ctCapsV1   The array of content type capability
     * @param exts       The array of experimental extension
     */
    public DevInf(final VerDTD      verDTD                ,
                  final String      man                   ,
                  final String      mod                   ,
                  final String      oem                   ,
                  final String      fwV                   ,
                  final String      swV                   ,
                  final String      hwV                   ,
                  final String      devID                 ,
                  final String      devTyp                ,
                  final boolean     utc                   ,
                  final boolean     supportLargeObjs      ,
                  final boolean     supportNumberOfChanges,
                  final DataStore[] dataStores            ,
                  final CTCapV1[]   ctCapsV1              ,
                  final Ext[]       exts                  ) {

        setVerDTD(verDTD);
        setMan   (man)   ;
        setMod   (mod)   ;
        this.oem = oem;
        setFwV   (fwV)   ;
        setSwV   (swV)   ;
        setHwV   (hwV)   ;
        setDevID (devID) ;
        setDevTyp(devTyp);
        this.utc                    = (utc) ? new Boolean(utc) : null;
        this.supportLargeObjs       = (supportLargeObjs)
                                    ? new Boolean(supportLargeObjs)
                                    : null;
        this.supportNumberOfChanges = (supportNumberOfChanges)
                                    ? new Boolean(supportNumberOfChanges)
                                    : null;

        setDataStores(dataStores);
        setCTCapsV1 (ctCapsV1)  ;
        setExts     (exts)      ;
    }

    /**
     * Creates a new DevInf object with the given parameter
     *
     * @param verDTD The DTD version - NOT NULL
     * @param man    The device manufacturer - NOT NULL
     * @param mod    The device model name or model number - NOT NULL
     * @param oem    The device OEM (Original Equipment Manufacturer)
     * @param fwV    The device firmware version - NOT NULL
     * @param swV    The device software version - NOT NULL
     * @param hwV    The device hardware version - NOT NULL
     * @param devID  The identifier of the source synchronization device - NOT NULL
     * @param devTyp The device type - NOT NULL
     * @param utc    Specify if the device supports UTC based time
     * @param supportLargeObjs Specify if the device supports handling of large objects
     * @param supportNumberOfChanges Specify if the device supports number of changes
     * @param dataStores The array of datastore - NOT NULL
     * @param exts       The array of experimental extension
     */
    public DevInf(final VerDTD      verDTD                ,
                  final String      man                   ,
                  final String      mod                   ,
                  final String      oem                   ,
                  final String      fwV                   ,
                  final String      swV                   ,
                  final String      hwV                   ,
                  final String      devID                 ,
                  final String      devTyp                ,
                  final boolean     utc                   ,
                  final boolean     supportLargeObjs      ,
                  final boolean     supportNumberOfChanges,
                  final DataStore[] dataStores            ,
                  final Ext[]       exts                  ) {
        this(verDTD, man, mod, oem, fwV, swV, hwV, devID, devTyp, utc,
             supportLargeObjs, supportNumberOfChanges, dataStores, null, exts);
    }

    /**
     * Creates a new DevInf object with the given parameter
     *
     * @param verDTD The DTD version - NOT NULL
     * @param man    The device manufacturer - NOT NULL
     * @param mod    The device model name or model number - NOT NULL
     * @param oem    The device OEM (Original Equipment Manufacturer)
     * @param fwV    The device firmware version - NOT NULL
     * @param swV    The device software version - NOT NULL
     * @param hwV    The device hardware version - NOT NULL
     * @param devID  The identifier of the source synchronization device - NOT NULL
     * @param devTyp The device type - NOT NULL
     * @param utc    Specify if the device supports UTC based time
     * @param supportLargeObjs Specify if the device supports handling of large objects
     * @param supportNumberOfChanges Specify if the device supports number of changes
     */
    public DevInf(final VerDTD      verDTD                ,
                  final String      man                   ,
                  final String      mod                   ,
                  final String      oem                   ,
                  final String      fwV                   ,
                  final String      swV                   ,
                  final String      hwV                   ,
                  final String      devID                 ,
                  final String      devTyp                ,
                  final boolean     utc                   ,
                  final boolean     supportLargeObjs      ,
                  final boolean     supportNumberOfChanges) {
        this(verDTD, man, mod, oem, fwV, swV, hwV, devID, devTyp, utc,
             supportLargeObjs, supportNumberOfChanges, new DataStore[0],
             null, new Ext[0]);
    }
    // ---------------------------------------------------------- Public methods

    /**
     * Gets the DTD version property
     *
     * @return the DTD version property
     */
    public VerDTD getVerDTD() {
        return verDTD;
    }

    /**
     * Sets the DTD version property
     *
     * @param verDTD the DTD version
     */
    public void setVerDTD(VerDTD verDTD) {
        if (verDTD == null) {
            throw new IllegalArgumentException("verDTD cannot be null");
        }
        this.verDTD = verDTD;
    }

    /**
     * Gets the device manufacturer
     *
     * @return the device manufacturer
     */
    public String getMan() {
        return man;
    }

    /**
     * Sets the device manufacturer
     *
     * @param man the device manufacturer
     */
    public void setMan(String man) {
        this.man = man;
    }

    /**
     * Gets the model name or model number of device
     *
     * @return the model name or model number of device
     */
    public String getMod() {
        return mod;
    }

    /**
     * Sets the device model name or model number of device
     *
     * @param mod the device model name or model number of device
     */
    public void setMod(String mod) {
        this.mod = mod;
    }

    /**
     * Gets the Original Equipment Manufacturer of the device
     *
     * @return the OEM property
     */
    public String getOEM() {
        return oem;
    }

    /**
     * Sets the Original Equipment Manufacturer of the device
     *
     * @param oem the Original Equipment Manufacturer of the device
     */
    public void setOEM(String oem) {
        this.oem = oem;
    }

    /**
     * Gets the firmware version property
     *
     * @return the firmware version property
     */
    public String getFwV() {
        return fwV;
    }

    /**
     * Sets the firmware version property
     *
     * @param fwV the firmware version property
     */
    public void setFwV(String fwV) {
        this.fwV =fwV;
    }

    /**
     * Gets the software version property
     *
     * @return the software version property
     */
    public String getSwV() {
        return swV;
    }

    /**
     * Sets the software version property
     *
     * @param swV the software version property
     */
    public void setSwV(String swV) {
        this.swV =swV;
    }

    /**
     * Gets the hardware version property
     *
     * @return the hardware version property
     */
    public String getHwV() {
        return hwV;
    }

    /**
     * Sets the hardware version property
     *
     * @param hwV the hardware version property
     */
    public void setHwV(String hwV) {
        this.hwV =hwV;
    }

    /**
     * Gets the identifier of the source synchronization device
     *
     * @return the identifier of the source synchronization device
     */
    public String getDevID() {
        return devID;
    }

    /**
     * Sets the identifier of the source synchronization device
     *
     * @param devID the identifier of the source synchronization device
     */
    public void setDevID(String devID) {
        if (devID == null) {
            throw new IllegalArgumentException("devID cannot be null");
        }
        this.devID = devID;
    }

    /**
     * Gets the device type
     *
     * @return the device type
     */
    public String getDevTyp() {
        return devTyp;
    }

    /**
     * Sets the device type
     *
     * @param devTyp the device type
     */
    public void setDevTyp(String devTyp) {
        if (devTyp == null) {
            throw new IllegalArgumentException("devTyp cannot be null");
        }
        this.devTyp = devTyp;
    }

    /**
     * Gets true if the device supports UTC based time
     *
     * @return true if the device supports UTC based time
     */
    public boolean isUTC() {
        return (utc != null);
    }

    /**
     * Sets the UTC property
     *
     * @param utc is true if the device supports UTC based time
     */
    public void setUTC(Boolean utc) {
        this.utc = (utc.booleanValue()) ? utc : null;
    }

    /**
     * Sets the UTC property
     *
     * @param utc is true if the device supports UTC based time
     */
    public void setUTC(boolean utc) {
       setUTC(new Boolean(utc));
    }

    /**
     * Gets the Boolean value of utc
     *
     * @return true if the device supports UTC based time
     */
    public Boolean getUTC() {
        if (utc == null || !utc.booleanValue()) {
            return null;
        }
        return utc;
    }

    /**
     * Gets true if the device supports handling of large objects
     *
     * @return true if the device supports handling of large objects
     */
    public boolean isSupportLargeObjs() {
        return (supportLargeObjs != null);
    }

    /**
     * Sets the SupportLargeObjs property
     *
     * @param supportLargeObjs is true if the device supports handling of large objects
     */
    public void setSupportLargeObjs(Boolean supportLargeObjs) {
        this.supportLargeObjs = (supportLargeObjs.booleanValue())
                              ? supportLargeObjs
                              : null;
    }

    /**
     * Sets the SupportLargeObjs property
     *
     * @param supportLargeObjs is true if the device supports handling of large objects
     */
    public void setSupportLargeObjs(boolean supportLargeObjs) {
       setSupportLargeObjs(new Boolean(supportLargeObjs));
    }

    /**
     * Gets the Boolean value of supportLargeObjs
     *
     * @return true if the device supports handling of large objects
     */
    public Boolean getSupportLargeObjs() {
        if (supportLargeObjs == null ||  !supportLargeObjs.booleanValue()) {
            return null;
        }
        return supportLargeObjs;
    }

    /**
     * Gets true if the device supports number of changes
     *
     * @return true if the device supports number of changes
     */
    public boolean isSupportNumberOfChanges() {
        return (supportNumberOfChanges != null);
    }

    /**
     * Sets the supportNumberOfChanges property
     *
     * @param supportNumberOfChanges is true if the device supports number of changes
     */
    public void setSupportNumberOfChanges(Boolean supportNumberOfChanges) {
        this.supportNumberOfChanges = (supportNumberOfChanges.booleanValue())
                                    ? supportNumberOfChanges
                                    : null;
    }

    /**
     * Sets the SupportNumberOfChanges property
     *
     * @param supportNumberOfChanges is true if the device supports number of changes
     */
    public void setSupportNumberOfChanges(boolean supportNumberOfChanges) {
       setSupportNumberOfChanges(new Boolean(supportNumberOfChanges));
    }

    /**
     * Gets the Boolean value of SupportNumberOfChanges
     *
     * @return true if the device supports number of changes
     */
    public Boolean getSupportNumberOfChanges() {
        if (supportNumberOfChanges == null || !supportNumberOfChanges.booleanValue()) {
            return null;
        }
        return supportNumberOfChanges;
    }

    /**
     * Gets the array of content type capability
     *
     * @return the array of content type capability
     */
    public ArrayList getCTCapsV1() {
        return this.ctCapsV1;
    }

    /**
     * Sets an array of content type capability
     *
     * @param ctCapsV1 an array of content type capability
     */
    public void setCTCapsV1(CTCapV1[] ctCapsV1) {
        if (ctCapsV1 != null) {
            this.ctCapsV1.clear();
            this.ctCapsV1.addAll(Arrays.asList(ctCapsV1));
        } else {
            this.ctCapsV1 = null;
        }
    }

    /**
     * Sets an array of content type capability
     *
     * @param ctCapsV1 an array of content type capability
     */
    public void setCTCapsV1(ArrayList ctCapsV1) {
        if (ctCapsV1 != null) {
            this.ctCapsV1.clear();
            this.ctCapsV1.addAll(ctCapsV1);
        } else {
            this.ctCapsV1 = null;
        }
    }

    /**
     * Gets the array of datastore
     *
     * @return the array of datastore
     */
    public ArrayList getDataStores() {
        return this.dataStores;
    }

    /**
     * Sets an array of DataStore object
     *
     * @param dataStores an array of DataStore object
     */
    public void setDataStores(DataStore[] dataStores) {
        if (dataStores == null ) {
            throw new IllegalArgumentException("datastores cannot be null");
        }
        this.dataStores.clear();
        this.dataStores.addAll(Arrays.asList(dataStores));
    }

    /**
     * Sets an array of DataStore
     *
     * @param dataStores an array of DataStore
     */
    public void setDataStores(ArrayList dataStores) {
        if (dataStores != null) {
            this.dataStores.clear();
            this.dataStores.addAll(dataStores);
        } else {
            throw new IllegalArgumentException("datastores cannot be null");
        }
    }


    /**
     * Gets the array of experimental extension
     *
     * @return the array of experimental extension
     */
    public ArrayList getExts() {
        return this.exts;
    }

    /**
     * Sets an array of experimental extension objects
     *
     * @param exts an array of experimental extension objects
     */
    public void setExts(Ext[] exts) {
        if (exts != null) {
            this.exts.clear();
            this.exts.addAll(Arrays.asList(exts));
        } else {
            this.exts = null;
        }
    }

    /**
     * Sets an array of experimental extension
     *
     * @param exts an array of experimental extension
     */
    public void setExts(ArrayList exts) {
        if (exts != null) {
            this.exts.clear();
            this.exts.addAll(exts);
        } else {
            this.exts = null;
        }
    }
    
    /**
     * Returns true if there is a DataStore whith the given SourceRef
     * @param dataStoreSourceRef 
     * @return true if there is a DataStore whith the given SourceRef,
     * false otherwise
     */
    public boolean containsDataStore(String dataStoreSourceRef) {

        Iterator iterator = dataStores.iterator();
        while (iterator.hasNext()) {
            DataStore dataStore = (DataStore) iterator.next();
            String sourceRef = dataStore.getSourceRef().getValue();
            if (dataStoreSourceRef.equals(sourceRef)) {
                return true;
            }
        }

        return false;
    }
}
