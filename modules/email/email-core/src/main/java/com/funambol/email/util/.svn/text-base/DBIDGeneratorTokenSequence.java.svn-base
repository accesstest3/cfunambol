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
package com.funambol.email.util;

import com.funambol.email.util.token.TokenException;
import com.funambol.email.util.token.TokenSequence;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorException;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;

import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Wrapper of the DBIDGenerator used to create the attachment authorization token
 * @version $Id: DBIDGeneratorTokenSequence.java,v 1.1 2008-06-03 09:10:13 testa Exp $
 */
public class DBIDGeneratorTokenSequence implements TokenSequence {

    private static final String CORE_DATASOURCE_JNDINAME = "jdbc/fnblcore";

    // ------------------------------------------------------------ Private data
    private DBIDGenerator sequence;

    // ------------------------------------------------------------ Constructors
    public DBIDGeneratorTokenSequence(String tokenSequenceName) throws TokenException {
        try {
            DataSource dataSource = DataSourceTools.lookupDataSource(CORE_DATASOURCE_JNDINAME);

            sequence =
                DBIDGeneratorFactory.getDBIDGenerator(tokenSequenceName,
                                                      dataSource);
            
        } catch (NamingException ex) {
            throw new TokenException("Error initializing DBIDGeneratorTokenSequence Object ", ex);
        }
    }

    // ---------------------------------------------------------- Public methods
    public long next() throws TokenException {
        try {
            return sequence.next();
        } catch (DBIDGeneratorException e) {
            throw new TokenException("Exception while getting token sequence value", e);
        }
    }

}
