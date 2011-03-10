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

/**
 * This interface defines the functionality of a SyncML message size calculator.
 *
 * @version $Id: MessageSizeCalculator.java,v 1.4 2007/07/30 22:10:06 stefano_fornari Exp $
 */
public interface MessageSizeCalculator {

    // ---------------------------------------------------------- Public Methods
    /**
     * Returns the default overhead for the SyncML message
     *
     * @return overhead for SyncML message
     */
    public long getMsgSizeOverhead();
    // ------------------------------------------------------------------ SyncML

    /**
     * Returns the WBXML overhead for SyncML object
     *     sizeof(<SyncML xmlns='SYNCML:SYNCML1.1'>\n</SyncML>\n)
     *
     * @return overhead for SyncML object
     */
    public long getSyncMLOverhead();

    /**
     * Returns the WBXML size of the SyncML object
     *
     * @param syncML the SyncML element
     *
     * @return size the WBXML size of the SyncML element
     */
    public long getSize(SyncML syncML);

    // ----------------------------------------------------------------- SyncHdr

    /**
     * Returns the WBXML size of the SyncHdr object
     *
     * @param syncHdr the SyncHdr element
     *
     * @return size the WBXML size of the SyncHdr element
     */
    public long getSize(SyncHdr syncHdr);

    /**
     * Returns the overhead of the RespURI element.
     *
     * @return the overhead of the RespURI element.
     */
    public long getRespURIOverhead();

    // ---------------------------------------------------------------- SyncBody

    /**
     * Returns the WBXML overhead for SyncBody object
     *     sizeof(<SyncBody>\n</SyncBody>\n)
     *
     * @return overhead for SyncBody object
     */
    public long getSyncBodyOverhead();

    /**
     * Returns the WBXML size of the SyncBody object
     *
     * @param syncBody the SyncBody element
     *
     * @return size the WBXML size of the SyncBody element
     */
    public long getSize(SyncBody syncBody);

    // ------------------------------------------------------------------ VerDTD

    /**
     * Returns the WBXML size of VerDTD object
     *
     * @return the WBXML size of VerDTD object
     */
    public long getSize(VerDTD verDTD);

    // ---------------------------------------------------------------- VerProto
    /**
     * Returns the WBXML size of VerProto object
     *
     * @return the WBXML size of VerProto object
     */
    public long getSize(VerProto verProto);

    // --------------------------------------------------------------- SessionID

    /**
     * Returns the WBXML size of SessionID object
     *
     * @return the WBXML size of SessionID object
     */
    public long getSize(SessionID sessionID);

    // ------------------------------------------------------------------ Target

    /**
     * Returns the WBXML size of this element.
     * @return the WBXML size of this element
     */
    public long getSize(Target target);

    // ------------------------------------------------------------------ Filter

    /**
     * Returns the WBXML size of Filter element.
     *
     * @return the WBXML size of Filter element
     */
    public long getSize(Filter filter);

    // ------------------------------------------------------------------ Source

    /**
     * Returns the WBXML size of this element.
     * @return the WBXML size of this element
     */
    public long getSize(Source source);

    // -------------------------------------------------------------------- Cred

    /**
     * Returns the WBXML size of this element.
     * @return the WBXML size of this element
     */
    public long getSize(Cred cred);

    // -------------------------------------------------------------------- Meta

    /**
     * Returns the WBXML size of this object.
     *
     * @return the WBXML size of this object
     */
    public long getSize(Meta meta);

    // ------------------------------------------------------------------ Anchor

    /**
     * Returns the WBXML size of this element.
     *
     * @return the WBXML size of this element
     */
    public long getSize(Anchor anchor);

    // --------------------------------------------------------------------- EMI
    /**
     * Returns the WBXML size of EMI element.
     *
     * @return the WBXML size of EMI element
     */
    public long getSize(EMI emi);

    // --------------------------------------------------------------- NextNonce
    /**
     * Returns the WBXML size of NextNonce element.
     *
     * @return the WBXML size of NextNonce element
     */
    public long getSize(NextNonce nextNonce);

    // --------------------------------------------------------------------- Mem
    /**
     * Returns the WBXML size of Mem object.
     *
     * @return the WBXML size of Mem object
     */
    public long getSize(Mem mem);

    // --------------------------------------------------------- AbstractCommand
    /**
     * Returns the WBXML size of AbstractCommand object.
     * @return the WBXML size of AbstractCommand object
     */
    public long getSize(AbstractCommand command);

    /**
     * Gets the XML size of the command
     *
     * @return the XML size of the command
     */
    public long getCommandSize(AbstractCommand command);

    // ------------------------------------------------------------------- CmdID
    /**
     * Returns the WBXML size of CmdID element.
     * @return the WBXML size of CmdID element
     */
    public long getSize(CmdID cmdID);

    // --------------------------------------------------------------------- Add
    /**
     * Returns the WBXML size of Add element.
     *
     * @return the WBXML size of Add element
     */
    public long getSize(Add add);

    // -------------------------------------------------------------------- Item
    /**
     * Returns the WBXML size of Item element.
     * @return the WBXML size of Item element
     */
    public long getSize(Item item);

    // ------------------------------------------------------------ SourceParent
    /**
     * Returns the WBXML size of SourceParent element.
     * @return the WBXML size of SourceParent element
     */
    public long getSize(SourceParent sourceParent);

    // ------------------------------------------------------------ TargetParent
    /**
     * Returns the WBXML size of TargetParent element.
     * @return the WBXML size of TargetParent element
     */
    public long getSize(TargetParent targetParent);

    // ------------------------------------------------------------- ComplexData
    /**
     * Returns the WBXML size of ComplexData element.
     *
     * @return the WBXML size of ComplexData element
     */
    public long getSize(ComplexData complexData);

    // -------------------------------------------------------------------- Data
    /**
     * Returns the WBXML size of Data element.
     *
     * @return the WBXML size of Data element
     */
    public long getSize(Data data);

    // -------------------------------------------------------------- DevInfData
    /**
     * Returns the WBXML size of DevInfData element.
     *
     * @return the WBXML size of DevInfData element
     */
    public long getSize(DevInfData devInfData);

    // ------------------------------------------------------------------ DevInf
    /**
     * Returns the WBXML size of DevInf element.
     *
     * @return the WBXML size of DevInf element
     */
    public long getSize(DevInf devInf);

    // --------------------------------------------------------------- DataStore
    /**
     * Returns the WBXML size of DataStore element.
     *
     * @return the WBXML size of DataStore element
     */
    public long getSize(DataStore dataStore);

    // ------------------------------------------------------------------ CTInfo
    /**
     * Returns the WBXML size of CTInfo element.
     *
     * @return the WBXML size of CTInfo element
     */
    public long getSize(CTInfo cTInfo);

    // --------------------------------------------------------------- SourceRef
    /**
     * Returns the WBXML size of SourceRef element.
     * @return the WBXML size of SourceRef element
     */
    public long getSize(SourceRef sourceRef);

    // ------------------------------------------------------------------- DSMem
    /**
     * Returns the WBXML size of DSMem element
     *
     * @return the WBXML size of DSMem element
     */
    public long getSize(DSMem dsMem);

    // ----------------------------------------------------------------- SyncCap
    /**
     * Returns the WBXML size of SyncCap object.
     *
     * @return the WBXML size of SyncCap object
     */
    public long getSize(SyncCap syncCap);

    // ---------------------------------------------------------------- SyncType
    /**
     * Returns the WBXML size of SyncType object.
     *
     * @return the WBXML size of SyncType object
     */
    public long getSize(SyncType syncType);

    // ----------------------------------------------------------------- CTCapV1

    /**
     * Returns the WBXML size of CTCap element for DevInf v1.1.x.
     *
     * @return the WBXML size of CTCap element
     */
    public long getSize(CTCapV1 ctCapV1);

    // ------------------------------------------------------------------- CTCap

    /**
     * Returns the WBXML size of CTCap element for DevInf v1.2.
     *
     * @return the WBXML size of CTCap element
     */
    public long getSize(CTCap ctCap);

    // ---------------------------------------------------------------- Property

    /**
     * Returns the WBXML size of Property element.
     *
     * @return the WBXML size of Property element
     */
    public long getSize(Property property);

    // --------------------------------------------------------------- PropParam

    /**
     * Returns the WBXML size of PropParam element.
     *
     * @return the WBXML size of PropParam element
     */
    public long getSize(PropParam propParam);

    // --------------------------------------------------------------- FilterCap

    /**
     * Returns the WBXML size of FilterCap element.
     *
     * @return the WBXML size of FilterCap element
     */
    public long getSize(FilterCap filterCap);

    // --------------------------------------------------------- CTTypeSupported

    /**
     * Returns the WBXML size of this element.
     *
     * @return the WBXML size of this element
     */
    public long getSize(CTType ctTypeSupported);

    // ---------------------------------------------------------------- Property
    /**
     * Returns the WBXML size of CTProperty element.
     *
     * @return the WBXML size of CTProperty element
     */
    public long getSize(CTProperty property);

    // ------------------------------------------------------------- CTParameter
    /**
     * Returns the WBXML size of CTParameter element.
     *
     * @return the WBXML size of CTParameter element
     */
    public long getSize(CTParameter parameter);

    // --------------------------------------------------------------------- Ext

    /**
     * Returns the WBXML size of Ext element.
     *
     * @return the WBXML size of Ext element
     */
    public long getSize(Ext ext);

    // ------------------------------------------------------------------- Alert

    /**
     * Returns the WBXML size of Alert element.
     *
     * @return the WBXML size of Alert element
     */
    public long getSize(Alert alert);

    // ------------------------------------------------------------------ Atomic

    /**
     * Returns the WBXML size of Atomic element.
     *
     * @return the WBXML size of Atomic element
     */
    public long getSize(Atomic atomic);

    // -------------------------------------------------------------------- Copy

    /**
     * Returns the WBXML size of Copy element.
     *
     * @return the WBXML size of Copy element
     */
    public long getSize(Copy copy);

    // ------------------------------------------------------------------ Delete
    /**
     * Returns the WBXML size of Delete element.
     *
     * @return the WBXML size of Delete element
     */
    public long getSize(Delete delete);

    // -------------------------------------------------------------------- Exec

    /**
     * Returns the WBXML size of Exec element.
     *
     * @return the WBXML size of Exec element
     */
    public long getSize(Exec exec);

    // --------------------------------------------------------------------- Get

    /**
     * Returns the WBXML size of Get element.
     *
     * @return the WBXML size of Get element
     */
    public long getSize(Get get);

    // --------------------------------------------------------------------- Map

    /**
     * Returns the WBXML size of Map element.
     *
     * @return the WBXML size of Map element
     */
    public long getSize(Map map);

    // -------------------------------------------------------------------- Move
    /**
     * Returns the WBXML size of Move element.
     *
     * @return the WBXML size of Move element
     */
    public long getSize(Move move);

    // ----------------------------------------------------------------- MapItem

    /**
     * Returns the WBXML size of MapItem element.
     *
     * @return the WBXML size of MapItem element
     */
    public long getSize(MapItem mapItem);

    // --------------------------------------------------------------------- Put

    /**
     * Returns the WBXML size of Put element.
     *
     * @return the WBXML size of Put element
     */
    public long getSize(Put put);

    // -------------------------------------------------------------- DevInfItem
    /**
     * Returns the WBXML size of Item element.
     * @return the WBXML size of Item element
     */
    public long getSize(DevInfItem devInfItem);

    // ----------------------------------------------------------------- Replace
    /**
     * Returns the WBXML size of Replace element.
     *
     * @return the WBXML size of Replace element
     */
    public long getSize(Replace replace);

    // ----------------------------------------------------------------- Results
    /**
     * Returns the WBXML size of Results element.
     *
     * @return the WBXML size of Results element
     */
    public long getSize(Results results);

    // --------------------------------------------------------------- TargetRef
    /**
     * Returns the WBXML size of TargetRef element.
     * @return the WBXML size of TargetRef element
     */
    public long getSize(TargetRef targetRef);

    // ------------------------------------------------------------------ Search
    /**
     * Returns the WBXML size of Search element.
     *
     * @return the WBXML size of Search element
     */
    public long getSize(Search search);

    // ---------------------------------------------------------------- Sequence
    /**
     * Returns the WBXML size of Sequence element.
     *
     * @return the WBXML size of Sequence element
     */
    public long getSize(Sequence sequence);

    // ------------------------------------------------------------------ Status
    /**
     * Returns the WBXML size of Status element.
     *
     * @return the WBXML size of Status element
     */
    public long getSize(Status status);

    // -------------------------------------------------------------------- Chal

    /**
     * Returns the WBXML size of Chal element.
     *
     * @return the WBXML size of Chal element
     */
    public long getSize(Chal chal);

    // -------------------------------------------------------------------- Sync

    /**
     * Returns the WBXML size of Sync element.
     *
     * @return the WBXML size of Sync element
     */
    public long getSize(Sync sync);

    // --------------------------------------------------------- ItemizedCommand

    /**
     * Returns the WBXML size of the ItemizedCommand element
     */
    public long getSize(ItemizedCommand itemCmd);

    // ----------------------------------------------------- ModificationCommand

    /**
     * Returns the WBXML size of ModificationCommand object.
     *
     * @return the WBXML size of ModificationCommand object
     */
    public long getSize(ModificationCommand modCmd);

    // --------------------------------------------------------- ResponseCommand

    /**
     * Returns the WBXML size of ResponseCommand object.
     *
     * @return the WBXML size of ResponseCommand object
     */
    public long getSize(ResponseCommand responseCmd);

}
