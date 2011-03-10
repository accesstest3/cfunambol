/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.common.pim.converter;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.icalendar.ICalendarParser;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.common.pim.xvcalendar.XVCalendarParser;

import com.funambol.framework.tools.IOTools;
import java.util.TimeZone;

/**
 * VCalendarConverter test cases
 * @version $Id: VCalendarConverterTest.java,v 1.8 2008-09-03 09:51:52 mauro Exp $
 */
public class VCalendarConverterTest extends TestCase {

    // ------------------------------------------------------------ Constructors
    public VCalendarConverterTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // All tests will be performed as if they were run on August 15th, 2008,
        // at noon
        TimeZoneHelper.setReferenceTime(1218801600000L);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        
        // Resets the TZ reference time to its default value, i.e. the current 
        // system time
        TimeZoneHelper.setReferenceTime(-1);
    }

    //--------------------------------------------------------------- Test cases
    public void test_XVCalendarBackAndForth1() throws Exception {
        String xvCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-1-input.vcs");
        XVCalendarParser parser =
                new XVCalendarParser(new ByteArrayInputStream(xvCalendar.getBytes()));
        VCalendar vc = parser.XVCalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, true);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        xvCalendar = writer.toString(vc);
        IOTools.writeFile(xvCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-1-output.vcs");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-1-output.vcs");

        assertEquals(expectedResult.replaceAll("\\r", ""), xvCalendar.replaceAll("\\r", ""));
    }

    public void test_XVCalendarBackAndForth2() throws Exception {
        String xvCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-2-input.vcs");
        XVCalendarParser parser =
                new XVCalendarParser(new ByteArrayInputStream(xvCalendar.getBytes()));
        VCalendar vc = parser.XVCalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, true);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        xvCalendar = writer.toString(vc);
        IOTools.writeFile(xvCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-2-output.vcs");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-2-output.vcs");

        assertEquals(expectedResult.replaceAll("\\r", ""), xvCalendar.replaceAll("\\r", ""));
    }

    public void test_XVCalendarBackAndForth3() throws Exception {
        String xvCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-3-input.vcs");
        XVCalendarParser parser =
                new XVCalendarParser(new ByteArrayInputStream(xvCalendar.getBytes()));
        VCalendar vc = parser.XVCalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, true);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        xvCalendar = writer.toString(vc);
        IOTools.writeFile(xvCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-3-output.vcs");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-3-output.vcs");

        assertEquals(expectedResult.replaceAll("\\r", ""), xvCalendar.replaceAll("\\r", ""));
    }

    public void test_XVCalendarBackAndForth4() throws Exception {
        String xvCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-4-input.vcs");
        XVCalendarParser parser =
                new XVCalendarParser(new ByteArrayInputStream(xvCalendar.getBytes()));
        VCalendar vc = parser.XVCalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, true);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        xvCalendar = writer.toString(vc);
        IOTools.writeFile(xvCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-4-output.vcs");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-4-output.vcs");

        assertEquals(expectedResult.replaceAll("\\r", ""), xvCalendar.replaceAll("\\r", ""));
    }

    public void test_XVCalendarBackAndForth5() throws Exception {
        String xvCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-1-input.vcs");
        XVCalendarParser parser =
                new XVCalendarParser(new ByteArrayInputStream(xvCalendar.getBytes()));
        VCalendar vc = parser.XVCalendar();
        VCalendarConverter converter =
                new VCalendarConverter(TimeZone.getTimeZone("Europe/London"), VCalendarConverter.CHARSET_UTF8, true);
        // The result should be different from test_XVCalendarBackAndForth1
        // because it's converted into the London time zone and the original
        // time zone information is removed
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, true);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        xvCalendar = writer.toString(vc);
        IOTools.writeFile(xvCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-5-output.vcs");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-5-output.vcs");

        assertEquals(expectedResult.replaceAll("\\r", ""), xvCalendar.replaceAll("\\r", ""));
    }
    
    public void test_XVCalendarBackAndForth6() throws Exception {
        String xvCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-6-input.vcs");
        XVCalendarParser parser =
                new XVCalendarParser(new ByteArrayInputStream(xvCalendar.getBytes()));
        VCalendar vc = parser.XVCalendar();
        VCalendarConverter converter =
                new VCalendarConverter(TimeZone.getTimeZone("Europe/London"), VCalendarConverter.CHARSET_UTF8, true);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, true);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        xvCalendar = writer.toString(vc);
        IOTools.writeFile(xvCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-6-output.vcs");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-6-output.vcs");

        assertEquals(expectedResult.replaceAll("\\r", ""), xvCalendar.replaceAll("\\r", ""));
    }
    
    public void test_XVCalendarBackAndForth7() throws Exception {
        String xvCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-7-input.vcs");
        XVCalendarParser parser =
                new XVCalendarParser(new ByteArrayInputStream(xvCalendar.getBytes()));
        VCalendar vc = parser.XVCalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, true);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        xvCalendar = writer.toString(vc);
        IOTools.writeFile(xvCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-7-output.vcs");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-7-output.vcs");

        assertEquals(expectedResult.replaceAll("\\r", ""), xvCalendar.replaceAll("\\r", ""));
    }
    
    public void test_ICalendarBackAndForth1() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-1-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-1-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-1-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }

    public void test_ICalendarBackAndForth2() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-2-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-2-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-2-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }

    public void test_ICalendarBackAndForth3() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-3-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-3-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-3-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }

    public void test_ICalendarBackAndForth4() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-1-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(TimeZone.getTimeZone("Asia/Tokyo"), VCalendarConverter.CHARSET_UTF8, true);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-4-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-4-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }
    
    public void test_ICalendarBackAndForth5() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-5-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-5-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-5-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }
    
    public void test_ICalendarBackAndForth6() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-6-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-6-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-6-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }
    
    public void test_ICalendarBackAndForth7() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-7-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-7-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-7-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }
    
    public void test_ICalendarBackAndForth8() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-8-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-8-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-8-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }
    
    public void test_ICalendarBackAndForth9() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-9-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-9-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-9-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }
    
    public void test_ICalendarBackAndForth10() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-10-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-10-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-10-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }

    public void test_ICalendarBackAndForth11() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-11-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-11-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-11-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }

    public void test_ICalendarBackAndForth12() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-12-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-12-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-12-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }
    
    public void test_ICalendarBackAndForth13() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-13-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-13-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-13-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }
    
    /**
     * Test X-MICROSOFT-CDO-BUSYSTATUS:BUSY
     * @throws java.lang.Exception
     */
    public void test_ICalendarBackAndForth14() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-14-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-14-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-14-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }
    
    /**
     * Test X-LOTUS-CHILD_UID:
     * @throws java.lang.Exception
     */
    public void test_ICalendarBackAndForth15() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-15-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-15-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-15-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }
    
    /**
     * Test X-MICROSOFT-CDO-BUSYSTATUS:TENTATIVE
     * @throws java.lang.Exception
     */
    public void test_ICalendarBackAndForth16() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-16-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-16-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-16-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    } 
    
    public void test_ICalendarBackAndForth17() throws Exception {
        String iCalendar =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-17-input.ics");
        ICalendarParser parser =
                new ICalendarParser(new ByteArrayInputStream(iCalendar.getBytes()));
        VCalendar vc = parser.ICalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, false);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        iCalendar = writer.toString(vc);
        IOTools.writeFile(iCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/icalendar-17-output.ics");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-17-output.ics");

        assertEquals(expectedResult.replaceAll("\\r", ""), iCalendar.replaceAll("\\r", ""));
    }

    public void test_VCalendarAllDayWithFloatingTimezoneBackAndForth() throws Exception {
        String xvCalendar =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-8-input.vcs");
        XVCalendarParser parser =
                new XVCalendarParser(new ByteArrayInputStream(xvCalendar.getBytes()));
        VCalendar vc = parser.XVCalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, true);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);

        xvCalendar = writer.toString(vc);
        IOTools.writeFile(xvCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-8-output.vcs");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-8-output.vcs");

        assertEquals(expectedResult.replaceAll("\\r", ""), xvCalendar.replaceAll("\\r", ""));
    }

    public void test_VCalendarWithFloatingTimezoneBackAndForth() throws Exception {
        String xvCalendar =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-9-input.vcs");
        XVCalendarParser parser =
                new XVCalendarParser(new ByteArrayInputStream(xvCalendar.getBytes()));
        VCalendar vc = parser.XVCalendar();
        VCalendarConverter converter =
                new VCalendarConverter(null, VCalendarConverter.CHARSET_UTF8, false);
        Calendar c = converter.vcalendar2calendar(vc);
        vc = converter.calendar2vcalendar(c, true);
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);

        xvCalendar = writer.toString(vc);
        IOTools.writeFile(xvCalendar,
                "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-9-output.vcs");
        String expectedResult =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-9-output.vcs");

        assertEquals(expectedResult.replaceAll("\\r", ""), xvCalendar.replaceAll("\\r", ""));
    }

    public void test_ICalendarToVCalendarRecurrence1() throws Exception {

        String iCal = "BEGIN:VCALENDAR\n" +
                      "PRODID:BlueTie Calendar - www.bluetie.com\n" +
                      "VERSION:2.0\n" +
                      "METHOD:PUBLISH\n" +
                      "BEGIN:VTIMEZONE\n" +
                      "TZID:/softwarestudio.org/Tzfile/America/New_York\n" +
                      "X-LIC-LOCATION:America/New_York\n" +
                      "BEGIN:STANDARD\n" +
                      "TZNAME:EST\n" +
                      "DTSTART:19701101T010000\n" +
                      "RRULE:FREQ=YEARLY;BYDAY=1SU;BYMONTH=11\n" +
                      "TZOFFSETFROM:-0400\n" +
                      "TZOFFSETTO:-0500\n" +                      "END:STANDARD\n" +
                      "BEGIN:DAYLIGHT\n" +
                      "TZNAME:EDT\n" +
                      "DTSTART:19700314T030000\n" +
                      "RRULE:FREQ=YEARLY;BYDAY=2SU;BYMONTH=3\n" +
                      "TZOFFSETFROM:-0500\n" +
                      "TZOFFSETTO:-0400\n" +
                      "END:DAYLIGHT\n" +
                      "END:VTIMEZONE\n" +
                      "BEGIN:VEVENT\n" +
                      "DESCRIPTION:Happy Hour occurs every Friday at 4pm starting on Friday  10/16\n" +
                      "CREATED:20091014T172803Z\n" +
                      "LOCATION:The bar\n" +
                      "LAST-MODIFIED:20091014T172803Z\n" +
                      "SUMMARY:Happy Hour - Every Friday at 4pm!\n" +
                      "DTSTART:20091016T200000Z\n" +
                      "DURATION:PT1H\n" +
                      "RRULE:FREQ=WEEKLY;BYDAY=FR\n" +
                      "CLASS:PUBLIC\n" +
                      "END:VEVENT\n" +
                      "END:VCALENDAR";

        // iCalendar (text item) to VCalendar (half-parsed Java object):
        ByteArrayInputStream buffer = new ByteArrayInputStream(iCal.getBytes());
        ICalendarParser parser = new ICalendarParser(buffer);
        VCalendar vcalendar = parser.ICalendar();

        // VCalendar (transitional Java object) to Calendar (model Java object):
        VCalendarConverter vcf =
                new VCalendarConverter(null, "UTF-8", false);
        Calendar cal = vcf.vcalendar2calendar(vcalendar);

        // Calendar (model Java object) to VCalendar (transitional Java object):
        vcalendar = vcf.calendar2vcalendar(cal, true); // text/x-vcalendar

        // VCalendar (transitional Java object) to vCalendar 1.0 (text item):
        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String vCal = writer.toString(vcalendar);

        String expectedVCal =
                      "BEGIN:VCALENDAR\r\n" +
                      "PRODID:BlueTie Calendar - www.bluetie.com\r\n" +
                      "VERSION:1.0\r\n" +
                      "METHOD:PUBLISH\r\n" +
                      "BEGIN:VEVENT\r\n" +
                      "SUMMARY:Happy Hour - Every Friday at 4pm!\r\n" +
                      "DESCRIPTION:Happy Hour occurs every Friday at 4pm starting on Friday  10/16\r\n" +
                      "LOCATION:The bar\r\n" +
                      "CLASS:PUBLIC\r\n" +
                      "DTSTART:20091016T200000Z\r\n" +
                      "DTEND:20091016T210000Z\r\n" +
                      "LAST-MODIFIED:20091014T172803Z\r\n" +
                      "RRULE:W1 FR #0\r\n" +
                      "X-FUNAMBOL-ALLDAY:0\r\n" +
                      "END:VEVENT\r\n" +
                      "END:VCALENDAR\r\n";

        assertEquals(expectedVCal, vCal);


    }
}
