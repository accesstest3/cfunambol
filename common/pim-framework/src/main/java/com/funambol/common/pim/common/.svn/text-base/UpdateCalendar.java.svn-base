/* 
 * Copyright(c)2004 Harrie Hazewinkel. All rights reserved.
 */

/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright(C)2006 - 2007 Funambol, Inc.
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
 * In accordance with Section 7(b)of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */
package com.funambol.common.pim.common;

import java.util.Iterator;
import com.funambol.common.pim.model.Property;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.common.pim.model.VComponent;
import com.funambol.common.pim.model.VEvent;
import com.funambol.common.pim.model.VTodo;

/**
 * This class is used to update a vCalendar/iCalendar item by visiting a 
 * VCalendar object.
 * 
 * @version $Id: UpdateCalendar.java,v 1.2 2007-11-28 11:14:04 nichele Exp $
 */
public class UpdateCalendar extends VisitorObjectWalk {

    @Override
    public Object visitVCalendar(VCalendar vc, Object arg)throws VisitorException {
        VCalendar vcUpdate = (VCalendar)arg;
        replaceProperties(vc, vcUpdate);
        
        // For the component list we use the iterator
        Iterator cIter = vc.getAllComponents().iterator();
        Iterator cUpdateIter = vcUpdate.getAllComponents().iterator();
        if(cIter.hasNext()&& cUpdateIter.hasNext()){
            VisitorInterface c = (VisitorInterface)cIter.next();
            c.accept(this, (VComponent)cUpdateIter.next());
        }
        return vc;
    }

    @Override
    public Object visitVComponent(VEvent ve, Object arg)throws VisitorException {
        VEvent veUpdate = (VEvent)arg;
        return replaceProperties(ve, veUpdate);
    }

    @Override
    public Object visitVComponent(VTodo vt, Object arg)throws VisitorException {
        VTodo vtUpdate = (VTodo)arg;
        return replaceProperties(vt, vtUpdate);
    }

    private VComponent replaceProperties(VComponent target, VComponent update){
        Iterator targetIter = target.getAllProperties().iterator();
        while (targetIter.hasNext()){
            Property targetProp = (Property)targetIter.next();
            System.out.print("    " + targetProp.getName());
            Property updateProp = update.getProperty(targetProp.getName());
            if(updateProp != null){ // there is an update
                System.out.print(" found");
                
                // Removes the old property
                target.delProperty(targetProp);
                
                // Inserts the updated property
                target.addProperty(updateProp);
            } else {
                target.setProperty(targetProp);
            }
            System.out.println();
        }
        return target;
    }
}
