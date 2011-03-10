/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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

package com.funambol.framework.protocol;

import com.funambol.framework.protocol.IdGenerator;
import com.funambol.framework.protocol.SimpleIdGenerator;
import com.funambol.framework.core.CmdID;

/**
 * This class creates command ids. Note that when a new CommandIdentifier is
 * created, it is stored also in <i>currentId</i>, which is returned by
 * <i>current()</i>. This is to avoid that <i>current()</i> creates a new
 * CommandIdentifier object each time is called.
 * <p>
 * This class is thread-safe.
 *
 *
 * @version $Id: CommandIdGenerator.java 31782 2009-08-07 13:39:36Z gmigliavacca $
 */
public class CommandIdGenerator
        implements java.io.Serializable {

  // ---------------------------------------------------------- Private fields

  /**
   * The underlying IdGenerator object
   */
  private IdGenerator idGenerator = null;

  /**
   * The current identifier
   */
  private CmdID currentId = null;

  // ------------------------------------------------------------ Constructors

  /** Creates a new instance of CommandIdGenerator */
  public CommandIdGenerator() {
    this(new SimpleIdGenerator());

  }

  /**
   * Creates a new instance of CommandIdGenerator given an IdGenerator
   *
   * @param idGenerator the idGenerator object - NOT NULL
   */
  public CommandIdGenerator(IdGenerator idGenerator) {
    if (idGenerator == null) {
      throw new NullPointerException("idGenerator cannot be null!");
    }
    this.idGenerator = idGenerator;
  }

  // ---------------------------------------------------------- Public methods

  /**
   * Returns a new generated command id.
   *
   * @return a new generated command id.
   */
  public synchronized CmdID next() {
    return (currentId = new CmdID(idGenerator.next()));
  }

  /**
   * Reset the Id counter
   */
  public synchronized void reset() {
    idGenerator.reset();
  }

  /**
   * Returns the last generated command id (which is the current command id).
   *
   * @return the last generated command id
   */
  public synchronized CmdID current() {
    return currentId;
  }
}
