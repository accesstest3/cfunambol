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

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;

import com.funambol.framework.engine.source.ContentType;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceInfo;

import com.funambol.common.pim.calendar.CalendarContent;
import com.funambol.common.pim.calendar.Event;
import com.funambol.common.pim.calendar.Task;

import com.funambol.foundation.engine.source.PIMCalendarSyncSource;
import com.funambol.foundation.engine.source.PIMSyncSource;

import com.funambol.admin.AdminException;

/**
 * This class does something.
 *
 * @version $Id: PIMCalendarSyncSourceConfigPanel.java,v 1.1.1.1 2008-03-20 21:38:30 stefano_fornari Exp $
 */
public class PIMCalendarSyncSourceConfigPanel extends PIMSyncSourceConfigPanel
        implements Serializable {

    // --------------------------------------------------------------- Constants
    protected static final String PANEL_NAME = "Edit PIM Calendar SyncSource";

    protected static final String TYPE_LABEL_SIFE = "SIF-E";
    protected static final String TYPE_LABEL_SIFT = "SIF-T";
    protected static final String TYPE_LABEL_VCAL = "vCalendar";
    protected static final String TYPE_LABEL_ICAL = "iCalendar";

    protected static final String TYPE_SIFE = "text/x-s4j-sife";
    protected static final String TYPE_SIFT = "text/x-s4j-sift";
    protected static final String TYPE_VCAL = "text/x-vcalendar";
    protected static final String TYPE_ICAL = "text/calendar";

    protected static final String VERSION_SIFE  = "1.0";
    protected static final String VERSION_SIFT  = "1.0";
    protected static final String VERSION_VCAL  = "1.0";
    protected static final String VERSION_ICAL  = "2.0";

    // ---------------------------------------------------------- Protected data
    protected JCheckBox eventValue;
    protected JCheckBox taskValue;

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of PIMCalendarSyncSourceConfigPanel. */
    public PIMCalendarSyncSourceConfigPanel() {
        super();
        typeLabel.setText("Default type: ");
        
        this.add(eventValue, null);
        this.add(taskValue, null);

        typeCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateEntityTypeCheckBoxes();
            }
        });

    }

    // ---------------------------------------------------------- Public methods

    /**
     * Updates the form
     */
    public void updateForm() {

        if (!(getSyncSource() instanceof PIMCalendarSyncSource)) {
            notifyError(
                    new AdminException("This is not a PIMCalendarSyncSource! "
                    + "Unable to process SyncSource values."
                    )
                    );
            return;
        }

        super.updateForm();

        updateEntityTypeCheckBoxes();
    }
    
    /**
     * Set preferredSize of the panel
     * @return preferredSize of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(525, 310);
    }    

    // ------------------------------------------------------- Protected methods

    /**
     * Adds extra components just above the confirm button.
     *
     * @param x horizontal position
     * @param y vertical position
     * @param xGap standard horizontal gap
     * @param yGap standard vertical gap
     * @return the new vertical position
     */
    protected int addExtraComponents(int x, int y, int xGap, int yGap) {

        eventValue = new JCheckBox("Events");
        taskValue  = new JCheckBox("Tasks");

        eventValue.setFont(defaultFont);
        eventValue.setSelected(true);
        eventValue.setBounds(170, y, 100, 25);
        eventValue.setEnabled(true);

        x += xGap; // Shift a bit to the right

        taskValue.setFont(defaultFont);
        taskValue.setSelected(true);
        taskValue.setBounds(170 + xGap, y, 100, 25);
        taskValue.setEnabled(true);

        return y + yGap;
    }

    /**
     * Updates entities checkboxes according to the selected type
     */
    protected void updateEntityTypeCheckBoxes() {
        if (isSIFSelected()) {
            eventValue.setSelected(areEventsAllowed());
            taskValue.setSelected(areTasksAllowed());

            eventValue.setEnabled(false);
            taskValue.setEnabled(false);
        } else {
            PIMCalendarSyncSource syncSource = (PIMCalendarSyncSource)getSyncSource();
            boolean events = false;
            boolean tasks = false;
            if (syncSource.getEntityType() == null ||
                    syncSource.getEntityType().isAssignableFrom(Event.class)) {
                events = true;
            }
            if (syncSource.getEntityType() == null ||
                    syncSource.getEntityType().isAssignableFrom(Task.class)) {
                tasks = true;
            }
            eventValue.setSelected(events);
            taskValue.setSelected(tasks);

            eventValue.setEnabled(true);
            taskValue.setEnabled(true);
        }
    }

    /**
     * Validates the values selected.
     * @throws java.lang.IllegalArgumentException  if there is an invalid value
     */
    @Override
    protected void validateValues() throws IllegalArgumentException {
        super.validateValues();

        if (!eventValue.isSelected() && !taskValue.isSelected()) {
            throw new IllegalArgumentException(
                    "Please check at least one between 'Events' and 'Tasks'.");
        }
    }

    /**
     * Sets the syncsource with the inserted values
     */
    @Override
    protected void getValues() {
        super.getValues();

        if ((eventValue == null) || (taskValue == null)) {
            return;
        }

        PIMCalendarSyncSource syncSource = (PIMCalendarSyncSource)getSyncSource();

        Class entityType;
        if (eventValue.isSelected()) {
            if (taskValue.isSelected()) {
                entityType = CalendarContent.class;
            } else {
                entityType = Event.class;
            }
        } else {
            entityType = Task.class;
        }
        syncSource.setEntityType(entityType);
    }

    /**
     * Returns the panel name
     * @return the panel name
     */
    @Override
    protected String getPanelName() {
        return PANEL_NAME;
    }

    /**
     * Returns the available types
     * @return the available types;
     */
    protected List getTypes() {
        List supportedTypes = new ArrayList();
        supportedTypes.add(TYPE_LABEL_SIFE);
        supportedTypes.add(TYPE_LABEL_SIFT);
        supportedTypes.add(TYPE_LABEL_VCAL);
        supportedTypes.add(TYPE_LABEL_ICAL);
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
            if (TYPE_ICAL.equals(preferredType)) {
                return TYPE_LABEL_ICAL;
            }
            if (TYPE_VCAL.equals(preferredType)) {
                return TYPE_LABEL_VCAL;
            }
            if (TYPE_SIFE.equals(preferredType)) {
                return TYPE_LABEL_SIFE;
            }
            if (TYPE_SIFT.equals(preferredType)) {
                return TYPE_LABEL_SIFT;
            }
        }
        return null;
    }

    /**
     * Sets the source info of the given syncsource based on the given selectedType
     * @param syncSource the source
     * @param selectedType the selected type
     */
    public void setSyncSourceInfo(SyncSource syncSource, String selectedType) {
        PIMSyncSource pimSource = (PIMSyncSource)syncSource;
        ContentType[] contentTypes = null;
        if (TYPE_LABEL_ICAL.equals(selectedType)) {
            contentTypes = new ContentType[2];
            contentTypes[0] = new ContentType(TYPE_ICAL, VERSION_ICAL);
            contentTypes[1] = new ContentType(TYPE_VCAL, VERSION_VCAL);
        } else if (TYPE_LABEL_VCAL.equals(selectedType)) {
            contentTypes = new ContentType[2];
            contentTypes[0] = new ContentType(TYPE_VCAL, VERSION_VCAL);
            contentTypes[1] = new ContentType(TYPE_ICAL, VERSION_ICAL);
        } else if (TYPE_LABEL_SIFE.equals(selectedType)) {
            contentTypes = new ContentType[1];
            contentTypes[0] = new ContentType(TYPE_SIFE, VERSION_SIFE);
        } else if (TYPE_LABEL_SIFT.equals(selectedType)) {
            contentTypes = new ContentType[1];
            contentTypes[0] = new ContentType(TYPE_SIFT, VERSION_SIFT);
        }

        pimSource.setInfo(new SyncSourceInfo(contentTypes, 0));
    }
    // --------------------------------------------------------- Private methods


    private boolean areEventsAllowed() {
        if (typeCombo.getSelectedItem() == null) {
            return false;
        }

        if (TYPE_LABEL_SIFT.equals((String)typeCombo.getSelectedItem())) {
            return false;
        }
        return true;
    }

    private boolean areTasksAllowed() {
        if (typeCombo.getSelectedItem() == null) {
            return false;
        }

        if (TYPE_LABEL_SIFE.equals((String)typeCombo.getSelectedItem())) {
            return false;
        }
        return true;
    }

}
