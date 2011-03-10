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

import java.util.List;
import java.util.ArrayList;

import com.funambol.framework.core.*;

/**
 *  Utility class for calculate the XML size and WBXML size of framework Objects
 *
 * @version $Id: XMLSizeCalculator.java,v 1.2 2007-11-28 11:13:24 nichele Exp $
 */
public class XMLSizeCalculator implements MessageSizeCalculator {

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the default XML overhead for the SyncML message
     *
     * @return overhead for SyncML message
     */
    public long getMsgSizeOverhead() {
        return 1475;
    }

    // ------------------------------------------------------------------ SyncML
    /**
     * Returns the XML overhead for SyncML object
     *     sizeof(<SyncML xmlns='SYNCML:SYNCML1.1'>\n</SyncML>\n)
     *
     * @return overhead for SyncML object
     */
    public long getSyncMLOverhead() {
        return 43;
    }

    /**
     * Returns the XML size of the SyncML object
     *    sizeof(<SyncML xmlns='SYNCML:SYNCML1.1'>\n) +
     *    sizeof(syncHdr)    +
     *    sizeof(syncBody)   +
     *    sizeof(</SyncML>)
     *
     * @param syncML the SyncML element
     *
     * @return size the XML size of the SyncML element
     */
    public long getSize(SyncML syncML) {
        SyncHdr  syncHdr  = syncML.getSyncHdr() ;
        SyncBody syncBody = syncML.getSyncBody();

        return 43
             + getSize(syncHdr )
             + getSize(syncBody)
             ;
    }

    // ----------------------------------------------------------------- SyncHdr
    /**
     * Returns the XML size of SyncHdr element as:
     *    sizeof(<SyncHdr>\n)      +
     *    if verDTD != null
     *        sizeof(verDTD)       +
     *    if verProto != null
     *        sizeof(verProto)     +
     *    if sessionID != null
     *        sizeof(<SessionID>)  +
     *    if msgId != null
     *        sizeof(<MsgID>)      +
     *        msgId.length         +
     *        sizeof(</MsgID>\n)   +
     *    if target != null
     *        sizeof(target)       +
     *    if source != null
     *        sizeof(source)       +
     *    if respURI
     *        sizeof(<RespURI>)    +
     *        sizeof(respURI)      +
     *        sizeof(</RespURI>\n) +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)  +
     *    if cred != null
     *        sizeof(cred)         +
     *    if meta != null
     *        sizeof(meta)         +
     *    sizeof(</SyncHdr>\n)     +
     *
     * @return the XML size of this element
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

        return 21
             + ((verDTD    != null) ? getSize(verDTD)    : 0)
             + ((verProto  != null) ? getSize(verProto)  : 0)
             + ((sessionID != null) ? getSize(sessionID) : 0)
             + ((msgID     != null) ? 16 + msgID.length(): 0)
             + ((target    != null) ? getSize(target)    : 0)
             + ((source    != null) ? getSize(source)    : 0)
             + ((respURI   != null) ? 19 + respURI.length() : 90)
             + (noResp              ? 18                 : 0)
             + ((cred      != null) ? getSize(cred)      : 0)
             + ((meta      != null) ? getSize(meta)      : 0)
             ;
    }

    /**
     * Returns the XML overhead of the RespURI element.
     *
     * @return the XML overhead of the RespURI element.
     */
    public long getRespURIOverhead() {
        return 150;
    }

    // ---------------------------------------------------------------- SyncBody
    /**
     * Returns the XML overhead for SyncBody object
     *     sizeof(<SyncBody>\n</SyncBody>\n)
     *
     * @return overhead for SyncBody object
     */
    public long getSyncBodyOverhead() {
        return 23;
    }

    /**
     * Returns the XML size of the SyncBody object
     *
     *    sizeof(<SyncBody>\n)              +
     *    for (i=0; i<commands.size(); i++)
     *        sizeof(commands[i])           +
     *    if final
     *        sizeof(<Final></Final>\n)     +
     *    sizeof(</SyncBody>\n)             +
     * @param syncBody the SyncBody element
     *
     * @return size the XML size of the SyncBody element
     */
    public long getSize(SyncBody syncBody) {
        ArrayList commands = syncBody.getCommands();

        long size = 23
                  + ((syncBody.isFinalMsg()) ? 16 : 0)
                  ;

        for (int i=0; i<commands.size(); i++) {
            size += getCommandSize((AbstractCommand)commands.get(i));
        }
        return size;
    }

    // ------------------------------------------------------------------ VerDTD
    /**
     * Returns the XML size of VerDTD object:
     *    sizeof(<VerDTD>)    +
     *    sizeof(value)       +
     *    sizeof(</VerDTD>\n)
     *
     * @return the XML size of VerDTD object
     */
    public long getSize(VerDTD verDTD) {
        return 18
             + verDTD.getValue().length()
             ;
    }

    // ---------------------------------------------------------------- VerProto
    /**
     * Returns the XML size of VerProto object:
     *    sizeof("<VerProto>") +
     *    sizeof(version)      +
     *    sizeof("</VerProto>\n")
     *
     * @return the XML size of this object
     */
    public long getSize(VerProto verProto) {
        return 22
             + verProto.getVersion().length()
             ;
    }

    // --------------------------------------------------------------- SessionID
    /**
     * Returns the XML size of SessionID element as:
     *    sizeof("<SessionID>") +
     *    sizeof(sessionID)     +
     *    sizeof("</SessionID>\n")
     *
     * @return the XML size of this element
     */
    public long getSize(SessionID sessionID) {
        return 24
             + sessionID.getSessionID().length()
             ;
    }

    // ------------------------------------------------------------------ Target
    /**
     * Returns the XML size of Target element:
     *    sizeof(<Target>\n)      +
     *    if locURI != null
     *        sizeof(<LocURI>)    +
     *        sizeof(locURI)      +
     *        sizeof(</LocURI>\n) +
     *    if locName != null
     *      sizeof(<LocName>)     +
     *      sizeof(locName)       +
     *      sizeof(</LocName>\n)  +
     *    if filter != null
     *      sizeof(filter)        +
     *    sizeof(</Target>\n)
     *
     * @return the XML size of this element
     */
    public long getSize(Target target) {
        String locURI  = target.getLocURI() ;
        String locName = target.getLocName();
        Filter filter  = target.getFilter() ;

        return 19
             + ((locURI  != null) ? (18 + locURI.length() ) : 0)
             + ((locName != null) ? (20 + locName.length()) : 0)
             + ((filter  != null) ? getSize(filter)         : 0)
             ;
    }

    // ------------------------------------------------------------ TargetParent
    /**
     * Returns the XML size of TargetParent element:
     *    sizeof(<TargetParent>\n)      +
     *    if locURI != null
     *        sizeof(<LocURI>)    +
     *        sizeof(locURI)      +
     *        sizeof(</LocURI>\n) +
     *    sizeof(</TargetParent>\n)
     *
     * @return the XML size of this element
     */
    public long getSize(TargetParent targetParent) {
        String locURI = targetParent.getLocURI();

        return 31
             + ((locURI != null) ? (18 + locURI.length()) : 0)
             ;
    }

    // ------------------------------------------------------------------ Filter
    /**
     * Returns the XML size of Filter element:
     *    sizeof(<Filter>\n)        +
     *    if meta != null
     *        sizeof(meta)          +
     *    if field != null
     *      sizeof(<Field>)         +
     *      sizeof(field)           +
     *      sizeof(</Field>\n)      +
     *    if record != null
     *      sizeof(<Record>)        +
     *      sizeof(record)          +
     *      sizeof(</Record>\n)     +
     *    if filterType != null
     *      sizeof(<FilterType>)    +
     *      sizeof(filterType)      +
     *      sizeof(</FilterType>\n) +
     *    sizeof(</Filter>\n)
     *
     * @return the XML size of this element
     */
    public long getSize(Filter filter) {
        Meta   meta       = filter.getMeta()      ;
        Item   field      = filter.getField()     ;
        Item   record     = filter.getRecord()    ;
        String filterType = filter.getFilterType();

        return 19
             + ((meta   != null)     ? getSize(meta)            : 0)
             + ((field  != null)     ? 16 + getSize(field)      : 0)
             + ((record != null)     ? 18 + getSize(record)     : 0)
             + ((filterType != null) ? 26 + filterType.length() : 0)
             ;
    }

    // ------------------------------------------------------------------ Source
    /**
     * Returns the XML size of Source element:
     *    sizeof(<Source>\n)      +
     *    if locURI != null
     *        sizeof(<LocURI>)    +
     *        sizeof(locURI)      +
     *        sizeof(</LocURI>\n) +
     *    if locName != null
     *      sizeof(<LocName>)     +
     *      sizeof(locName)       +
     *      sizeof(</LocName>\n)  +
     *    sizeof(</Source>\n)
     *
     * @return the XML size of this element
     */
    public long getSize(Source source) {
        String locURI  = source.getLocURI() ;
        String locName = source.getLocName();

        return 19
             + ((locURI != null)  ? (18 + locURI.length() ) : 0)
             + ((locName != null) ? (20 + locName.length()) : 0)
             ;
    }

    // ------------------------------------------------------------ SourceParent
    /**
     * Returns the XML size of SourceParent element:
     *    sizeof(<SourceParent>\n)      +
     *    if locURI != null
     *        sizeof(<LocURI>)    +
     *        sizeof(locURI)      +
     *        sizeof(</LocURI>\n) +
     *    sizeof(</SourceParent>\n)
     *
     * @return the XML size of this element
     */
    public long getSize(SourceParent sourceParent) {
        String locURI = sourceParent.getLocURI();

        return 31
             + ((locURI != null) ? (18 + locURI.length()) : 0)
             ;
    }

    // -------------------------------------------------------------------- Cred

    /**
     * Returns the XML size of Cred element as:
     *    sizeof(<Cred>\n)      +
     *    if meta != null
     *      sizeof(meta)        +
     *    if data != null
     *        sizeof(<Data>)    +
     *        sizeof(data)      +
     *        sizeof(</Data>\n) +
     *    sizeof(</Cred>\n)
     *
     * @return the XML size of this element
     */
    public long getSize(Cred cred) {
        Authentication auth = cred.getAuthentication();
        Meta   meta = auth.getMeta();
        String data = cred.getData();

        return 15
             + ((meta != null) ? getSize(meta)      : 0)
             + ((data != null) ? 14 + data.length() : 0)
             ;
    }

    // -------------------------------------------------------------------- Meta

    /**
     * Returns the XML size of Meta object.
     *    sizeof(<Meta>\n)                               +
     *    if fieldLevel
     *        sizeof(<FieldLevel></FieldLevel>\n)  +
     *    if format != null
     *        sizeof(<Format>)                        +
     *        sizeof(format)                          +
     *        sizeof(</Format>\n)                     +
     *    if type != null
     *        sizeof(<Type>)                          +
     *        sizeof(type)                            +
     *        sizeof(</Type>\n)                       +
     *    if mark != null
     *        sizeof(mark)                            +
     *    if size != null
     *        sizeof(<Size>)                          +
     *        sizeof(size)                            +
     *        sizeof(</Size>\n)                       +
     *    if anchor != null
     *        sizeof(anchor)                          +
     *    if version != null
     *        sizeof(<Version>)                       +
     *        sizeof(version)                         +
     *        sizeof(</Version>\n)                    +
     *    if nextNonce != null
     *        sizeof(nextNonce)                       +
     *    if maxMsgSize != null
     *        sizeof(<MaxMsgSize>)                    +
     *        sizeof(maxMsgSize)                      +
     *        sizeof(</MaxMsgSize>\n)                 +
     *    if maxObjSize != null
     *        sizeof(<MaxObjSize>)                    +
     *        sizeof(maxObjSize)                      +
     *        sizeof(</MaxObjSize>\n)                 +
     *    for (i=0; emi != null && i<emi.size(); i++)
     *        sizeof(emi[i]                           +
     *    if mem != null
     *        sizeof(mem)                             +
     *    sizeof(</Meta>\n)
     * @return the XML size of Meta object
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

        sizeMeta = 37
                 + (fieldLevel          ? 26                                : 0)
                 + ((format    != null) ? 18 + format.length()              : 0)
                 + ((type      != null) ? 14 + type.length()                : 0)
                 + ((mark      != null) ? 14 + mark.length()                : 0)
                 + ((size      != null) ? 14 + String.valueOf(size).length(): 0)
                 + ((anchor    != null) ? getSize(anchor)                   : 0)
                 + ((version   != null) ? 20 + version.length()             : 0)
                 + ((nextNonce != null) ? getSize(nextNonce)                : 0)
                 + ((maxMsgSize!= null) ? 26 + String.valueOf(maxMsgSize).length() : 0)
                 + ((maxObjSize!= null) ? 26 + String.valueOf(maxObjSize).length() : 0)
                 + ((mem       != null) ? getSize(mem)                      : 0)
                 ;

	    for (int i=0; emi != null && i < emi.size(); i++) {
            sizeMeta += getSize((EMI)emi.get(i));
        }

       return sizeMeta;
    }

    // ------------------------------------------------------------------ Anchor

    /**
     * Returns the XML size of Anchor element as:
     *    sizeof(<Anchor xmlns='syncml:metinf'>\n) +
     *    if last != null
     *        sizeof(<Last>)                       +
     *        sizeof(last)                         +
     *        sizeof(</Last>\n)                    +
     *    if next != null
     *        sizeof(<Next>)                       +
     *        sizeof(next)                         +
     *        sizeof(</Next>\n)                    +
     *    sizeof(</Anchor>\n)                      +
     *
     * @return the XML size of this element
     */
    public long getSize(Anchor anchor) {
        String last = anchor.getLast();
        String next = anchor.getNext();
        return 41
             + ((last != null) ? 14 + last.length() : 0)
             + ((next != null) ? 14 + next.length() : 0)
             ;
    }


    // --------------------------------------------------------------------- EMI
    /**
     * Returns the XML size of EMI element as:
     *    sizeof(<EMI>)   +
     *    sizeof(value)   +
     *    sizeof(</EMI>\n)
     *
     * @return the XML size of this element
     */
    public long getSize(EMI emi) {
        return 12
             + emi.getValue().length()
             ;
    }

    // --------------------------------------------------------------- NextNonce
    /**
     * Returns the XML size of NextNonce element as:
     *    sizeof(<NextNonce>)    +
     *    sizeof(value)          +
     *    sizeof(</NextNonce>\n)
     *
     * @return the XML size of NextNonce element
     */
    public long getSize(NextNonce nextNonce) {
        return 24
             + nextNonce.getValueAsBase64().length()
             ;
    }

    // --------------------------------------------------------------------- Mem
    /**
     * Returns the XML size of Mem element as:
     *    sizeof("<Mem>\n")                 +
     *    if sharedMem
     *        sizeof("<Shared></Shared>\n") +
     *    if freeMem
     *        sizeof("<FreeMem>")           +
     *        sizeof(freeMem)               +
     *        sizeof("</FreeMem>\n")        +
     *    if freeID
     *        sizeof("<FreeID>")            +
     *        sizeof(freeID)                +
     *        sizeof("</FreeID>\n")         +
     *    sizeof("</Mem>\n")
     *
     * @return the XML size of Mem element
     */
    public long getSize(Mem mem) {
        boolean sharedMem = mem.isSharedMem();
        long freeMem      = mem.getFreeMem() ;
        long freeID       = mem.getFreeID()  ;

        return 13
             + ((sharedMem)    ? 18                                    : 0)
             + ((freeMem != 0) ? 20 + String.valueOf(freeMem).length() : 0)
             + ((freeID  != 0) ? 18 + String.valueOf(freeID).length()  : 0)
             ;
    }

    // --------------------------------------------------------- AbstractCommand
    /**
     * Gets the XML size of all element of AbstractCommand
     *    if cmdID != null
     *        sizeof(cmdID)               +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n) +
     *    if cred != null
     *        sizeof(cred)                +
     * @return the XML size of all element of AbstractCommand
     */
    public long getSize(AbstractCommand command) {
        CmdID cmdID = command.getCmdID();
        Cred  cred  = command.getCred() ;

        return ((cmdID != null)      ? getSize(cmdID) : 0)
             + ((command.isNoResp()) ? 18                : 0)
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
     * Returns the XML size of CmdID element as:
     *    sizeof(<CmdID>)    +
     *    sizeof(cmdID)      +
     *    sizeof(</CmdID>\n)
     *
     * @return the XML size of CmdID element
     */
    public long getSize(CmdID cmdID) {
        return 16
             + cmdID.getCmdID().length();
    }

    // --------------------------------------------------------------------- Add
    /**
     * Returns the XML size of Add element as:
     *    sizeof(<Add>\n)                                     +
     *    if cmdID != null
     *        sizeof(cmdID)                                   +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                     +
     *    if cred != null
     *        sizeof(cred)                                    +
     *    if meta != null
     *      sizeof(meta)                                      +
     *    for (int i=0; items != null && i<items.size(); i++)
     *      sizeof(items[i])                                  +
     *    sizeof(</Add>\n)
     *
     * @return the XML size of Add element
     */
    public long getSize(Add add) {
        Meta meta       = add.getMeta() ;
        ArrayList items = add.getItems();

        long size = 13
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
     * Returns the XML size of Item element as:
     *    sizeof(<Item>\n)                    +
     *    if target != null
     *        sizeof(target)                  +
     *    if source != null
     *        sizeof(source)                  +
     *    if sourceparent != null
     *        sizeof(sourceparent)            +
     *    if targetparent != null
     *        sizeof(targetparent)            +
     *    if meta != null
     *        sizeof(meta)                    +
     *    if data != null
     *        sizeof(data)                    +
     *    if moreData
     *        sizeof(<MoreData></MoreData>\n) +
     *    sizeof(</Item>\n)
     *
     * @return the XML size of Item element
     */
    public long getSize(Item item) {
        Target       target       = item.getTarget()      ;
        Source       source       = item.getSource()      ;
        SourceParent sourceParent = item.getSourceParent();
        TargetParent targetParent = item.getTargetParent();
        Meta         meta         = item.getMeta()        ;
        ComplexData  data         = item.getData()        ;
        
        return 15
             + ((target       != null) ? getSize(target)       : 0)
             + ((source       != null) ? getSize(source)       : 0)
             + ((sourceParent != null) ? getSize(sourceParent) : 0)
             + ((targetParent != null) ? getSize(targetParent) : 0)
             + ((meta         != null) ? getSize(meta)         : 0)
             + ((data         != null) ? getSize(data)         : 0)
             + ((item.isMoreData())    ? 22                    : 0)
             ;
    }

    // ------------------------------------------------------------- ComplexData
    /**
     * Returns the XML size of ComplexData element as:
     *    sizeof(<Data>\n)   +
     *    if data != null
     *        sizeof(data)   +
     *    if anchor != null
     *        sizeof(anchor) +
     *    if devinf != null
     *        sizeof(devinf) +
     *    sizeof(</Data>\n)
     * @return the XML size of ComplexData element
     */
    public long getSize(ComplexData complexData) {
        Anchor anchor = complexData.getAnchor();
        DevInf devInf = complexData.getDevInf();

        return 15
             + complexData.getSize()
             + ((anchor != null) ? getSize(anchor) :0)
             + ((devInf != null) ? getSize(devInf) :0)
             ;
    }

    // -------------------------------------------------------------------- Data
    /**
     * Returns the XML size of Data element as:
     *    sizeof(<Data>)    +
     *    sizeof(data)       +
     *    sizeof(</Data>\n)
     *
     * @return the XML size of Data element
     */
    public long getSize(Data data) {
        return 14
             + data.getSize();
    }

    // -------------------------------------------------------------- DevInfData
    /**
     * Returns the XML size of DevInfData element as:
     *    sizeof(<Data>\n)   +
     *    id devInf != null
     *        sizeof(devInf) +
     *    sizeof(</Data>\n)
     *
     * @return the XML size of DevInfData element
     */
    public long getSize(DevInfData devInfData) {
        DevInf devInf = devInfData.getDevInf();
        return 15
             + ((devInf != null) ? getSize(devInf) : 0)
             ;
    }

    // ------------------------------------------------------------------ DevInf
    /**
     * Returns the XML size of DevInf element as:
     *    sizeof(<DevInf xmlns='syncml:devinf'>\n)                        +
     *    if verDTD != null
     *        sizeof(verDTD)                                              +
     *    if man != null
     *        sizeof(<Man>)                                               +
     *        sizeof(man)                                                 +
     *        sizeof(</Man>\n)                                            +
     *    if mod != null
     *        sizeof(<Mod>)                                               +
     *        sizeof(mod)                                                 +
     *        sizeof(</Mod>\n)                                            +
     *    if oem != null
     *        sizeof(<OEM>)                                               +
     *        sizeof(oem)                                                 +
     *        sizeof(</OEM>\n)                                            +
     *    if fwV != null
     *        sizeof(<FwV>)                                               +
     *        sizeof(fwV)                                                 +
     *        sizeof(</FwV>\n)                                            +
     *    if swV != null
     *        sizeof(<SwV>)                                               +
     *        sizeof(swV)                                                 +
     *        sizeof(</SwV>\n)                                            +
     *    if hwV != null
     *        sizeof(<HwV>)                                               +
     *        sizeof(hwV)                                                 +
     *        sizeof(</HwV>\n)                                            +
     *    if devID != null
     *        sizeof(<DevID>)                                             +
     *        sizeof(devID)                                               +
     *        sizeof(</DevID>\n)                                          +
     *    if devTyp != null
     *        sizeof(<DevTyp>)                                            +
     *        sizeof(devTyp)                                              +
     *        sizeof(</DevTyp>\n)                                         +
     *    if utc
     *        sizeof(<UTC></UTC>\n)                                       +
     *    if supportLargeObjs
     *        sizeof(<SupportLargeObjs></SupportLargeObjs>\n)             +
     *    if supportNumberOfChanges
     *        sizeof(<SupportNumberOfChanges></SupportNumberOfChanges>\n) +
     *    for (int i=0; datastores != null && i<datastores.size(); i++)
     *        sizeof(datastores[i])                                       +
     *    for (int i=0; ctCapsV1 != null && i<ctCapsV1.size(); i++)
     *        sizeof(ctCapsV1[i])                                         +
     *    for (int i=0; exts != null && i<exts.size(); i++)
     *        sizeof(exts[i])                                             +
     *    sizeof(</DevInf>\n)
     *
     * @return the XML size of DevInf element
     */
    public long getSize(DevInf devInf) {
        VerDTD verDTD   = devInf.getVerDTD()    ;
        String man      = devInf.getMan()       ;
        String mod      = devInf.getMod()       ;
        String oem      = devInf.getOEM()       ;
        String fwV      = devInf.getFwV()       ;
        String swV      = devInf.getSwV()       ;
        String hwV      = devInf.getHwV()       ;
        String devID    = devInf.getDevID()     ;
        String devTyp   = devInf.getDevTyp()    ;
        List dataStores = devInf.getDataStores();
        List ctCapsV1   = devInf.getCTCapsV1()  ;
        List exts       = devInf.getExts()      ;

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

        return 41
             + ((verDTD != null)                    ? getSize(verDTD)      : 0)
             + ((man    != null)                    ? 12 + man.length()    : 0)
             + ((mod    != null)                    ? 12 + mod.length()    : 0)
             + ((oem    != null)                    ? 12 + oem.length()    : 0)
             + ((fwV    != null)                    ? 12 + fwV.length()    : 0)
             + ((swV    != null)                    ? 12 + swV.length()    : 0)
             + ((hwV    != null)                    ? 12 + hwV.length()    : 0)
             + ((devID  != null)                    ? 16 + devID.length()  : 0)
             + ((devTyp != null)                    ? 18 + devTyp.length() : 0)
             + ((devInf.isUTC())                    ? 12                   : 0)
             + ((devInf.isSupportLargeObjs())       ? 38                   : 0)
             + ((devInf.isSupportNumberOfChanges()) ? 50                   : 0)
             + size;
    }

    // --------------------------------------------------------------- DataStore
    /**
     * Returns the XML size of DataStore element as:
     *    sizeof(<DataStore>\n)                               +
     *    if sourceRef != null
     *        sizeof(sourceRef)                               +
     *    if displayName != null && displayName.length() != 0
     *        sizeof(<DisplayName>)                           +
     *        sizeof(displayName)                             +
     *        sizeof(</DisplayName>\n)                        +
     *    if maxGUIDSize > 0
     *        sizeof(<MaxGUIDSize>)                           +
     *        sizeof(maxGUIDSize)                             +
     *        sizeof(</MaxGUIDSize>\n)                        +
     *    if rxPref != null
     *        sizeof(<Rx-Pref>\n)                             +
     *        sizeof(rxPref)                                  +
     *        sizeof(</Rx-Pref>\n)                            +
     *    for (int i=0; rxs != null && i<rxs.size(); i++)
     *      sizeof(<Rx>\n)                                    +
     *      sizeof(rxs[i])                                    +
     *      sizeof(</Rx>\n)                                   +
     *    if txPref != null
     *        sizeof(<Tx-Pref>\n)                             +
     *        sizeof(txPref)                                  +
     *        sizeof(</Tx-Pref>\n)                            +
     *    for (int i=0; txs != null && i<txs.size(); i++)
     *      sizeof(<Tx>\n)                                    +
     *      sizeof(txs[i])                                    +
     *      sizeof(</Tx>\n)                                   +
     *    for (int i=0; ctCaps != null && i<ctCaps.size(); i++)
     *        sizeof(ctCaps[i])                               +
     *    if dsMem != null
     *      sizeof(dsMem)                                     +
     *    if supportHierarchicalSync
     *        sizeof(<SupportHierarchicalSync></SupportHierarchicalSync>\n) +
     *    if syncCap != null
     *        sizeof(syncCap)                                 +
     *    for (int i=0; filterRxs != null && i<filterRxs.size(); i++)
     *      sizeof(<Filter-Rx>\n)                             +
     *      sizeof(filterRxs[i])                              +
     *      sizeof(</Filter-Rx>\n)                            +
     *    for (int i=0; filterCaps != null && i<filterCaps.size(); i++)
     *      sizeof(<FilterCap>\n)                             +
     *      sizeof(filterCaps[i])                             +
     *      sizeof(</FilterCap>\n)                            +
     *    sizeof(</DataStore>\n)
     *
     * @return the XML size of DataStore element
     */
    public long getSize(DataStore dataStore) {
        SourceRef       sourceRef   = dataStore.getSourceRef()  ;
        String          displayName = dataStore.getDisplayName();
        long            maxGUIDSize = dataStore.getMaxGUIDSize();
        CTInfo rxPref      = dataStore.getRxPref()     ;
        List            rxs         = dataStore.getRxs()        ;
        CTInfo txPref      = dataStore.getTxPref()     ;
        List            txs         = dataStore.getTxs()        ;
        List            ctCaps      = dataStore.getCTCaps()     ;
        DSMem           dsMem       = dataStore.getDSMem()      ;
        SyncCap         syncCap     = dataStore.getSyncCap()    ;
        List            filterRxs   = dataStore.getFilterRxs()  ;
        List            filterCaps  = dataStore.getFilterCaps() ;

        long size = 0;

        for (int i=0; rxs != null && i<rxs.size(); i++) {
            size += 11 + getSize((CTInfo)rxs.get(i));
        }
        for (int i=0; txs != null && i<txs.size(); i++) {
            size += 11 + getSize((CTInfo)txs.get(i));
        }
        for (int i=0; ctCaps != null && i<ctCaps.size(); i++) {
            size += getSize((CTCap)ctCaps.get(i));
        }
        for (int i=0; filterRxs != null && i<filterRxs.size(); i++) {
            size += 24 + getSize((CTInfo)filterRxs.get(i));
        }
        for (int i=0; filterCaps != null && i<filterCaps.size(); i++) {
            size += getSize((FilterCap)filterCaps.get(i));
        }

        return 25
             + ((sourceRef   != null) ? getSize(sourceRef)            : 0)
             + ((displayName != null && displayName.length() != 0)
                             ? 28 + displayName.length()              : 0)
             + ((maxGUIDSize >= 0)
                             ? 28 + String.valueOf(maxGUIDSize).length() : 0)
             + ((rxPref      != null) ? 21 + getSize(rxPref)          : 0)
             + ((txPref      != null) ? 21 + getSize(txPref)          : 0)
             + ((dsMem       != null) ? getSize(dsMem)                : 0)
             + ((dataStore.isSupportHierarchicalSync()) ? 52          : 0)
             + ((syncCap     != null) ? getSize(syncCap)              : 0)
             + size
             ;
    }

    // ------------------------------------------------------------------ CTInfo
    /**
     * Returns the XML size of CTInfo element as:
     *    if ctType != null
     *        sizeof(<CTType>)    +
     *        sizeof(ctType)      +
     *        sizeof(</CTType>\n) +
     *    if verCT != null
     *        sizeof(<VerCT>)     +
     *        sizeof(verCT)       +
     *        sizeof(</VerCT>\n)  +
     *
     * @return the XML size of CTInfo element
     */
    public long getSize(CTInfo cTInfo) {
        String ctType = cTInfo.getCTType();
        String verCT  = cTInfo.getVerCT() ;

        return ((ctType != null) ? 18 + ctType.length() : 0)
             + ((verCT  != null) ? 16 + verCT.length()  : 0)
             ;
    }

    // --------------------------------------------------------------- FilterCap
    /**
     * Returns the XML size of FilterCap element as:
     *    sizeof(<FilterCap>\n) +
     *    if ctType != null
     *        sizeof(<CTType>)    +
     *        sizeof(ctType)      +
     *        sizeof(</CTType>\n) +
     *    if verCT != null
     *        sizeof(<VerCT>)     +
     *        sizeof(verCT)       +
     *        sizeof(</VerCT>\n)  +
     *    if filterKeyword != null && filterKeyword.size() != 0
     *        sizeof(<FilterKeyword>)    +
     *        sizeof(filterKeyword)      +
     *        sizeof(</FilterKeyword>\n) +
     *    for (int i=0; propNames != null && i<propNames.size(); i++)
     *      sizeof(<PropName>)                 +
     *      sizeof(propNames[i])                 +
     *      sizeof(</PropName>\n)                +
     *    sizeof(</FilterCap>\n) +
     *
     * @return the XML size of FilterCap element
     */
    public long getSize(FilterCap filterCap) {
        CTInfo cti     = filterCap.getCTInfo();
        ArrayList filterKeywords = filterCap.getFilterKeywords();
        ArrayList propNames      = filterCap.getPropNames();

        long size = 25;
        for (int i=0; filterKeywords != null && i<filterKeywords.size(); i++) {
            size += 32 + ((String)filterKeywords.get(i)).length();
        }
        for (int i=0; propNames != null && i<propNames.size(); i++) {
            size += 22 + ((String)propNames.get(i)).length();
        }

        return ((cti != null) ? getSize(cti)          : 0)
        + size;

    }

    // --------------------------------------------------------------- SourceRef
    /**
     * Returns the XML size of SourceRef element as:
     *    sizeof(<SourceRef>)    +
     *    if value != null
     *        sizeof(value)      +
     *    if source != null
     *        sizeof(source)     +
     *    sizeof(</SourceRef>\n)
     *
     * @return the XML size of SourceRef element
     */
    public long getSize(SourceRef sourceRef) {
        String value  = sourceRef.getValue() ;
        Source source = sourceRef.getSource();

        return 24
             + ((value  != null) ? value.length()     : 0)
             + ((source != null) ? getSize(source) : 0)
             ;
    }

    // ------------------------------------------------------------------- DSMem
    /**
     * Returns the XML size of DSMem element as:
     *    sizeof(<DSMem>\n)        +
     *    if sharedMem
     *      sizeof(<SharedMem></SharedMem>\n) +
     *    if maxMem >= 0
     *      sizeof(<MaxMem>)       +
     *      sizeof(maxMem)  +
     *      sizeof(</<MaxMem>\n)   +
     *    if maxID >= 0
     *      sizeof(<MaxID>)        +
     *      sizeof(maxID)          +
     *      sizeof(</MaxID>\n)    +
     *    sizeof(</DSMem>\n)
     *
     * @return the XML size of DSMem element
     */
    public long getSize(DSMem dsMem) {
        long maxMem = dsMem.getMaxMem();
        long maxID  = dsMem.getMaxID() ;

        return 17
             + ((dsMem.isSharedMem()) ? 24                                   :0)
             + ((maxMem>=0)           ? 18 + String.valueOf(maxMem).length() :0)
             + ((maxID >=0)           ? 16 + String.valueOf(maxID).length()  :0)
             ;
    }

    // ----------------------------------------------------------------- SyncCap
    /**
     * Returns the XML size of SyncCap element as:
     *    sizeof(<SyncCap>\n)                                       +
     *    for (i=0; syncTypes != null && i < syncTypes.size(); i++)
     *        sizeof(syncTypes[i])                                  +
     *    sizeof(</SyncCap>\n)
     *
     * @return the XML size of SyncCap element
     */
    public long getSize(SyncCap syncCap) {
        ArrayList syncTypes = syncCap.getSyncType();
        long size = 21;

        for (int i=0; i<syncTypes.size(); ++i) {
            size += getSize((SyncType)syncTypes.get(i));
        }
        return size;
    }

    // ---------------------------------------------------------------- SyncType
    /**
     * Returns the XML size of SyncType element as:
     *    sizeof(<SyncType>)    +
     *    sizeof(syncType)      +
     *    sizeof(</SyncType>\n)
     *
     * @return the XML size of SyncType element
     */
    public long getSize(SyncType syncType) {
        return 22
             + String.valueOf(syncType.getType()).length()
             ;
    }
    // ----------------------------------------------------------------- CTCapV1
    /**
     * Returns the XML size of CTCap element for DevInf v1.1.x as:
     *  sizeof(<CTCap>\n)                  +
     *  for (int i=0; ctTypes != null && i<ctTypes.size(); i++)
     *      sizeof(ctTypes.get(i)) +
     *    sizeof(</CTCap>\n)
     *
     * @return the XML size of CTCap element
     */
    public long getSize(CTCapV1 ctCapV1) {
        ArrayList ctTypeSup = ctCapV1.getCTTypes();
        long size = 17;

        for (int i=0; ctTypeSup != null && i<ctTypeSup.size(); i++) {
            size += getSize((CTType)ctTypeSup.get(i));
        }
        return size;
    }

    // ------------------------------------------------------------------- CTCap
    /**
     * Returns the XML size of CTCap element for DevInf v1.2 as:
     *  sizeof(<CTCap>\n)         +
     *    if ctType != null
     *        sizeof(<CTType>)    +
     *        sizeof(ctType)      +
     *        sizeof(</CTType>\n) +
     *    if verCT != null
     *        sizeof(<VerCT>)     +
     *        sizeof(verCT)       +
     *        sizeof(</VerCT>\n)  +
     *    if fieldLevel
     *        sizeof(<FieldLevel></FieldLevel>\n) +
     *    for (int i=0; properties != null && i<properties.size(); i++)
     *      sizeof(<Property>\n)                  +
     *      sizeof(properties[i])                 +
     *      sizeof(</Property>\n)                 +
     *    sizeof(</CTCap>\n)
     *
     * @return the XML size of CTCap element
     */
    public long getSize(CTCap ctCap) {
        CTInfo cti  = ctCap.getCTInfo();
        ArrayList properties = ctCap.getProperties();

        long size = 17;

        for (int i=0; properties != null && i<properties.size(); i++) {
            size += getSize((Property)properties.get(i));
        }
        return ((cti != null) ? getSize(cti)  : 0)
               + ((ctCap.isFieldLevel()) ? 26 : 0)
               + size;
    }

    // ---------------------------------------------------------------- Property
    /**
     * Returns the XML size of Property element as:
     *    sizeof(<Property>\n)                    +
     *    sizeof(<PropName>)                      +
     *    sizeof(propName)                        +
     *    sizeof(</PropName>\n)                   +
     *    if dataType != null
     *        sizeof(<DataType>)                  +
     *        sizeof(dataType)                    +
     *        sizeof(</DataType>\n)               +
     *    if maxOccur != 0
     *        sizeof(<MaxOccur>)                  +
     *        sizeof(maxOccur)                    +
     *        sizeof(</MaxOccur>\n)               +
     *    if maxSize != 0
     *        sizeof(<MaxSize>)                   +
     *        sizeof(maxSize)                     +
     *        sizeof(</MaxSize>\n)                +
     *    if noTruncate
     *        sizeof(<NoTruncate></NoTruncate>\n) +
     *    for (int i=0; valEnums != null && i<valEnums.size(); i++)
     *        sizeof(<ValEnum>)                   +
     *        sizeof(valEnum.get(i))              +
     *        sizeof(</ValEnum>\n)                +
     *    if displayName != null
     *        sizeof(<DisplayName>)               +
     *        sizeof(displayName)                 +
     *        sizeof(</DisplayName>\n)            +
     *    for (int i=0; propParams != null && i<propParams.size(); i++)
     *        sizeof(<PropParam>)                 +
     *        sizeof(propParams.get(i))           +
     *        sizeof(</PropParam>\n)              +
     *    sizeof(</Property>\n)                   +
     * @return the XML size of Property element
     */
    public long getSize(Property property) {
        String    propName    = property.getPropName()   ;
        String    dataType    = property.getDataType()   ;
        int       maxOccur    = property.getMaxOccur()   ;
        int       maxSize     = property.getMaxSize()    ;
        ArrayList valEnums    = property.getValEnums()   ;
        String    displayName = property.getDisplayName();
        ArrayList propParams  = property.getPropParams() ;

        long size = 23;
        for (int i=0; valEnums != null && i<valEnums.size(); i++) {
            size += 20 + ((String)valEnums.get(i)).length();
        }
        for (int i=0; propParams != null && i<propParams.size(); i++) {
            size += getSize((PropParam)propParams.get(i));
        }

        return ((propName != null) ? 22 + propName.length()                 : 0)
             + ((dataType != null) ? 22 + dataType.length()                 : 0)
             + ((maxOccur != 0   ) ? 22 + String.valueOf(maxOccur).length() : 0)
             + ((maxSize  != 0   ) ? 20 + String.valueOf(maxSize).length()  : 0)
             + ((property.isNoTruncate()) ? 26                              : 0)
             + ((displayName != null)     ? 28 + displayName.length()       : 0)
             + size
             ;
    }

    // --------------------------------------------------------------- PropParam
    /**
     * Returns the XML size of PropParam element as:
     *    sizeof(<PropParam>\n)                     +
     *    sizeof(<ParamName>)                     +
     *    sizeof(paramName)                       +
     *    sizeof(</ParamName>\n)                  +
     *    if dataType != null
     *        sizeof(<DataType>)                 +
     *        sizeof(dataType)                   +
     *        sizeof(</DataType>\n)              +
     *    for (int i=0; valEnums != null && i<valEnums.size(); i++)
     *        sizeof(<ValEnum>)                  +
     *        sizeof(valEnums.get(i))            +
     *        sizeof(</ValEnum>\n)               +
     *    if displayName != null
     *        sizeof(<DisplayName>)              +
     *        sizeof(displayName)                +
     *        sizeof(</DisplayName>\n)           +
     *    sizeof(</PropParam>\n)                     +
     * @return the XML size of PropParam element
     */
    public long getSize(PropParam propParam) {
        String    paramName   = propParam.getParamName()  ;
        String    dataType    = propParam.getDataType()   ;
        ArrayList valEnums    = propParam.getValEnums()   ;
        String    displayName = propParam.getDisplayName();

        long size = 25;
        for (int i=0; valEnums != null && i<valEnums.size(); i++) {
            size += 20 + ((String)valEnums.get(i)).length();
        }

        return ((paramName   != null) ? 24 + paramName.length()   : 0)
             + ((dataType    != null) ? 22 + dataType.length()    : 0)
             + ((displayName != null) ? 28 + displayName.length() : 0)
             + size
             ;
    }

    // --------------------------------------------------------- CTTypeSupported
    /**
     * Returns the XML size of CTTypeSupported element as:
     *    if ctType != null
     *        sizeof(ctType)           +
     *  for (int i=0; ctPropParams != null && i<ctPropParams.size(); i++)
     *      sizeof(ctPropParam.get(i))
     *
     * @return the XML size of CTTypeSupported element
     */
    public long getSize(CTType ctTypeSupported) {
        String    ctType       = ctTypeSupported.getCTType()      ;
        ArrayList ctProperties = ctTypeSupported.getCTProperties();
        long size = 0;

        for (int i=0; ctProperties != null && i<ctProperties.size(); i++) {
            size += getSize((CTProperty)ctProperties.get(i));
        }
        return ((ctType != null) ? 18 + ctType.length() : 0)
             + size
             ;
    }


    // ------------------------------------------------------------- CTPropParam
    /**
     * Returns the XML size of CTPropParam element as:
     *    sizeof(<PropName>)                     +
     *    sizeof(propName)                       +
     *    sizeof(</PropName>\n)                  +
     *    for (int i=0; valEnums != null && i<valEnums.size(); i++)
     *        sizeof(<ValEnum>)                  +
     *        sizeof(valEnums.get(i))            +
     *        sizeof(</ValEnum>\n)               +
     *    if dataType != null
     *        sizeof(<DataType>)                 +
     *        sizeof(dataType)                   +
     *        sizeof(</DataType>\n)              +
     *    if size != 0
     *        sizeof(<Size>)                     +
     *        sizeof(size)                       +
     *        sizeof(</Size>\n)                  +
     *    if displayName != null
     *        sizeof(<DisplayName>)              +
     *        sizeof(displayName)                +
     *        sizeof(</DisplayName>\n)           +
     *    for (int i=0; ctParameters != null && i<ctParameters.size(); i++)
     *        sizeof(ctParameters.get(i))
     * @return the XML size of CTPropParam element
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
            sizeCTPP += 20 + ((String)valEnums.get(i)).length();
        }

        for (int i=0; ctParameters!=null && i<ctParameters.size(); i++) {
            sizeCTPP += getSize((CTParameter)ctParameters.get(i));
        }

        return ((propName    != null) ? 22 + propName.length()             : 0)
             + ((dataType    != null) ? 22 + dataType.length()             : 0)
             + ((size        != null) ? 14 + String.valueOf(size.longValue()).length() : 0)
             + ((displayName != null) ? 28 + displayName.length()          : 0)
             + sizeCTPP
             ;
    }

    // ---------------------------------------------------- ContentTypeParameter
    /**
     * Returns the XML size of ContentTypeParameter element as:
     *    sizeof(<ParamName>)                    +
     *    sizeof(paramName)                      +
     *    sizeof(</ParamName>\n)                 +
     *    for (int i=0; valEnums != null && i<valEnums.size(); i++)
     *        sizeof(<ValEnum>)                  +
     *        sizeof(valEnum.get(i))             +
     *        sizeof(</ValEnum>\n)               +
     *    if dataType != null
     *        sizeof(<DataType>)                 +
     *        sizeof(dataType)                   +
     *        sizeof(</DataType>\n)              +
     *    if size != 0
     *        sizeof(<Size>)                     +
     *        sizeof(size)                       +
     *        sizeof(</Size>\n)                  +
     *    if displayName != null
     *        sizeof(<DisplayName>)              +
     *        sizeof(displayName)                +
     *        sizeof(</DisplayName>\n)           +
     *
     * @return the XML size of CTPropParam element
     */
    public long getSize(CTParameter ctParameter) {
        String    paramName   = ctParameter.getName()       ;
        ArrayList valEnums    = ctParameter.getValEnum()    ;
        String    dataType    = ctParameter.getDataType()   ;
        Long      size        = ctParameter.getSize()       ;
        String    displayName = ctParameter.getDisplayName();
        long sizeCTP = 0;

        for (int i=0; valEnums != null && i<valEnums.size(); i++) {
            sizeCTP += 20 + ((String)valEnums.get(i)).length();
        }

        return ((paramName   != null) ? 24 + paramName.length()            : 0)
             + ((dataType    != null) ? 22 + dataType.length()             : 0)
             + ((size        != null) ? 14 + String.valueOf(size.longValue()).length() : 0)
             + ((displayName != null) ? 28 + displayName.length()          : 0)
             + sizeCTP
             ;
    }

    // --------------------------------------------------------------------- Ext
    /**
     * Returns the XML size of Ext element as:
     *    sizeof(<Ext>\n)  +
     *    if name != null
     *        sizeof(<XNam>)    +
     *        sizeof(name) +
     *        sizeof(</XNam>\n) +
     *    for (int i=0; values != null && i<values.size(); i++)
     *        sizeof(<XVal>)  +
     *        sizeof(values[i])  +
     *        sizeof(</XVal>\n)  +
     *    sizeof(</Ext>\n)
     *
     * @return the XML size of Ext element
     */
    public long getSize(Ext ext) {
        String    name   = ext.getXNam();
        ArrayList values = ext.getXVal();
        long size = 13;

        for (int i=0; values != null && i<values.size(); i++) {
            size += 14 + ((String)values.get(i)).length();
        }

        return ((name != null) ? 14 + name.length() : 0)
             + size
             ;
    }
    // ------------------------------------------------------------------- Alert
    /**
     * Returns the XML size of Alert element as:
     *    sizeof(<Alert>\n)               +
     *    if cmdID != null
     *        sizeof(cmdID)               +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n) +
     *    if cred != null
     *        sizeof(cred)                +
     *    if data != 0
     *        sizeof(<Data>)              +
     *        sizeof(data)                +
     *        sizeof(</Data>\n)           +
     *    if correlator != null
     *        sizeof(<Correlator>)        +
     *        sizeof(correlator)          +
     *        sizeof(</Correlator>\n)     +
     *    for (int i=0; items != null && i<items.size(); i++)
     *        sizeof(items[i])            +
     *    sizeof(</Alert>\n)
     *
     * @return the XML size of Alert element
     */
    public long getSize(Alert alert) {
        int       data       = alert.getData()      ;
        String    correlator = alert.getCorrelator();
        ArrayList items      = alert.getItems()     ;

        long size = 17
                  + getSize((AbstractCommand)alert)
                  + ((data != 0) ? 14 + String.valueOf(data).length() : 0)
                  + ((correlator != null) ? 26 + correlator.length()  : 0)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // ------------------------------------------------------------------ Atomic
    /**
     * Returns the XML size of Atomic element as:
     *    sizeof(<Atomic>\n)                                        +
     *    if cmdID != null
     *        sizeof(cmdID)                                         +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                           +
     *    if cred != null
     *        sizeof(cred)                                          +
     *    if meta != null
     *        sizeof(meta)                                          +
     *    for (int i=0; commands != null && i<commands.size(); i++)
     *        sizeof(commands[i])                                   +
     *    sizeof(</Atomic>\n)
     *
     * @return the XML size of Atomic element
     */
    public long getSize(Atomic atomic) {
        Meta      meta     = atomic.getMeta()    ;
        ArrayList commands = atomic.getCommands();

        long size = 19
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
     * Returns the XML size of Copy element as:
     *    sizeof(<Copy>\n)                                    +
     *    if cmdID != null
     *        sizeof(cmdID)                                   +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                     +
     *    if cred != null
     *        sizeof(cred)                                    +
     *    if meta != null
     *        sizeof(meta)                                    +
     *    for (int i=0; items != null && i<items.size(); i++)
     *        sizeof(items[i])                                +
     *    sizeof(</Copy>\n)
     *
     * @return the XML size of Copy element
     */
    public long getSize(Copy copy) {
        Meta      meta  = copy.getMeta() ;
        ArrayList items = copy.getItems();

        long size = 13
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
     * Returns the XML size of Delete element as:
     *    sizeof(<Delete>\n)                                  +
     *    if cmdID != null
     *        sizeof(cmdID)                                   +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                     +
     *    if cred != null
     *        sizeof(cred)                                    +
     *    if archive
     *        sizeof(<Archive></Archive>\n)                   +
     *    if sftDel
     *        sizeof(<SftDel></SftDel>\n)                     +
     *    if meta != null
     *        sizeof(meta)                                    +
     *    for (int i=0; items != null && i<items.size(); i++)
     *        sizeof(items[i])                                +
     *    sizeof(</Delete>\n)
     *
     * @return the XML size of Delete element
     */
    public long getSize(Delete delete) {
        Meta      meta  = delete.getMeta() ;
        ArrayList items = delete.getItems();

        long size = 20
                  + getSize((AbstractCommand)delete)
                  + ((delete.isArchive()) ? 20               : 0)
                  + ((delete.isSftDel() ) ? 18               : 0)
                  + ((meta != null      ) ? getSize(meta) : 0)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // -------------------------------------------------------------------- Exec
    /**
     * Returns the XML size of Exec element as:
     *    sizeof(<Exec>\n)                +
     *    if cmdID != null
     *        sizeof(cmdID)               +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n) +
     *    if cred != null
     *        sizeof(cred)                +
     *    if correlator != null
     *        sizeof(<Correlator>)        +
     *        sizeof(correlator)          +
     *        sizeof(</Correlator>\n)     +
     *    for (int i=0; items != null && i<items.size(); i++)
     *        sizeof(items[i])            +
     *    sizeof(</Exec>\n)
     *
     * @return the XML size of Delete element
     */
    public long getSize(Exec exec) {
        String    correlator = exec.getCorrelator();
        ArrayList items      = exec.getItems()     ;

        long size = 14
                  + getSize((AbstractCommand)exec)
                  + ((correlator != null) ? 26 + correlator.length() : 0)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // --------------------------------------------------------------------- Get
    /**
     * Returns the XML size of Get element as:
     *    sizeof(<Get>\n)                                     +
     *    if cmdID != null
     *        sizeof(cmdID)                                   +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                     +
     *    if cred != null
     *        sizeof(cred)                                    +
     *    if lang != null
     *        sizeof(<Lang>)                                  +
     *        sizeof(lang)                                    +
     *        sizeof(</Lang>\n)                               +
     *    if meta != null
     *        sizeof(meta)                                    +
     *    for (int i=0; items != null && i<items.size(); i++)
     *        sizeof(items[i])                                +
     *    sizeof(</Get>\n)
     *
     * @return the XML size of Get element
     */
    public long getSize(Get get) {
        String    lang  = get.getLang();
        Meta      meta  = get.getMeta() ;
        ArrayList items = get.getItems();

        long size = 13
                  + getSize((AbstractCommand)get)
                  + ((lang != null) ? 14 + lang.length() : 0)
                  + ((meta != null) ? getSize(meta)   : 0)
                  ;
        for (int i=0; items != null && i<items.size(); i++) {
            size += getSize((Item)items.get(i));
        }
        return size;
    }

    // --------------------------------------------------------------------- Map
    /**
     * Returns the XML size of Map element as:
     *    sizeof(<Map>\n)                                     +
     *    if cmdID != null
     *        sizeof(cmdID)                                   +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                     +
     *    if cred != null
     *        sizeof(cred)                                    +
     *    if target != null
     *        sizeof(target)                                  +
     *    if source != null
     *        sizeof(source)                                  +
     *    if meta != null
     *        sizeof(meta)                                    +
     *    for (int i=0; mapItems != null && i<mapItems.size(); i++)
     *        sizeof(mapItems[i])                             +
     *    sizeof(</Map>\n)
     *
     * @return the XML size of Map element
     */
    public long getSize(Map map) {
        Target    target   = map.getTarget();
        Source    source   = map.getSource();
        Meta      meta     = map.getMeta() ;
        ArrayList mapItems = map.getMapItems();

        long size = 13
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
     * Returns the XML size of MapItem element as:
     *    sizeof(<MapItem>\n)  +
     *    if target != null
     *        sizeof(target)   +
     *    if source != null
     *        sizeof(source)   +
     *    sizeof(</MapItem>\n)
     *
     * @return the XML size of MapItem element
     */
    public long getSize(MapItem mapItem) {
        Target    target   = mapItem.getTarget();
        Source    source   = mapItem.getSource();

        return 21
             + ((target != null) ? getSize(target) : 0)
             + ((source != null) ? getSize(source) : 0)
             ;
    }

    // -------------------------------------------------------------------- Move
    /**
     * Returns the XML size of Move element as:
     *    sizeof(<Move>\n)                                     +
     *    if cmdID != null
     *        sizeof(cmdID)                                   +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                     +
     *    if cred != null
     *        sizeof(cred)                                    +
     *    if meta != null
     *      sizeof(meta)                                      +
     *    for (int i=0; items != null && i<items.size(); i++)
     *      sizeof(items[i])                                  +
     *    sizeof(</Move>\n)
     *
     * @return the XML size of Move element
     */
    public long getSize(Move move) {
        Meta meta       = move.getMeta() ;
        ArrayList items = move.getItems();

        long size = 7
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
     * Returns the XML size of Put element as:
     *    sizeof(<Put>\n)                                     +
     *    if cmdID != null
     *        sizeof(cmdID)                                   +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                     +
     *    if cred != null
     *        sizeof(cred)                                    +
     *    if lang != null
     *        sizeof(<Lang>)                                  +
     *        sizeof(lang)                                    +
     *        sizeof(</Lang>\n)                               +
     *    if meta != null
     *        sizeof(meta)                                    +
     *    for (int i=0; devInfItems != null && i<devInfItems.size(); i++)
     *        sizeof(devInfItems[i])                          +
     *    sizeof(</Put>\n)
     *
     * @return the XML size of Put element
     */
    public long getSize(Put put) {
        String    lang        = put.getLang();
        Meta      meta        = put.getMeta() ;
        ArrayList devInfItems = put.getItems();

        long size = 14
                  + getSize((AbstractCommand)put)
                  + ((lang != null) ? 14 + lang.length() : 0)
                  + ((meta != null) ? getSize(meta)   : 0)
                  ;
        for (int i=0; devInfItems != null && i<devInfItems.size(); i++) {
            size += getSize((DevInfItem)devInfItems.get(i));
        }
        return size;
    }

    // -------------------------------------------------------------- DevInfItem
     /**
     * Returns the XML size of Item element as:
     *    sizeof(<Item>\n)                    +
     *    if target != null
     *        sizeof(target)                  +
     *    if source != null
     *        sizeof(source)                  +
     *    if meta != null
     *        sizeof(meta)                    +
     *    if data != null
     *        sizeof(data)                    +
     *    sizeof(</Item>\n)
     *
     * @return the XML size of Item element
     */
    public long getSize(DevInfItem devInfItem) {
        Target     target     = devInfItem.getTarget()    ;
        Source     source     = devInfItem.getSource()    ;
        Meta       meta       = devInfItem.getMeta()      ;
        DevInfData devInfData = devInfItem.getDevInfData();

        return 15
             + ((target     != null) ? getSize(target)     : 0)
             + ((source     != null) ? getSize(source)     : 0)
             + ((meta       != null) ? getSize(meta)       : 0)
             + ((devInfData != null) ? getSize(devInfData) : 0)
             ;
    }

    // ----------------------------------------------------------------- Replace
    /**
     * Returns the XML size of Replace element as:
     *    sizeof(<Replace>\n)                                 +
     *    if cmdID != null
     *        sizeof(cmdID)                                   +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                     +
     *    if cred != null
     *        sizeof(cred)                                    +
     *    if meta != null
     *        sizeof(meta)                                    +
     *    for (int i=0; items != null && i<items.size(); i++)
     *        sizeof(items[i])                                +
     *    sizeof(</Replace>\n)
     *
     * @return the XML size of Replace element
     */
    public long getSize(Replace replace) {
        Meta      meta  = replace.getMeta() ;
        ArrayList items = replace.getItems();

        long size = 21
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
     * Returns the XML size of Results element as:
     *    sizeof(<Results>\n)                                 +
     *    if cmdID != null
     *        sizeof(cmdID)                                   +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                     +
     *    if cred != null
     *        sizeof(cred)                                    +
     *    if msgRef != null
     *        sizeof(<MsgRef>)                                +
     *        sizeof(msgRef)                                  +
     *        sizeof(</MsgRef>\n)                             +
     *    if cmdRef != null
     *        sizeof(<CmdRef>)                                +
     *        sizeof(cmdRef)                                  +
     *        sizeof(</CmdRef>\n)                             +
     *    if meta != null
     *        sizeof(meta)                                    +
     *    for (int i=0; targetRefs != null && i<targetRefs.size(); i++)
     *        sizeof(targetRefs[i])                           +
     *    for (int i=0; sourceRefs != null && i<sourceRefs.size(); i++)
     *        sizeof(sourceRefs[i])                           +
     *    for (int i=0; items != null && i<items.size(); i++)
     *        sizeof(items[i])                                +
     *    sizeof(</Results>\n)
     *
     * @return the XML size of Results element
     */
    public long getSize(Results results) {
        String    msgRef     = results.getMsgRef()   ;
        String    cmdRef     = results.getCmdRef()   ;
        Meta      meta       = results.getMeta()     ;
        ArrayList targetRefs = results.getTargetRef();
        ArrayList sourceRefs = results.getSourceRef();
        ArrayList items      = results.getItems()    ;

        long size = 21
                  + getSize((AbstractCommand)results)
                  + ((msgRef != null) ? 18 + msgRef.length() : 0)
                  + ((cmdRef != null) ? 18 + cmdRef.length() : 0)
                  + ((meta   != null) ? getSize(meta)     : 0)
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
     * Returns the XML size of TargetRef element as:
     *    sizeof(<TargetRef>)    +
     *    if value != null
     *        sizeof(value)      +
     *    if target != null
     *        sizeof(targe)     +
     *    sizeof(</TargetRef>\n)
     *
     * @return the XML size of TargetRef element
     */
    public long getSize(TargetRef targetRef) {
        String value  = targetRef.getValue() ;
        Target target = targetRef.getTarget();

        return 24
             + ((value  != null) ? value.length()     : 0)
             + ((target != null) ? getSize(target) : 0)
             ;
    }

    // ------------------------------------------------------------------ Search
    /**
     * Returns the XML size of Search element as:
     *    sizeof(<Search>\n)                                  +
     *    if cmdID != null
     *        sizeof(cmdID)                                   +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                     +
     *    if cred != null
     *        sizeof(cred)                                    +
     *    if noResults
     *        sizeof(<NoResults></NoResults>\n)               +
     *    if target != null
     *        sizeof(target)                                  +
     *    for (int i=0; sources != null && i<sources.size(); i++)
     *        sizeof(sources[i])                              +
     *    if lang != null
     *        sizeof(<Lang>)                                  +
     *        sizeof(lang)                                    +
     *        sizeof(</Lang)\n)                               +
     *    if meta != null
     *        sizeof(meta)                                    +
     *    if data != null
     *        sizeof(data)                                    +
     *    sizeof(</Search>\n)
     *
     * @return the XML size of Search element
     */
    public long getSize(Search search) {
        Target    target  = search.getTarget() ;
        ArrayList sources = search.getSources();
        String    lang    = search.getLang()   ;
        Meta      meta    = search.getMeta()   ;
        Data      data    = search.getData()   ;

        long size =
                  + getSize((AbstractCommand)search)
                  + ((search.isNoResults()) ? 24                 : 0)
                  + ((target != null)       ? getSize(target) : 0)
                  + ((lang   != null)       ? 14 + lang.length() : 0)
                  + ((meta   != null)       ? getSize(meta)   : 0)
                  + ((data   != null)       ? getSize(data)   : 0)
                  ;
        for (int i=0; sources != null && i<sources.size(); i++) {
            size += getSize((Source)sources.get(i));
        }
        return size;
    }

    // ---------------------------------------------------------------- Sequence
    /**
     * Returns the XML size of Sequence element as:
     *    sizeof(<Sequence>\n)                                        +
     *    if cmdID != null
     *        sizeof(cmdID)                                         +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                           +
     *    if cred != null
     *        sizeof(cred)                                          +
     *    if meta != null
     *        sizeof(meta)                                          +
     *    for (int i=0; commands != null && i<commands.size(); i++)
     *        sizeof(commands[i])                                   +
     *    sizeof(</Sequence>\n)
     *
     * @return the XML size of Sequence element
     */
    public long getSize(Sequence sequence) {
        Meta      meta     = sequence.getMeta()    ;
        ArrayList commands = sequence.getCommands();

        long size = 23
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
     * Returns the XML size of Status element as:
     *    sizeof(<Status>\n)                                  +
     *    if cmdID != null
     *        sizeof(cmdID)                                   +
     *    if msgRef != null
     *        sizeof(<MsgRef>)                                +
     *        sizeof(msgRef)                                  +
     *        sizeof(</MsgRef>\n)                             +
     *    if cmdRef != null
     *        sizeof(<CmdRef>)                                +
     *        sizeof(cmdRef)                                  +
     *        sizeof(</CmdRef>\n)                             +
     *    if cmd != null
     *        sizeof(<Cmd)                                    +
     *        sizeof(cmd)                                     +
     *        sizeof(</Cmd>\n)                                +
     *    for (int i=0; targetRefs != null && i<targetRefs.size(); i++)
     *        sizeof(targetRefs[i])                           +
     *    for (int i=0; sourceRefs != null && i<sourceRefs.size(); i++)
     *        sizeof(sourceRefs[i])                           +
     *    if cred != null
     *        sizeof(cred)                                    +
     *    if chal != null
     *        sizeof(chal)                                    +
     *    if data != null
     *        sizeof(data)                                    +
     *    for (int i=0; items != null && i<items.size(); i++)
     *        sizeof(items[i])                                +
     *    sizeof(</Status>\n)
     *
     * @return the XML size of Status element
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

        long size = 19
                  + ((cmdID  != null) ? getSize(cmdID)    : 0)
                  + ((msgRef != null) ? 18 + msgRef.length() : 0)
                  + ((cmdRef != null) ? 18 + cmdRef.length() : 0)
                  + ((cmd    != null) ? 12 + cmd.length()    : 0)
                  + ((cred   != null) ? getSize(cred)     : 0)
                  + ((chal   != null) ? getSize(chal)     : 0)
                  + ((data   != null) ? getSize(data)     : 0)
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
     * Returns the XML size of Chal element as:
     *    sizeof(<Chal>\n) +
     *    if meta != null
     *        sizeof(meta) +
     *    sizeof</Chal>\n)
     *
     * @return the XML size of Chal element
     */
    public long getSize(Chal chal) {
        Meta meta = chal.getMeta();

        return 15
             + ((meta != null) ? getSize(meta) : 0)
             ;
    }

    // -------------------------------------------------------------------- Sync
    /**
     * Returns the XML size of Sync element as:
     *    sizeof(<Sync>\n)                                          +
     *    if cmdID != null
     *        sizeof(cmdID)                                         +
     *    if noResp
     *        sizeof(<NoResp></NoResp>\n)                           +
     *    if cred != null
     *        sizeof(cred)                                          +
     *    if target != null
     *        sizeof(target)                                        +
     *    if source != null
     *        sizeof(source)                                        +
     *    if meta != null
     *        sizeof(meta)                                          +
     *    if numberOfChanges != 0
     *        sizeof(<NumberOfChanges>)                             +
     *        sizeof(numberOfChanges)                               +
     *        sizeof(</NumberOfChanges>\n)                          +
     *    for (int i=0; commands != null && i<commands.size(); i++)
     *        sizeof(commands[i])                                   +
     *    sizeof(</Sync>\n)
     *
     * @return the XML size of Sync element
     */
    public long getSize(Sync sync) {
        Target    target          = sync.getTarget()         ;
        Source    source          = sync.getSource()         ;
        Meta      meta            = sync.getMeta()           ;
        Long      numberOfChanges = sync.getNumberOfChanges();
        ArrayList commands        = sync.getCommands()       ;

        long size = 15
                  + getSize((AbstractCommand)sync)
                  + ((target          != null) ? getSize(target) : 0)
                  + ((source          != null) ? getSize(source) : 0)
                  + ((meta            != null) ? getSize(meta)   : 0)
                  + ((numberOfChanges != null) ? 36              : 0)
                  ;
        for (int i=0; commands != null && i<commands.size(); i++) {
            size += getCommandSize((AbstractCommand)commands.get(i));
        }
        return size;
    }

    // --------------------------------------------------------- ItemizedCommand
    /**
     * Returns the XML size of the ItemizedCommand element
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
     * Returns the XML size of ModificationCommand object.
     *
     * @return the XML size of ModificationCommand object
     */
    public long getSize(ModificationCommand modCmd) {
        return getSize((ItemizedCommand)modCmd);
    }

    // --------------------------------------------------------- ResponseCommand
    /**
     * Returns the XML size of ResponseCommand object.
     *
     * @return the XML size of ResponseCommand object
     */
    public long getSize(ResponseCommand responseCmd) {
        String msgRef        = responseCmd.getMsgRef()   ;
        String cmdRef        = responseCmd.getCmdRef()   ;
        ArrayList targetRefs = responseCmd.getTargetRef();
        ArrayList sourceRefs = responseCmd.getSourceRef();
        long size = 0;

        size = getSize((ItemizedCommand)responseCmd)
             + ((msgRef != null) ? 18 + msgRef.length() : 0)
             + ((cmdRef != null) ? 18 + cmdRef.length() : 0)
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
