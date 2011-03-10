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

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import com.funambol.framework.core.*;

/**
 *  Utility class for calculate the XML size and WBXML size of framework Objects
 *
 * @version $Id: WBXMLSizeCalculator.java,v 1.2 2007-11-28 11:13:24 nichele Exp $
 */
public class WBXMLSizeCalculator implements MessageSizeCalculator {

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the default WBXML overhead for the SyncML message
     *
     * @return overhead for SyncML message
     */
    public long getMsgSizeOverhead() {
        return 725;
    }

    // ------------------------------------------------------------------ SyncML
    /**
     * Returns the WBXML overhead for SyncML object
     *     sizeof(<SyncML xmlns='SYNCML:SYNCML1.1'>\n</SyncML>\n)
     *
     * @return overhead for SyncML object
     */
    public long getSyncMLOverhead() {
        return 29;
    }

    /**
     * Returns the WBXML size of the SyncML object
     *
     * @param syncML the SyncML element
     *
     * @return size the WBXML size of the SyncML element
     */
    public long getSize(SyncML syncML) {
        SyncHdr  syncHdr  = syncML.getSyncHdr() ;
        SyncBody syncBody = syncML.getSyncBody();

        return 29
             + getSize(syncHdr )
             + getSize(syncBody)
             ;
    }

    // ----------------------------------------------------------------- SyncHdr
    /**
     * Returns the WBXML size of the SyncHdr object
     *
     * @param syncHdr the SyncHdr element
     *
     * @return size the WBXML size of the SyncHdr element
     */
    public long getSize(SyncHdr syncHdr) {
        VerDTD    verDTD    = syncHdr.getVerDTD()   ;
        VerProto  verProto  = syncHdr.getVerProto() ;
        SessionID sessionID = syncHdr.getSessionID();
        String    msgID     = syncHdr.getMsgID()    ;
        Target    target    = syncHdr.getTarget()   ;
        Source    source    = syncHdr.getSource()   ;
        String    respURI   = syncHdr.getRespURI()  ;
        boolean   noResp    = syncHdr.isNoResp()    ;
        Cred      cred      = syncHdr.getCred()     ;
        Meta      meta      = syncHdr.getMeta()     ;

        return 4
             + ((verDTD    != null) ? getSize(verDTD)     :  0)
             + ((verProto  != null) ? getSize(verProto)   :  0)
             + ((sessionID != null) ? getSize(sessionID)  :  0)
             + ((msgID     != null) ? 4 + msgID.length()  :  0)
             + ((target    != null) ? getSize(target)     :  0)
             + ((source    != null) ? getSize(source)     :  0)
             + ((respURI   != null) ? 4 + respURI.length(): 80)
             + (noResp              ? 1                   :  0)
             + ((cred      != null) ? getSize(cred)       :  0)
             + ((meta      != null) ? getSize(meta)       :  0)
             ;
    }

    /**
     * Returns the WBXML overhead of the RespURI element.
     *
     * @return the WBXML overhead of the RespURI element.
     */
    public long getRespURIOverhead() {
        return 150;
    }

    // ---------------------------------------------------------------- SyncBody
    /**
     * Returns the WBXML overhead for SyncBody object
     *     sizeof(<SyncBody>\n</SyncBody>\n)
     *
     * @return overhead for SyncBody object
     */
    public long getSyncBodyOverhead() {
        return 4;
    }

    /**
     * Returns the WBXML size of the SyncBody object
     *
     * @param syncBody the SyncBody element
     *
     * @return size the WBXML size of the SyncBody element
     */
    public long getSize(SyncBody syncBody) {
        ArrayList commands = syncBody.getCommands();

        long size = 4
                  + ((syncBody.isFinalMsg()) ? 1 : 0)
                  ;

        for (int i=0; i<commands.size(); i++) {
           size += getCommandSize((AbstractCommand)commands.get(i));
       }
       return size;
    }

    // ------------------------------------------------------------------ VerDTD
    /**
     * Returns the WBXML size of VerDTD object
     *
     * @return the WBXML size of VerDTD object
     */
    public long getSize(VerDTD verDTD) {
        return 4
             + verDTD.getValue().length()
             ;
    }

    // ---------------------------------------------------------------- VerProto
    /**
     * Returns the WBXML size of VerProto object
     *
     * @return the WBXML size of VerProto object
     */
    public long getSize(VerProto verProto) {
        return 4
             + verProto.getVersion().length()
             ;
    }

    // --------------------------------------------------------------- SessionID
    /**
     * Returns the WBXML size of SessionID object
     *
     * @return the WBXML size of SessionID object
     */
    public long getSize(SessionID sessionID) {
        return 4
             + sessionID.getSessionID().length()
             ;
    }

    // ------------------------------------------------------------------ Target
    /**
     * Returns the WBXML size of this element.
     * @return the WBXML size of this element
     */
    public long getSize(Target target) {
        String locURI  = target.getLocURI() ;
        String locName = target.getLocName();
        Filter filter  = target.getFilter() ;

        return 4
             + ((locURI  != null) ? (4 + locURI.length() ) : 0)
             + ((locName != null) ? (4 + locName.length()) : 0)
             + ((filter  != null) ? getSize(filter)        : 0)
             ;
    }

    // ------------------------------------------------------------ TargetParent
    /**
     * Returns the WBXML size of this element.
     * @return the WBXML size of this element
     */
    public long getSize(TargetParent targetParent) {
        String locURI  = targetParent.getLocURI() ;

        return 4
             + ((locURI  != null) ? (4 + locURI.length()) : 0)
             ;
    }

    // ------------------------------------------------------------------ Filter
    /**
     * Returns the WBXML size of this element.
     * @return the WBXML size of this element
     */
    public long getSize(Filter filter) {
        Meta   meta       = filter.getMeta()      ;
        Item   field      = filter.getField()     ;
        Item   record     = filter.getRecord()    ;
        String filterType = filter.getFilterType();

        return 4
             + ((meta   != null)     ? getSize(meta)           : 0)
             + ((field  != null)     ? 4 + getSize(field)      : 0)
             + ((record != null)     ? 4 + getSize(record)     : 0)
             + ((filterType != null) ? 4 + filterType.length() : 0)
             ;
    }

    // ------------------------------------------------------------------ Source
    /**
     * Returns the WBXML size of this element.
     * @return the WBXML size of this element
     */
    public long getSize(Source source) {
        String locURI  = source.getLocURI() ;
        String locName = source.getLocName();

        return 4
             + ((locURI != null)  ? (4 + locURI.length() ) : 0)
             + ((locName != null) ? (4 + locName.length()) : 0)
             ;
    }

    // ------------------------------------------------------------ SourceParent
    /**
     * Returns the WBXML size of this element.
     * @return the WBXML size of this element
     */
    public long getSize(SourceParent sourceParent) {
        String locURI  = sourceParent.getLocURI() ;

        return 4
             + ((locURI  != null) ? (4 + locURI.length()) : 0)
             ;
    }

    // -------------------------------------------------------------------- Cred

    /**
     * Returns the WBXML size of this element.
     * @return the WBXML size of this element
     */
    public long getSize(Cred cred) {
        Authentication auth = cred.getAuthentication();
        Meta   meta = auth.getMeta();
        String data = cred.getData();

        return 4
             + ((meta != null) ? getSize(meta)     : 0)
             + ((data != null) ? 4 + data.length() : 0)
             ;
    }

    // -------------------------------------------------------------------- Meta

    /**
     * Returns the WBXML size of this object.
     *
     * @return the WBXML size of this object
     */
    public long getSize(Meta meta) {
        long sizeMeta = 0;

        boolean fieldLevel  = meta.isFieldLevel() ;
        String format       = meta.getFormat()    ;
        String type         = meta.getType()      ;
        String mark         = meta.getMark()      ;
        Long size           = meta.getSize()      ;
        Anchor anchor       = meta.getAnchor()    ;
        String version      = meta.getVersion()   ;
        NextNonce nextNonce = meta.getNextNonce() ;
        Long maxMsgSize     = meta.getMaxMsgSize();
        Long maxObjSize     = meta.getMaxObjSize();
        ArrayList emi       = meta.getEMI()       ;
        Mem mem             = meta.getMem()       ;

        sizeMeta = 4
                 + (fieldLevel          ? 1                                :  0)
                 + ((format    != null) ? 4 + format.length()               : 0)
                 + ((type      != null) ? 4 + type.length()                 : 0)
                 + ((mark      != null) ? 4 + mark.length()                 : 0)
                 + ((size      != null) ? 4 + String.valueOf(size).length() : 0)
                 + ((anchor    != null) ? getSize(anchor)                   : 0)
                 + ((version   != null) ? 4 + version.length()              : 0)
                 + ((nextNonce != null) ? getSize(nextNonce)                : 0)
                 + ((maxMsgSize!= null) ? 4 + String.valueOf(maxMsgSize).length() : 0)
                 + ((maxObjSize!= null) ? 4 + String.valueOf(maxObjSize).length() : 0)
                 + ((mem       != null) ? getSize(mem)                      : 0)
                 ;

        for (int i=0; emi != null && i < emi.size(); i++) {
            sizeMeta += getSize((EMI)emi.get(i));
        }

       return sizeMeta;
    }

    // ------------------------------------------------------------------ Anchor

    /**
     * Returns the WBXML size of this element.
     *
     * @return the WBXML size of this element
     */
    public long getSize(Anchor anchor) {
        String last = anchor.getLast();
        String next = anchor.getNext();

        return 25
             + ((last != null) ? 4 + last.length() : 0)
             + ((next != null) ? 4 + next.length() : 0)
             ;
    }

    // --------------------------------------------------------------------- EMI
    /**
     * Returns the WBXML size of EMI element.
     *
     * @return the WBXML size of EMI element
     */
    public long getSize(EMI emi) {
        return 4
             + emi.getValue().length()
             ;
    }

    // --------------------------------------------------------------- NextNonce
    /**
     * Returns the WBXML size of NextNonce element.
     *
     * @return the WBXML size of NextNonce element
     */
    public long getSize(NextNonce nextNonce) {
        return 4
             + nextNonce.getValueAsBase64().length()
             ;
    }

    // --------------------------------------------------------------------- Mem
    /**
     * Returns the WBXML size of Mem object.
     *
     * @return the WBXML size of Mem object
     */
    public long getSize(Mem mem) {
        boolean sharedMem = mem.isSharedMem();
        long freeMem      = mem.getFreeMem() ;
        long freeID       = mem.getFreeID()  ;

        return 4
             + ((sharedMem)    ? 1                                    : 0)
             + ((freeMem != 0) ? 4 + String.valueOf(freeMem).length() : 0)
             + ((freeID  != 0) ? 4 + String.valueOf(freeID).length()  : 0)
             ;
    }

    // --------------------------------------------------------- AbstractCommand
    /**
     * Returns the WBXML size of AbstractCommand object.
     * @return the WBXML size of AbstractCommand object
     */
    public long getSize(AbstractCommand command) {
        CmdID cmdID = command.getCmdID();
        Cred  cred  = command.getCred() ;

        return ((cmdID != null)      ? getSize(cmdID) : 0)
             + ((command.isNoResp()) ? 1                   : 0)
             + ((cred  != null)      ? getSize(cred)  : 0)
             ;
    }

    /**
     * Gets the XML size of the command
     *
     * @return the XML size of the command
     */
    public long getCommandSize(AbstractCommand command) {
        long size = 0;
        if (command instanceof Add) {
            size = getSize((Add)command);
        } else if (command instanceof Alert) {
            size = getSize((Alert)command);
        } else if (command instanceof Atomic) {
            size = getSize((Atomic)command);
        } else if (command instanceof Copy) {
            size = getSize((Copy)command);
        } else if (command instanceof Delete) {
            size = getSize((Delete)command);
        } else if (command instanceof Exec) {
            size = getSize((Exec)command);
        } else if (command instanceof Get) {
            size = getSize((Get)command);
        } else if (command instanceof Map) {
            size = getSize((Map)command);
        } else if (command instanceof Move) {
            size = getSize((Move)command);
        } else if (command instanceof Put) {
            size = getSize((Put)command);
        } else if (command instanceof Replace) {
            size = getSize((Replace)command);
        } else if (command instanceof Results) {
            size = getSize((Results)command);
        } else if (command instanceof Search) {
            size = getSize((Search)command);
        } else if (command instanceof Sequence) {
            size = getSize((Sequence)command);
        } else if (command instanceof Status) {
            size = getSize((Status)command);
        } else if (command instanceof Sync) {
            size = getSize((Sync)command);
        }

        return size;
    }

    // ------------------------------------------------------------------- CmdID
    /**
     * Returns the WBXML size of CmdID element.
     * @return the WBXML size of CmdID element
     */
    public long getSize(CmdID cmdID) {
        return 2
             + cmdID.getCmdID().length();
    }

    // --------------------------------------------------------------------- Add
    /**
     * Returns the WBXML size of Add element.
     *
     * @return the WBXML size of Add element
     */
    public long getSize(Add add) {
        Meta meta       = add.getMeta() ;
        ArrayList items = add.getItems();

        long size = 4
                  + getSize((AbstractCommand)add)
                  + ((meta != null) ? getSize(meta) : 0)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // -------------------------------------------------------------------- Item
    /**
     * Returns the WBXML size of Item element.
     * @return the WBXML size of Item element
     */
    public long getSize(Item item) {
        Target       target       = item.getTarget()      ;
        Source       source       = item.getSource()      ;
        SourceParent sourceParent = item.getSourceParent();
        TargetParent targetParent = item.getTargetParent();
        Meta         meta         = item.getMeta()        ;
        ComplexData  data         = item.getData()        ;

        return 4
             + ((target       != null) ? getSize(target)       : 0)
             + ((source       != null) ? getSize(source)       : 0)
             + ((sourceParent != null) ? getSize(sourceParent) : 0)
             + ((targetParent != null) ? getSize(targetParent) : 0)
             + ((meta         != null) ? getSize(meta)         : 0)
             + ((data         != null) ? getSize(data)         : 0)
             + ((item.isMoreData())    ? 1                     : 0)
             ;
    }

    // ------------------------------------------------------------- ComplexData
    /**
     * Returns the WBXML size of ComplexData element.
     *
     * @return the WBXML size of ComplexData element
     */
    public long getSize(ComplexData complexData) {
        String data   = complexData.getData()  ;
        Anchor anchor = complexData.getAnchor();
        DevInf devInf = complexData.getDevInf();

        return 4
             + ((data   != null) ? data.length()        :0)
             + ((anchor != null) ? getSize(anchor) :0)
             + ((devInf != null) ? getSize(devInf) :0)
             ;
    }

    // -------------------------------------------------------------------- Data
    /**
     * Returns the WBXML size of Data element.
     *
     * @return the WBXML size of Data element
     */
    public long getSize(Data data) {
        return 4
             + data.getData().length();
    }

    // -------------------------------------------------------------- DevInfData
    /**
     * Returns the WBXML size of DevInfData element.
     *
     * @return the WBXML size of DevInfData element
     */
    public long getSize(DevInfData devInfData) {
        DevInf devInf = devInfData.getDevInf();
        return 4
             + ((devInf != null) ? getSize(devInf) : 0)
             ;
    }

    // ------------------------------------------------------------------ DevInf
    /**
     * Returns the WBXML size of DevInf element.
     *
     * @return the WBXML size of DevInf element
     */
    public long getSize(DevInf devInf) {
        VerDTD verDTD        = devInf.getVerDTD()    ;
        String man           = devInf.getMan()       ;
        String mod           = devInf.getMod()       ;
        String oem           = devInf.getOEM()       ;
        String fwV           = devInf.getFwV()       ;
        String swV           = devInf.getSwV()       ;
        String hwV           = devInf.getHwV()       ;
        String devID         = devInf.getDevID()     ;
        String devTyp        = devInf.getDevTyp()    ;
        ArrayList dataStores = devInf.getDataStores();
        ArrayList ctCapsV1   = devInf.getCTCapsV1()  ;
        ArrayList exts       = devInf.getExts()      ;

        long size = 0;

        for (int i=0; dataStores != null && i<dataStores.size(); i++) {
            size += getSize((DataStore)dataStores.get(i));
        }
        for (int i=0; ctCapsV1 != null && i<ctCapsV1.size(); i++) {
            size += getSize((CTCapV1)ctCapsV1.get(i));
        }
        for (int i=0; exts != null && i<exts.size(); i++) {
            size += getSize((Ext)exts.get(i));
        }

        return 4
             + ((verDTD != null)                    ? getSize(verDTD)      : 0)
             + ((man    != null)                    ? 4 + man.length()     : 0)
             + ((mod    != null)                    ? 4 + mod.length()     : 0)
             + ((oem    != null)                    ? 4 + oem.length()     : 0)
             + ((fwV    != null)                    ? 4 + fwV.length()     : 0)
             + ((swV    != null)                    ? 4 + swV.length()     : 0)
             + ((hwV    != null)                    ? 4 + hwV.length()     : 0)
             + ((devID  != null)                    ? 4 + devID.length()   : 0)
             + ((devTyp != null)                    ? 4 + devTyp.length()  : 0)
             + ((devInf.isUTC())                    ? 1                    : 0)
             + ((devInf.isSupportLargeObjs())       ? 1                    : 0)
             + ((devInf.isSupportNumberOfChanges()) ? 1                    : 0)
             + size;
    }

    // --------------------------------------------------------------- DataStore
    /**
     * Returns the WBXML size of DataStore element.
     *
     * @return the WBXML size of DataStore element
     */
    public long getSize(DataStore dataStore) {
        SourceRef       sourceRef   = dataStore.getSourceRef()  ;
        String          displayName = dataStore.getDisplayName();
        long            maxGUIDSize = dataStore.getMaxGUIDSize();
        CTInfo rxPref      = dataStore.getRxPref()     ;
        ArrayList       rxs         = dataStore.getRxs()        ;
        CTInfo txPref      = dataStore.getTxPref()     ;
        ArrayList       txs         = dataStore.getTxs()        ;
        ArrayList       ctCaps      = dataStore.getCTCaps()     ;
        DSMem           dsMem       = dataStore.getDSMem()      ;
        SyncCap         syncCap     = dataStore.getSyncCap()    ;
        ArrayList       filterRxs   = dataStore.getFilterRxs()  ;
        ArrayList       filterCaps  = dataStore.getFilterCaps() ;

        long size = 0;

        for (int i=0; rxs != null && i<rxs.size(); i++) {
            size += 4 + getSize((CTInfo)rxs.get(i));
        }
        for (int i=0; txs != null && i<txs.size(); i++) {
            size += 4 + getSize((CTInfo)txs.get(i));
        }
        for (int i=0; ctCaps != null && i<ctCaps.size(); i++) {
            size += getSize((CTCap)ctCaps.get(i));
        }
        for (int i=0; filterRxs != null && i<filterRxs.size(); i++) {
            size += 4 + getSize((CTInfo)filterRxs.get(i));
        }
        for (int i=0; filterCaps != null && i<filterCaps.size(); i++) {
            size += getSize((FilterCap)filterCaps.get(i));
        }

        return 4
             + ((sourceRef   != null) ? getSize(sourceRef)           : 0)
             + ((displayName != null && displayName.length() != 0)
                             ? 1 + displayName.length()              : 0)
             + ((maxGUIDSize >= 0)
                             ? 4 + String.valueOf(maxGUIDSize).length() : 0)
             + ((rxPref      != null) ? 4 + getSize(rxPref)          : 0)
             + ((txPref      != null) ? 4 + getSize(txPref)          : 0)
             + ((dsMem       != null) ? getSize(dsMem)               : 0)
             + ((dataStore.isSupportHierarchicalSync()) ? 1          : 0)
             + ((syncCap     != null) ? getSize(syncCap)             : 0)
             + size
             ;
    }

    // ------------------------------------------------------------------ CTInfo
    /**
     * Returns the WBXML size of CTInfo element.
     *
     * @return the WBXML size of CTInfo element
     */
    public long getSize(CTInfo cTInfo) {
        String ctType = cTInfo.getCTType();
        String verCT  = cTInfo.getVerCT() ;

        return ((ctType != null) ? 4 + ctType.length() : 0)
             + ((verCT  != null) ? 4 + verCT.length()  : 0)
             ;
    }

    // --------------------------------------------------------------- FilterCap
    /**
     * Returns the WBXML size of FilterCap element as:
     *
     * @return the WBXML size of FilterCap element
     */
    public long getSize(FilterCap filterCap) {
        CTInfo    cti            = filterCap.getCTInfo()        ;
        ArrayList filterKeywords = filterCap.getFilterKeywords();
        ArrayList propNames      = filterCap.getPropNames()     ;

        long size = 4;
        for (int i=0; filterKeywords != null && i<filterKeywords.size(); i++) {
            size += 4 + ((String)filterKeywords.get(i)).length();
        }
        for (int i=0; propNames != null && i<propNames.size(); i++) {
            size += 4 + ((String)propNames.get(i)).length();
        }

        return ((cti != null) ? getSize(cti) : 0)
        + size;

    }

    // --------------------------------------------------------------- SourceRef
    /**
     * Returns the WBXML size of SourceRef element.
     * @return the WBXML size of SourceRef element
     */
    public long getSize(SourceRef sourceRef) {
        String value  = sourceRef.getValue() ;
        Source source = sourceRef.getSource();

        return 4
             + ((value  != null) ? value.length()  : 0)
             + ((source != null) ? getSize(source) : 0)
             ;
    }

    // ------------------------------------------------------------------- DSMem
    /**
     * Returns the WBXML size of DSMem element
     *
     * @return the WBXML size of DSMem element
     */
    public long getSize(DSMem dsMem) {
        long maxMem = dsMem.getMaxMem();
        long maxID  = dsMem.getMaxID() ;

        return 4
             + ((dsMem.isSharedMem())  ? 1                                   :0)
             + ((maxMem >=0)           ? 4 + String.valueOf(maxMem).length() :0)
             + ((maxID  >=0)           ? 4 + String.valueOf(maxID).length()  :0)
             ;
    }

    // ----------------------------------------------------------------- SyncCap
    /**
     * Returns the WBXML size of SyncCap object.
     *
     * @return the WBXML size of SyncCap object
     */
    public long getSize(SyncCap syncCap) {
        ArrayList syncTypes = syncCap.getSyncType();
        long size = 4;

        for (int i=0; i<syncTypes.size(); ++i) {
            size += getSize((SyncType)syncTypes.get(i));
        }
        return size;
    }

    // ---------------------------------------------------------------- SyncType
    /**
     * Returns the WBXML size of SyncType object.
     *
     * @return the WBXML size of SyncType object
     */
    public long getSize(SyncType syncType) {
       return 4
            + String.valueOf(syncType.getType()).length()
            ;
    }
    // ----------------------------------------------------------------- CTCapV1
    /**
     * Returns the WBXML size of CTCap element for DevInf v1.1.x as:
     *
     * @return the WBXML size of CTCap element
     */
    public long getSize(CTCapV1 ctCapV1) {
        ArrayList ctTypeSup = ctCapV1.getCTTypes();
        long size = 4;

        for (int i=0; ctTypeSup != null && i<ctTypeSup.size(); i++) {
            size += getSize((CTType)ctTypeSup.get(i));
        }
        return size;
    }

    // ------------------------------------------------------------------- CTCap
    /**
     * Returns the WBXML size of CTCap element for DevInf v1.2 as:
     *
     * @return the WBXML size of CTCap element
     */
    public long getSize(CTCap ctCap) {
        CTInfo    cti        = ctCap.getCTInfo()    ;
        ArrayList properties = ctCap.getProperties();

        long size = 4;

        for (int i=0; properties != null && i<properties.size(); i++) {
            size += getSize((Property)properties.get(i));
        }
        return ((cti != null) ? getSize(cti) : 0)
             + ((ctCap.isFieldLevel()) ? 1 : 0)
             + size
             ;
    }

    // ---------------------------------------------------------------- Property
    /**
     * Returns the WBXML size of Property element as:
     *
     * @return the WBXML size of Property element
     */
    public long getSize(Property property) {
        String    propName    = property.getPropName()   ;
        String    dataType    = property.getDataType()   ;
        int       maxOccur    = property.getMaxOccur()   ;
        int       maxSize     = property.getMaxSize()    ;
        ArrayList valEnums    = property.getValEnums()   ;
        String    displayName = property.getDisplayName();
        ArrayList propParams  = property.getPropParams() ;

        long size = 4;
        for (int i=0; valEnums != null && i<valEnums.size(); i++) {
            size += 20 + ((String)valEnums.get(i)).length();
        }
        for (int i=0; propParams != null && i<propParams.size(); i++) {
            size += getSize((PropParam)propParams.get(i));
        }

        return ((propName != null) ? 4 + propName.length()                 : 0)
             + ((dataType != null) ? 4 + dataType.length()                 : 0)
             + ((maxOccur != 0   ) ? 4 + String.valueOf(maxOccur).length() : 0)
             + ((maxSize  != 0   ) ? 4 + String.valueOf(maxSize).length()  : 0)
             + ((property.isNoTruncate()) ? 1                              : 0)
             + ((displayName != null) ? 4 + displayName.length()           : 0)
             + size
             ;
    }

    // --------------------------------------------------------------- PropParam
    /**
     * Returns the WBXML size of PropParam element as:
     *
     * @return the WBXML size of PropParam element
     */
    public long getSize(PropParam propParam) {
        String    paramName   = propParam.getParamName()  ;
        String    dataType    = propParam.getDataType()   ;
        ArrayList valEnums    = propParam.getValEnums()   ;
        String    displayName = propParam.getDisplayName();

        long size = 4;
        for (int i=0; valEnums != null && i<valEnums.size(); i++) {
            size += 4 + ((String)valEnums.get(i)).length();
        }

        return ((paramName   != null) ? 4 + paramName.length()   : 0)
             + ((dataType    != null) ? 4 + dataType.length()    : 0)
             + ((displayName != null) ? 4 + displayName.length() : 0)
             + size
             ;
    }

    // --------------------------------------------------------- CTTypeSupported
    /**
     * Returns the WBXML size of this element.
     *
     * @return the WBXML size of this element
     */
    public long getSize(CTType ctTypeSupported) {
        String    ctType       = ctTypeSupported.getCTType()      ;
        ArrayList ctPropParams = ctTypeSupported.getCTProperties();
        long size = 0;

        for (int i=0; ctPropParams != null && i<ctPropParams.size(); i++) {
            size += getSize((CTProperty)ctPropParams.get(i));
        }
        return ((ctType != null) ? 4 + ctType.length() : 0)
             + size
             ;
    }

    // -------------------------------------------------------------- CTProperty
    /**
     * Returns the WBXML size of CTPropParam element.
     *
     * @return the WBXML size of CTPropParam element
     */
    public long getSize(CTProperty ctPropParam) {
        String    propName     = ctPropParam.getName()       ;
        ArrayList valEnums     = ctPropParam.getValEnum()              ;
        String    dataType     = ctPropParam.getDataType()             ;
        Long      size         = ctPropParam.getSize()       ;
        String    displayName  = ctPropParam.getDisplayName()          ;
        ArrayList ctParameters = ctPropParam.getParameters() ;

        long sizeCTPP = 0;
        for (int i=0; valEnums != null && i<valEnums.size(); i++) {
            sizeCTPP += 4 + ((String)valEnums.get(i)).length();
        }

        for (int i=0; ctParameters!=null && i<ctParameters.size(); i++) {
            sizeCTPP += getSize((CTParameter)ctParameters.get(i));
        }

        return ((propName    != null) ? 4 + propName.length()             : 0)
             + ((dataType    != null) ? 4 + dataType.length()             : 0)
             + ((size        != null) ? 4 + String.valueOf(size.longValue()).length() : 0)
             + ((displayName != null) ? 4 + displayName.length()          : 0)
             + sizeCTPP
             ;
    }

    // ---------------------------------------------------- ContentTypeParameter
    /**
     * Returns the WBXML size of ContentTypeParameter element.
     *
     * @return the WBXML size of ContentTypeParameter element
     */
    public long getSize(CTParameter ctParameter) {
        String    paramName   = ctParameter.getName()       ;
        ArrayList valEnums    = ctParameter.getValEnum()    ;
        String    dataType    = ctParameter.getDataType()   ;
        Long      size        = ctParameter.getSize()       ;
        String    displayName = ctParameter.getDisplayName();
        long sizeCTP = 0;

        for (int i=0; valEnums != null && i<valEnums.size(); i++) {
            sizeCTP += 4 + ((String)valEnums.get(i)).length();
        }

        return ((paramName   != null) ? 4 + paramName.length()            : 0)
             + ((dataType    != null) ? 4 + dataType.length()             : 0)
             + ((size        != null) ? 4 + String.valueOf(size.longValue()).length() : 0)
             + ((displayName != null) ? 4 + displayName.length()          : 0)
             + sizeCTP
             ;
    }

    // --------------------------------------------------------------------- Ext
    /**
     * Returns the WBXML size of Ext element.
     *
     * @return the WBXML size of Ext element
     */
    public long getSize(Ext ext) {
        String    name   = ext.getXNam();
        ArrayList values = ext.getXVal();
        long size = 4;

        for (int i=0; values != null && i<values.size(); i++) {
            size += 4 + ((String)values.get(i)).length();
        }

        return ((name != null) ? 4 + name.length() : 0)
             + size
             ;
    }
    // ------------------------------------------------------------------- Alert
    /**
     * Returns the WBXML size of Alert element.
     *
     * @return the WBXML size of Alert element
     */
    public long getSize(Alert alert) {
        int       data       = alert.getData()      ;
        String    correlator = alert.getCorrelator();
        ArrayList items      = alert.getItems()     ;

        long size = 4
                  + getSize((AbstractCommand)alert)
                  + ((data != 0) ? 2 + String.valueOf(data).length() : 0)
                  + ((correlator != null) ? 1 + correlator.length()  : 0)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // ------------------------------------------------------------------ Atomic
    /**
     * Returns the WBXML size of Atomic element.
     *
     * @return the WBXML size of Atomic element
     */
    public long getSize(Atomic atomic) {
        Meta      meta     = atomic.getMeta()    ;
        ArrayList commands = atomic.getCommands();

        long size = 4
                  + getSize((AbstractCommand)atomic)
                  + ((meta != null) ? getSize(meta) : 0)
                  ;
        for (int i=0; commands != null && i<commands.size(); i++) {
            size += getCommandSize((AbstractCommand)commands.get(i));
        }
        return size;
    }

    // -------------------------------------------------------------------- Copy
    /**
     * Returns the WBXML size of Copy element.
     *
     * @return the WBXML size of Copy element
     */
    public long getSize(Copy copy) {
        Meta      meta  = copy.getMeta() ;
        ArrayList items = copy.getItems();

        long size = 4
                  + getSize((AbstractCommand)copy)
                  + ((meta != null) ? getSize(meta) : 0)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // ------------------------------------------------------------------ Delete
    /**
     * Returns the WBXML size of Delete element.
     *
     * @return the WBXML size of Delete element
     */
    public long getSize(Delete delete) {
        Meta meta       = delete.getMeta() ;
        ArrayList items = delete.getItems();

        long size = 4
                  + getSize((AbstractCommand)delete)
                  + ((delete.isArchive()) ? 1                  : 0)
                  + ((delete.isSftDel() ) ? 1                  : 0)
                  + ((meta != null      ) ? getSize(meta) : 0)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // -------------------------------------------------------------------- Exec
    /**
     * Returns the WBXML size of Exec element.
     *
     * @return the WBXML size of Exec element
     */
    public long getSize(Exec exec) {
        ArrayList items = exec.getItems();

        long size = 4
                  + getSize((AbstractCommand)exec)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // --------------------------------------------------------------------- Get
    /**
     * Returns the WBXML size of Get element.
     *
     * @return the WBXML size of Get element
     */
    public long getSize(Get get) {
        String    lang  = get.getLang();
        Meta      meta  = get.getMeta() ;
        ArrayList items = get.getItems();

        long size = 4
                  + getSize((AbstractCommand)get)
                  + ((lang != null) ? 1 + lang.length() : 0)
                  + ((meta != null) ? getSize(meta)     : 0)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // --------------------------------------------------------------------- Map
    /**
     * Returns the WBXML size of Map element.
     *
     * @return the WBXML size of Map element
     */
    public long getSize(Map map) {
        Target    target   = map.getTarget();
        Source    source   = map.getSource();
        Meta      meta     = map.getMeta() ;
        ArrayList mapItems = map.getMapItems();

        long size = 4
                  + getSize((AbstractCommand)map)
                  + ((target != null) ? getSize(target) : 0)
                  + ((source != null) ? getSize(source) : 0)
                  + ((meta   != null) ? getSize(meta)   : 0)
                  ;
        for (int i=0; mapItems != null && i<mapItems.size(); i++) {
            size += getSize((MapItem)mapItems.get(i));
        }
        return size;
    }

    // ----------------------------------------------------------------- MapItem
    /**
     * Returns the WBXML size of MapItem element.
     *
     * @return the WBXML size of MapItem element
     */
    public long getSize(MapItem mapItem) {
        Target    target   = mapItem.getTarget();
        Source    source   = mapItem.getSource();

        return 2
             + ((target != null) ? getSize(target) : 0)
             + ((source != null) ? getSize(source) : 0)
             ;
    }

    // -------------------------------------------------------------------- Move
    /**
     * Returns the WBXML size of Move element.
     *
     * @return the WBXML size of Move element
     */
    public long getSize(Move move) {
        Meta meta       = move.getMeta() ;
        ArrayList items = move.getItems();

        long size = 4
                  + getSize((AbstractCommand)move)
                  + ((meta != null) ? getSize(meta) : 0)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // --------------------------------------------------------------------- Put
    /**
     * Returns the WBXML size of Put element.
     *
     * @return the WBXML size of Put element
     */
    public long getSize(Put put) {
        String    lang        = put.getLang();
        Meta      meta        = put.getMeta() ;
        ArrayList devInfItems = put.getItems();

        long size = 4
                  + getSize((AbstractCommand)put)
                  + ((lang != null) ? 1 + lang.length()  : 0)
                  + ((meta != null) ? getSize(meta) : 0)
                  ;
        for (int i=0; devInfItems != null && i<devInfItems.size(); i++) {
            size += getSize((DevInfItem)devInfItems.get(i));
        }
        return size;
    }

    // -------------------------------------------------------------- DevInfItem
    /**
     * Returns the WBXML size of Item element.
     * @return the WBXML size of Item element
     */
    public long getSize(DevInfItem devInfItem) {
        Target     target     = devInfItem.getTarget()    ;
        Source     source     = devInfItem.getSource()    ;
        Meta       meta       = devInfItem.getMeta()      ;
        DevInfData devInfData = devInfItem.getDevInfData();

        return 4
             + ((target     != null) ? getSize(target)     : 0)
             + ((source     != null) ? getSize(source)     : 0)
             + ((meta       != null) ? getSize(meta)       : 0)
             + ((devInfData != null) ? getSize(devInfData) : 0)
             ;
    }

    // ----------------------------------------------------------------- Replace
    /**
     * Returns the WBXML size of Replace element.
     *
     * @return the WBXML size of Replace element
     */
    public long getSize(Replace replace) {
        Meta      meta  = replace.getMeta() ;
        ArrayList items = replace.getItems();

        long size = 4
                  + getSize((AbstractCommand)replace)
                  + ((meta != null) ? getSize(meta) : 0)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // ----------------------------------------------------------------- Results
    /**
     * Returns the WBXML size of Results element.
     *
     * @return the WBXML size of Results element
     */
    public long getSize(Results results) {
        String    msgRef     = results.getMsgRef()   ;
        String    cmdRef     = results.getCmdRef()   ;
        Meta      meta       = results.getMeta()     ;
        ArrayList targetRefs = results.getTargetRef();
        ArrayList sourceRefs = results.getSourceRef();
        ArrayList items      = results.getItems()    ;

        long size = 4
                  + getSize((AbstractCommand)results)
                  + ((msgRef != null) ? 4 + msgRef.length() : 0)
                  + ((cmdRef != null) ? 4 + cmdRef.length() : 0)
                  + ((meta   != null) ? getSize(meta)  : 0)
                  ;
        for (int i=0; targetRefs != null && i<targetRefs.size(); i++) {
            size += getSize((TargetRef)targetRefs.get(i));
        }
        for (int i=0; sourceRefs != null && i<sourceRefs.size(); i++) {
            size += getSize((SourceRef)sourceRefs.get(i));
        }
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // --------------------------------------------------------------- TargetRef
    /**
     * Returns the WBXML size of TargetRef element.
     * @return the WBXML size of TargetRef element
     */
    public long getSize(TargetRef targetRef) {
        String value  = targetRef.getValue() ;
        Target target = targetRef.getTarget();

        return 4
             + ((value  != null) ? value.length()       : 0)
             + ((target != null) ? getSize(target) : 0)
             ;
    }

    // ------------------------------------------------------------------ Search
    /**
     * Returns the WBXML size of Search element.
     *
     * @return the WBXML size of Search element
     */
    public long getSize(Search search) {
        Target    target  = search.getTarget() ;
        ArrayList sources = search.getSources();
        String    lang    = search.getLang()   ;
        Meta      meta    = search.getMeta()   ;
        Data      data    = search.getData()   ;

        long size =
                  + getSize((AbstractCommand)search)
                  + ((search.isNoResults()) ? 1                  : 0)
                  + ((target != null)       ? getSize(target) : 0)
                  + ((lang   != null)       ? 1 + lang.length()  : 0)
                  + ((meta   != null)       ? getSize(meta) : 0)
                  + ((data   != null)       ? getSize(data) : 0)
                  ;
        for (int i=0; sources != null && i<sources.size(); i++) {
            size += getSize((Source)sources.get(i));
        }
        return size;
    }

    // ---------------------------------------------------------------- Sequence
    /**
     * Returns the WBXML size of Sequence element.
     *
     * @return the WBXML size of Sequence element
     */
    public long getSize(Sequence sequence) {
        Meta      meta     = sequence.getMeta()    ;
        ArrayList commands = sequence.getCommands();

        long size = 4
                  + getSize((AbstractCommand)sequence)
                  + ((meta != null) ? getSize(meta) : 0)
                  ;
        for (int i=0; commands != null && i<commands.size(); i++) {
            size += getCommandSize((AbstractCommand)commands.get(i));
        }
        return size;
    }

    // ------------------------------------------------------------------ Status
    /**
     * Returns the WBXML size of Status element.
     *
     * @return the WBXML size of Status element
     */
    public long getSize(Status status) {
        CmdID     cmdID      = status.getCmdID()    ;
        String    msgRef     = status.getMsgRef()   ;
        String    cmdRef     = status.getCmdRef()   ;
        String    cmd        = status.getCmd()      ;
        ArrayList targetRefs = status.getTargetRef();
        ArrayList sourceRefs = status.getSourceRef();
        Cred      cred       = status.getCred()     ;
        Chal      chal       = status.getChal()     ;
        Data      data       = status.getData()     ;
        ArrayList items      = status.getItems()    ;

        long size = 4
                  + ((cmdID  != null) ? getSize(cmdID) : 0)
                  + ((msgRef != null) ? 4 + msgRef.length() : 0)
                  + ((cmdRef != null) ? 4 + cmdRef.length() : 0)
                  + ((cmd    != null) ? 4 + cmd.length()    : 0)
                  + ((cred   != null) ? getSize(cred)  : 0)
                  + ((chal   != null) ? getSize(chal)  : 0)
                  + ((data   != null) ? getSize(data)  : 0)
                  ;
        for (int i=0; targetRefs != null && i<targetRefs.size(); i++) {
            size += getSize((TargetRef)targetRefs.get(i));
        }
        for (int i=0; sourceRefs != null && i<sourceRefs.size(); i++) {
            size += getSize((SourceRef)sourceRefs.get(i));
        }
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }
    // -------------------------------------------------------------------- Chal
    /**
     * Returns the WBXML size of Chal element.
     *
     * @return the WBXML size of Chal element
     */
    public long getSize(Chal chal) {
        Meta meta = chal.getMeta();

        return 4
             + ((meta != null) ? getSize(meta) : 0)
             ;
    }

    // -------------------------------------------------------------------- Sync
    /**
     * Returns the WBXML size of Sync element.
     *
     * @return the WBXML size of Sync element
     */
    public long getSize(Sync sync) {
        Target    target          = sync.getTarget()         ;
        Source    source          = sync.getSource()         ;
        Meta      meta            = sync.getMeta()           ;
        Long      numberOfChanges = sync.getNumberOfChanges();
        ArrayList commands        = sync.getCommands()       ;

        long size = 4
                  + getSize((AbstractCommand)sync)
                  + ((target          != null) ? getSize(target) : 0)
                  + ((source          != null) ? getSize(source) : 0)
                  + ((meta            != null) ? getSize(meta)   : 0)
                  + ((numberOfChanges != null) ? 4               : 0)
                  ;
        for (int i=0; commands != null && i<commands.size(); i++) {
            size += getCommandSize((AbstractCommand)commands.get(i));
        }
        return size;
    }

    // --------------------------------------------------------- ItemizedCommand
    /**
     * Returns the WBXML size of the ItemizedCommand element
     */
    public long getSize(ItemizedCommand itemCmd) {
        Meta      meta  = itemCmd.getMeta() ;
        ArrayList items = itemCmd.getItems();
        long size = 0;

        size = getSize((AbstractCommand)itemCmd)
             + ((meta != null) ? getSize(meta) : 0)
             ;

        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // ----------------------------------------------------- ModificationCommand
    /**
     * Returns the WBXML size of ModificationCommand object.
     *
     * @return the WBXML size of ModificationCommand object
     */
    public long getSize(ModificationCommand modCmd) {
        return getSize((ItemizedCommand)modCmd);
    }

    // --------------------------------------------------------- ResponseCommand
    /**
     * Returns the WBXML size of ResponseCommand object.
     *
     * @return the WBXML size of ResponseCommand object
     */
    public long getSize(ResponseCommand responseCmd) {
        String msgRef        = responseCmd.getMsgRef()   ;
        String cmdRef        = responseCmd.getCmdRef()   ;
        ArrayList targetRefs = responseCmd.getTargetRef();
        ArrayList sourceRefs = responseCmd.getSourceRef();
        long size = 0;

        size = getSize((ItemizedCommand)responseCmd)
             + ((msgRef != null) ? 4 + msgRef.length() : 0)
             + ((cmdRef != null) ? 4 + cmdRef.length() : 0)
             ;

        for (int i=0; targetRefs != null && i<targetRefs.size(); ++i) {
            size += getSize((TargetRef)targetRefs.get(i));
        }
        for (int i=0; sourceRefs != null && i<sourceRefs.size(); ++i) {
            size += getSize((SourceRef)sourceRefs.get(i));
        }
        return size;
    }
}
