/*
 * Copyright (C) 2007 Funambol, Inc.
 *
 * Copies of this file are distributed by Funambol as part of server-side
 * programs (such as Funambol Data Synchronization Server) installed on a
 * server and also as part of client-side programs installed on individual
 * devices.
 *
 * The following license notice applies to copies of this file that are
 * distributed as part of server-side programs:
 *
 * Copyright (C) 2007 Funambol, Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the Honest Public License, as published by
 * Funambol, either version 1 or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY, TITLE, NONINFRINGEMENT or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the Honest Public License for more details.
 *
 * You should have received a copy of the Honest Public License
 * along with this program; if not, write to Funambol,
 * 643 Bair Island Road, Suite 305 - Redwood City, CA 94063, USA
 *
 * The following license notice applies to copies of this file that are
 * distributed as part of client-side programs:
 *
 * Copyright (C) 2007 Funambol, Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY, TITLE, NONINFRINGEMENT or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307  USA
 */

package com.funambol.common.pim.icalendar;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.converter.VCalendarConverter;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.framework.tools.IOTools;
import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

/**
 * XVCalendarParser test cases
 * @version $Id: ICalendarParserTest.java,v 1.1 2008-04-10 11:25:48 mauro Exp $
 */
public class ICalendarParserTest extends TestCase {


    // ------------------------------------------------------------- Constructor
    public ICalendarParserTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    /*
     * Tests line-break parsing. The line break uses a white space.
     */
    public void testParse_LineBreaks_1() throws Exception {
        String icalendar = IOTools.readFileString("src/test/data/com/funambol/common/pim/icalendar/icalendar-parser/icalendar-1.ics");
        ICalendarParser parser = new ICalendarParser(new ByteArrayInputStream(icalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter = new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        
        assertEquals("Summary",
                c.getCalendarContent().getSummary().getPropertyValueAsString());
        assertEquals("Location",
                c.getCalendarContent().getLocation().getPropertyValueAsString());        
        assertEquals("Categories written on several lines and using Quoted-Printable encoding", 
                c.getCalendarContent().getCategories().getPropertyValueAsString());
        assertEquals("Description written on several lines",
                c.getCalendarContent().getDescription().getPropertyValueAsString());        
    }    
    
    /*
     * Tests line-break parsing. The line breaks uses a tab space.
     */
    public void testParse_LineBreaks_2() throws Exception {
        String icalendar = IOTools.readFileString("src/test/data/com/funambol/common/pim/icalendar/icalendar-parser/icalendar-2.ics");
        ICalendarParser parser = new ICalendarParser(new ByteArrayInputStream(icalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter = new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        
        assertEquals("pavia",
                c.getCalendarContent().getLocation().getPropertyValueAsString());
        assertEquals("040000008200E00074C5B7101A82E0080000000090958F22793EC9010000000000000000100000004FB33779B9B95B4E8A6710B01C3627AD",
                c.getCalendarContent().getUid().getPropertyValueAsString());
    }    
    
    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}