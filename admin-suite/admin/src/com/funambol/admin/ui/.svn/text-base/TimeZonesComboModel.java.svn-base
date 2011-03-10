/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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
package com.funambol.admin.ui;

import javax.swing.DefaultComboBoxModel;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.TimeZone;

import com.funambol.framework.tools.TimeZonesTools;

/**
 * Manages timezone.
 * 
 * @version $Id: TimeZonesComboModel.java,v 1.6 2007-11-28 10:28:18 nichele Exp $
 */
public class TimeZonesComboModel extends DefaultComboBoxModel {

    private static String[] timeZonesDescr =
        TimeZonesTools.getAvailablesTimeZonesDescr();

    private static Map timeZones   =
        TimeZonesTools.getAvailablesTimeZones();

    static {
        String[] tmp = TimeZonesTools.getAvailablesTimeZonesDescr();
        int len      = tmp.length;
        timeZonesDescr = new String[len +1];
        timeZonesDescr[0] = "Not specified";
        System.arraycopy(tmp, 0, timeZonesDescr, 1, len);
    }

    private String selectedTimeZoneId    = null;
    private String selectedTimeZoneDescr = null;

    public Object getElementAt(int position) {
        return timeZonesDescr[position];
    }

    public void setSelectedItem(Object obj) {
        selectedTimeZoneDescr = (String)obj;
        selectedTimeZoneId    = (String)timeZones.get(obj);
    }

    public void setSelectedIndex(int index) {
        setSelectedItem(timeZonesDescr[index]);
    }

    public Object getSelectedItem() {
        return selectedTimeZoneDescr;
    }

    public String getSelectedTimeZoneId() {
        return selectedTimeZoneId;
    }

    public boolean setSelectedTimeZoneId(String timeZoneId) {
        String descr = TimeZonesTools.getTimeZoneDescr(timeZoneId);
        String checkId = (String)timeZones.get(descr);
        if (checkId == null) {
            return false;
        }
        setSelectedItem(descr);
        return true;
    }

    public void setFirstItem() {
        setSelectedIndex(0);
    }
    public int getSize() {
        return timeZonesDescr.length;
    }
}
