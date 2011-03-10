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
package com.funambol.common.pim.note;


import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.common.XTag;
import com.funambol.framework.tools.merge.MergeResult;
import com.funambol.framework.tools.merge.MergeUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Represent a Note
 *
 * @version $Id: Note.java,v 1.5 2008-04-28 15:48:49 piter_may Exp $
 */
public class Note {
    
    //------------------------------------------------------------  Private data
    
    private Property   uid               = null  ; // String
    private Property   subject           = null  ; // String;        Sif
    private Property   textDescription   = null  ; // String; plain, Sif
    private Property   date              = null  ; // Date;          Sif (no more used)
    
    
    /**
     * Examples: 
     *   <Categories>Business, Competition, Favorites</Categories>
     */
    private Property   categories        = null  ; // String; Sif
    
    /**
     * Example: 
     *   DEFAULT_FOLDER\\folder1\\folder2
     *   DEFAULT_FOLDER
     */
    private Property   folder            = null  ; // String; Sif
    
    private Property   color             = null  ; // int; Sif
    private Property   height            = null  ; // int; Sif
    private Property   width             = null  ; // int; Sif
    private Property   top               = null  ; // int; Sif
    private Property   left              = null  ; // int; Sif
    
    private List xTags;

    //------------------------------------------------------------- Constructors

    /**
     * Creates a Note
     */
    public Note() {
        uid              = new Property();
        subject          = new Property();
        textDescription  = new Property();
        date             = new Property();
        
        categories       = new Property();
        
        folder           = new Property();
        color            = new Property();
        height           = new Property();
        width            = new Property();
        top              = new Property();
        left             = new Property();
        
        xTags    = null;
    }

    
    //------------------------------------------------------------ setter/getter
    
    public Property getUid() {
        return this.uid;
    }

    public Property getSubject() {
        return this.subject;
    }

    public Property getTextDescription() {
        return this.textDescription;
    }

    public Property getDate() {
        return this.date;
    }

    public void setUid(Property uid) {
        this.uid = uid;
    }

    public void setSubject(Property subject) {
        this.subject = subject;
    }

    public void setTextDescription(Property textDescription) {
        this.textDescription = textDescription;
    }

    public void setDate(Property date) {
        this.date = date;
    }
    
    public Property getCategories() {
        return categories;
    }

    public void setCategories(Property categories) {
        this.categories = categories;
    }

    public Property getColor() {
        return color;
    }

    public void setColor(Property color) {
        this.color = color;
    }

    public Property getFolder() {
        return folder;
    }

    public void setFolder(Property folder) {
        this.folder = folder;
    }

    public Property getHeight() {
        return height;
    }

    public void setHeight(Property height) {
        this.height = height;
    }

    public Property getLeft() {
        return left;
    }

    public void setLeft(Property left) {
        this.left = left;
    }

    public Property getTop() {
        return top;
    }

    public void setTop(Property top) {
        this.top = top;
    }

    public Property getWidth() {
        return width;
    }

    public void setWidth(Property width) {
        this.width = width;
    }
    
    /**
     * Returns the xTag for this note
     *
     * @return the xTag for this note
     */
    public List getXTags () {
        return xTags;
    }

    /**
     * Setter for property xTags.
     * @param xTags New value of property xTags.
     */
    public void setXTags(List xTags) {
        this.xTags = xTags;
    }

    /**
     * Adds a new xtag to the xtag list
     *
     * @param tag the new xtag
     */
    public void addXTag(XTag tag) {
        if (tag == null) {
            return;
        }

        if (xTags == null) {
            xTags = new ArrayList();
        }

        int pos = xTags.indexOf(tag);
        if (pos < 0) {
            xTags.add(tag);
        } else {
            xTags.set(pos, tag);
        }
    }
    
    // ---------------------------------------------------------- Public methods
    /**
     * Merge this contact with the given contact
     * @param contact Contact
     * @return true if the original contact is changed
     */
    public MergeResult merge(Note otherNote) {

        MergeResult noteMergeResult = new MergeResult("Contact");

        if (otherNote == null) {
            throw new IllegalStateException("The given note must be not null");
        }

        //
        // MergeResult used for each fields
        //
        MergeResult result = null;

        // UID
        Property otherUid = otherNote.getUid();
        Property myUid    = this.getUid();

        result = MergeUtils.compareStrings(
                myUid.getPropertyValueAsString(),
                otherUid.getPropertyValueAsString());
        if (result.isSetARequired()) {
            this.setUid(otherUid);
        } else if (result.isSetBRequired()) {
            otherNote.setUid(myUid);
        }
        noteMergeResult.addMergeResult(result, "Uid");

        // TextDescription
        Property otherTextDescription = otherNote.getTextDescription();
        Property myTextDescription    = this.getTextDescription();
        
        result = MergeUtils.compareStrings(
                myTextDescription.getPropertyValueAsString(),
                otherTextDescription.getPropertyValueAsString());
        if (result.isSetARequired()) {
            this.setTextDescription(otherTextDescription);
        } else if (result.isSetBRequired()) {
            otherNote.setTextDescription(myTextDescription);
        }
        noteMergeResult.addMergeResult(result, "TextDescription");
        
        // Categories
        Property otherCategories = otherNote.getCategories();
        Property myCategories    = this.getCategories();
        
        result = MergeUtils.compareStrings(
                myCategories.getPropertyValueAsString(),
                otherCategories.getPropertyValueAsString());
        if (result.isSetARequired()) {
            this.setCategories(otherCategories);
        } else if (result.isSetBRequired()) {
            otherNote.setCategories(myCategories);
        }
        noteMergeResult.addMergeResult(result, "Categories");

        // Folder
        Property otherFolder = otherNote.getFolder();
        Property myFolder    = this.getFolder();
        
        result = MergeUtils.compareStrings(
                myFolder.getPropertyValueAsString(),
                otherFolder.getPropertyValueAsString());
        if (result.isSetARequired()) {
            this.setFolder(otherFolder);
        } else if (result.isSetBRequired()) {
            otherNote.setFolder(myFolder);
        }
        noteMergeResult.addMergeResult(result, "Folder");

        // Color
        Property otherColor = otherNote.getColor();
        Property myColor    = this.getColor();
        
        result = MergeUtils.compareStrings(
                myColor.getPropertyValueAsString(),
                otherColor.getPropertyValueAsString());
        if (result.isSetARequired()) {
            this.setColor(otherColor);
        } else if (result.isSetBRequired()) {
            otherNote.setColor(myColor);
        }
        noteMergeResult.addMergeResult(result, "Color");

        // Height
        Property otherHeight = otherNote.getHeight();
        Property myHeight    = this.getHeight();
        
        result = MergeUtils.compareStrings(
                myHeight.getPropertyValueAsString(),
                otherHeight.getPropertyValueAsString());
        if (result.isSetARequired()) {
            this.setHeight(otherHeight);
        } else if (result.isSetBRequired()) {
            otherNote.setHeight(myHeight);
        }
        noteMergeResult.addMergeResult(result, "Height");

        // Width
        Property otherWidth = otherNote.getWidth();
        Property myWidth    = this.getWidth();

        result = MergeUtils.compareStrings(
                myWidth.getPropertyValueAsString(),
                otherWidth.getPropertyValueAsString());
        if (result.isSetARequired()) {
            this.setWidth(otherWidth);
        } else if (result.isSetBRequired()) {
            otherNote.setWidth(myWidth);
        }
        noteMergeResult.addMergeResult(result, "Width");

        // Top
        Property otherTop = otherNote.getTop();
        Property myTop    = this.getTop();

        result = MergeUtils.compareStrings(
                myTop.getPropertyValueAsString(),
                otherTop.getPropertyValueAsString());
        if (result.isSetARequired()) {
            this.setTop(otherTop);
        } else if (result.isSetBRequired()) {
            otherNote.setTop(myTop);
        }
        noteMergeResult.addMergeResult(result, "Top");

        // Left
        Property otherLeft = otherNote.getLeft();
        Property myLeft    = this.getLeft();

        result = MergeUtils.compareStrings(
                myLeft.getPropertyValueAsString(),
                otherLeft.getPropertyValueAsString());
        if (result.isSetARequired()) {
            this.setLeft(otherLeft);
        } else if (result.isSetBRequired()) {
            otherNote.setLeft(myLeft);
        }
        noteMergeResult.addMergeResult(result, "Left");

        return noteMergeResult;
    }
}
