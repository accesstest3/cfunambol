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
package com.funambol.tools.test;

import com.funambol.framework.tools.IOTools;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.funambol.framework.core.Sync4jException;
import com.funambol.framework.tools.WBXMLTools;

/**
 * <p>An utility to convert XML files to WBXML and vice versa.</p>
 *
 * @version $Id: WBXMLConverter.java,v 1.2 2007-11-28 10:52:31 nichele Exp $
 */

public class WBXMLConverter {
    private static boolean xmlToWbxml = false;
    private static boolean wbxmlToXml = false;

    public WBXMLConverter() {
    }

    public static void main(String args[])
    throws Exception {
        try {
            checkArguments(args);
        } catch (IllegalArgumentException e) {
            System.out.println("usage:" );
            System.out.println(WBXMLConverter.class.getName() + " [-xml|-wbxml] <xml dir>");
            return;
        }

        convert((args.length == 1) ? args[0] : args[1]);
    }

    private static void checkArguments(String[] args)
    throws IllegalArgumentException {
        switch (args.length) {
            case 0:
                throw new IllegalArgumentException();

            case 1:
                wbxmlToXml = false;
                xmlToWbxml = true ;
                break;

            case 2:
                wbxmlToXml = "-wbxml".equalsIgnoreCase(args[0]);
                xmlToWbxml = "-xml".equalsIgnoreCase(args[0]);

                if (!wbxmlToXml && !xmlToWbxml) {
                    throw new IllegalArgumentException();
                }
                break;

            default:
                throw new IllegalArgumentException();
        }
    }

    private static void convert(String xmlPath)
    throws IOException, Sync4jException {
        System.out.println("converting xmlPath:" + xmlPath);

        File xmlDir = new File(xmlPath);
        List filesToConvert = getFilesInDir(xmlDir);
        for (Iterator it=filesToConvert.iterator();it.hasNext();){
            try {
                File file = (File)it.next();
                System.out.println("file:" + file.getAbsolutePath());

                if (file.getName().equalsIgnoreCase("build.xml") ||
                    file.getName().equalsIgnoreCase("DBOfficer.xml")) {
                    continue;
                }

                if (xmlToWbxml) {
                    xmlToWbxml(file);
                }
                if (wbxmlToXml) {
                    wbxmlToXml(file);
                }

            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private static void xmlToWbxml(File file)
    throws IOException, Sync4jException {
        String xml = IOTools.readFileString(file);

        String verDTD = getVersionDTD(xml);

        byte[] wbxml = WBXMLTools.toWBXML(xml, verDTD);

        String xmlFileName = file.getAbsolutePath();
        String wbxmlFileName = xmlFileName.substring(0, xmlFileName.lastIndexOf('.'));
        wbxmlFileName = wbxmlFileName + ".wbxml";

        //now write the wbxml to a file
        System.out.println("Writing to: " + wbxmlFileName);
        IOTools.writeFile(wbxml, wbxmlFileName);
    }

    private static void wbxmlToXml(File file)
    throws IOException, Sync4jException {
        byte[] wbxml = IOTools.readFileBytes(file);

        String xml = WBXMLTools.wbxmlToXml(wbxml);

        String wbxmlFileName = file.getAbsolutePath();
        String xmlFileName = wbxmlFileName.substring(0, wbxmlFileName.lastIndexOf('.'));
        xmlFileName = xmlFileName + ".xml";

        //now write the xml to a file
        System.out.println("Writing to: " + xmlFileName);
        IOTools.writeFile(xml, xmlFileName);
    }

    private static List getFilesInDir(File dir){
        List files = new ArrayList();
        File[] dirFiles = dir.listFiles();
        for (int i=0; ((dirFiles != null) && (i<dirFiles.length)); i++){
            File file = dirFiles[i];
            if (file.isFile()){
                if (xmlToWbxml) {
                    if (file.getName().toUpperCase().endsWith(".XML")){
                        files.add(file);
                    }
                } else {
                    if (file.getName().toUpperCase().endsWith(".WBXML")){
                        files.add(file);
                    }
                }
            } else {
                files.addAll(getFilesInDir(file));
            }
        }
        return files;
    }


    public static String readWbxmlAsXml(File wbxmlFile)
    throws IOException, Sync4jException {
        FileInputStream fis = new FileInputStream(wbxmlFile);
        byte[] byteArray = new byte[(int)wbxmlFile.length()];
        fis.read(byteArray);
        fis.close();

        return WBXMLTools.wbxmlToXml(byteArray);
    }

    /**
     * Returns the DTD version if the input string is a SyncML message into xml
     * format.
     *
     * @param xml the xml message
     * @return the DTD version
     */
    private static String getVersionDTD(String xml) {
        String verDTD = null;
        int start = xml.indexOf("<VerDTD>");
        if (start != -1) {
            int end = xml.indexOf("</VerDTD>", start);
            verDTD = xml.substring(start + 8, end);
        }
        return verDTD;
    }
}
