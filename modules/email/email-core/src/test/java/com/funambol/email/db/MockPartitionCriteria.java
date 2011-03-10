/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.email.db;

import com.funambol.server.db.Partition;
import com.funambol.server.db.PartitioningCriteria;
import com.funambol.server.db.PartitioningCriteriaException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provide a fake partition criteria that can be used during tests.
 * According to that criteria there are three different partitions:
 * - PART1
 * - PART2
 * - PART3
 * 
 * And the relation between users and partitions is explained in the following
 * table:
 * 
 * --------------------------------------------
 * |     PART1    |    PART2    |    PART3    |
 * --------------------------------------------
 * |  username1   | username1   | username3   |
 * --------------------------------------------
 * |  username9   | username5   | username7   |
 * --------------------------------------------
 * |  username100 | username101 | username8   |
 * --------------------------------------------
 * |  username103 | username104 | username102 |
 * --------------------------------------------
 *
 * @version $Id: MockPartitionCriteria.java 35029 2010-07-12 14:21:06Z filmac $
 */
public class MockPartitionCriteria implements PartitioningCriteria {

    Map<String,String> userDictionary = new HashMap<String, String>();

    public MockPartitionCriteria() {
        buildDictionary();
    }

    public Partition getPartition(String partitioningKey) throws PartitioningCriteriaException {
        String partitionName = resolveFromUserName(partitioningKey);
        
        if(partitionName==null)
            throw  new PartitioningCriteriaException("Partition not found for key ["+partitioningKey+"].");

        return new Partition(partitionName);
    }

    public void init() throws PartitioningCriteriaException {
        System.out.println("Initing mock criteria.");
    }

    public void configure() throws PartitioningCriteriaException {
        System.out.println("Configuring mock criteria.");
    }

    private void buildDictionary() {
        userDictionary.put("username1", "user_part1");
        userDictionary.put("username9", "user_part1");
        userDictionary.put("username100", "user_part1");
        userDictionary.put("username103", "user_part1");

        userDictionary.put("username2", "user_part2");
        userDictionary.put("username5", "user_part2");
        userDictionary.put("username101", "user_part2");
        userDictionary.put("username104", "user_part2");
        

        userDictionary.put("username3", "user_part3");
        userDictionary.put("username7", "user_part3");
        userDictionary.put("username8", "user_part3");
        userDictionary.put("username102", "user_part3");
    }

    private String resolveFromUserName(String partitioningKey) {
        if(userDictionary.containsKey(partitioningKey)) {
            return userDictionary.get(partitioningKey);
        }
        return null;
    }

}
