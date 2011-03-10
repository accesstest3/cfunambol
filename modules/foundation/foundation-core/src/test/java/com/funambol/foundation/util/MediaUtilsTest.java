/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2011 Funambol, Inc.
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
package com.funambol.foundation.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.Sanselan;

import com.funambol.common.media.file.FileDataObject;

import com.funambol.framework.tools.IOTools;

import com.funambol.foundation.exception.FileDataObjecyUtilsException;
import com.funambol.foundation.items.model.FileDataObjectWrapper;

/**
 * Test cases for MediaUtils class.
 *
 * @version $Id: MediaUtilsTest.java 36684 2011-02-24 16:38:19Z ubertu $
 */
public class MediaUtilsTest extends TestCase {

    // ------------------------------------------------------------ Private data
    private MediaUtils mediaUtils = null;

    private static final String ROOT_PATH = 
        "target/test-classes/data/com/funambol/foundation/util/";
    private static final String EXT_PATH = ROOT_PATH + "ext/";
    
    private static final String PNG_IMAGE_PATH = ROOT_PATH + "simple.png";
    private static final String GIF_IMAGE_PATH = ROOT_PATH + "simple.gif";
    private static final String JPG_IMAGE_PATH = ROOT_PATH + "simple.jpg";
    private static final String BMP_IMAGE_PATH = ROOT_PATH + "simple.bmp";
    private static final String PNG_FILE_PATH = ROOT_PATH + "simple2.png";
    private static final String JPG_IMAGE_PATH_2 = ROOT_PATH + "photo2.jpg";
    private static final String PNG_FILE_PATH_90 =
        ROOT_PATH + "simple_rotate90.png";
    private static final String PNG_FILE_PATH_180 =
        ROOT_PATH + "simple_rotate180.png";
    private static final String PNG_FILE_PATH_270 =
        ROOT_PATH + "simple_rotate270.png";
    private static final String TRANSPARENT_PNG_FILE_PATH =
        ROOT_PATH + "transparent.png";
    private static final String TRANSPARENT_PNG_FILE_PATH_90 =
        ROOT_PATH + "transparent_rotate90.png";
    private static final String TRANSPARENT_PNG_FILE_PATH_90_TEMP =
        ROOT_PATH + "transparent_rotate90_temp.png";
    private static final String JPG_IMAGE_PATH_90 =
        ROOT_PATH + "photo_rotated90.jpg";
    private static final String JPG_IMAGE_PATH_180 =
        ROOT_PATH + "photo_rotated180.jpg";
    private static final String JPG_IMAGE_PATH_270 =
        ROOT_PATH + "photo_rotated270.jpg";

    private static BufferedImage bufImage;

    // ------------------------------------------------------------ Constructors
    public MediaUtilsTest(String testName) {
        super(testName);
        
        mediaUtils = new MediaUtils();
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        File ext = new File(EXT_PATH);
        if (!ext.exists()) {
            if (!ext.mkdirs()) {
                fail("Error during creation of EXT folder");
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (!IOTools.deleteDirectory(EXT_PATH, null)) {
            fail("Error during deletion of EXT folder");
        }
    }

    // -------------------------------------------------------------- Test cases
    public void testCreateThumbnails_ErrorCase() throws Exception {

        FileDataObjectWrapper fdow = null;
        String userId = "";
        String thumbnailFolderPath = "";
        String[] thumbnailsSize = null;
        double thumbnailThreshold = 0.0;

        FileDataObject fdo = new FileDataObject();
        fdo.setName("simple.jpg");
        fdo.setBodyFile(new File(JPG_IMAGE_PATH));

        // FileDataObjectWrapper is null
        try {
            mediaUtils.createThumbnails(fdow,
                                      userId,
                                      thumbnailFolderPath,
                                      thumbnailsSize,
                                      thumbnailThreshold);

            fail("A FileDataObjecyUtilsException was expected!");
        } catch(Exception e) {
            assertTrue("A different Exception has been caught" , 
                       e instanceof FileDataObjecyUtilsException);
            assertEquals("The FileDataObjectWrapper cannot be null.",
                         e.getMessage());
        }

        // the array of thumbnail size is null
        fdow = new FileDataObjectWrapper("1", "guest", fdo);
        try {
            mediaUtils.createThumbnails(fdow,
                                      userId,
                                      thumbnailFolderPath,
                                      thumbnailsSize,
                                      thumbnailThreshold);

            fail("A FileDataObjecyUtilsException was expected!");
        } catch(Exception e) {
            assertTrue("A different Exception has been caught" ,
                       e instanceof FileDataObjecyUtilsException);
            assertEquals("The thumbnails size must be set.",
                         e.getMessage());
        }

        // the file extension is not supported
        fdo.setName("simple.bmp");
        fdo.setBodyFile(new File(BMP_IMAGE_PATH));
        fdow = new FileDataObjectWrapper("1", "guest", fdo);
        thumbnailsSize = new String[]{"200", "200x175"};
        try {
            mediaUtils.createThumbnails(fdow,
                                      userId,
                                      thumbnailFolderPath,
                                      thumbnailsSize,
                                      thumbnailThreshold);

            fail("A FileDataObjecyUtilsException was expected!");
        } catch(Exception e) {
            assertTrue("A different Exception has been caught" ,
                       e instanceof FileDataObjecyUtilsException);
            assertEquals("The file extension or the format is not supported.",
                         e.getMessage());
        }
    }

    public void testCreateThumbnails() throws Exception {

        FileDataObjectWrapper fdow = null;
        String userId = "guest";
        String thumbnailFolderPath = EXT_PATH;
        File pictureFile = new File(JPG_IMAGE_PATH);
        String[] thumbnailsSize = new String[]{"200", "200x175"};
        double thumbnailThreshold = 1;

        FileDataObject fdo = new FileDataObject();
        fdo.setName("simple.jpg");
        fdo.setBodyFile(pictureFile);

        Map<String, File> result = null;

        fdow = new FileDataObjectWrapper("1", "guest", fdo);
        result = mediaUtils.createThumbnails(fdow,
                                           userId,
                                           thumbnailFolderPath,
                                           thumbnailsSize,
                                           thumbnailThreshold);

        assertEquals(8, result.size());

        Set<Entry<String, File>> entries = result.entrySet();
        Iterator<Entry<String, File>> it = entries.iterator();
        while(it.hasNext()) {
            Entry<String, File> entry = it.next();
            assertTrue("The file '"+entry.getValue().getName() +
                       "' does not exist", entry.getValue().exists());
        }

        //
        // the picture is smaller than required size increased by % tolerance
        // so the thumbnails will be created using the original picture
        //
        thumbnailsSize = new String[]{"250", "250x175"};
        pictureFile = new File(GIF_IMAGE_PATH);
        fdo = new FileDataObject();
        fdo.setName("simple.gif");
        fdo.setBodyFile(pictureFile);
        fdow = new FileDataObjectWrapper("1", "guest", fdo);
        
        thumbnailThreshold = 20;
        result = mediaUtils.createThumbnails(fdow,
                                           userId,
                                           thumbnailFolderPath,
                                           thumbnailsSize,
                                           thumbnailThreshold);

        assertEquals(8, result.size());

        entries = result.entrySet();
        it = entries.iterator();
        while(it.hasNext()) {
            Entry<String, File> entry = it.next();
            assertTrue("The file '"+entry.getValue().getName() +
                       "' does not exist", entry.getValue().exists());
        }

    }

    public void testHasASupportedFormat() throws Throwable {

        // png
        File image = new File(PNG_IMAGE_PATH);
        ImageFormat format = Sanselan.guessFormat(image);
        Boolean result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedFormat",
            new Class[]{ImageFormat.class},
            new Object[]{format});
        assertTrue(result);

        // gif
        image = new File(GIF_IMAGE_PATH);
        format = Sanselan.guessFormat(image);
        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedFormat",
            new Class[]{ImageFormat.class},
            new Object[]{format});
        assertTrue(result);

        // jpg
        image = new File(JPG_IMAGE_PATH);
        format = Sanselan.guessFormat(image);
        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedFormat",
            new Class[]{ImageFormat.class},
            new Object[]{format});
        assertTrue(result);

        // bmp is not supported
        image = new File(BMP_IMAGE_PATH);
        format = Sanselan.guessFormat(image);
        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedFormat",
            new Class[]{ImageFormat.class},
            new Object[]{format});
        assertFalse(result);

        // image null
        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedFormat",
            new Class[]{ImageFormat.class},
            new Object[]{null});
        assertFalse(result);
    }

    public void testHasASupportedExtension() throws Throwable {

        Boolean result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedExtension",
            new Class[]{String.class},
            new Object[]{"image.jpg"});
        assertTrue(result);

        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedExtension",
            new Class[]{String.class},
            new Object[]{"image.jpeg"});
        assertTrue(result);

        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedExtension",
            new Class[]{String.class},
            new Object[]{"image.png"});
        assertTrue(result);

        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedExtension",
            new Class[]{String.class},
            new Object[]{"image.gif"});
        assertTrue(result);

        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedExtension",
            new Class[]{String.class},
            new Object[]{"image.jpe"});
        assertTrue(result);

        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedExtension",
            new Class[]{String.class},
            new Object[]{"image.jfif"});
        assertTrue(result);

        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedExtension",
            new Class[]{String.class},
            new Object[]{"image.jif"});
        assertTrue(result);

        // extension not supported
        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedExtension",
            new Class[]{String.class},
            new Object[]{"image.bmp"});
        assertFalse(result);

        // filename null
        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "hasASupportedExtension",
            new Class[]{String.class},
            new Object[]{null});
        assertFalse(result);
    }

    public void testIsAValidPicture() throws Throwable {

        FileDataObject fdo = new FileDataObject();
        fdo.setName("simple.jpg");
        fdo.setBodyFile(new File(JPG_IMAGE_PATH));
        Boolean result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "isAValidPicture",
            new Class[]{FileDataObject.class},
            new Object[]{fdo});
        assertTrue(result);

        // extension not supported
        fdo = new FileDataObject();
        fdo.setName("simple.bmp");
        fdo.setBodyFile(new File(BMP_IMAGE_PATH));
        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "isAValidPicture",
            new Class[]{FileDataObject.class},
            new Object[]{fdo});
        assertFalse(result);

        // image format not supported
        fdo = new FileDataObject();
        fdo.setName("simple.gif");
        fdo.setBodyFile(new File(BMP_IMAGE_PATH));
        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "isAValidPicture",
            new Class[]{FileDataObject.class},
            new Object[]{fdo});
        assertFalse(result);

        // image null
        fdo = new FileDataObject();
        fdo.setName("simple.jpeg");
        fdo.setBodyFile(null);
        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "isAValidPicture",
            new Class[]{FileDataObject.class},
            new Object[]{fdo});
        assertFalse(result);
    }

    public void testCreateThumbnail() throws Throwable {

        File imageFile = new File(JPG_IMAGE_PATH);
        File thumbFile = new File(EXT_PATH + "176_11111111.jpg");
        int thumbX = 176;
        int thumbY = 176;
        String imageName = JPG_IMAGE_PATH;
        double tolerance = 20;

        Boolean result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "createThumbnail",
            new Class[]{File.class,
                        File.class,
                        int.class,
                        int.class,
                        String.class,
                        double.class},
            new Object[]{imageFile,
                         thumbFile,
                         thumbX,
                         thumbY,
                         imageName,
                         tolerance});
        assertTrue(result);

        // picture is smaller than required size increased by % tolerance
        imageFile = new File(GIF_IMAGE_PATH);
        thumbFile = new File(EXT_PATH + "176_12121212.jpg");
        imageName = GIF_IMAGE_PATH;

        result = (Boolean) PrivateAccessor.invoke(
            mediaUtils,
            "createThumbnail",
            new Class[]{File.class,
                        File.class,
                        int.class,
                        int.class,
                        String.class,
                        double.class},
            new Object[]{imageFile,
                         thumbFile,
                         thumbX,
                         thumbY,
                         imageName,
                         tolerance});
        assertFalse(result);
    }

    public void testCreateThumbnailName() throws Throwable {

        String size = "200";
        String prefix = size;
        String suffix = ".jpg";
        String result = (String) PrivateAccessor.invoke(
            mediaUtils,
            "createThumbnailName",
            new Class[]{String.class},
            new Object[]{size});
        assertTrue(result.startsWith(prefix));
        assertTrue(result.endsWith(suffix));

        size = "175*150";
        prefix = size;
        result = (String) PrivateAccessor.invoke(
            mediaUtils,
            "createThumbnailName",
            new Class[]{String.class},
            new Object[]{size});
        assertTrue(result.startsWith(prefix));
        assertTrue(result.endsWith(suffix));

        size = "";
        prefix = size;
        result = (String) PrivateAccessor.invoke(
            mediaUtils,
            "createThumbnailName",
            new Class[]{String.class},
            new Object[]{size});
        assertTrue(result.startsWith(prefix));
        assertTrue(result.endsWith(suffix));
    }

    public void testCreateExtFolderIfNeeded() throws Throwable {

        String folderPath = ROOT_PATH + "tmp-extfolder";
        File extFolder = new File(folderPath);
        if (!IOTools.deleteDirectory(extFolder, null)) {
            fail("Error during deletion of a tmp EXT folder");
        }
        assertFalse(extFolder.exists());

        PrivateAccessor.invoke(
            mediaUtils,
            "createExtFolderIfNeeded",
            new Class[]{String.class},
            new Object[]{folderPath});
        assertTrue(extFolder.exists());

        folderPath = EXT_PATH;
        PrivateAccessor.invoke(
            mediaUtils,
            "createExtFolderIfNeeded",
            new Class[]{String.class},
            new Object[]{folderPath});
        extFolder = new File(folderPath);
        assertTrue(extFolder.exists());
    }

    public void testSetFDODates() throws Throwable {

        FileDataObject fdo = new FileDataObject();
        String createDate = null;
        String modifyDate = null;

        MediaUtils.setFDODates(fdo, createDate, modifyDate);
        assertNull(fdo.getCreated());
        assertNull(fdo.getModified());

        createDate = "20110726T142550Z";
        modifyDate = null;

        MediaUtils.setFDODates(fdo, createDate, modifyDate);
        assertNotNull(fdo.getCreated());
        assertNotNull(fdo.getModified());
        assertEquals("Create and Modify date should be equal",
                     fdo.getCreated(), fdo.getModified());

        fdo.setCreated(null);
        fdo.setModified(null);

        createDate = null;
        modifyDate = "20110526T142550Z";

        MediaUtils.setFDODates(fdo, createDate, modifyDate);
        assertNotNull(fdo.getCreated());
        assertNotNull(fdo.getModified());
        assertEquals("Create and Modify date should be equal",
                     fdo.getCreated(), fdo.getModified());

        fdo.setCreated(null);
        fdo.setModified(null);

        createDate = null;
        modifyDate = null;

        MediaUtils.setFDODates(fdo, createDate, modifyDate);
        assertNull(fdo.getCreated());
        assertNull(fdo.getModified());

        createDate = "20110525T142550Z";
        modifyDate = "20110626T142550Z";

        MediaUtils.setFDODates(fdo, createDate, modifyDate);
        assertNotNull(fdo.getCreated());
        assertNotNull(fdo.getModified());
        assertEquals("Wrong create date returned",
                     createDate, fdo.getCreated());
        assertEquals("Wrong modify date returned",
                     modifyDate, fdo.getModified());

        createDate = null;
        modifyDate = "20110626T142550Z";
        fdo.setCreated("20110525T142550Z");
        fdo.setModified(null);

        MediaUtils.setFDODates(fdo, createDate, modifyDate);
        assertNotNull(fdo.getCreated());
        assertNotNull(fdo.getModified());
        assertEquals("Wrong create date returned",
                     "20110525T142550Z", fdo.getCreated());
        assertEquals("Wrong modify date returned",
                     modifyDate, fdo.getModified());

        createDate = "20110725T142550Z";
        modifyDate = null;
        fdo.setCreated(null);
        fdo.setModified("20110626T142550Z");

        MediaUtils.setFDODates(fdo, createDate, modifyDate);
        assertNotNull(fdo.getCreated());
        assertNotNull(fdo.getModified());
        assertEquals("Wrong create date returned",
                     createDate, fdo.getCreated());
        assertEquals("Wrong modify date returned",
                     "20110626T142550Z", fdo.getModified());

    }

    public void testHandleEXIF_JPG() throws Throwable {

        FileDataObject fdo = new FileDataObject();
        fdo.setName("simple.jpg");
        fdo.setBodyFile(new File(JPG_IMAGE_PATH));
        FileDataObjectWrapper fdow =
            new FileDataObjectWrapper("10", "guest", fdo);

        assertNull("Initial create date set", fdo.getCreated());
        assertNull("Initial modify date set", fdo.getModified());

        PrivateAccessor.invoke(mediaUtils,
                               "handleEXIF",
                               new Class[]{FileDataObjectWrapper.class},
                               new Object[]{fdow});
        FileDataObject expFDO = fdow.getFileDataObject();
        Map<String, String> expProps = expFDO.getProperties();
        assertFalse("No properties set", expProps.isEmpty());
        assertTrue("Map does not contain EXIF property", 
                   expProps.containsKey(MediaUtils.EXIF_PROPERTY_NAME));
        String result = expProps.get(MediaUtils.EXIF_PROPERTY_NAME);
        String expected =
            "{\"Make\":\"'Panasonic'\",\"Model\":\"'DMC-FX3'\",\"Orientation\":\"0\",\"XResolution\":\"72\",";

        assertTrue("Wrong exif value returned", result.startsWith(expected));
        assertFalse("Create date not set", expFDO.getCreated().isEmpty());
        assertFalse("Modify date not set", expFDO.getModified().isEmpty());
    }

    public void testHandleEXIF_PNG() throws Throwable {

        FileDataObject fdo = new FileDataObject();
        fdo.setName("simple.png");
        fdo.setBodyFile(new File(PNG_IMAGE_PATH));
        FileDataObjectWrapper fdow =
            new FileDataObjectWrapper("11", "guest", fdo);

        assertNull("Initial created date set", fdo.getCreated());
        assertNull("Initial modified date set", fdo.getModified());

        PrivateAccessor.invoke(mediaUtils,
                               "handleEXIF",
                               new Class[]{FileDataObjectWrapper.class},
                               new Object[]{fdow});
        FileDataObject expFDO = fdow.getFileDataObject();
        Map<String, String> expProps = expFDO.getProperties();
        assertTrue("Properties set", expProps.containsKey(MediaUtils.ROTATION_PROPERTY_NAME));
        expProps.remove(MediaUtils.ROTATION_PROPERTY_NAME);
        assertTrue("Empty properties set", expProps.isEmpty());

        assertNull("Created date should be null", expFDO.getCreated());
        assertNull("Modified date should be null", expFDO.getModified());
    }

    public void testRotateJpgKeepsSize() throws IOException {

        File original = new File(JPG_IMAGE_PATH_2);
        byte[] bytes = null;
        try {
            bytes = MediaUtils.rotateImage(original, 90);
        } catch (FileDataObjecyUtilsException ex) {
            fail("image not rotated");
        }


        File rotated = new File(ROOT_PATH + "temp.jpg");
        FileOutputStream fos = new FileOutputStream(rotated);
        fos.write(bytes);
        fos.close();

        // check size is similar
        long diff = Math.abs(original.length() - rotated.length());
        double ratio = 1.0*diff/original.length();
        assertTrue("After JPG rotation size changed more than 5%: " +ratio, ratio < 5.0/100);
        rotated.delete();

    }

    public void testRotateImageInvalidValue() throws IOException {
        boolean happy = false;
        try {
            getImageAfterRotation(PNG_FILE_PATH, 30);
        } catch (FileDataObjecyUtilsException ex) {
            // is what we want!
            happy = true;
        }
        
        assertTrue("exception has not been trown when passing invalid rotation amount", happy);
    }

    public void testRotateImage0() throws IOException {

        bufImage = ImageIO.read(new File(PNG_FILE_PATH));
        BufferedImage rotatedImage = null;
        try {
            rotatedImage = getImageAfterRotation(PNG_FILE_PATH, 0);
        } catch (FileDataObjecyUtilsException ex) {
            fail("image not rotated");
        }
        assertTrue("rotation by 0 deg result in a different image from the starting one", 
                   areImagesEquals(bufImage, rotatedImage));
        bufImage = null;
    }

    public void testRotateJpgImage90() throws IOException {
        BufferedImage rotatedImage = null;
        try {
            rotatedImage = getImageAfterRotation(JPG_IMAGE_PATH_2, 90);
        } catch (FileDataObjecyUtilsException ex) {
            fail("image not rotated");
        }
        bufImage = ImageIO.read(new File(JPG_IMAGE_PATH_90));
        assertTrue(areImagesEquals(bufImage, rotatedImage));
        bufImage = null;
    }

    public void testRotateJpgImage180() throws IOException {
        BufferedImage rotatedImage = null;
        try {
            rotatedImage = getImageAfterRotation(JPG_IMAGE_PATH_2, 180);
        } catch (FileDataObjecyUtilsException ex) {
            fail("image not rotated");
        }
        bufImage = ImageIO.read(new File(JPG_IMAGE_PATH_180));
        assertTrue(areImagesEquals(bufImage, rotatedImage));
        bufImage = null;
    }

    public void testRotateJpgImage270() throws IOException {
        BufferedImage rotatedImage = null;
        try {
            rotatedImage = getImageAfterRotation(JPG_IMAGE_PATH_2, 270);
        } catch (FileDataObjecyUtilsException ex) {
            fail("image not rotated");
        }
        bufImage = ImageIO.read(new File(JPG_IMAGE_PATH_270));
        assertTrue(areImagesEquals(bufImage, rotatedImage));
        bufImage = null;
    }

    public void testRotatePngImage90() throws IOException {
        BufferedImage rotatedImage = null;
        try {
            rotatedImage = getImageAfterRotation(PNG_FILE_PATH, 90);
        } catch (FileDataObjecyUtilsException ex) {
            fail("image not rotated");
        }
        bufImage = ImageIO.read(new File(PNG_FILE_PATH_90));
        assertTrue(areImagesEquals(bufImage, rotatedImage));
        bufImage = null;
    }

    public void testRotatePngImage180() throws IOException {
        BufferedImage rotatedImage = null;
        try {
            rotatedImage = getImageAfterRotation(PNG_FILE_PATH, 180);
        } catch (FileDataObjecyUtilsException ex) {
            fail("image not rotated");
        }
        bufImage = ImageIO.read(new File(PNG_FILE_PATH_180));
        assertTrue(areImagesEquals(bufImage, rotatedImage));
        bufImage = null;
    }

    public void testRotatePngImage270() throws IOException {
        BufferedImage rotatedImage = null;
        try {
            rotatedImage = getImageAfterRotation(PNG_FILE_PATH, 270);
        } catch (FileDataObjecyUtilsException ex) {
            fail("image not rotated");
        }
        bufImage = ImageIO.read(new File(PNG_FILE_PATH_270));
        assertTrue(areImagesEquals(bufImage, rotatedImage));
        bufImage = null;
    }

    public void testRotateTransparentPng() throws Exception {
        File temp = new File(TRANSPARENT_PNG_FILE_PATH_90_TEMP);

        BufferedImage rotatedImage = getImageAfterRotation(TRANSPARENT_PNG_FILE_PATH, 90);
        saveImage(rotatedImage, TRANSPARENT_PNG_FILE_PATH_90_TEMP, "png");
        BufferedImage b2 = ImageIO.read(temp);
        bufImage = ImageIO.read(new File(TRANSPARENT_PNG_FILE_PATH_90));
        try {
            assertTrue(areImagesEquals(bufImage, b2));

        } finally {
            temp.delete();
        }

        bufImage = null;
    }

    // --------------------------------------------------------- Private methods
    private BufferedImage getImageAfterRotation(String filePath, int degree)
    throws IOException, FileDataObjecyUtilsException {

        ByteArrayInputStream is = 
            new ByteArrayInputStream(MediaUtils.rotateImage(new File(filePath), degree));
        BufferedImage rotatedImage = ImageIO.read(is);
        return rotatedImage;

    }

    private boolean areImagesEquals(BufferedImage image1, BufferedImage image2) {

        if (image1.getWidth() != image2.getWidth() ||
            image1.getHeight() != image2.getHeight()) {
            return false;
        }

        return true;

    }

    private void saveImage(BufferedImage image1, String filename, String imageFrmat)
    throws FileNotFoundException, IOException {

        if ("jpg".equals(imageFrmat) || "jpeg".equals(imageFrmat)) {
            Iterator iter = ImageIO.getImageWritersByFormatName(imageFrmat);
            ImageWriter writer = (ImageWriter) iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(1);
            File file = new File(filename);
            FileImageOutputStream output = new FileImageOutputStream(file);
            writer.setOutput(output);
            IIOImage image = new IIOImage(image1, null, null);
            writer.write(null, image, iwp);
            writer.dispose();
        } else {
            // write image data into body
            ImageIO.write(image1, imageFrmat, new File(filename));
        }
    }
}
