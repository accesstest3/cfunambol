/*
 * <FUNAMBOLCOPYRIGHT>
 * Copyright (C) 2009 Funambol.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Funambol.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Funambol MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. Funambol SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * </FUNAMBOLCOPYRIGHT>
 */

package com.funambol.common.pim.contact;

import com.funambol.common.pim.common.MergeableWithURI;
import com.funambol.framework.tools.merge.MergeResult;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the address and features of an Internet Messaging 
 * and/or Presence Protocol service used by a contact.
 *
 * @version $Id$
 */
public class IMPPAddress implements MergeableWithURI {

    //--------------------------------------------------------------- Properties

    /** The address URI */
    private String uri;

    /** Personal or business? */
    public final static Boolean PERSONAL = Boolean.TRUE;
    public final static Boolean BUSINESS = Boolean.FALSE;
    private Boolean type = null; // null means undefined

    /** The most usual location this IM/PP address is used from */
    public final static Short HOME   = 0;
    public final static Short WORK   = 1;
    public final static Short MOBILE = 2;
    private Short location = null; // null means undefined

    /** Preferred IMPP address? */
    private boolean preferred = false;

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the type
     */
    public Boolean getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Boolean type) {
        this.type = type;
    }

    /**
     * @return the location
     */
    public Short getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Short location) {
        this.location = location;
    }

    /**
     * @return the preferred
     */
    public boolean isPreferred() {
        return preferred;
    }

    /**
     * @param preferred the preferred to set
     */
    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }
    
    //------------------------------------------------------------- Constructors

    public IMPPAddress(String uri) {

        this.uri = uri;
    }

    public IMPPAddress(String uri,
                       Boolean type,
                       Short location,
                       boolean preferred) {

        this.uri       = uri;
        this.type      = type;
        this.location  = location;
        this.preferred = preferred;
    }

     //---------------------------------------------------------- Public methods

    public MergeResult merge(IMPPAddress otherAddress) {
        boolean setARequired = false;
        boolean setBRequired = false;

        // URI
        if (!this.uri.equals(otherAddress.uri)) {
            return null; // They do not represent the same IMPP address!
        }

        // Type
        if (this.type == null) { // A is undefined
            if (otherAddress.type != null) { // B is defined
                // Sets A's type to B's type
                setARequired = true;
                this.type = otherAddress.type;
            }
        } else { // A is defined
            if (otherAddress.type != this.type) { // B is undefined or different
                // Sets B's type to A's type
                setBRequired = true;
                otherAddress.type = this.type;
            }
        }

        // Location
        if (this.location == null) { // A is undefined
            if (otherAddress.location != null) { // B is defined
                // Sets A's location to B's type
                setARequired = true;
                this.location = otherAddress.location;
            }
        } else { // A is defined
            if (otherAddress.location != this.location) { // B is undefined or different
                // Sets B's type to A's type
                setBRequired = true;
                otherAddress.location = this.location;
            }
        }

        // Preferred
        if (this.preferred != otherAddress.preferred) { // B is different
                // A's preferred flag always prevails (this is to avoid having
                // several preferred IMPPs)
                setBRequired = true;
                otherAddress.preferred = this.preferred;
        }

        return new MergeResult(setARequired, setBRequired);
    }

    public MergeResult merge(Object otherObject) {

        if (!(otherObject instanceof IMPPAddress)) {
            return null;
        }
        return merge((IMPPAddress) otherObject);
    }

    public List<String> getVCardTypeList() {

        List<String> types = new ArrayList<String>(3);

        // Type
        if (IMPPAddress.PERSONAL.equals(getType())) {
            types.add("PERSONAL");
        } else if (IMPPAddress.BUSINESS.equals(getType())) {
            types.add("BUSINESS");
        }

        // Location
        if (IMPPAddress.HOME.equals(getLocation())) {
            types.add("HOME");
        } else if (IMPPAddress.WORK.equals(getLocation())) {
            types.add("WORK");
        } else if (IMPPAddress.MOBILE.equals(getLocation())) {
            types.add("MOBILE");
        }

        // Preferred
        if (isPreferred()) {
            types.add("PREF");
        }

        return types;
    }
}
