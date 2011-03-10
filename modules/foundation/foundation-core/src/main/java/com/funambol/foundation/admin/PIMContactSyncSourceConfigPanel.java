/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.foundation.admin;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import com.funambol.foundation.engine.source.PIMContactSyncSource;
import com.funambol.foundation.engine.source.PIMSyncSource;

import com.funambol.framework.engine.source.ContentType;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceInfo;

import com.funambol.admin.AdminException;

/**
 * This class does something.
 *
 * @version $Id: PIMContactSyncSourceConfigPanel.java,v 1.1.1.1 2008-03-20 21:38:30 stefano_fornari Exp $
 */
public class PIMContactSyncSourceConfigPanel extends PIMSyncSourceConfigPanel
        implements Serializable {

    // --------------------------------------------------------------- Constants
    protected static final String PANEL_NAME = "Edit PIM Contact SyncSource";

    protected static final String TYPE_LABEL_SIFC  = "SIF-C";
    protected static final String TYPE_LABEL_VCARD = "VCard";

    protected static final String TYPE_SIFC  = "text/x-s4j-sifc";
    protected static final String TYPE_VCARD = "text/x-vcard";

    protected static final String VERSION_SIFC  = "1.0";
    protected static final String VERSION_VCARD = "2.1";

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of PIMContactSyncSourceConfigPanel. */
    public PIMContactSyncSourceConfigPanel() {
        super();
        typeLabel.setText("Default type: ");
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Updates the form
     */
    public void updateForm() {

        if (!(getSyncSource() instanceof PIMContactSyncSource)) {
            notifyError(
                    new AdminException("This is not a PIMContactSyncSources! "
                    + "Unable to process SyncSource values."
                    )
                    );
            return;
        }

        super.updateForm();
    }

    // ------------------------------------------------------- Protected methods

    /**
     * Returns the panel name
     * @return the panel name
     */
    protected String getPanelName() {
        return PANEL_NAME;
    }

    /**
     * Returns the available types
     * @return the available types;
     */
    protected List getTypes() {
        List supportedTypes = new ArrayList();
        supportedTypes.add(TYPE_LABEL_SIFC);
        supportedTypes.add(TYPE_LABEL_VCARD);
        return supportedTypes;
    }

    /**
     * Returns the type to select based on the given syncsource
     * @return the type to select based on the given syncsource
     */
    protected String getTypeToSelect(SyncSource syncSource) {
        String preferredType = null;
        if (syncSource.getInfo() != null &&
            syncSource.getInfo().getPreferredType() != null) {

            preferredType = syncSource.getInfo().getPreferredType().getType();
            if (TYPE_VCARD.equals(preferredType)) {
                return TYPE_LABEL_VCARD;
            }
            if (TYPE_SIFC.equals(preferredType)) {
                return TYPE_LABEL_SIFC;
            }
        }
        return null;
    }

    /**
     * Sets the source info of the given syncsource based on the given selectedType
     */
    public void setSyncSourceInfo(SyncSource syncSource, String selectedType) {
        PIMSyncSource pimSource = (PIMSyncSource)syncSource;
        ContentType[] contentTypes = null;
        if (TYPE_LABEL_SIFC.equals(selectedType)) {
            contentTypes = new ContentType[1];
            contentTypes[0] = new ContentType(TYPE_SIFC, VERSION_SIFC);
        } else if (TYPE_LABEL_VCARD.equals(selectedType)) {
            contentTypes = new ContentType[1];
            contentTypes[0] = new ContentType(TYPE_VCARD, VERSION_VCARD);
        }

        pimSource.setInfo(new SyncSourceInfo(contentTypes, 0));
    }
    // --------------------------------------------------------- Private methods


}
