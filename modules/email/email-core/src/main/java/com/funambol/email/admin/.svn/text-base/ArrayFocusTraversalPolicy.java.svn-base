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
package com.funambol.email.admin;

import java.awt.Component;
import java.awt.Container;
import javax.swing.LayoutFocusTraversalPolicy;

/**
 * This class represent a focus traversal policy based on an array of
 * components; the order is determined by the order of the components in that
 * array.
 *
 * @version $Id: ArrayFocusTraversalPolicy.java,v 1.1 2008-03-25 11:28:17 gbmiglia Exp $
 */
public class ArrayFocusTraversalPolicy extends LayoutFocusTraversalPolicy {

    // ------------------------------------------------------------ Private data

    /** components upon which the traversal order is determined */
    private Component[] traversalOrder;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of ArrayFocusTraversalPolicy
     *
     *
     * @param traversalOrder components to determine the traversal order
     */
    public ArrayFocusTraversalPolicy( Component[] traversalOrder ) {
        this.traversalOrder = traversalOrder;
    }

    // ------------------------------------------------------- Overrides methods

    @Override
    public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
        for ( int k = 0; k < traversalOrder.length - 1; k++ ) {

            // identify aComponent in the list of components
            if ( traversalOrder[k].equals( aComponent ) ) {

                // return the first enabled field that follows aComponent
                for (int i = k + 1; i < traversalOrder.length; i++) {
                    if(!traversalOrder[i].isEnabled()){
                        continue;
                    }
                    return traversalOrder[i];
                }
            }
        }
        return super.getComponentAfter( focusCycleRoot, aComponent );
    }

    @Override
    public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
        for ( int k = 1; k < traversalOrder.length; k++ ) {

            // identify aComponent in the list of components
            if ( traversalOrder[k].equals( aComponent ) ) {

                // return the first enabled field that precedes aComponent
                for (int i = k - 1; i >= 0; i--) {
                    if(!traversalOrder[i].isEnabled()){
                        continue;
                    }
                    return traversalOrder[i];
                }
            }
        }
        return super.getComponentBefore( focusCycleRoot, aComponent );
    }
}
