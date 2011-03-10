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


package com.funambol.framework.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * This class allows to copy data extracted by an input stream to a proper
 * output stream.
 * It's possible to specify a maximum amount of data that will be transferred.
 *
 * @version $Id$
 */
public class StreamCopy {
    //---------------------------------------------------------------- Constants

    public static final String MAX_BYTES_TRANSFERRED_ERROR_MSG =
                                                 "Max number of bytes reached.";

    public static final String CLOSING_INPUT_STREAM_ERROR_MSG =
                                              "Error closing the input stream.";

    public static final String CLOSING_OUTPUT_STREAM_ERROR_MSG =
                                             "Error closing the output stream.";

    public static final String WRITING_BYTES_ERROR_MSG =
                               "An error occurred writing bytes to the target.";

    public static final String READING_BYTES_ERROR_MSG =
                             "An error occurred reading bytes from the source.";

    public static final String SOURCE_NULL_ERROR_MSG =
                                      "Unable to read data from a null source.";

    public static final String TARGET_NULL_ERROR_MSG =
                                       "Unable to write data to a null target.";


    // Set to 3MB
    private static final int MAX_DATA_TRANSFERRED = 3*1024*1024;

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    //------------------------------------------------------------ Instance data
    private InputStream  source             = null;
    private OutputStream target             = null;
    private long          maxDataTransferred = 0;
    private int          bufferSize         = 0;

    /**
     * Creates a StreamCopy object that will copy all the data available from the
     * source to the target up to the MAX_FILE_SIZE value (3Mb)
     * The DEFAULT_BUFFER_SIZE will be used as buffer size.
     * 
     * @param source it's the input stream the bytes are read from
     * @param target it's the output stream the bytes are written to
     */
    public StreamCopy(InputStream source,
                      OutputStream target) {
        this(source, target, -1, -1);
                
    }

    /**
     * Creates a StreamCopy object that will copy all the data available from the
     * source to the target up to the max number of data specified as input parameter.
     * The DEFAULT_BUFFER_SIZE will be used as buffer size.
     *
     * @param source it's the input stream the bytes are read from
     * @param target it's the output stream the bytes are written to
     * @param maxDataTransferred it's the maximum amout of bytes we want to be
     * transferred from the source to the the target
     */
    public StreamCopy(InputStream source,
                      OutputStream target,
                      long maxDataTransferred) {
        this(source,
             target,
             maxDataTransferred,
             -1);
    }


    /**
     * Creates a StreamCopy object that will copy all the data available from the
     * source to the target up to the max number of data specified as input parameter.
     * The specified bufferSize will be used as size of the buffer of bytes used
     * to read and write data
     *
     * @param source it's the input stream the bytes are read from
     * @param target it's the output stream the bytes are written to
     * @param maxDataTransferred it's the maximum amout of bytes we want to be
     * transferred from the source to the the target
     * @param bufferSize the size of the buffer of bytes used to read and write
     * data
     */
    public StreamCopy(InputStream source,
                      OutputStream target,
                      long maxDataTransferred,
                      int bufferSize) {
        this.source = source;
        this.target = target;
        this.maxDataTransferred = 
                (maxDataTransferred<0)?MAX_DATA_TRANSFERRED:maxDataTransferred;

        this.bufferSize = (bufferSize<0)?DEFAULT_BUFFER_SIZE:bufferSize;
     }

    /**
     * This method allows to copy the byte from a source to a target using a buffer
     * of bytes of the desired size transferring a number of bytes up to the desired
     * limit
     *
     * @return the number of read bytes
     *
     * @throws IOException if any error occurs during the copy
     */
    public long performCopy() throws IOException {
        return performCopy(true, true);
    }

    /**
     * This method allows to copy the byte from a source to a target using a buffer
     * of bytes of the desired size transferring a number of bytes up to the desired
     * limit
     *
     * @return the number of read bytes
     *
     * @throws IOException if any error occurs during the copy
     */
    public long performCopy(boolean closeIn, boolean closeOut) throws IOException {
        if(source==null) {
            throw new IllegalStateException(SOURCE_NULL_ERROR_MSG);
        }

        if(target==null) {
            throw new IllegalStateException(TARGET_NULL_ERROR_MSG);
        }

        byte[] buffer = new byte[bufferSize];
        long count = 0;
        int n      = 0;
        try {
            while (-1 != (n = readBytes(buffer))) {
                if(count+n>maxDataTransferred) {
                    writeBytes(buffer, (int)(maxDataTransferred-count));
                    throw new MaxDataTransferredException(MAX_BYTES_TRANSFERRED_ERROR_MSG);
                }

                writeBytes(buffer, n);
                count += n;
            }
        } finally {
            if(closeIn) {
                close(source);
            }
            if(closeOut) {
                close(target);
            }
            target = null;
        }
        return count;
    }



    /**
     * Writes the byte contained into the iput buffer from 0 to n
     * @param buffer the buffer of bytes to be written
     * @param n the number of bytes to be written
     * @throws IOException if any error occurred writing bytes to the target
     */
    private void writeBytes(byte[] buffer, int n) throws IOException {
        try {
            target.write(buffer, 0, n);
        } catch(IOException e) {
            throw e;
        }
    }

    /**
     * Read the bytes from the source filling the given array of bytes
     * @param buffer the buffer to be filled with the data read from the source
     * @return the number of bytes read
     * @throws IOException if any error occurred reading bytes from the source
     */

    private int readBytes(byte[] buffer) throws IOException {
        try {
            return source.read(buffer);
        } catch(IOException e) {
            throw e;
        }
    }

    /**
     * close the given inputStream
     * @param inputStream
     */
    private void close(InputStream inputStream) {
        try {
            inputStream.close();
        } catch(IOException e) {
        }
    }

    /**
     * close the given outputStream after flushing it
     * @param outputStream
     */
    private void close(OutputStream outputStream) {
        try {
            outputStream.flush();
        } catch(IOException e) {
            
        }
        try {
            outputStream.close();
        } catch(IOException e) {
        }
    }

    /**
     * This exception is thrown when the maximum number of transferred bytes
     * is reached while performing the copy from an input stream to an output stream
     */
    public static class MaxDataTransferredException extends IOException {


        /**
         * creates an exception with the given message
         * @param message it's the message contained inside the exception
         */
        public MaxDataTransferredException(String message) {
            super(message);
        }

    }

}
