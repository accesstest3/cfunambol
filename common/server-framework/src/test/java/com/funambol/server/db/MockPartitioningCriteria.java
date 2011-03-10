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

package com.funambol.server.db;

/**
 * It's a simple partition criteria that return a partition with the same name
 * of the partitioning key
 * @version $Id: MockPartitioningCriteria.java,v 1.2 2008-05-15 05:19:25 nichele Exp $
 */
public class MockPartitioningCriteria implements PartitioningCriteria {

    /**
     * Returns a partition with the same name of the partitioning key.
     * For testing purpose:
     * <br/>If the partitioning key is null, a null is returned.
     * <br/>If the partitioning key is 'locked' a LockedPartitionException.
     *  <br/>If the partitioning key is 'exception' a PartitioningCriteria is thrown.
     * @param partitioningKey the partitioningKey
     * @return a partition with the same name of the partitioning key
     */
    public Partition getPartition(String partitioningKey) throws PartitioningCriteriaException {
        if (partitioningKey == null) {
            return null;
        }
        if ("locked".equalsIgnoreCase(partitioningKey)) {
            throw new LockedPartitionException(new Partition("locket-partition-for-testing-purpose"));
        } else if ("exception".equalsIgnoreCase(partitioningKey)) {
            throw new PartitioningCriteriaException("PartitioningCriteriaException for testing purpose");
        }

        return new Partition(partitioningKey);
    }

    public void init() throws PartitioningCriteriaException {
        //
        // Nothing to do
        //
    }

    public void configure() throws PartitioningCriteriaException {
        //
        // Nothing to do
        //
    }

}