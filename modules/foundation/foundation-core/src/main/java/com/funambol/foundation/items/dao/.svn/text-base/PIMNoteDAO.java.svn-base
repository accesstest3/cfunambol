/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.note.Note;

import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.model.NoteWrapper;
import com.funambol.foundation.util.Def;

import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.SourceUtils;
import com.funambol.server.config.Configuration;
import java.util.StringTokenizer;

/**
 * <code>PIMNoteDAO</code> implementation.
 * 
 * @author $Id: PIMNoteDAO.java,v 1.12 2008-08-22 10:43:05 piter_may Exp $
 */
public class PIMNoteDAO extends EntityDAO {

    // --------------------------------------------------------------- Constants
    
    //
    // Table column names.
    //
    protected static final String SQL_FIELD_ID              = "id";
    protected static final String SQL_FIELD_USERID          = "userid";
    protected static final String SQL_FIELD_LAST_UPDATE     = "last_update";
    protected static final String SQL_FIELD_STATUS          = "status";
    protected static final String SQL_FIELD_SUBJECT         = "subject";
    protected static final String SQL_FIELD_TEXTDESCRIPTION = "textdescription";
    protected static final String SQL_FIELD_CATEGORIES      = "categories";
    protected static final String SQL_FIELD_FOLDER          = "folder";
    protected static final String SQL_FIELD_COLOR           = "color";
    protected static final String SQL_FIELD_HEIGHT          = "height";
    protected static final String SQL_FIELD_WIDTH           = "width";
    protected static final String SQL_FIELD_TOP             = "top";
    protected static final String SQL_FIELD_LEFT_MARGIN     = "leftmargin";
    protected static final String SQL_FIELD_CRC             = "crc";
    
    //
    // Max size of columns
    //
    protected static final int SQL_SUBJECT_DIM         = 255;
    protected static final int SQL_TEXTDESCRIPTION_DIM = 65535;
    protected static final int SQL_CATEGORIES_DIM      = 255;
    protected static final int SQL_FOLDER_DIM          = 255;
    
    //
    // Queries.
    // 
    private static final String SQL_ORDER_BY_ID = "ORDER BY id";
    private static final String SQL_GET_FNBL_PIM_NOTE_ID_LIST =
            "SELECT id FROM fnbl_pim_note ";
    private static final String SQL_GET_FNBL_PIM_NOTE_ID_LIST_BY_USER =
            SQL_GET_FNBL_PIM_NOTE_ID_LIST
            + "WHERE userid = ? "
            + "AND status <> 'D' ";
    private static final String SQL_DELETE_NOTE_BY_ID_USERID =
            "UPDATE fnbl_pim_note SET status = 'D', last_update = ? "
            + "WHERE id = ? AND userid = ? ";
    private static final String SQL_INSERT_INTO_FNBL_PIM_NOTE =
            "INSERT INTO fnbl_pim_note "
            + "(id, userid, last_update, status, subject, textdescription, categories, "
            + "folder, color, height, width, top, leftmargin, crc) "
            + "VALUES "
            + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
    private static final String SQL_GET_FNBL_PIM_NOTE =
            "SELECT id, userid, last_update, status, subject, textdescription, categories, "
            + "folder, color, height, width, top, leftmargin "
            + "FROM fnbl_pim_note ";
    private static final String SQL_GET_FNBL_PIM_NOTE_BY_ID_USER =
            SQL_GET_FNBL_PIM_NOTE
            + "WHERE id = ? AND userid = ? ";
    private static final String SQL_DELETE_NOTES_BY_USERID =
            "UPDATE fnbl_pim_note SET status = 'D', last_update = ? "
            + "WHERE status <> 'D' AND userid = ?";
    private static final String SQL_GET_STATUS_BY_ID_USER_TIME =
            "SELECT status FROM fnbl_pim_note "
            + "WHERE id = ? AND userid = ? AND last_update > ? ";
    private static final String
            SQL_GET_FNBL_PIM_NOTE_ID_LIST_BY_USER_TIME_STATUS =
            SQL_GET_FNBL_PIM_NOTE_ID_LIST
            + "WHERE "
            + "userid = ? "
            + "AND last_update > ? "
            + "AND last_update < ? "
            + "AND status = ? ";

    private static final String SQL_UPDATE_FNBL_PIM_NOTE_BEGIN =
            "UPDATE fnbl_pim_note SET ";
    private static final String SQL_EQUALS_QUESTIONMARK_COMMA = " = ?, ";
    private static final String SQL_UPDATE_FNBL_PIM_NOTE_END =
            " WHERE id = ? AND userid = ? ";
    private static final String SQL_GET_NOTE_TWIN_ID_LIST = 
            SQL_GET_FNBL_PIM_NOTE_ID_LIST
            + "WHERE userid = ? " 
            + "AND crc = ? " 
            + "AND status != 'D' ";
    private static final String SQL_GET_NOTE_TWIN_ID_LIST_CRC_NULL = 
            SQL_GET_FNBL_PIM_NOTE_ID_LIST
            + "WHERE userid = ? "
            + "AND crc is null "
            + "AND status != 'D' ";
    private static final String SQL_GET_CHANGED_NOTES_BY_USER_AND_LAST_UPDATE =
            "select id,status from fnbl_pim_note where userid=? and last_update>? and last_update<? order by id";

    //------------------------------------------------------------- Constructors

    /**
     * Creates a new DAO instance
     */
    public PIMNoteDAO(String userId)  {
        super(userId, Def.ID_COUNTER);
        if (log.isTraceEnabled()) {
            log.trace("Created new PIMNoteDAOImpl for user ID " + userId);
        }
    }

    //----------------------------------------------------------- Public methods

    public void addItem(NoteWrapper nw) throws DAOException {
        if (log.isTraceEnabled()) {
            log.trace("PIMNoteDAO addItem begin");
        }

        Connection con = null;
        PreparedStatement ps = null;

        long id = 0;
        String sId = null;

        Timestamp lastUpdate = nw.getLastUpdate();
        if (lastUpdate == null) {
            lastUpdate = new Timestamp(System.currentTimeMillis());
        }

        try {

            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);

            // calculate table row id
            sId = nw.getId();
            if (sId == null) { // ...as it should be
                sId = getNextID();
                nw.setId(sId);
            }
            id = Long.parseLong(sId);

            ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_PIM_NOTE);

            int k = 1;
            //
            // GENERAL
            //

            if (log.isTraceEnabled()) {
                log.trace("Preparing statement with ID " + id);
            }
            ps.setLong   (k++, id);

            if (log.isTraceEnabled()) {
                log.trace("Preparing statement with user ID " + userId);
            }
            ps.setString (k++, userId);
            
            ps.setLong   (k++, lastUpdate.getTime());
            ps.setString (k++, String.valueOf(Def.PIM_STATE_NEW));
            
            Note note = nw.getNote();
            
            ps.setString(k++, StringUtils.left(
                    note.getSubject().getPropertyValueAsString()        , SQL_SUBJECT_DIM));
            
            String textDescription = note.getTextDescription().getPropertyValueAsString();
            if (textDescription != null) {
                textDescription = textDescription.replace('\0', ' ');
            }
            String truncatedTextDescription = StringUtils.left(textDescription,   SQL_TEXTDESCRIPTION_DIM);
            ps.setString(k++, truncatedTextDescription);
            
            ps.setString(k++, truncateCategoriesField(
                    note.getCategories().getPropertyValueAsString()     , SQL_CATEGORIES_DIM));            
            ps.setString(k++, truncateFolderField(
                    note.getFolder().getPropertyValueAsString()         , SQL_FOLDER_DIM));
            
            Property color  = note.getColor();
            Property height = note.getHeight();
            Property width  = note.getWidth();
            Property top    = note.getTop();
            Property left   = note.getLeft();
            
            if (Property.isEmptyProperty(color)){
                ps.setNull(k++, Types.INTEGER);
            } else {
                ps.setInt(k++, Integer.parseInt(color.getPropertyValueAsString()));
            }

            if (Property.isEmptyProperty(height)){
                ps.setNull(k++, Types.INTEGER);
            } else {
                ps.setInt(k++, Integer.parseInt(height.getPropertyValueAsString()));
            }

            if (Property.isEmptyProperty(width)){
                ps.setNull(k++, Types.INTEGER);
            } else {
                ps.setInt(k++, Integer.parseInt(width.getPropertyValueAsString()));
            }

            if (Property.isEmptyProperty(top)){
                ps.setNull(k++, Types.INTEGER);
            } else {
                ps.setInt(k++, Integer.parseInt(top.getPropertyValueAsString()));
            }
            
            if (Property.isEmptyProperty(left)){
                ps.setNull(k++, Types.INTEGER);
            } else {
                ps.setInt(k++, Integer.parseInt(left.getPropertyValueAsString()));
            }
            
            Long crc = calculateCrc(truncatedTextDescription);
            if (crc == null){
                ps.setNull(k++, Types.BIGINT);
            } else {
                ps.setLong(k++, crc);
            }
            
            ps.executeUpdate();
            
        } catch (Exception e) {
            throw new DAOException("Error adding note.", e);
        } finally {
            DBTools.close(con, ps, null);
        }

        if(log.isTraceEnabled()) {
            log.trace("Added item with ID " + id);
            log.trace("PIMNoteDAO addItem end");
        }
    }

    public NoteWrapper getItem(String uid) throws DAOException {
        if (log.isTraceEnabled()) {
            log.trace("PIMNoteDAO start getItem " + uid);
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        NoteWrapper nw;

        Long id = Long.parseLong(uid);

        try {
            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_FNBL_PIM_NOTE_BY_ID_USER);
            ps.setLong(1, id);
            ps.setString(2, userId);

            rs = ps.executeQuery();

            nw = createNoteWrapper(uid, rs);

            DBTools.close(null, ps, rs);

        } catch (Exception e) {
            throw new DAOException("Error seeking note.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return nw;
    }

    public List getTwinItems(Note note) throws DAOException {
        if (log.isTraceEnabled()) {
            log.trace("PIMNoteDAO getTwinItems begin");
        }

        List twins = new ArrayList();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if(!isTwinSearchAppliableOn(note)) {
            return twins;
        }

        try {

            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);
            
            // calculate crc
            String textDescription = note.getTextDescription().getPropertyValueAsString();
            if (textDescription != null) {
                textDescription = textDescription.replace('\0', ' ');
            }
            String truncatedTextDescription = StringUtils.left(textDescription, SQL_TEXTDESCRIPTION_DIM);
            Long crc = calculateCrc(truncatedTextDescription);
            
            //
            // If funambol is not in the debug mode is not possible to print the
            // note because it contains sensitive data.
            //
            if (Configuration.getConfiguration().isDebugMode()) {
                if (log.isTraceEnabled()) {
                    
                    String tdSearch = (crc == null ? "<N/A>" : crc.toString());
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append("Looking for items having: ")
                    .append("\n> crc: '").append(tdSearch).append('\'');
                    
                    log.trace(sb.toString());
                }
            }
                        
            if (crc == null){
                ps = con.prepareStatement(SQL_GET_NOTE_TWIN_ID_LIST_CRC_NULL
                        + SQL_ORDER_BY_ID);
            } else {
                ps = con.prepareStatement(SQL_GET_NOTE_TWIN_ID_LIST 
                        + SQL_ORDER_BY_ID);
                ps.setLong(2, crc);
            }
            ps.setString(1, userId);
            
            rs = ps.executeQuery();
            
            long twinId;
            while(rs.next()) {
                twinId = rs.getLong(1); // The id is the first
                                        // and only column
                if (log.isTraceEnabled()) {
                    log.trace("Twin found: " + twinId);
                }
                twins.add(Long.toString(twinId));
            }

        } catch (Exception e) {
            throw new DAOException("Error retrieving twin.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }
        if (log.isTraceEnabled()) {
            log.trace("PIMNoteDAO getTwinItems end");
        }

        return twins;
    }

    public String updateItem(NoteWrapper nw) throws DAOException {
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            
            //
            // Note fields
            //
            Note note = nw.getNote();

            StringBuilder updateQuery = new StringBuilder();
            
            updateQuery.append(SQL_UPDATE_FNBL_PIM_NOTE_BEGIN);

            updateQuery.append(SQL_FIELD_LAST_UPDATE + SQL_EQUALS_QUESTIONMARK_COMMA);
            updateQuery.append(SQL_FIELD_STATUS + SQL_EQUALS_QUESTIONMARK_COMMA);
            
            String subject = note.getSubject().getPropertyValueAsString();
            if (subject != null) {
                updateQuery.append(SQL_FIELD_SUBJECT
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            String textDescription = note.getTextDescription().getPropertyValueAsString();
            if (textDescription != null) {
                updateQuery.append(SQL_FIELD_TEXTDESCRIPTION
                        + SQL_EQUALS_QUESTIONMARK_COMMA);

                updateQuery.append(SQL_FIELD_CRC
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            String categories      = note.getCategories().getPropertyValueAsString();
            if (categories != null) {
                updateQuery.append(SQL_FIELD_CATEGORIES
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            
            String folder         = note.getFolder().getPropertyValueAsString();
            if (folder != null) {
                updateQuery.append(SQL_FIELD_FOLDER
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            String color          = note.getColor().getPropertyValueAsString();
            if (color != null) {
                updateQuery.append(SQL_FIELD_COLOR
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            String height         = note.getHeight().getPropertyValueAsString();
            if (height != null) {
                updateQuery.append(SQL_FIELD_HEIGHT
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            
            String width          = note.getWidth().getPropertyValueAsString();
            if (width != null) {
                updateQuery.append(SQL_FIELD_WIDTH
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            
            String top            = note.getTop().getPropertyValueAsString();
            if (top != null) {
                updateQuery.append(SQL_FIELD_TOP
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            
            String left           = note.getLeft().getPropertyValueAsString();
            if (left != null) {
                updateQuery.append(SQL_FIELD_LEFT_MARGIN
                        + SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            
            if (updateQuery.charAt(updateQuery.length() - 2) == ','){
                updateQuery.deleteCharAt(updateQuery.length() - 2);
            }
                        
            updateQuery.append(SQL_UPDATE_FNBL_PIM_NOTE_END);
                
            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);

            ps = con.prepareStatement(updateQuery.toString());
            
            int k = 1;
            
            //
            // last update
            //
            Timestamp lastUpdate =
                    (nw.getLastUpdate() == null) ?
                        new Timestamp(System.currentTimeMillis())
                        : nw.getLastUpdate();
            ps.setLong(k++, lastUpdate.getTime());
            
            //
            // status
            //
            ps.setString(k++, String.valueOf(Def.PIM_STATE_UPDATED));
            
            //
            // subject
            //
            if (subject != null){
                ps.setString(k++, StringUtils.left(subject, SQL_SUBJECT_DIM));
            }
            
            //
            // textDescription
            //
            if (textDescription != null){
                textDescription = textDescription.replace('\0', ' ');
                String truncatedTextDescription = StringUtils.left(textDescription, SQL_TEXTDESCRIPTION_DIM);
                
                ps.setString(k++, truncatedTextDescription);
                ps.setLong(k++, calculateCrc(truncatedTextDescription));
            }
            
            //
            // categories
            //
            if (categories != null){
                ps.setString(k++, truncateCategoriesField(categories, SQL_CATEGORIES_DIM));
            }
            
            //
            // folder
            //
            if (folder != null){
                ps.setString(k++, truncateFolderField(folder, SQL_FOLDER_DIM));
            }
                        
            //
            // color
            //
            if (color != null){
                if (color.length() == 0){
                    ps.setNull(k++, Types.INTEGER);
                } else {
                    ps.setInt(k++, Integer.parseInt(color));                    
                }
            }
            
            //
            // height
            //
            if (height != null){
                if (height.length() == 0){
                    ps.setNull(k++, Types.INTEGER);                    
                } else {
                    ps.setInt(k++, Integer.parseInt(height));                    
                }
            }
            
            //
            // width
            //
            if (width != null){
                if (width.length() == 0){
                    ps.setNull(k++, Types.INTEGER);                    
                } else {
                    ps.setInt(k++, Integer.parseInt(width));
                }
            }
            
            //
            // top
            //
            if (top != null){
                if (top.length() == 0){
                    ps.setNull(k++, Types.INTEGER);                    
                } else {
                    ps.setInt(k++, Integer.parseInt(top));
                }
            }
            
            //
            // left
            //
            if (left != null){
                if (left.length() == 0){
                    ps.setNull(k++, Types.INTEGER);                    
                } else {
                    ps.setInt(k++, Integer.parseInt(left));
                }
            }

            //
            // id
            //
            ps.setLong(k++, Long.parseLong(nw.getId()));
            //
            // userId
            //
            ps.setString(k++, userId);            

            ps.executeUpdate();

            DBTools.close(null, ps, null);
        } catch (Exception e) {
            throw new DAOException("Error updating note.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return nw.getId();
    }

    /**
     * This method allows to understand if is possible to run the twin search
     * on the given note.
     * Fields used in the twin search are:
     * - text description
     *
     * @param note the note we want to check.
     *
     * @return true if at least one field used for twin search contains meaningful
     * data, false otherwise.
     *
     */

    public boolean isTwinSearchAppliableOn(Note note) {
           return note!=null &&
                  (note.getTextDescription()!=null &&
                   note.getTextDescription().getPropertyValueAsString()!=null &&
                   note.getTextDescription().getPropertyValueAsString().length()>0);
    }

    // --------------------------------------------------------- Private methods
    private NoteWrapper createNoteWrapper(String wrapperId, ResultSet rs)
    throws SQLException, NotFoundException {

        if (!rs.next()) {
            throw new NotFoundException("No note found.");
        }

        Note note = new Note();
        note.setSubject         (new Property(rs.getString(SQL_FIELD_SUBJECT)));
        note.setTextDescription (new Property(rs.getString(SQL_FIELD_TEXTDESCRIPTION)));
        note.setCategories      (new Property(rs.getString(SQL_FIELD_CATEGORIES)));
        note.setFolder          (new Property(rs.getString(SQL_FIELD_FOLDER)));
        note.setColor           (new Property(rs.getString(SQL_FIELD_COLOR)));
        note.setHeight          (new Property(rs.getString(SQL_FIELD_HEIGHT)));
        note.setWidth           (new Property(rs.getString(SQL_FIELD_WIDTH)));
        note.setTop             (new Property(rs.getString(SQL_FIELD_TOP)));
        note.setLeft            (new Property(rs.getString(SQL_FIELD_LEFT_MARGIN)));
        
        String userId_ = rs.getString(SQL_FIELD_USERID);
        NoteWrapper nw = new NoteWrapper(wrapperId, userId_, note);
        nw.setLastUpdate (new Timestamp(rs.getLong(SQL_FIELD_LAST_UPDATE)));
        nw.setStatus     (rs.getString(SQL_FIELD_STATUS).charAt(0));
        
        return nw;
    }
    
    private static String truncateFolderField(String folderField, int truncationSize){
          return truncateStringList(folderField, "/\\", truncationSize);      
    }

    private static String truncateCategoriesField(String categoriesField, int truncationSize){
        return truncateStringList(categoriesField, ",", truncationSize);
    }
    
    private static String truncateStringList(
            String categoriesField, 
            String separatorsString, 
            int truncationSize){
        
        if (categoriesField == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(categoriesField, separatorsString, true);        
        StringBuilder sb = new StringBuilder("");
        
        while (st.hasMoreTokens()){
            
            String token = st.nextToken();
            
            if (sb.length() + token.length() > truncationSize){
                break;
            }
            
            sb.append(token);
        }
        
        if (sb.length() > 0) {
            char[] separators = separatorsString.toCharArray();
            for (char separator : separators) {
                if (sb.charAt(sb.length() - 1) == separator) {
                    sb.deleteCharAt(sb.length() - 1);
                    break;
                }
            }
        }
        
        return sb.toString();        
    }
    
    /**
     * Calculate a hashcode of a note given its body.
     * 
     * @param textDescription
     * @return the hashcode, or <code>null</code> if body is <code>null</code>.
     */
    private Long calculateCrc(String textDescription){
        if (textDescription == null){
            return null;
        }
        
        textDescription =
                textDescription.replaceAll("(\n|\r)+", "").toLowerCase();
        
        long crc = SourceUtils.computeCRC(textDescription.getBytes());
        
        return new Long(crc);
    }
    
    /**
     * Return the query string to use to retrieve all the Items belonging to a user
     * @return the query string to use to retrieve all the Items belonging to a user
     */
    @Override
    protected String getAllItemsQuery() {
        return SQL_GET_FNBL_PIM_NOTE_ID_LIST_BY_USER + SQL_ORDER_BY_ID;
    }
    
    /**
     * Return the query string to use to remove the Item belonging to a user
     * @return the query string to use to remove the Item belonging to a user
     */
    @Override
    protected String getRemoveItemQuery() {
        return SQL_DELETE_NOTE_BY_ID_USERID;
    }
    
    /**
     * Return the query string to use to remove the all Items belonging to a user
     * @return the query string to use to remove the all Items belonging to a user
     */
    @Override
    protected String getRemoveAllItemsQuery() {
        return SQL_DELETE_NOTES_BY_USERID;
    }
    
    /**
     * Return the query string to use to retrieve the status of an Items 
     * belonging to a user
     * @return the query string to use to retrieve the status of an Items 
     * belonging to a user
     */
    @Override
    protected String getItemStateQuery() {
        return SQL_GET_STATUS_BY_ID_USER_TIME;
    }
    
    @Override
    protected String getChangedItemsQuery() {
        return SQL_GET_CHANGED_NOTES_BY_USER_AND_LAST_UPDATE;
    }

}
