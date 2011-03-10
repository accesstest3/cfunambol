/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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
 * You can event Funambol, Inc. headquarters at 643 Bair Island Road, Suite
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
package com.funambol.foundation.util;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.media.file.FileDataObjectBody;
import com.funambol.common.media.file.FileDataObjectMetadata;
import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.framework.tools.IOTools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;

/**
 * Helper for tasks with FileDataObject tests
 *
 * @version $Id: FileDataObjectHelper.java 36662 2011-02-17 18:05:07Z luigiafassina $
 */
public class FileDataObjectHelper {


    // --------------------------------------------------------------- Constants


    // ---------------------------------------------------------- Private fields
    protected final String resourceBasePath;
    protected final String localBasePath;

    // ------------------------------------------------------------ Constructors
    /**
     *
     * @param resourceBasePath Resource folder of the package, generally where
     *                         source files are placed
     * @param localBasePath Temporary path where files could be written, deleted
     *                      etc
     */
    public FileDataObjectHelper(String resourceBasePath, String localBasePath) {
        this.resourceBasePath = resourceBasePath;
        this.localBasePath = localBasePath;
    }



    // ---------------------------------------------------------- Public methods

    public File createTemporaryFileFromFileInResource(
            String resourceFileName, String temporaryFileName)
    throws IOException {

        //generate a temporary file name
        File destFile = copyFile(resourceBasePath, resourceFileName, localBasePath, temporaryFileName);

        return destFile;
    }



    /**
     * Creates a {@link FileDataObjectWrapper} from a physical file
     *
     * @param userName the username
     * @param fileName the local file name
     * @param bodyFile the source file
     * @return
     * @throws IOException
     */
    public FileDataObjectWrapper createFileDataObjectWrapper(
            String userName,
            String fileName,
            File bodyFile)
    throws IOException {
        String itemId = "0";
        int fileSysDirDepth = 8;
        String localFileName = new FileDataObjectNamingStrategy(fileSysDirDepth).createFileName(itemId);
        FileDataObjectBody body = new FileDataObjectBody();
        body.setBodyFile(bodyFile);
        FileDataObjectMetadata metadata = new FileDataObjectMetadata(fileName);
        FileDataObject fdo = new FileDataObject();
        fdo.setBody(body);
        fdo.setMetadata(metadata);

        FileDataObjectWrapper fdow =
                new FileDataObjectWrapper(itemId, userName, fdo);

        fdow.setLocalName(localFileName);

        return fdow;
    }

    /**
     * Creates a {@link FileDataObjectWrapper} from a file inside package
     * resources. The resource file is copied from it's location inside a temp
     * file.
     *
     * @param userName the username
     * @param fileName the local file name
     * @param resourceFileName the source file
     * @return
     * @throws IOException
     */
    public FileDataObjectWrapper createFileDataObjectWrapperFromResouceFile(
            String userName,
            String fileName,
            String resourceFileName)
    throws IOException {
        //copy file from resources to temp directory
        String tempFileName = getTempFileName();
        File sourceFile = createTemporaryFileFromFileInResource(resourceFileName, tempFileName);
        return createFileDataObjectWrapper(userName, fileName, sourceFile);
    }

    /**
     * Creates a {@link FileDataObjectWrapper} from a file which content came
     * from a byte array.
     * The array is saved in a temporary file, and it is assigned to the body
     * of FDOW
     *
     * @param userName
     * @param fileName
     * @param bodyContent
     * @return
     */
    public FileDataObjectWrapper createFileDataObjectWrapperFromByteArray(
            String userName,
            String fileName,
            byte[] bodyContent)
    throws IOException {

        //write
        File bodyFile = new File(localBasePath, getTempFileName());
        IOTools.writeFile(bodyContent, bodyFile);
        return createFileDataObjectWrapper(userName, fileName, bodyFile);
    }

    /**
     * Creates a {@link FileDataObjectWrapper} from a file which content came
     * from a byte array random generated.
     * The array is saved in a temporary file, and it is assigned to the body
     * of FDOW
     *
     * @param userName
     * @param fileName
     * @param fileSize
     * @return
     */
    public FileDataObjectWrapper createFileDataObjectWrapperFromRandomByteArray(
            String userName,
            String fileName,
            int fileSize)
    throws IOException {

        //create the byte array
        byte[] content = createRandomByteArray(fileSize);
        File bodyFile = new File(localBasePath, getTempFileName());
        IOTools.writeFile(content, bodyFile);
        return createFileDataObjectWrapper(userName, fileName, bodyFile);
    }





    // --------------------------------------------------------- Private methods

    /**
     * Copy source file into destination file
     * @param sourceDirName
     * @param sourceFileName
     * @param targetDirName
     * @param targetFileName
     * @return {@link File} pointer to the target file
     * @throws IOException
     */
    private File copyFile(String sourceDirName,
                          String sourceFileName,
                          String targetDirName,
                          String targetFileName)
    throws IOException {

        File sourceFile = new File(sourceDirName, sourceFileName);
        if(!sourceFile.exists() || ! sourceFile.isFile()) {
            throw new IOException("Source file '"+sourceFileName+"' doesn't exist.");
        }

        File targetDir = new File(targetDirName);
        if(!targetDir.exists()) {
            if(!targetDir.mkdirs()) {
                throw new IOException("Error creating target directory '"+targetDirName+"'.");
            }
        }

        File targetFile = new File(targetDir, targetFileName);

        InputStream inputStream = new FileInputStream(sourceFile);
        OutputStream outputStream = new FileOutputStream(targetFile,false);
        try {
            IOUtils.copy(inputStream, outputStream);
        } finally {
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }

        return targetFile;
    }


    /**
     * Generate a temporary file name
     * @return
     */
    private String getTempFileName() {
        return RandomStringUtils.randomAlphabetic(10) + ".tmp";
    }




    private byte[] createRandomByteArray(int fileSize) {
        byte[] dummyByteArray = new byte[fileSize];
//        for (int i = 0; i < fileSize; i++) {
//            dummyByteArray[i] = (byte) (fileSize - i);
//        }
        Random rand = new Random();
        rand.nextBytes(dummyByteArray);
        return dummyByteArray;
    }

}
