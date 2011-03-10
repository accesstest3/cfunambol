/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.foundation.items.model;

import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.contact.Photo;

/**
 *
 * @version $Id: ContactWrapper.java,v 1.1.1.1 2008-03-20 21:38:41 stefano_fornari Exp $
 */
public class ContactWrapper extends EntityWrapper {

    // --------------------------------------------------------------- Constants
    public static final Short EMPTY_PHOTO = 0;
    public static final Short PHOTO_IMAGE = 1;
    public static final Short PHOTO_URL   = 2;

    //--------------------------------------------------------------- Properties

    /** The contact */
    private Contact c = null;

    public Contact getContact() {
        return c;
    }

    /** The type of the photo */
    private Short photoType = EMPTY_PHOTO;

    public Short getPhotoType() {
        return photoType;
    }

    public void setPhotoType(Short photoType) {
        if (!EMPTY_PHOTO.equals(photoType) &&
            !PHOTO_IMAGE.equals(photoType) &&
            !PHOTO_URL.equals(photoType) &&
            photoType != null) {
            throw new IllegalArgumentException(String.valueOf(photoType) + " is not a valid photoType");
        }

        this.photoType = photoType;
    }

    public boolean hasPhoto() {
        return photoType != null && !EMPTY_PHOTO.equals(photoType);
    }

    //------------------------------------------------------------- Constructors

    /**
     * Creates a new instance of ContactWrapper.
     *
     * @param id the unique ID of this wrapper
     * @param userId the ID of the user who owns this contact
     * @param c the wrapped Contact object
     */
    public ContactWrapper(String id, String userId, Contact c) {
        this(id, userId, c, null);
        if (c != null && c.getPersonalDetail() != null) {
            Photo photo = c.getPersonalDetail().getPhotoObject();
            if (photo != null) {
                if (photo.getImage() != null) {
                    setPhotoType(PHOTO_IMAGE);
                } else if (photo.getUrl() != null) {
                    setPhotoType(PHOTO_URL);
                } else {
                    setPhotoType(EMPTY_PHOTO);
                }
            }
        }
    }

    /**
     * Creates a new instance of ContactWrapper.
     *
     * @param id the unique ID of this wrapper
     * @param userId the ID of the user who owns this contact
     * @param c the wrapped Contact object
     * @param photoType the type of the photo
     */
    public ContactWrapper(String id, String userId, Contact c, Short photoType) {
        super(id, userId);
        this.c         = c;
        this.photoType = photoType;
    }
}
