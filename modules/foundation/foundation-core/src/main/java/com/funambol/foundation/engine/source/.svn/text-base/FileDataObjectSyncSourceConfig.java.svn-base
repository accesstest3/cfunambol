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
package com.funambol.foundation.engine.source;

import java.util.HashMap;
import java.util.Map;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.tools.StringTools;

import com.funambol.foundation.util.Def;

/**
 * This is a class for the configuration parameters of a
 * {@link FileDataObjectSyncSource}.
 *
 * @version $Id$
 */
public class FileDataObjectSyncSourceConfig {

    //---------------------------------------------------------------- Constants

    public static final int DEFAULT_FILE_SYS_DIR_DEPTH = 8;

    //----------------------------------------------------------- Protected data

    protected static final FunambolLogger log =
            FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    //------------------------------------------------------------- Private data

    //--------------------------------------------------------------- Properties

    /**
     * The root folder of the file data objects subtree
     *
     * @param rootPath
     * @deprecated
     */
    public void setRootPath(String rootPath) {
        throw new IllegalArgumentException("Deprecated, use jclouds properties instead");
    }

    /**
     *
     * @return
     * @deprecated
     */
    public String getRootPath() {
        throw new IllegalArgumentException("Deprecated, use jclouds properties instead");
    }

    /**
     * Base path where temp files are stored
     */
    private String localRootPath =  null;

    public void setLocalRootPath(String localRootPath) {
        this.localRootPath = localRootPath;
    }

    public String getLocalRootPath() {
        return localRootPath;
    }

    /** The maximum size allowed for each file (in bytes) */
    private String maxSize = null;

    public void setMaxSize(String maxSize) {
        this.maxSize = maxSize;
    }

    public String getMaxSize() {
        return maxSize;
    }

    /**
     * Return the MaxSize value but as a byte value
     * @return
     */
    public long getMaxSizeAsLong() {

        try {
            Long maxSizeL = StringTools.converterStringSizeInBytes(maxSize);
            if (maxSizeL != null) {
                return maxSizeL.longValue();
            }
            return -1L;
        } catch (NumberFormatException ex) {
            if (log.isErrorEnabled()) {
                log.error("Invalid max size '" + maxSize + "'");
            }
            throw ex;
        }
    }

    /**
     * holds the max quota threshold for roles
     */
    private Map<String, String> roleQuota = new HashMap<String, String>();

    public void setRoleQuota(Map<String, String> roleQuota) {
        this.roleQuota = roleQuota;
    }

    public Map<String, String> getRoleQuota() {
        return this.roleQuota;
    }


    /**
     * The nested directory depth for the file system data store
     */
    private int fileSysDirDepth = DEFAULT_FILE_SYS_DIR_DEPTH;

    public void setFileSysDirDepth(int fileSysDirDepth) {
        this.fileSysDirDepth = fileSysDirDepth;
    }

    public int getFileSysDirDepth() {
        return this.fileSysDirDepth;
    }

    /**
     * Choose of delete propagation. If true, the delete sent from client
     * for media are propagate to the server, if false, server returns to the
     * client 200 OK but the deletion is not performed server side.
     * Default value is true, in order tom mantein same behaviour of v90
     */
    private boolean propagateDelete = false;

    public boolean getPropagateDelete() {
        return propagateDelete;
    }

    public void setPropagateDelete(boolean propagateDelete) {
        this.propagateDelete = propagateDelete;
    }
    
    /**
     * Base path when using jclouds filesystem provider.  The
     * container name is attached to obtain the final path
     */
    private String storageFilesystemRootPath =  null;
    public void setStorageFilesystemRootPath(String storageFilesystemRootPath) {
        this.storageFilesystemRootPath = storageFilesystemRootPath;
    }
    public String getStorageFilesystemRootPath() {
        return storageFilesystemRootPath;
    }

    private String storageProvider = null;
    public void setStorageProvider(String storageProvider) {
        this.storageProvider = storageProvider;
    }
    public String getStorageProvider() {
        return this.storageProvider;
    }

    private String storageIdentity = null;
    public void setStorageIdentity(String storageIdentity) {
        this.storageIdentity = storageIdentity;
    }
    public String getStorageIdentity() {
        return this.storageIdentity;
    }

    private String storageCredential = null;
    public void setStorageCredential(String storageCredential) {
        this.storageCredential = storageCredential;
    }
    public String getStorageCredential() {
        return this.storageCredential;
    }

    private String storageContainerName = null;
    public void setStorageContainerName(String storageContainerName) {
        this.storageContainerName = storageContainerName;
    }
    public String getStorageContainerName() {
        return this.storageContainerName;
    }

    /**
     * holds the additional properties that could be set for a specific FDO
     */
    private Map<String, String[]> additionalProperties =
        new HashMap<String, String[]>();

    public void setAdditionalProperties(Map<String, String[]> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public Map<String, String[]> getAdditionalProperties() {
        return this.additionalProperties;
    }

    // ------------------------------------------------------------ Constructors

    public FileDataObjectSyncSourceConfig() {
    }

    //----------------------------------------------------------- Public methods

    /**
     * Returns the quota assigned to the user
     * @param user the user for which the quota as to be retrieved
     * @return the quota assigned to the user
     */
    public long getQuotaPerUser(Sync4jUser user) {
        String[] roles = user.getRoles();
        if (roles == null) {
            if (log.isErrorEnabled()) {
                log.error("No role defined for user '" + user.getUsername() + "'."
                    + " No quota assigned");
            }
            return 0;
        }
        return getQuotaPerUser(roles);
    }

    //-------------------------------------------------------- Protected methods

    //---------------------------------------------------------- Private methods

    /**
     * Returns the max quota among the quotas associated to the given roles
     * @param roles the rolses
     * @return the max quota among the quotas associated to the given roles
     */
    private long getQuotaPerUser(String[] roles) {
        long quotaPerUser = 0;

        for (String role : roles) {
            String quotaAsStr = getRoleQuota().get(role);
            try {
                Long quota = StringTools.converterStringSizeInBytes(quotaAsStr);
                if (quota != null && quota.compareTo(quotaPerUser) > 0) {
                    quotaPerUser = quota.longValue();
                }
            } catch (NumberFormatException ex) {
                if (log.isErrorEnabled()) {
                    log.error("Invalid quota '" + quotaAsStr + "' for role '" +
                        role + "'");
                }
                throw ex;
            }
        }

        return quotaPerUser;
    }

}
