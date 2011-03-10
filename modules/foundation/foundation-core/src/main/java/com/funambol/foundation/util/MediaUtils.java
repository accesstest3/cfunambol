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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.lang.StringUtils;

import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

import mediautil.image.jpeg.LLJTran;
import mediautil.image.jpeg.LLJTranException;

import net.sf.json.JSONObject;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.pim.utility.TimeUtils;

import com.funambol.framework.tools.IOTools;

import com.funambol.foundation.exception.FileDataObjecyUtilsException;
import com.funambol.foundation.items.model.FileDataObjectWrapper;

/**
 * This class contains usefull methods to use for working with FileDataObject
 * objects type, in particular when they represent media objects (picture,
 * video and so on).
 *
 * @version $Id: MediaUtils.java 36684 2011-02-24 16:38:19Z ubertu $
 */
public class MediaUtils {

    // --------------------------------------------------------------- Constants
    // JPEG: .jpg, .jpeg, .jpe, .jif, .jfif
    // PNG:  .png
    // GIF:  .gif
    private final static String[] SUPPORTED_PICTURE_EXTENSIONS =
        {".jpg", ".jpeg", ".png", ".gif", ".jpe", ".jfif", ".jif"};

    public final static String EXIF_PROPERTY_NAME = "exif";
    public final static String ROTATION_PROPERTY_NAME = "rotation";

    // ---------------------------------------------------------- Public methods
    /**
     * Returns an array of thumbnail keys created on file system starting from
     * the given picture.
     *
     * @param fdow the FDO wrapper
     * @param userId the user identifier
     * @param thumbnailFolderPath the absolute path where the thumbnails should
     * be saved
     * @param thumbnailsSize an array of valid size for thumbnails. It can be
     * either a number, for example 200, or 2D, for example 200x150.
     * Better to respect JPEG Minimum Coded Unit (MCU) and choose number that
     * can be diveded by 8.
     * @param thumbnailThreshold the thumbnail size threshold.
     * It is a percentage tolerance in the minimum image size used to create a
     * thumbnail. The default is 20, so for example a 400x400 image will not be
     * rescaled for 500x375 thumbnail size. Set it to 0 means that the thumbnail
     * is always generated if maximum size is larger than thumbnail size.
     * @return a map of thumbnail key and the corresponding thumbnail file
     * @throws FileDataObjecyUtilsException if an error occurs
     */
    public Map<String, File> createThumbnails(FileDataObjectWrapper fdow,
                                              String userId,
                                              String thumbnailFolderPath,
                                              String[] thumbnailsSize,
                                              double thumbnailThreshold)
    throws FileDataObjecyUtilsException {

        if (fdow == null) {
            throw new FileDataObjecyUtilsException("The FileDataObjectWrapper cannot be null.");
        }

        if (thumbnailsSize == null) {
            throw new FileDataObjecyUtilsException("The thumbnails size must be set.");
        }

        FileDataObject fdo = fdow.getFileDataObject();
        //
        // even if it is not possible to create a FDOWrapper with a FDO null
        // it's better to check it once
        //
        if (fdo == null) {
            throw new FileDataObjecyUtilsException("The FileDataObject cannot be null.");
        }

        // checks if file extention and format are supported
        if (!isAValidPicture(fdo)) {
            throw new FileDataObjecyUtilsException("The file extension or the format is not supported.");
        }

        createExtFolderIfNeeded(thumbnailFolderPath);

        int thumbnailsSizeLength = thumbnailsSize.length;

        Map<String, File> thumbnails = new HashMap<String, File>();

        for (int j = 0; j < thumbnailsSizeLength; j++) {

            String thumbnailFileName = createThumbnailName(thumbnailsSize[j]);

            File thumbnailFile = new File(thumbnailFolderPath, thumbnailFileName);

            int x = 0;
            int y = 0;

            //Support both 200x300 and 200 formats in conf
            if (thumbnailsSize[j].toLowerCase().indexOf('x') > 0) {
                //200x300
                x = Integer.parseInt(thumbnailsSize[j].substring(0, thumbnailsSize[j].indexOf('x')));
                y = Integer.parseInt(thumbnailsSize[j].substring(thumbnailsSize[j].indexOf('x') + 1));
            } else {
                //200
                x = Integer.parseInt(thumbnailsSize[j]);
                y = Integer.parseInt(thumbnailsSize[j]);
            }

            boolean isCreated = false;
            try {
                isCreated =
                    createThumbnail(fdo.getBodyFile(), thumbnailFile, x, y, fdo.getName(), thumbnailThreshold);
            } catch (IOException e) {
                //
                // If the thumbnail has not been created for some reasons,
                // the original FDO body will be used as thumbnail.
                // In this way, we will have always all the required
                // thumbnails even if they should contain the original file
                // instead of a resized file.
                //
            }

            if (!isCreated) {
                forceThumbnailCreation(thumbnailFile, fdo.getBodyFile());
                thumbnails.put(thumbnailFileName, thumbnailFile);
            } else {
                thumbnails.put(thumbnailFileName, thumbnailFile);
            }
            //create rotated thumbnails and add it on thumbnails array
            for (int degree=90; degree<360; degree+=90) {
                String rotatedThumbnailName =
                    createRotatedThumbnailName(thumbnailsSize[j], degree);
                File rotatedThumbnail =
                    new File(thumbnailFolderPath, rotatedThumbnailName);
                
                // rotate thumbnail according to degree
                byte[] rotatedBA = rotateImage(thumbnailFile, degree);
                try {
                    ImageIO.write(
                        ImageIO.read(new ByteArrayInputStream(rotatedBA)),
                        ImageFormat.IMAGE_FORMAT_JPEG.name,
                        rotatedThumbnail);
                } catch (IOException ex) {
                    throw new FileDataObjecyUtilsException("Error creating the rotated thumbnail, file can't be written", ex);
                }
                thumbnails.put(rotatedThumbnailName,rotatedThumbnail);
            }

        }

        return thumbnails;
    }

    /**
     * Retrieves the EXIF data from the FDO body file and set it in the
     * properties map.
     * The EXIF data are supported by JPEG, TIFF Rev. 6.0, and RIFF WAV file
     * formats. It is not supported in JPEG 2000, PNG, or GIF.
     *
     * @param fdow the wrapper containing the file to retrieve the EXIF data
     * @throws FileDataObjecyUtilsException if an error occurs
     */
    public void handleEXIF(FileDataObjectWrapper fdow)
    throws FileDataObjecyUtilsException {

        FileDataObject fdo = fdow.getFileDataObject();
        Map<String, String> properties = fdo.getProperties();
        JSONObject exifData = new JSONObject();
        File bodyFile = fdo.getBodyFile();
        String degree = "0";

        try {

            ArrayList exifItems = null;
            IImageMetadata metadata = Sanselan.getMetadata(bodyFile);

            if (metadata!= null && metadata instanceof JpegImageMetadata) {
                JpegImageMetadata jpegMetadata = (JpegImageMetadata)metadata;

                exifItems = jpegMetadata.getItems();
                int size = exifItems.size();
                Object exifItem = null;
                String tagName = null;
                String tagValueDesc = null;
                String createDateTag = TiffConstants.EXIF_TAG_CREATE_DATE.name;
                String modifyDateTag = TiffConstants.EXIF_TAG_MODIFY_DATE.name;
                String orientationTag = TiffConstants.EXIF_TAG_ORIENTATION.name;
                String createDate = null;
                String modifyDate = null;
                for (int i=0; i<size; i++) {
                    exifItem = exifItems.get(i);

                    if (exifItem instanceof TiffImageMetadata.Item) {
                        TiffImageMetadata.Item item =
                            (TiffImageMetadata.Item)exifItem;
                        TiffField field = item.getTiffField();
                        if (field != null) {
                            tagName = field.getTagName();
                            tagValueDesc = field.getValueDescription();

                            if (tagValueDesc != null && !"".equals(tagValueDesc)) {
                                if (createDateTag.equals(tagName)) {
                                    createDate = fixExifDate(tagValueDesc);
                                } else if (modifyDateTag.equals(tagName)) {
                                    modifyDate = fixExifDate(tagValueDesc);
                                } else if (orientationTag.equals(tagName)) {
                                    degree = calculateDegree(tagValueDesc);
                                }
                             }
                        }
                        exifData.put(tagName, tagValueDesc);
                    }
                }
                setFDODates(fdo, createDate, modifyDate);
            }

            if (!exifData.isEmpty()) {
                properties.put(EXIF_PROPERTY_NAME, exifData.toString());
            }
            
            properties.put(ROTATION_PROPERTY_NAME, degree);

        } catch (IOException e) {
            throw new FileDataObjecyUtilsException("Error retrieving file metadata for '"+fdow.getId()+"'", e);
        } catch (ImageReadException e) {
            throw new FileDataObjecyUtilsException("Error reading Image metadata for '"+fdow.getId()+"'", e);
        }

    }

    /**
     * Sets creation and modification dates for the given file data object.
     * If the input create date is null and in the fdo this date is not set,
     * set it with the value of modify date (if set).
     * If the input modify date is null and in the fdo this date is not set,
     * set it with the value of the create date (if set).
     *
     * @param fdo the file data object which on set dates
     * @param createDate the creation date of the fdo
     * @param modifyDate the modification date of the fdo
     */
    public static void setFDODates(FileDataObject fdo,
                                   String createDate,
                                   String modifyDate) {

        boolean isCreateDateSet = (createDate != null);
        boolean isModifyDateSet = (modifyDate != null);

        if (isCreateDateSet) {
            fdo.setCreated(createDate);
            if (!isModifyDateSet &&
                (fdo.getModified() == null || "".equals(fdo.getModified()))) {

                fdo.setModified(createDate);
            }
        }
        if (isModifyDateSet) {
            fdo.setModified(modifyDate);
            if (!isCreateDateSet &&
                (fdo.getCreated() == null || "".equals(fdo.getCreated()))) {

                fdo.setCreated(modifyDate);
            }
        }
    }

    // --------------------------------------------------------- Private methods
    /**
     * Checks if the file extention and it's format are supported.
     *
     * @param fdo the object containing the picture file to check
     * @return true if the file is supported, false otherwise
     */
    private static boolean isAValidPicture(FileDataObject fdo) {
        if (!hasASupportedExtension(fdo.getName())) {
            return false;
        }

        File bodyFile = fdo.getBodyFile();
        if (bodyFile == null || !bodyFile.isFile()) {
            return false;
        }

        try {
            ImageFormat image_format = Sanselan.guessFormat(bodyFile);
            if (!hasASupportedFormat(image_format)) {
                return false;
            }
        } catch (IOException ex) {
            return false;
        } catch (ImageReadException e) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the given file name ends with a supported extension.
     *
     * @param name the file name to check
     * @return true if the extension is a supported one, false otherwise
     */
    private static boolean hasASupportedExtension(String name) {
         if (name == null) {
            return false;
        }

        name = name.toLowerCase();
        for (int ii = 0; ii < SUPPORTED_PICTURE_EXTENSIONS.length; ii++) {
            if (name.endsWith(SUPPORTED_PICTURE_EXTENSIONS[ii])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given image format is supported.
     *
     * @param format the image format to check
     * @return true if the image format is a supported one, false otherwise
     */
    private static boolean hasASupportedFormat(ImageFormat format) {

        if (format == null) {
            return false;
        }

        if (format.equals(ImageFormat.IMAGE_FORMAT_JPEG) ||
            format.equals(ImageFormat.IMAGE_FORMAT_PNG ) ||
            format.equals(ImageFormat.IMAGE_FORMAT_GIF )  ) {
            return true;
        }

        return false;
    }

    /**
     * Creates the thumbnail.
     *
     * @param imageFile the image file
     * @param thumbFile the empty thumbnail file
     * @param thumbX the width of the thumbnail
     * @param thumbY the height of the thumbnail
     * @param imageName the image file name with extension
     * @param tolerance the percentage of tolerance before creating a thumbnail
     * @return true is the thumbnail has been created, false otherwise
     * @throws IOException if an error occurs
     */
    private static boolean createThumbnail(File imageFile,
                                           File thumbFile,
                                           int thumbX,
                                           int thumbY,
                                           String imageName,
                                           double tolerance)
    throws IOException {

        FileInputStream  fileis  = null;
        ImageInputStream imageis = null;

        Iterator readers = null;

        try {

            readers = ImageIO.getImageReadersByFormatName(
                imageName.substring(imageName.lastIndexOf('.') + 1));
            if (readers == null || (!readers.hasNext())) {
                throw new IOException("File not supported");
            }

            ImageReader reader = (ImageReader) readers.next();

            fileis = new FileInputStream(imageFile);
            imageis = ImageIO.createImageInputStream(fileis);
            reader.setInput(imageis, true);

            // Determines thumbnail height, width and quality
            int thumbWidth = thumbX;
            int thumbHeight = thumbY;

            double thumbRatio = (double) thumbWidth / (double) thumbHeight;
            int imageWidth  = reader.getWidth(0);
            int imageHeight = reader.getHeight(0);

            //
            // Don't create the thumbnail if the original file is smaller
            // than required size increased by % tolerance
            //
            if (imageWidth <= (thumbWidth * (1 + tolerance / 100))
                && imageHeight <= (thumbHeight * (1 + tolerance / 100))) {

                return false;
            }

            double imageRatio = (double) imageWidth / (double) imageHeight;
            if (thumbRatio < imageRatio) {
                thumbHeight = (int) (thumbWidth / imageRatio);
            } else {
                thumbWidth = (int) (thumbHeight * imageRatio);
            }

            ImageReadParam param = reader.getDefaultReadParam();
            param.setSourceSubsampling(3, 3, 0, 0);

            BufferedImage bi = reader.read(0, param);

            Image thumb = bi.getScaledInstance(thumbWidth, thumbHeight, Image.SCALE_SMOOTH);

            BufferedImage thumbImage =
                new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = thumbImage.createGraphics();
            graphics2D.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(thumb, 0, 0, thumbWidth, thumbHeight, null);

            FileOutputStream fileOutputStream = new FileOutputStream(thumbFile);
            ImageIO.write(thumbImage, "jpg", fileOutputStream);

            thumb.flush();
            thumbImage.flush();
            fileOutputStream.flush();
            fileOutputStream.close();
            graphics2D.dispose();

        } finally {
            if (fileis != null) {
                fileis.close();
            }
            if (imageis != null) {
                imageis.close();
            }
        }

        return true;
    }

    /**
     * Creates the ext folder if it doesn't exist.
     *
     * @param folderPath the folder path to create
     * @throws FileDataObjecyUtilsException
     */
    private void createExtFolderIfNeeded(String folderPath)
    throws FileDataObjecyUtilsException {

        File extFolder = new File(folderPath);
        if (!extFolder.exists()) {
            if (!extFolder.mkdirs()) {
                throw new FileDataObjecyUtilsException(
                    "Unable to create folder '" + folderPath + "'");
            }
        }
    }

    /**
     * Creates a thumbnail name based on the given thumbnail size.
     *
     * @param thumbnailsSize the thumbnail size (i.e 200, 200x175 formats)
     * @return a thumbnail name
     */
    private String createThumbnailName(String thumbnailsSize) {
        // TODO for now the thumbnail name does not have the timestamp
        // but may be necessary to change the name if the file is changed
        // this to avoid cacheing on browser
        // : append('_').append(System.currentTimeMillis()).
        return new StringBuilder().append(thumbnailsSize).
                append(".jpg").
                toString();
    }

    /**
     * Creates a rotated thumbnail name based on the given thumbnail size and rotation degree
     *
     * @param thumbnailsSize the thumbnail size (i.e 200, 200x175 formats)
     * @return a thumbnail name
     */
    private String createRotatedThumbnailName(String thumbnailsSize, int degree) {
        // TODO for now the thumbnail name does not have the timestamp
        // but may be necessary to change the name if the file is changed
        // this to avoid cacheing on browser
        // : append('_').append(System.currentTimeMillis()).
        return new StringBuilder().append(thumbnailsSize).
                append(degree).
                append(".jpg").
                toString();
    }

    /**
     * Copies the file data object body in a new file using it as thumbnail.
     * In this way, we will have always all the required thumbnails even if they
     * should contain the original file instead of a resized file.
     *
     * @param thumbnailFile the final thumbnail file
     * @param bodyFile the original file
     */
    private void forceThumbnailCreation(File thumbnailFile, File bodyFile) {
        try {

            FileInputStream inStream = new FileInputStream(bodyFile);
            FileOutputStream outStream = new FileOutputStream(thumbnailFile);
            IOTools.copyStream(inStream, outStream);

        } catch (IOException e) {
            // do nothing
        }
    }

    /**
     * The dates in the EXIF have the format yyyy:MM:dd HH:mm:ss that should
     * be converted in the UTC format. Since the exif specification does not
     * declare that this is a date in local time, no timezone will be applied
     * during convertion in UTC format.
     *
     * @param date the exif string date to convert
     * @return the date in UTC format (yyyyMMdd'T'HHmmss'Z') or null
     */
    private String fixExifDate(String tagValueDesc) {
        try {

            tagValueDesc = StringUtils.removeStart(tagValueDesc, "'");
            tagValueDesc = StringUtils.removeEnd(tagValueDesc, "'");

            return TimeUtils.convertDateFromTo(tagValueDesc,
                                               TimeUtils.PATTERN_EXIF_DATE,
                                               TimeUtils.PATTERN_UTC);

        } catch (ParseException e) {
            // don't set the creation date of the FDO
        }
        return null;
    }

    /**
     * Rotates given image file by given amount of degree.
     * The valid degree values are 0, 90, 180, 270.
     * If the image is a jpg, the rotation is lossless, exif data are preserved
     * and image size is almost the same.
     *
     * @param imageFile the image file
     * @param degree amount of degree to apply
     * @return a byte array containing rotated image data
     * @throws PicturesException if amount of degree is invalid or if an
     *         IOException occurs
     */
    protected static byte[] rotateImage(File imageFile, int degree)
    throws FileDataObjecyUtilsException {

        if (!isAValidRotationDegree(degree)) {
            throw new FileDataObjecyUtilsException(
                "Error rotating image since the '" + degree +
                "' degree value is unsupported");
        }

        String format = getImageFormat(imageFile);
        if ("jpg".equalsIgnoreCase(format) || "jpeg".equalsIgnoreCase(format)) {
            return rotateJpeg(imageFile, degree);
        } else {
            BufferedImage image;
            try {
                image = ImageIO.read(imageFile);
            } catch (IOException ex) {
                throw new FileDataObjecyUtilsException(
                    "Error reading data from input byte array during rotation", ex);
            }

            BufferedImage rotatedImage = rotateImage(image, degree);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {

                ImageIO.write(rotatedImage, format, out);
                return out.toByteArray();

            } catch (IOException ex) {
                throw new FileDataObjecyUtilsException(
                    "Error writing rotated image data into body", ex);
            } finally {
                try {
                    out.close();
                } catch(IOException e) {
                    // do nothing
                }
            }
        }
    }

    /**
     * Rotates given buffered image by given amount of degree.
     * The valid degree values are 0, 90, 180, 270.
     * If the image is a jpg, the rotation is lossless, exif data are preserved
     * and image size is almost the same.
     *
     * @param bufImage the buffered image
     * @param degree amount of degree to apply
     * @return a buffered image containing rotated image data
     * @throws PicturesException if amount of degree is invalid or if an
     *         IOException occurs
     */
    private static BufferedImage rotateImage(BufferedImage bufImage, int degree)
    throws FileDataObjecyUtilsException {

        degree = degree % 360;
        int h;
        int w;

        switch (degree) {
            case 0:
            case 180:
                h = bufImage.getHeight();
                w = bufImage.getWidth();
                break;
            case 90:
            case 270:
                h = bufImage.getWidth();
                w = bufImage.getHeight();
                break;
            default:
                throw new FileDataObjecyUtilsException(
                    "Error rotating image since the '" + degree +
                    "' degree value is unsupported");
        }

        BufferedImage out = null;

        int bufImageType = bufImage.getType();
        if (BufferedImage.TYPE_BYTE_INDEXED == bufImageType ||
            BufferedImage.TYPE_BYTE_BINARY  == bufImageType  ) {

            IndexColorModel model = (IndexColorModel) bufImage.getColorModel();
            out = new BufferedImage(w, h,bufImage.getType(), model);

        } else if (BufferedImage.TYPE_CUSTOM == bufImageType) {
            
            // we don't know what type of image it can be

            // there's a bug in some VM that cause some PNG images to have 
            // type custom: this should take care of this issue

            //check if we need to have alpha channel
            boolean alpha = bufImage.getTransparency() != BufferedImage.OPAQUE;

            if (alpha) {
                // TYPE_INT_ARGB_PRE gives you smaller output images
                // than TYPE_INT_ARGB
                out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
            } else {
                out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            }

        } else {

            out = new BufferedImage(w, h, bufImage.getType());
        }

        Graphics2D g2d = out.createGraphics();

        Map renderingHints = new HashMap();

        renderingHints.put(RenderingHints.KEY_RENDERING,
                           RenderingHints.VALUE_RENDER_QUALITY);
        renderingHints.put(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2d.setRenderingHints(renderingHints);
        g2d.rotate(Math.toRadians(degree));

        switch (degree) {
            case 90:
                g2d.translate(0, -w);
                break;
            case 180:
                g2d.translate(-w, -h);
                break;
            case 270:
                g2d.translate(-h, 0);
                break;
        }

        g2d.drawImage(bufImage, null, 0, 0);
        g2d.dispose();

        return out;
    }

    /**
     * Checks if the given degree is a valid number.
     * The valid values are only: 0, 90, 180, 270, 360.
     *
     * @param degree the value to check
     * @return true if the value is valid, false otherwise
     */
    public static boolean isAValidRotationDegree(int degree) {

        return  degree == 0   ||
                degree == 90  ||
                degree == 180 ||
                degree == 270 ||
                degree == 360;
    }

    /**
     * Retrieves the guess image format reading the file 'magic number' (it's
     * the first bytes of the file) and returns the corresponding format
     * extension.
     * If image format could not be guessed, returns file extension.
     *
     * @param file the file to check
     * @return guessed format extension, or file extension if unable to guess.
     */
    public static String getImageFormat(File file) {
        try {
            return Sanselan.guessFormat(file).extension;
        } catch (Exception ex) {
            // returning extension
            String filename = file.getName();
            String ext = filename.substring(
                filename.lastIndexOf(".")+1,
                filename.length()
            );
            return ext;
        }
    }

    /**
     * Rotates the given jpg image applying the specified degree.
     *
     * @param imageFile the image to rotate
     * @param degree the degree to apply
     * @return a byte array containing the rotated image
     * @throws FileDataObjecyUtilsException if an error occurs
     */
    private static byte[] rotateJpeg(File imageFile, int degree)
    throws FileDataObjecyUtilsException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            int op = getLLJRotationOp(degree);
            LLJTran llj = new LLJTran(imageFile);
            try {
                llj.read(LLJTran.READ_ALL, true);
            } catch (LLJTranException ex) {
               throw new FileDataObjecyUtilsException("Error rotating jpg image", ex);
            }

            // this means we are cropping images with sizes that is
			// not a multiple of MCU.
            int options =
                LLJTran.OPT_DEFAULTS & LLJTran.OPT_WRITE_APPXS |
                LLJTran.OPT_XFORM_TRIM | LLJTran.OPT_XFORM_ORIENTATION;

            llj.transform(op, options);
            llj.save(out, LLJTran.OPT_WRITE_ALL);
            llj.freeMemory();

            return out.toByteArray();
        } catch (IOException ex) {
           throw new FileDataObjecyUtilsException(
               "Error writing rotated jpg image", ex);
        } finally {
            try {
                out.close();
            } catch(IOException e) {
                // do nothing
            }
        }
    }

    /**
     * Returns the corresponding LLJTRAN operation given degree.
     * Accepted values are 0, 90, 180, 270 and for the other values returns
     * LLJTran.NONE operation.
     *
     * @param degree the degree to check
     * @throws FileDataObjecyUtilsException if an error occurs
     */
    private static int getLLJRotationOp(int degree)
    throws FileDataObjecyUtilsException {
        
        switch (degree%360) {
            case 0:
                return LLJTran.NONE;
            case 90:
                return LLJTran.ROT_90;
            case 180:
                return LLJTran.ROT_180;
            case 270:
                return LLJTran.ROT_270;
            default:
                throw new FileDataObjecyUtilsException(
                    "Error retrieving LLJTRAN value for '" + degree + "'");
        }
    }

    /**
     * This method returns the correct rotation degree starting from
     * @param orientation
     * @return
     */
    private String calculateDegree(String orientation) {
        String degree = "0";

        /*
         *     device orientation:
         *              A 6
         *              |
         *         1 <-----> 3
         *              |
         *              V 8
         * 
         */

        switch (Integer.valueOf(orientation).intValue()) {
            case 1:
            case 2: {
                //means device placed horizontal, i.e. Iphone with
                //power button on the left side
                degree = "0";
                break;
            }
            case 3:
            case 4: {
                //means device placed horizontal, i.e. Iphone with
                //power button on the right side
                degree = "180";
                break;
            }
            case 5:
            case 6: {
                //means device placed vertical, i.e. Iphone with
                //power button on the up side
                degree = "90";
                break;
            }
            case 7:
            case 8: {
                //means device placed vertical, i.e. Iphone with
                //power button on the bottom side
                degree = "270";
                break;
            }
            default: {
                degree = "0";
                break;
            }
        }

        return degree;
    }
}
