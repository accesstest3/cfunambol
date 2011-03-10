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

/**
 * This class works as a simple id generator that after being initialized with a
 * numeric value, increments the counter each time the next method is called.
 * <p>
 * SimpleIdGenerator is thread-safe
 *
 *
 * @version $Id: SimpleIdGenerator.java 31782 2009-08-07 13:39:36Z gmigliavacca $
 */
public class SimpleIdGenerator
implements IdGenerator, java.io.Serializable {

    // -------------------------------------------------------------- Properties

    /**
     * The counter
     */
    private long counter = 0;

    public long getCounter() {
        return counter;
    }

    /**
     * Standard setter method
     */
    public void setCounter(long counter) {
        this.counter = counter;
    }

    /**
     * The units the counter must be incremented each time next is called
     */
    private int increment = 1;

    public int getIncrement() {
        return increment;
    }

    /**
     * Standard setter method
     */
    public void setIncrement(int increment) {
        this.increment = increment;
    }

    /**
     * The maximum value allowed
     */
    private long max = Long.MAX_VALUE;

    /**
     * Getter for max
     *
     * @return max
     */
    public long getMax() {
        return max;
    }

    /**
     * Setter for max
     *
     * @param max new value
     */
    public void setMax(long max) {
        this.max = max;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of SimpleIdGenerator
     *
     * @param counter the starting value
     * @param increment the increment
     */
    public SimpleIdGenerator(long counter, int increment) {
        this.counter = counter;
        this.increment = increment;
    }

    /**
     * Like this(counter,1)
     *
     * @param counter the starting value
     */
    public SimpleIdGenerator(int counter) {
        this(counter, 1);
    }

    /**
     * Like this(0, 1)
     *
     */
    public SimpleIdGenerator() {
        this(0, 1);
    }

    /**
     * Reset the generator to 0.
     */
    public void reset() {
        this.counter = 0;
    }

    /**
     * Returns the next value of the counter (incrementing the counter by the
     * increment)
     *
     * @return the next generated value
     */
    public synchronized String next() {
        if (counter == max) {
            reset();
        }

        counter += increment;

        return String.valueOf(counter);
    }

    /**
     * Returns the last generated id (which is the current id).
     *
     * @return the last generated id
     */
    public synchronized String current() {
        return String.valueOf(counter);
    }
}
