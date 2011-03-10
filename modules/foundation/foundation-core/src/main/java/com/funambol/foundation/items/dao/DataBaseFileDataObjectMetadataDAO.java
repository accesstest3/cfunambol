/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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
package com.funambol.foundation.items.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;

import com.funambol.server.config.Configuration;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.media.file.FileDataObjectMetadata;
import com.funambol.common.pim.utility.TimeUtils;

import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.foundation.util.Def;
import com.funambol.foundation.util.MediaUtils;
import com.funambol.foundation.util.UploadCompletedStatusProvider;

/**
 * DAO for file data object data to be stored into the database.
 * @version $Id: DataBaseFileDataObjectMetadataDAO.java 36704 2011-03-07 11:50:01Z ubertu $
 */
public class DataBaseFileDataObjectMetadataDAO
extends EntityDAO
implements FileDataObjectMetadataDAO {

    // --------------------------------------------------------------- Constants

    private static final String ONE = "1"; // used as "true" on the DB
    private static final String NIL = "0"; // used as "false" on the DB
    public static final String FILE_DATA_OBJECT_ID_COUNTER = 
        "file-data-object.id";
    public static final String FILE_DATA_OBJECT_PROPERTY_ID_COUNTER =
        "file-data-object-property.id";

    //
    // Table fnbl_file_data_object column names.
    //
    protected static final String SQL_FIELD_ID              = "id";
    protected static final String SQL_FIELD_USERID          = "userid";
    protected static final String SQL_FIELD_SOURCE_URI      = "source_uri";
    protected static final String SQL_FIELD_LAST_UPDATE     = "last_update";
    protected static final String SQL_FIELD_STATUS          = "status";
    protected static final String SQL_FIELD_UPLOAD_STATUS   = "upload_status";
    protected static final String SQL_FIELD_LOCAL_NAME      = "local_name";
    protected static final String SQL_FIELD_CRC             = "crc";
    protected static final String SQL_FIELD_TRUE_NAME       = "true_name";
    protected static final String SQL_FIELD_CREATED         = "created";
    protected static final String SQL_FIELD_MODIFIED        = "modified";
    protected static final String SQL_FIELD_ACCESSED        = "accessed";
    protected static final String SQL_FIELD_H               = "h";
    protected static final String SQL_FIELD_S               = "s";
    protected static final String SQL_FIELD_A               = "a";
    protected static final String SQL_FIELD_D               = "d";
    protected static final String SQL_FIELD_W               = "w";
    protected static final String SQL_FIELD_R               = "r";
    protected static final String SQL_FIELD_X               = "x";
    protected static final String SQL_FIELD_CTTYPE          = "cttype";
    protected static final String SQL_FIELD_SIZE            = "object_size";
    protected static final String SQL_FIELD_SIZE_ON_STORAGE = "size_on_storage";

    //
    // Table fnbl_file_data_object_property column names
    //
    protected static final String SQL_FIELD_PROPERTY_ID = "id";
    protected static final String SQL_FIELD_FDO_ID      = "fdo_id";
    protected static final String SQL_FIELD_NAME        = "name";
    protected static final String SQL_FIELD_VALUE       = "value";

    protected static final int SQL_LOCAL_NAME_DIM = 255;
    protected static final int SQL_TRUE_NAME_DIM  = 255;
    protected static final int SQL_CTTYPE_DIM     = 255;

    private static final String SQL_FDO_ALL_FIELDS =
        "id, userid, source_uri, last_update, status, upload_status, local_name, crc, true_name, " +
        "created, modified, accessed, h, s, a, d, w, r, x, cttype, object_size, size_on_storage";
    private static final String SQL_INSERT_INTO_FNBL_FILE_DATA_OBJECT =
        "INSERT INTO fnbl_file_data_object (" + SQL_FDO_ALL_FIELDS + ") " +
        "VALUES (?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

    private static final String SQL_GET_FDO_AND_PROPERTY_BY_USER_ID =
        "SELECT fnbl_file_data_object.id, userid, source_uri, last_update, " +
        "status, upload_status, local_name, crc, true_name, created, " +
        "modified, accessed, h, s, a, d, w, r, x, cttype, object_size, " +
        "size_on_storage, fnbl_file_data_object_property.id, fdo_id, name, value " +
        "FROM fnbl_file_data_object " +
        "LEFT OUTER JOIN fnbl_file_data_object_property ON " +
        "fnbl_file_data_object.id = fnbl_file_data_object_property.fdo_id " +
        "WHERE fnbl_file_data_object.id = ? and userid=? and source_uri=?";

    private static final String SQL_GET_FNBL_FILE_DATA_OBJECT_ID_LIST =
        "SELECT id FROM fnbl_file_data_object ";

    private static final String SQL_ORDER_BY_ID = "ORDER BY id";
    private static final String SQL_ORDER_BY_DATE =
        "ORDER BY last_update DESC, id DESC";

    private static final String SQL_UPDATE_FNBL_FILE_DATA_OBJECT_BEGIN =
        "UPDATE fnbl_file_data_object SET ";

    private static final String SQL_EQUALS_QUESTIONMARK_COMMA = " = ?, ";
    private static final String SQL_EQUALS_NULL_COMMA = " = null, ";

    private static final String SQL_UPDATE_FNBL_FILE_DATA_OBJECT_END =
        " WHERE id = ? AND userid = ? AND source_uri = ? ";
    private static final String SQL_GET_FILE_DATA_OBJECT_TWIN_ID_LIST =
        SQL_GET_FNBL_FILE_DATA_OBJECT_ID_LIST
        + "WHERE userid = ? AND source_uri = ? "
        + "AND crc = ? "
        + "AND status != 'D' ";
    private static final String SQL_GET_FILENAME_BY_ID =
        "SELECT local_name FROM fnbl_file_data_object "
        + "WHERE id = ? AND userid = ? AND source_uri = ? ";

    private static final String SQL_GET_SIZE_BY_ID =
        "SELECT object_size FROM fnbl_file_data_object "
        + "WHERE id = ? AND userid = ? AND source_uri = ? ";
    private static final String SQL_GET_TOTAL_RESERVED_SIZE_BY_USER =
        "SELECT SUM(object_size) FROM fnbl_file_data_object "
        + "WHERE userid = ? AND source_uri = ? AND status != 'D' ";
    private static final String SQL_GET_TOTAL_SIZE_ON_STORAGE_BY_USER =
        "SELECT SUM(size_on_storage) FROM fnbl_file_data_object "
        + "WHERE userid = ? AND source_uri = ? AND status != 'D' ";

    private static final String SQL_DELETE_EXPIRED_INCOMPLETE_FILE_DATA_OBJECTS_BY_USER_ID =
        "UPDATE fnbl_file_data_object SET status = 'D', local_name = ? " +
        "WHERE status <> 'D' AND upload_status <> '" +
        UploadCompletedStatusProvider.getCompletedUploadStatusAsString() +"' " +
        "AND last_update < ? AND userid = ? AND source_uri = ? ";
    private static final String SQL_RESET_EXPIRED_MISALIGNED_FILE_DATA_OBJECTS_BY_USER_ID =
        "UPDATE fnbl_file_data_object SET object_size = size_on_storage " +
        "WHERE status <> 'D' AND last_update < ? AND userid = ? " +
        "AND source_uri = ? AND object_size <> size_on_storage ";

    private static final String SQL_FDO_PROPERTY_ALL_FIELDS =
        "id, fdo_id, name, value";
    private static final String SQL_INSERT_INTO_FNBL_FILE_DATA_OBJECT_PROPERTY =
        "INSERT INTO fnbl_file_data_object_property (" +
        SQL_FDO_PROPERTY_ALL_FIELDS + ") VALUES (?, ?, ?, ?) ";
    private static final String SQL_GET_ALL_FNBL_FILE_DATA_OBJECT_PROPERTY_BY_FDO_ID =
        "SELECT name, value FROM fnbl_file_data_object_property " +
        "WHERE fdo_id = ? ORDER BY id ";
    private static final String SQL_DELETE_ALL_FNBL_FILE_DATA_OBJECT_PROPERTY_BY_FDO_ID =
        "DELETE FROM fnbl_file_data_object_property WHERE fdo_id = ? ";
    private static final String SQL_DELETE_ALL_FNBL_FILE_DATA_OBJECT_PROPERTY_BY_USER =
        "DELETE FROM fnbl_file_data_object_property " +
        "WHERE fdo_id IN (SELECT id FROM fnbl_file_data_object " +
        "WHERE status = 'D' AND userid = ? AND source_uri = ?) ";
    private static final String SQL_DELETE_ALL_PROPERTY_OF_EXPIRED_INCOMPLETE_FDOS =
        "DELETE FROM fnbl_file_data_object_property " +
        "WHERE fdo_id IN (SELECT id FROM fnbl_file_data_object " +
        "WHERE status = 'D' AND upload_status <> '" +
        UploadCompletedStatusProvider.getCompletedUploadStatusAsString() +"' " +
        "AND last_update < ? AND userid = ? AND source_uri = ? ) ";

    // ------------------------------------------------------------ Private data

    protected final String sourceURI;

    private String sqlGetFnblFileDataObjectIDListByUser;
    private String sqlDeleteFileDataObjectByIDUserID;
    private String sqlDeleteFileDataObjectsByUserID;
    private String sqlGetStatusByIDUserTime;
    private String sqlGetChangedFilesByUserSourceUriLastUpdate;

    private DBIDGenerator propertyIdGenerator;
    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new DAO instance
     * @param userId the user id corresponding to the "userid" field of the
     *        fnbl_file_data_object table.
     * @param sourceURI the source URI corresponding to the "source_uri" field
     *        of the fnbl_file_data_object table.
     */
    protected DataBaseFileDataObjectMetadataDAO(String userId, String sourceURI)  {
        super(userId, FILE_DATA_OBJECT_ID_COUNTER);

        propertyIdGenerator =
            DBIDGeneratorFactory.getDBIDGenerator(FILE_DATA_OBJECT_PROPERTY_ID_COUNTER, coreDataSource);

        this.sourceURI = sourceURI;

        // Generates sourceURI-dependent query strings.
        sqlGetFnblFileDataObjectIDListByUser =
            SQL_GET_FNBL_FILE_DATA_OBJECT_ID_LIST
            + "WHERE userid = ?  AND source_uri = '" + sourceURI + "' "
            // TODO: check how to handle exported
            + "AND upload_status = '"+
            UploadCompletedStatusProvider.getCompletedUploadStatusAsString()+
            "' AND status <> 'D' ";
        sqlDeleteFileDataObjectByIDUserID =
            "UPDATE fnbl_file_data_object SET status = 'D', last_update = ? "
            + "WHERE id = ? AND userid = ? AND source_uri = '"
            + sourceURI + "' ";
        sqlDeleteFileDataObjectsByUserID =
            "UPDATE fnbl_file_data_object SET status = 'D', last_update = ? "
            + "WHERE status <> 'D' AND userid = ? AND source_uri = '"
            + sourceURI + "' ";
        sqlGetStatusByIDUserTime =
            "SELECT status FROM fnbl_file_data_object "
            + "WHERE id = ? AND userid = ? AND source_uri = '"
            + sourceURI + "' AND last_update > ? ";
        sqlGetChangedFilesByUserSourceUriLastUpdate =
            "select id,status from fnbl_file_data_object where userid = ? and " 
            +"source_uri = '" + sourceURI + "' "
            + "AND upload_status = '"+
            UploadCompletedStatusProvider.getCompletedUploadStatusAsString()+"' "
            +"AND (object_size = size_on_storage OR (status = 'D' OR status = 'd') ) "
            +"and last_update>? and last_update<? ";

    }

    // ---------------------------------------------------------- Public methods

    /**
     * Adds file data object metadata of an item to the database.
     * @param fdow
     * @throws com.funambol.foundation.exception.DAOException
     */
    public String addItem(FileDataObjectWrapper fdow) throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;

        try {

            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);

            if (fdow.getId() == null) {
                throw new DAOException("Unable to add item: id should be already defined");
            }

            ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_FILE_DATA_OBJECT);

            int k = 1;

            ps.setLong   (k++, Long.parseLong(fdow.getId()));

            ps.setString (k++, userId);

            ps.setString (k++, sourceURI);

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());

            Timestamp lastUpdate = fdow.getLastUpdate();
            if (lastUpdate == null) {
                lastUpdate = currentTime;
            }
            ps.setLong   (k++, lastUpdate.getTime());

            ps.setString (k++, String.valueOf(SyncItemState.NEW));

            FileDataObject fdo = fdow.getFileDataObject();

            ps.setString(k++, String.valueOf(fdo.getUploadStatus()));

            String fileName = fdow.getLocalName();
            if (fileName == null || fileName.length() == 0){
                ps.setNull(k++, Types.VARCHAR);
            } else {
                ps.setString(k++, StringUtils.left(fileName, SQL_LOCAL_NAME_DIM));
            }

            Long crc = Long.valueOf(fdow.getFileDataObject().getCrc());
            ps.setLong(k++, crc);

            ps.setString(k++, StringUtils.left(fdo.getName(),SQL_TRUE_NAME_DIM));

            MediaUtils.setFDODates(fdo, fdo.getCreated(), fdo.getModified());

            Timestamp created = timestamp(fdo.getCreated());
            if (created != null) {
                ps.setTimestamp(k++, created);
            } else {
                ps.setTimestamp(k++, currentTime);
            }

            Timestamp modified = timestamp(fdo.getModified());
            if (modified != null) {
                ps.setTimestamp(k++, modified);
            } else {
                ps.setTimestamp(k++, currentTime);
            }
            
            ps.setTimestamp(k++, timestamp(fdo.getAccessed()));

            ps.setString(k++, fdo.isHidden()     ? ONE : NIL);
            ps.setString(k++, fdo.isSystem()     ? ONE : NIL);
            ps.setString(k++, fdo.isArchived()   ? ONE : NIL);
            ps.setString(k++, fdo.isDeleted()    ? ONE : NIL);
            ps.setString(k++, fdo.isWritable()   ? ONE : NIL);
            ps.setString(k++, fdo.isReadable()   ? ONE : NIL);
            ps.setString(k++, fdo.isExecutable() ? ONE : NIL);

            if (fdo.getContentType() != null) {
                ps.setString(k++, StringUtils.left(fdo.getContentType(), SQL_CTTYPE_DIM));
            } else {
                ps.setNull(k++, Types.VARCHAR);
            }

            if (fdo.getSize() == null) {
                ps.setNull(k++, Types.BIGINT);
            } else {
                ps.setLong(k++, fdo.getSize());
            }

            if (fdow.getSizeOnStorage() == null) {
                ps.setNull(k++, Types.BIGINT);
            } else {
                ps.setLong(k++, fdow.getSizeOnStorage());
            }

            ps.executeUpdate();
            DBTools.close(null, ps, null);

            // stores file data object properties
            addProperties(fdow);

        } catch (Exception e) {
            throw new DAOException("Error adding file data object.", e);
        } finally {
            DBTools.close(con, ps, null);
        }
        return fdow.getId();
    }

    /**
     * Retrieves file data object metadata and all its properties.
     *
     * @param uid the file data object identifier to search
     * @return <code>FileDataObjectWrapper</code> instance. <code>content</code>
     * is null.
     * @throws DAOException if an error occurs during searching
     */
    public FileDataObjectWrapper getItem(String uid) throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        FileDataObjectWrapper fdow;

        try {

            Long id = Long.parseLong(uid);

            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_FDO_AND_PROPERTY_BY_USER_ID);
            ps.setLong(1, id);
            ps.setString(2, userId);
            ps.setString(3, sourceURI);

            rs = ps.executeQuery();

            fdow = createFileDataObjectWrapper(uid, rs);

            DBTools.close(con, ps, rs);

        } catch (Exception e) {
            throw new DAOException("Error seeking file data object and its properties.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return fdow;
    }

    /**
     * Retrieves the list of the ids of all the twin items of a file data object.
     * @param fdo
     * @return the list of all the twin items
     * @throws com.funambol.foundation.exception.DAOException
     */
    public List getTwinItems(FileDataObject fdo) throws DAOException {

        List twins = new ArrayList();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            // calculate crc
            Long crc = Long.valueOf(fdo.getCrc());

            if (Configuration.getConfiguration().isDebugMode()) {
                if (log.isTraceEnabled()) {

                    String tdSearch = crc.toString();

                    StringBuilder sb = new StringBuilder();
                    sb.append("Looking for items having: ")
                    .append("\n> crc: '").append(tdSearch).append('\'');

                    log.trace(sb.toString());
                }
            }

            ps = con.prepareStatement(SQL_GET_FILE_DATA_OBJECT_TWIN_ID_LIST
                    + SQL_ORDER_BY_ID);
            ps.setString(1, userId);
            ps.setString(2, sourceURI);
            ps.setLong(3, crc);

            rs = ps.executeQuery();

            long twinId;
            while(rs.next()) {
                twinId = rs.getLong(1); // The id is the first
                                        // and only column
                if (log.isTraceEnabled()) {
                    log.trace("Twin found for item " + fdo.getName() +
                              " with id: " + twinId);
                }
                twins.add(Long.toString(twinId));
            }

        } catch (Exception e) {
            throw new DAOException("Error retrieving twin.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return twins;
    }

    /**
     * Updates file data object metadata. <code>content</code> is not used.
     * @param fdow
     * @throws com.funambol.foundation.exception.DAOException
     */
    public void updateItem(FileDataObjectWrapper fdow) throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            FileDataObject fdo = fdow.getFileDataObject();
            Long fdoId = Long.valueOf(fdow.getId());

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());

            StringBuilder updateQuery = new StringBuilder();

            updateQuery.append(SQL_UPDATE_FNBL_FILE_DATA_OBJECT_BEGIN);

            updateQuery.append(SQL_FIELD_LAST_UPDATE)
                       .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            updateQuery.append(SQL_FIELD_STATUS)
                       .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            updateQuery.append(SQL_FIELD_UPLOAD_STATUS)
                       .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            String localName = fdow.getLocalName();
            if (localName != null) {
                updateQuery.append(SQL_FIELD_LOCAL_NAME)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            } else {
                // if the item was deleted and readded again with a sync
                // considering only the metadata, the local name must be set to
                // null, since the previous file was already deleted and the new
                // file would be uploaded later.
                updateQuery.append(SQL_FIELD_LOCAL_NAME)
                           .append(SQL_EQUALS_NULL_COMMA);
            }

            Long crc = Long.valueOf(fdow.getFileDataObject().getCrc());
            updateQuery.append(SQL_FIELD_CRC)
                       .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            String trueName = fdo.getName();
            if (trueName != null) {
                updateQuery.append(SQL_FIELD_TRUE_NAME)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            MediaUtils.setFDODates(fdo, fdo.getCreated(), fdo.getModified());

            Timestamp created = timestamp(fdo.getCreated());
            if (created == null) {
                created = currentTime;
            }

            Timestamp modified = timestamp(fdo.getModified());
            if (modified == null) {
                modified = currentTime;
            }
            updateQuery.append(SQL_FIELD_CREATED)
                       .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            updateQuery.append(SQL_FIELD_MODIFIED)
                       .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            Timestamp accessed = timestamp(fdo.getAccessed());
            if (accessed != null) {
                updateQuery.append(SQL_FIELD_ACCESSED)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            Boolean hidden = fdo.getHidden();
            if (hidden != null) {
                updateQuery.append(SQL_FIELD_H)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            Boolean system = fdo.getSystem();
            if (system != null) {
                updateQuery.append(SQL_FIELD_S)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            Boolean archived = fdo.getArchived();
            if (archived != null) {
                updateQuery.append(SQL_FIELD_A)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            Boolean deleted = fdo.getDeleted();
            if (deleted != null) {
                updateQuery.append(SQL_FIELD_D)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            Boolean writable = fdo.getWritable();
            if (writable != null) {
                updateQuery.append(SQL_FIELD_W)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            Boolean readable = fdo.getReadable();
            if (readable != null) {
                updateQuery.append(SQL_FIELD_R)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            Boolean executable = fdo.getExecutable();
            if (executable != null) {
                updateQuery.append(SQL_FIELD_X)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            String contentType = fdo.getContentType();
            if (contentType != null) {
                updateQuery.append(SQL_FIELD_CTTYPE)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            Long size = fdo.getSize();
            if (size != null) {
                updateQuery.append(SQL_FIELD_SIZE)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            Long sizeOnStorage = fdow.getSizeOnStorage();
            if (sizeOnStorage != null) {
                updateQuery.append(SQL_FIELD_SIZE_ON_STORAGE)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            if (updateQuery.charAt(updateQuery.length() - 2) == ','){
                updateQuery.deleteCharAt(updateQuery.length() - 2);
            }

            updateQuery.append(SQL_UPDATE_FNBL_FILE_DATA_OBJECT_END);

             // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);

            ps = con.prepareStatement(updateQuery.toString());

            int k = 1;

            Timestamp lastUpdate =
                    (fdow.getLastUpdate() == null) ?
                        currentTime : fdow.getLastUpdate();
            ps.setLong(k++, lastUpdate.getTime());

            ps.setString(k++, String.valueOf(Def.PIM_STATE_UPDATED));
            ps.setString(k++, String.valueOf(fdo.getUploadStatus()));

            if (localName != null){
                ps.setString(k++, StringUtils.left(localName, SQL_LOCAL_NAME_DIM));
            }

            ps.setLong(k++, crc);

            if (trueName != null) {
                ps.setString(k++, StringUtils.left(trueName, SQL_TRUE_NAME_DIM));
            }

            // cannot be null
            ps.setTimestamp(k++, created);
            ps.setTimestamp(k++, modified);
            
            if (accessed != null) {
                ps.setTimestamp(k++, accessed);
            }

            if (hidden != null) {
                ps.setString(k++, hidden ? ONE : NIL);
            }

            if (system != null) {
                ps.setString(k++, system ? ONE : NIL);
            }

            if (archived != null) {
                ps.setString(k++, archived ? ONE : NIL);
            }

            if (deleted != null) {
                ps.setString(k++, deleted ? ONE : NIL);
            }

            if (writable != null) {
                ps.setString(k++, writable ? ONE : NIL);
            }

            if (readable != null) {
                ps.setString(k++, readable ? ONE : NIL);
            }

            if (executable != null) {
                ps.setString(k++, executable ? ONE : NIL);
            }

            if (contentType != null) {
                ps.setString(k++, StringUtils.left(contentType, SQL_CTTYPE_DIM));
            }

            if (size != null) {
                ps.setLong(k++, size);
            }

            if (sizeOnStorage != null) {
                ps.setLong(k++, sizeOnStorage);
            }

            ps.setLong(k++, fdoId);
            ps.setString(k++, userId);
            ps.setString(k++, sourceURI);

            ps.executeUpdate();

            // delete and add the properties associated to the new FDO
            removeAllProperties(fdow.getId());
            addProperties(fdow);

        } catch (Exception e) {
            throw new DAOException("Error updating file data object.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }
    }

    /**
     * Updates metadata fields when FileDataObject body changes, like crc, size,
     * localname etc and the properties associated to it.
     *
     * @param fdow the wrapper with the new body file
     * @throws DAOException if an error occurs
     */
    public void updateItemWhenBodyChanges(FileDataObjectWrapper fdow)
    throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;

        try {

            Long fdoId = Long.valueOf(fdow.getId());

            FileDataObject fdo = fdow.getFileDataObject();

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());

            StringBuilder updateQuery = new StringBuilder();

            updateQuery.append(SQL_UPDATE_FNBL_FILE_DATA_OBJECT_BEGIN);

            updateQuery.append(SQL_FIELD_LAST_UPDATE)
                       .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            String localName = fdow.getLocalName();
            if (localName != null) {
                updateQuery.append(SQL_FIELD_LOCAL_NAME)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            updateQuery.append(SQL_FIELD_UPLOAD_STATUS)
                       .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            Long crc = Long.valueOf(fdow.getFileDataObject().getCrc());
            updateQuery.append(SQL_FIELD_CRC)
                       .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            Long size = fdo.getSize();
            if (size != null) {
                updateQuery.append(SQL_FIELD_SIZE)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            Long sizeOnStorage = fdow.getSizeOnStorage();
            if (sizeOnStorage != null) {
                updateQuery.append(SQL_FIELD_SIZE_ON_STORAGE)
                           .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            // set always created and modified dates
            updateQuery.append(SQL_FIELD_CREATED)
                       .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            updateQuery.append(SQL_FIELD_MODIFIED)
                       .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            if (updateQuery.charAt(updateQuery.length() - 2) == ','){
                updateQuery.deleteCharAt(updateQuery.length() - 2);
            }

            updateQuery.append(SQL_UPDATE_FNBL_FILE_DATA_OBJECT_END);

             // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);

            ps = con.prepareStatement(updateQuery.toString());

            int k = 1;

            Timestamp lastUpdate =
                    (fdow.getLastUpdate() == null) ?
                        currentTime : fdow.getLastUpdate();
            ps.setLong(k++, lastUpdate.getTime());

            if (localName != null){
                ps.setString(k++, StringUtils.left(localName, SQL_LOCAL_NAME_DIM));
            }

            ps.setString(k++, ""+fdo.getUploadStatus());
            ps.setLong(k++, crc);

            if (size != null) {
                ps.setLong(k++, size);
            }

            if (sizeOnStorage != null) {
                ps.setLong(k++, sizeOnStorage);
            }

            MediaUtils.setFDODates(fdo, fdo.getCreated(), fdo.getModified());

            Timestamp created = timestamp(fdo.getCreated());
            if (created != null) {
                ps.setTimestamp(k++, created);
            } else {
                ps.setTimestamp(k++, currentTime);
            }

            Timestamp modified = timestamp(fdo.getModified());
            if (modified != null) {
                ps.setTimestamp(k++, modified);
            } else {
                ps.setTimestamp(k++, currentTime);
            }

            ps.setLong(k++, fdoId);
            ps.setString(k++, userId);
            ps.setString(k++, sourceURI);

            ps.executeUpdate();

            DBTools.close(con, ps, null);
            
            // delete and add the properties associated to the new FDO
            removeAllProperties(fdow.getId());
            addProperties(fdow);

        } catch (Exception e) {
            throw new DAOException("Error updating file data object and its properties.", e);
        } finally {
            DBTools.close(con, ps, null);
        }
    }

    /**
     * Retrieves the file name of the file data object content file.
     * @param uid file data object unique identifier
     * @return the filename of the content file
     * @throws com.funambol.foundation.exception.DAOException
     */
    public String getLocalName(String uid) throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String fileName;

        try {
            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_FILENAME_BY_ID);

            ps.setLong(1, Long.parseLong(uid));
            ps.setString(2, userId);
            ps.setString(3, sourceURI);

            rs = ps.executeQuery();
            if(rs.next()) {
                fileName = rs.getString(1);
            } else {
                throw new DAOException("No local name found, item with uid '"+uid+"' doesn't exist.");
            }

        } catch(DAOException e) {
            throw e;
        } catch (Exception e) {
            throw new DAOException("Error retrieving local file name.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return fileName;
    }

    /**
     * Gets just the size of a file by extracting that information from its
     * metadata.
     *
     * @param uid the unique locator of the file data object in the DB
     * @return the file size or 0 if the item cannot be found
     * @throws com.funambol.foundation.exception.DAOException
     */
    public long getItemSize(String uid) throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        long size = 0L;

        try {

            Long id = Long.parseLong(uid);

            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_SIZE_BY_ID);
            ps.setLong(1, id);
            ps.setString(2, userId);
            ps.setString(3, sourceURI);

            rs = ps.executeQuery();

            if (rs.isBeforeFirst()) {
                rs.next();
                size = rs.getLong(SQL_FIELD_SIZE);
            }
            // If the result set is empty, nothing happens and size remains 0

            DBTools.close(null, ps, rs);

        } catch (Exception e) {
            throw new DAOException("Error seeking file data object.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return size;
    }

    /**
     * Gets the total size of all the files (except the deleted ones) by
     * extracting that information from their metadata.
     *
     * @return the storage space filled by the user's files (in bytes)
     * @throws DAOException if an error occurs
     */
    public long getReservedStorageSpace() throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        long totalSize;

        try {
            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_TOTAL_RESERVED_SIZE_BY_USER);
            ps.setString(1, userId);
            ps.setString(2, sourceURI);

            rs = ps.executeQuery();
            rs.next();

            totalSize = rs.getLong(1);

            DBTools.close(null, ps, rs);

        } catch (Exception e) {
            throw new DAOException("Error calculating used and reserved " +
                "storage space.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return totalSize;
    }

    /**
     * Gets the total storage size for all files (except the deleted ones).
     *
     * @return the storage space filled by the user's files
     * @throws DAOException if an error occurs
     */
    public long getStorageSpaceUsage() throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        long totalSize;

        try {
            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_TOTAL_SIZE_ON_STORAGE_BY_USER);
            ps.setString(1, userId);
            ps.setString(2, sourceURI);

            rs = ps.executeQuery();
            rs.next();

            totalSize = rs.getLong(1);

            DBTools.close(null, ps, rs);

        } catch (Exception e) {
            throw new DAOException("Error calculating used storage space.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return totalSize;
    }

    /**
     * Removes from the database the expired incomplete items.
     * Items are incomplete if they don't have a corresponding file on
     * the file system.
     * Incomplete items are considered expired if older than 24h.
     * @throws DAOException if an error occurs
     */
    public void removeExpiredIncompleteItems() throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;

        try {
            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(false);

            long lastUpdate = getCutOffTime();
            ps = con.prepareStatement(SQL_DELETE_EXPIRED_INCOMPLETE_FILE_DATA_OBJECTS_BY_USER_ID);
            ps.setNull(1, Types.VARCHAR);
            ps.setLong(2, lastUpdate);
            ps.setString(3, userId);
            ps.setString(4, sourceURI);

            ps.execute();

            DBTools.close(con, ps, null);

            removeAllPropertiesOfExpiredIncompleteItems(lastUpdate);

        } catch (Exception e) {
            throw new DAOException("Error deleting the expired incomplete items.", e);
        } finally {
            DBTools.close(con, ps, null);
        }
    }

    /**
     * Removes all the properties associated to the expired incomplete items.
     * Items are incomplete if they don't have a corresponding file on
     * the file system.
     * Incomplete items are considered expired if older than 24h.
     *
     * @param lastUpdate the last update time
     * @throws DAOException if an error occurs during deletion
     */
    public void removeAllPropertiesOfExpiredIncompleteItems(long lastUpdate)
    throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;

        try {
            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(false);

            ps = con.prepareStatement(SQL_DELETE_ALL_PROPERTY_OF_EXPIRED_INCOMPLETE_FDOS);
            ps.setLong(1, getCutOffTime());
            ps.setString(2, userId);
            ps.setString(3, sourceURI);

            ps.execute();

        } catch (Exception e) {
            throw new DAOException("Error deleting all properties for expired incomplete items.", e);
        } finally {
            DBTools.close(con, ps, null);
        }
    }

    /**
     * Reset the size field to the value of the size_on_storage field for the
     * misaligned items i.e. items for which the metadata has been updated but
     * the updated file has not been uploaded.
     */
    public void resetSizeForMisalignedItems() throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;

        try {
            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(false);

            ps = con.prepareStatement(SQL_RESET_EXPIRED_MISALIGNED_FILE_DATA_OBJECTS_BY_USER_ID);
            ps.setLong(1, getCutOffTime());
            ps.setString(2, userId);
            ps.setString(3, sourceURI);

            ps.execute();

        } catch (Exception e) {
            throw new DAOException("Error deleting the expired incomplete items.", e);
        } finally {
            DBTools.close(con, ps, null);
        }
    }

    /**
     * Inserts file data object's properties.
     * 
     * @param fdow the wrapper that contains the fdo properties
     * @throws DAOException if an error occurs during the adding
     */
    public void addProperties(FileDataObjectWrapper fdow) throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;

        if (fdow.getId() == null) {
            throw new DAOException("Unable to add properties since the FDO identifier must be defined");
        }

        FileDataObject fdo = fdow.getFileDataObject();
        long fdoId = Long.parseLong(fdow.getId());

        try {

            con = getUserDataSource().getRoutedConnection(userId);

            int k = 0;
            Map<String, String> properties = fdo.getProperties();
            if (properties != null && !properties.isEmpty()) {

                ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_FILE_DATA_OBJECT_PROPERTY);

                Iterator<Entry<String, String>> entries =
                    properties.entrySet().iterator();
                while (entries.hasNext()) {
                    Entry<String, String> entry = entries.next();

                    k = 1;
                    ps.setLong(k++, propertyIdGenerator.next());
                    ps.setLong(k++, fdoId);
                    ps.setString(k++, entry.getKey());
                    ps.setString(k++, entry.getValue());

                    ps.executeUpdate();
                }

            }
        } catch (Exception e) {
            throw new DAOException("Error adding file data object properties for '"+fdoId+"'.", e);
        } finally {
            DBTools.close(con, ps, null);
        }
    }

    /**
     * Deletes all file data object's properties for the given fdo identifier.
     *
     * @param fdoId the fdo identifier
     * @throws DAOException if an error occurs during the deletion
     */
    @Override
    public void removeAllProperties(String fdoId) throws DAOException {
        Connection con = null;
        PreparedStatement ps = null;

        try {

            con = getUserDataSource().getRoutedConnection(userId);

            ps = con.prepareStatement(SQL_DELETE_ALL_FNBL_FILE_DATA_OBJECT_PROPERTY_BY_FDO_ID);
            ps.setLong(1, Long.valueOf(fdoId));

            ps.executeUpdate();

        } catch (Exception ex) {
            throw new DAOException("Error deleting all properties for '" + fdoId + "'", ex);
        } finally {
            DBTools.close(con, ps, null);
        }
    }

    /**
     * Deletes all file data object's properties for every fdo with status 'D'
     * for the given user and source uri.
     *
     * @throws DAOException if an error occurs during deletion
     */
    @Override
    public void removeAllPropertiesByUserID() throws DAOException {
        Connection con = null;
        PreparedStatement ps = null;

        try {

            con = getUserDataSource().getRoutedConnection(userId);

            ps = con.prepareStatement(SQL_DELETE_ALL_FNBL_FILE_DATA_OBJECT_PROPERTY_BY_USER);
            ps.setString(1, userId);
            ps.setString(2, sourceURI);

            ps.executeUpdate();

        } catch (Exception ex) {
            throw new DAOException("Error deleting all properties for user '" + userId + "'", ex);
        } finally {
            DBTools.close(con, ps, null);
        }
    }

    /**
     * Retrieves all file data object's properties for the given fdo identifier
     * specified in the wrapper and set them in the fdo metadata.
     *
     * @param fdow the wrapper which on set the properties
     * @throws DAOException if an error occurs during the getting of properties
     */
    public void getProperties(FileDataObjectWrapper fdow) throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        Long fdoId = Long.valueOf(fdow.getId());
        try {

            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_ALL_FNBL_FILE_DATA_OBJECT_PROPERTY_BY_FDO_ID);
            ps.setLong(1, fdoId);

            rs = ps.executeQuery();

            setFileDataObjectProperties(fdow.getFileDataObject(), rs);

        } catch (Exception e) {
            throw new DAOException("Error retrieving file data object's properties for '"+fdoId+"'", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

    }

    // --------------------------------------------------------- Private methods

    /**
     * Creates a <code>FileDataObjectMetadata</code> object and fills metadata
     * from a database row (content is still null).
     *
     * @param wrapperId the file data object identifier
     * @param rs the ResultSet
     * @return a FileDataObjectWrapper set with the results of the query
     * @throws java.sql.SQLException if an error occurs
     * @throws com.funambol.framework.server.store.NotFoundException if no file
     *         data object has been found with the given id
     */
    private FileDataObjectWrapper createFileDataObjectWrapper(String wrapperId,
                                                              ResultSet rs)
    throws SQLException, NotFoundException {

        FileDataObjectWrapper fdow = null;
        FileDataObject fdo = new FileDataObject();
        Map<String, String> properties = new LinkedHashMap<String, String>();

        boolean isFirstTime = true;
        while(rs.next()) {

            if (isFirstTime) {

                FileDataObjectMetadata metadata =
                    new FileDataObjectMetadata(rs.getString(SQL_FIELD_TRUE_NAME));

                metadata.setCreated (utcDateTime(rs.getTimestamp(SQL_FIELD_CREATED )));
                metadata.setModified(utcDateTime(rs.getTimestamp(SQL_FIELD_MODIFIED)));
                metadata.setAccessed(utcDateTime(rs.getTimestamp(SQL_FIELD_ACCESSED)));

                metadata.setHidden    (ONE.equals(rs.getString(SQL_FIELD_H)));
                metadata.setSystem    (ONE.equals(rs.getString(SQL_FIELD_S)));
                metadata.setArchived  (ONE.equals(rs.getString(SQL_FIELD_A)));
                metadata.setDeleted   (ONE.equals(rs.getString(SQL_FIELD_D)));
                metadata.setWritable  (ONE.equals(rs.getString(SQL_FIELD_W)));
                metadata.setReadable  (ONE.equals(rs.getString(SQL_FIELD_R)));
                metadata.setExecutable(ONE.equals(rs.getString(SQL_FIELD_X)));

                metadata.setUploadStatus(rs.getString(SQL_FIELD_UPLOAD_STATUS));

                metadata.setContentType(rs.getString(SQL_FIELD_CTTYPE));

                long size = rs.getLong(SQL_FIELD_SIZE);
                if (rs.wasNull()) {
                    metadata.setSize(null);
                } else {
                    metadata.setSize(size);
                }
                fdo.setMetadata(metadata);

                fdow = new FileDataObjectWrapper(wrapperId,
                                                 rs.getString(SQL_FIELD_USERID),
                                                 fdo);

                fdow.setLocalName(rs.getString(SQL_FIELD_LOCAL_NAME));

                long sizeOnStorage = rs.getLong(SQL_FIELD_SIZE_ON_STORAGE);
                if (rs.wasNull()) {
                    fdow.setSizeOnStorage(null);
                } else {
                    fdow.setSizeOnStorage(sizeOnStorage);
                }

                fdow.setStatus(rs.getString(SQL_FIELD_STATUS).charAt(0));
                fdow.setLastUpdate(new Timestamp(rs.getLong(SQL_FIELD_LAST_UPDATE)));

                isFirstTime = false;
            }

            long fdoId = rs.getLong(SQL_FIELD_FDO_ID);
            if (!rs.wasNull()) {
                properties.put(rs.getString(SQL_FIELD_NAME),
                               rs.getString(SQL_FIELD_VALUE));
            }
        }
        fdo.setProperties(properties);

        if (fdow == null) {
            throw new NotFoundException("No file data object found for '"+wrapperId+"'.");
        }
        return fdow;
    }

    private void setFileDataObjectProperties(FileDataObject fdo, ResultSet rs)
    throws SQLException, NotFoundException {

        Map<String, String> properties = new LinkedHashMap<String, String>();
        while (rs.next()) {
            properties.put(rs.getString(1), rs.getString(2));
        }

        fdo.setProperties(properties);
    }

    private static Timestamp timestamp(String utcDateTime) {

        if (utcDateTime == null) {
            return null;
        }

        SimpleDateFormat formatter =
                new SimpleDateFormat(TimeUtils.PATTERN_UTC);
        formatter.setTimeZone(TimeUtils.TIMEZONE_UTC);

        try {
            Date date = formatter.parse(utcDateTime);

            return new Timestamp(date.getTime());

        } catch (ParseException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to parse date/time " + utcDateTime );
            }
            return null;
        }
    }

    private static String utcDateTime(Timestamp timestamp) {

        if (timestamp == null) {
            return null;
        }

        SimpleDateFormat formatter =
                new SimpleDateFormat(TimeUtils.PATTERN_UTC);
        formatter.setTimeZone(TimeUtils.TIMEZONE_UTC);

        return formatter.format(new Date(timestamp.getTime()));

    }

    /**
     * Return the query string to use to retrieve all the Items belonging to a user
     * @return the query string to use to retrieve all the Items belonging to a user
     */
    protected String getAllItemsQuery() {

        return sqlGetFnblFileDataObjectIDListByUser + SQL_ORDER_BY_DATE;
    }

    /**
     * Return the query string to use to remove the Item belonging to a user
     * @return the query string to use to remove the Item belonging to a user
     */
    protected String getRemoveItemQuery() {
        return sqlDeleteFileDataObjectByIDUserID;
    }

    /**
     * Return the query string to use to remove the all Items belonging to a user
     * @return the query string to use to remove the all Items belonging to a user
     */
    @Override
    protected String getRemoveAllItemsQuery() {
        return sqlDeleteFileDataObjectsByUserID;
    }

    /**
     * Return the query string to use to retrieve the status of an Items
     * belonging to a user
     * @return the query string to use to retrieve the status of an Items
     * belonging to a user
     */
    @Override
    protected String getItemStateQuery() {
        return sqlGetStatusByIDUserTime;
    }

    /**
     * Return the query string to use to retrieve the changed items
     * belonging to a user
     * @return the query string to use to retrieve the changed items
     * belonging to a user
     */
    @Override
    protected String getChangedItemsQuery() {
        return sqlGetChangedFilesByUserSourceUriLastUpdate + SQL_ORDER_BY_ID;
    }

    /**
     * Returns the cut off time.
     * @return the cut off time.
     */
    protected long getCutOffTime() {

        return System.currentTimeMillis() - (24 * 60 * 60 * 1000);
    }

}