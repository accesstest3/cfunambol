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
 * This class represents the &lt;Item&gt; tag ias defined by the SyncML
 * representation specifications
 *
 * @version $Id: Item.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class Item implements java.io.Serializable {

    // ------------------------------------------------------------ Private data
    private Target       target      ;
    private Source       source      ;
    private SourceParent sourceParent;
    private TargetParent targetParent;
    private Meta         meta        ;
    private ComplexData  data        ;
    private Boolean      moreData    ;

    private boolean      incompleteInfo = false;

    // ------------------------------------------------------------ Constructors

    public Item() {}

    /**
     * Creates a new Item object.
     *
     * @param target the item target - NULL ALLOWED
     * @param source the item source - NULL ALLOWED
     * @param sourceParent the SourceParent - NULL ALLOWED
     * @param targetParent the TargetParent - NULL ALLOWED
     * @param meta   the item meta data - NULL ALLOWED
     * @param data   the item data - NULL ALLOWED
     * @param moreData need more data?
     */
    public Item(
                final Source       source      ,
                final Target       target      ,
                final SourceParent sourceParent,
                final TargetParent targetParent,
                final Meta         meta        ,
                final ComplexData  data        ,
                final boolean      moreData    ) {
        this.source       = source      ;
        this.target       = target      ;
        this.sourceParent = sourceParent;
        this.targetParent = targetParent;
        this.meta         = meta        ;
        this.data         = data        ;
        this.moreData     = (moreData) ? new Boolean(moreData) : null;
    }

    /**
     * Creates a new Item object.
     *
     * @param source item source - NULL ALLOWED
     * @param target item target - NULL ALLOWED
     * @param meta item meta data - NULL ALLOWED
     * @param data item data - NULL ALLOWED
     * @param moreData  need more data?
     *
     */
    public Item(final Source      source  ,
                final Target      target  ,
                final Meta        meta    ,
                final ComplexData data    ,
                final boolean     moreData) {
        this(source, target, null, null, meta, data, moreData);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the item target
     *
     * @return the item target
     */
    public Target getTarget() {
        return target;
    }

    /**
     * Sets the item target
     *
     * @param target the target
     *
     */
    public void setTarget(Target target) {
        this.target = target;
    }

    /**
     * Returns the item source
     *
     * @return the item source
     */
    public Source getSource() {
        return source;
    }

    /**
     * Sets the item source
     *
     * @param source the source
     *
     */
    public void setSource(Source source) {
        this.source = source;
    }

    /**
     * Returns the item source parent
     *
     * @return the item source parent
     */
    public SourceParent getSourceParent() {
        return sourceParent;
    }

    /**
     * Sets the parent information of the item
     *
     * @param sourceParent the parent information of the item
     *
     */
    public void setSourceParent(SourceParent sourceParent) {
        this.sourceParent = sourceParent;
    }

    /**
     * Returns the item target parent
     *
     * @return the item target parent
     */
    public TargetParent getTargetParent() {
        return targetParent;
    }

    /**
     * Sets the parent information of the item
     *
     * @param targetParent the parent information of the item
     *
     */
    public void setTargetParent(TargetParent targetParent) {
        this.targetParent = targetParent;
    }

    /**
     * Returns the item meta element
     *
     * @return the item meta element
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Sets the meta item
     *
     * @param meta the item meta element
     *
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * Returns the item data
     *
     * @return the item data
     *
     */
    public ComplexData getData() {
        return data;
    }
    
    /**
     * Returns the hidden data. This is used in the bindingHiddenData.xml in order
     * to avoid to show sensitive data.
     *
     * @return <i>*****</i>
     */
    public ComplexData getHiddenData() {
        return new ComplexData("*****");
    }

    /**
     * Sets the item data
     *
     * @param data the item data
     *
     */
    public void setData(ComplexData data) {
        this.data = data;
    }

    /**
     * Gets moreData property
     *
     * @return true if the data item is incomplete and has further chunks
     *         to come, false otherwise
     */
    public boolean isMoreData() {
        return (moreData != null);
    }

    /**
     * Gets the Boolean value of moreData
     *
     * @return true if the data item is incomplete and has further chunks
     *         to come, false otherwise
     */
    public Boolean getMoreData() {
        if (moreData == null || !moreData.booleanValue()) {
            return null;
        }
        return moreData;
    }

    /**
     * Sets the moreData property
     *
     * @param moreData the moreData property
     */
    public void setMoreData(Boolean moreData) {
        this.moreData = (moreData.booleanValue()) ? moreData : null;
    }

    /**
     * Returns incompleteInfo property
     * @return the property incompleteInfo
     */
    public boolean isWithIncompleteInfo() {
        return this.incompleteInfo;
    }

    /**
     * Sets the property incompleteInfo
     * @param incompleteInfo boolean
     */
    public void setIncompleteInfo(boolean incompleteInfo) {
        this.incompleteInfo = incompleteInfo;
    }

}
