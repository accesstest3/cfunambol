/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

package com.funambol.foundation.items.dao;

import com.funambol.server.db.Partition;
import com.funambol.server.db.PartitioningCriteria;
import com.funambol.server.db.PartitioningCriteriaException;

/**
 * It's a simple partition criteria that return a partition with the same name
 * of the partitioning key
 * @version $Id: MockFoundationPartitioningCriteria.java,v 1.1 2008-05-17 19:17:16 nichele Exp $
 */
public class MockFoundationPartitioningCriteria implements PartitioningCriteria {

    /**
     * Returns a partition with the same name of the partitioning key.
     * If the partitioning key is null, a null is returned
     * @param partitioningKey the partitioningKey
     * @return a partition with the same name of the partitioning key
     */
    public Partition getPartition(String partitioningKey) {
        if (partitioningKey == null) {
            return null;
        }
        return new Partition(partitioningKey);
    }

    public void init() throws PartitioningCriteriaException {
        // nothing to do
    }

    public void configure() throws PartitioningCriteriaException {
        // nothing to do
    }

}
